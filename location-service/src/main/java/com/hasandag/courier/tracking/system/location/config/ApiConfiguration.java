package com.hasandag.courier.tracking.system.location.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Consolidated API Configuration that combines OpenAPI spec and Swagger UI config
 * to avoid bean conflicts
 */
@Configuration
public class ApiConfiguration implements WebMvcConfigurer {

    @Value("${spring.application.name:location-service}")
    private String applicationName;

    @Value("${server.port:8084}")
    private String serverPort;

    @Bean
    @Primary
    public OpenAPI locationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Location Service API")
                        .description("API for tracking courier locations in the Courier Tracking System")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Hasan Dag")
                                .email("hasan_email@example.com")
                                .url("https://github.com/hasandogn"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("http://location-service:8084")
                                .description("Docker Container Server"),
                        new Server()
                                .url("/")
                                .description("Default Server URL")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("JWT Authorization header using Bearer scheme. Example: \"Authorization: Bearer {token}\""))
                )
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Add resource handlers for Swagger UI
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/4.15.5/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirect to the Swagger UI
        registry.addViewController("/swagger-ui/")
                .setViewName("forward:/swagger-ui/index.html");

        // Default redirect for swagger-ui.html (which we set in properties)
        registry.addViewController("/swagger-ui.html")
                .setViewName("forward:/swagger-ui/index.html");
    }
} 