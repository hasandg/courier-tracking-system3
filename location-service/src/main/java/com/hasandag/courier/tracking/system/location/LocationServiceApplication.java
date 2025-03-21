package com.hasandag.courier.tracking.system.location;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class LocationServiceApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(LocationServiceApplication.class);
        app.setAllowBeanDefinitionOverriding(true);
        app.run(args);
    }
}
