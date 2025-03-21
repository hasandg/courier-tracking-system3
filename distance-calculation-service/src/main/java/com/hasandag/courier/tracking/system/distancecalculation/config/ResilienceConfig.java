package com.hasandag.courier.tracking.system.distancecalculation.config;

import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build())
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .build());
    }

    @Bean
    public Customizer<CircuitBreakerRegistry> customizeCircuitBreaker() {
        return registry -> {
            // Custom configuration for Redis operations
            registry.addConfiguration("redisCustomConfig",
                    CircuitBreakerConfig.custom()
                            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                            .slidingWindowSize(10)
                            .failureRateThreshold(50)
                            .waitDurationInOpenState(Duration.ofSeconds(10))
                            .permittedNumberOfCallsInHalfOpenState(5)
                            .slowCallRateThreshold(50)
                            .slowCallDurationThreshold(Duration.ofSeconds(2))
                            .build());

            // Configuration specifically for database operations
            registry.addConfiguration("databaseOperationsConfig",
                    CircuitBreakerConfig.custom()
                            .slidingWindowSize(5)
                            .failureRateThreshold(40)
                            .waitDurationInOpenState(Duration.ofSeconds(20))
                            .permittedNumberOfCallsInHalfOpenState(3)
                            .build());
        };
    }

    @Bean
    public Customizer<RetryRegistry> customizeRetry() {
        return registry -> {
            registry.addConfiguration("locationServiceRetryConfig",
                    RetryConfig.custom()
                            .maxAttempts(3)
                            .waitDuration(Duration.ofMillis(500))
                            .retryExceptions(Exception.class)
                            .ignoreExceptions(IllegalArgumentException.class)
                            .build());
        };
    }

    @Bean
    public Customizer<BulkheadRegistry> customizeBulkhead() {
        return registry -> {
            registry.addConfiguration("locationServiceBulkheadConfig",
                    BulkheadConfig.custom()
                            .maxConcurrentCalls(10)
                            .maxWaitDuration(Duration.ofMillis(500))
                            .build());
        };
    }
} 