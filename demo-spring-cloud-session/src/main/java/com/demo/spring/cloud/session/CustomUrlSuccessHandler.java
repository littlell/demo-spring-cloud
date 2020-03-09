package com.demo.spring.cloud.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CustomUrlSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${security.success.path}")
    private String casSuccessPath;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        setDefaultTargetUrl(casSuccessPath);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
