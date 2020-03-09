package com.demo.spring.cloud.session;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.demo.spring.cloud.session", "com.glodon.ngtrade.util.security"})
public class SessionMain {
    public static void main(String[] args) {
        SpringApplication.run(SessionMain.class);
    }
}
