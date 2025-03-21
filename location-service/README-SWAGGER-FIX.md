# OpenAPI/Swagger Configuration Conflict Fix

## Problem

The Location Service fails to start with the following error:

```
Error creating bean with name 'openAPIBuilder': Unsatisfied dependency expressed through method 'openAPIBuilder' parameter 0: 
No qualifying bean of type 'io.swagger.v3.oas.models.OpenAPI' available: expected single matching bean but found 2: 
locationServiceOpenAPI,customOpenAPI
```

This happens because there are two `OpenAPI` bean definitions in the application:

1. `locationServiceOpenAPI()` in `OpenApiConfig.java`
2. `customOpenAPI()` in `SwaggerConfig.java` (in compiled bytecode but not in source code)

## Solution

To fix this issue, follow these steps:

1. Delete the existing configuration files:
    - `src/main/java/com/hasandag/courier/tracking/system/location/config/OpenApiConfig.java`
    - `src/main/java/com/hasandag/courier/tracking/system/location/config/SwaggerConfig.java`

2. Create a single consolidated configuration class:
    - `src/main/java/com/hasandag/courier/tracking/system/location/config/ApiConfiguration.java`

3. Make sure the new class includes:
    - The necessary imports
    - A single `@Primary` annotated `OpenAPI` bean
    - Implementations of WebMvcConfigurer methods for handling Swagger UI

## Implementation Example

```java
package com.hasandag.courier.tracking.system.location.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
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
                        .description("API for tracking courier locations")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Hasan Dag")
                                .email("hasan_email@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Server"),
                        new Server()
                                .url("/")
                                .description("Default Server URL")
                ));
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
```

## Starting the Service

After making these changes, you can start the Location Service with:

```bash
java -jar location-service/target/location-service-0.0.1-SNAPSHOT.jar --logging.level.net.logstash=OFF
```

If you're unable to rebuild the JAR, you can use the following command to force bean definition overriding:

```bash
java -jar location-service/target/location-service-0.0.1-SNAPSHOT.jar --spring.main.allow-bean-definition-overriding=true --logging.level.net.logstash=OFF
``` 