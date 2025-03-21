package com.hasandag.courier.tracking.system.store.kafka;

import com.hasandag.courier.tracking.system.dto.LocationEvent;
import com.hasandag.courier.tracking.system.store.service.StoreEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationConsumer {

    private final StoreEntryService storeEntryService;

    @KafkaListener(
            topics = "${application.kafka.topics.location-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeLocationEvent(@Payload LocationEvent locationEvent) {
        log.debug("Received location event: {}", locationEvent);
        try {
            storeEntryService.processLocationEvent(locationEvent);
        } catch (Exception e) {
            log.error("Error processing location event: {}", locationEvent, e);
        }
    }
}
