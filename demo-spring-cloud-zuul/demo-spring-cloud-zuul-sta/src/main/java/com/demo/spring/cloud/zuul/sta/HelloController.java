package com.demo.spring.cloud.zuul.sta;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
  @GetMapping("/local/hello")
  public String localHello() {
    return "Hello, local!";
  }
}
