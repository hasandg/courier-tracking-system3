package com.hasandag.courier.tracking.system.distancecalculation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:distance-calculation-service}")
    private String applicationName;

    @Bean
    public OpenAPI distanceCalculationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Distance Calculation Service API")
                        .description("API for calculating and tracking courier travel distances")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Hasan Dag")
                                .email("contact@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Default Server URL")
                ));
    }
} 