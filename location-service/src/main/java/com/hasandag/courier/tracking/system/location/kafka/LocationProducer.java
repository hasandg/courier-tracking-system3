package com.hasandag.courier.tracking.system.location.kafka;


import com.hasandag.courier.tracking.system.dto.LocationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationProducer {

    private final KafkaTemplate<String, LocationEvent> kafkaTemplate;

    @Value("${application.kafka.topics.location-events}")
    private String locationTopic;

    public void sendLocationEvent(LocationEvent event) {
        String key = event.getCourierId();

        CompletableFuture<SendResult<String, LocationEvent>> future =
                kafkaTemplate.send(locationTopic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Location event sent successfully: {}, partition: {}, offset: {}",
                        event, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send location event: {}", event, ex);
            }
        });
    }
}
