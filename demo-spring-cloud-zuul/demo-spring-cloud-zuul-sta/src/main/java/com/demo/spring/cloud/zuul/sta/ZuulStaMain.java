package com.demo.spring.cloud.zuul.sta;

import com.demo.spring.cloud.zuul.AccessFilter;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;

@EnableZuulProxy
@SpringCloudApplication
public class ZuulStaMain {

  public static void main(String[] args) {
    SpringApplication.run(ZuulStaMain.class);
  }

  @Bean
  public AccessFilter accessFilter() {
    return new AccessFilter();
  }

  @Bean
  public PatternServiceRouteMapper serviceRouteMapper() {
    return new PatternServiceRouteMapper("(?<service>^.+)-(?<version>v.+$)", "${version}/${service}");
  }

}
