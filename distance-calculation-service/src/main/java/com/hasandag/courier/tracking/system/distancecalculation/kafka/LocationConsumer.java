package com.hasandag.courier.tracking.system.distancecalculation.kafka;

import com.hasandag.courier.tracking.system.distancecalculation.service.DistanceCalculationService;
import com.hasandag.courier.tracking.system.dto.LocationEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationConsumer {

    private final DistanceCalculationService distanceCalculationService;

    @KafkaListener(
            topics = "${application.kafka.topics.location-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @CircuitBreaker(name = "kafkaProcessing", fallbackMethod = "consumeLocationEventFallback")
    public void consumeLocationEvent(@Payload LocationEvent locationEvent) {
        log.debug("Received location event for distance calculation: {}", locationEvent);
        try {
            distanceCalculationService.processLocationEvent(locationEvent);
        } catch (Exception e) {
            log.error("Error processing location event for distance calculation: {}", locationEvent, e);
            throw e; // Re-throw to trigger circuit breaker
        }
    }

    public void consumeLocationEventFallback(LocationEvent locationEvent, Throwable t) {
        log.error("Circuit breaker triggered for Kafka consumer. Error processing location event: {}", t.getMessage());
        // Could store in a dead letter queue or local storage for retry later
        log.warn("Failed to process location event in consumer: {}", locationEvent);
    }
}