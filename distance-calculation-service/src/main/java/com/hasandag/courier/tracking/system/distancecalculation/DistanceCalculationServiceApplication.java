package com.hasandag.courier.tracking.system.distancecalculation;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.hasandag.courier.tracking.system.distancecalculation.client")
@EnableScheduling
public class DistanceCalculationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DistanceCalculationServiceApplication.class, args);
    }
}
