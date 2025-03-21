package com.hasandag.courier.tracking.system.location.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SwaggerRedirectController {

    @Value("${server.port:8084}")
    private String serverPort;

    @GetMapping("/")
    public RedirectView redirectToSwaggerUI() {
        return new RedirectView("/swagger-ui/index.html");
    }

    @GetMapping("/swagger-config")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSwaggerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("url", "/api-docs");
        config.put("displayRequestDuration", true);
        config.put("deepLinking", true);
        config.put("layout", "BaseLayout");
        config.put("showExtensions", true);
        config.put("showCommonExtensions", true);

        return new ResponseEntity<>(config, HttpStatus.OK);
    }
} 