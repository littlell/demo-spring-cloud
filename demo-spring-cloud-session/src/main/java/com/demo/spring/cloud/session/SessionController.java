package com.demo.spring.cloud.session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SessionController {

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  @Value("${service.host}/${cas.filter.processing.path}")
  private String casCheckUrl;

  @Value("${cas.host}/serviceLogin")
  private String casLoginUrl;

  @Value("${service.host}")
  private String host;

  private String serviceKey="9j7p95yBTBnTKRHJpeeu8913K2nlQuRD";

  @GetMapping(value = "/cas/return")
  public String casReturn(HttpServletRequest request, HttpServletResponse response)
      throws URISyntaxException {
    Object realUrl = request.getSession().getAttribute("real_url");
    if (null != realUrl) {
      return "redirect:" + realUrl;
    }

    return "redirect:" + host;
  }

  @GetMapping(value = "/")
  @ResponseBody
  public String index(HttpServletRequest request) throws URISyntaxException {
    return "index";
  }

  @GetMapping(value = "/cas/login")
  public String casLogin(HttpServletRequest request, RedirectAttributes redirectAttributes,
      @RequestParam(value = "real_url", required = false) String realUrl)
      throws ServletException, IOException {

    if (StringUtils.isNotBlank(realUrl)) {
      request.getSession().setAttribute("real_url", realUrl);
    }

    redirectAttributes.addAttribute("service_key", serviceKey);
    redirectAttributes
        .addAttribute("callback_url", casCheckUrl);

    return "redirect:" + casLoginUrl;
  }

  @GetMapping(value = "/custom")
  @ResponseBody
  public String custom(HttpServletRequest request) {
    LOGGER.warn("warn");
    return "custom";
  }
}
