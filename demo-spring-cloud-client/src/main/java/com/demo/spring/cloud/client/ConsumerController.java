package com.demo.spring.cloud.client;

import com.netflix.discovery.converters.Auto;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class ConsumerController {

  private final Logger logger = Logger.getLogger(getClass());

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  private Registration registration;       // 服务注册

  @Autowired
  private DiscoveryClient discoveryClient;

  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  public String helloConsumer() {

    ServiceInstance serviceInstance = serviceInstance();
    logger.info("/ext/ribbon-consumer, host:" + serviceInstance.getHost() + ", service_id: " + serviceInstance.getServiceId());

    return restTemplate.getForEntity("http://HELLO-SERVICE/hello",
        String.class).getBody();
  }

  /**
   * 获取当前服务的服务实例
   *
   * @return ServiceInstance
   */
  public ServiceInstance serviceInstance() {
    List<ServiceInstance> list = discoveryClient.getInstances(registration.getServiceId());
    if (list != null && list.size() > 0) {
      return list.get(0);
    }
    return null;
  }
}
