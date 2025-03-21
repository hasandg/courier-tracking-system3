package com.hasandag.courier.tracking.system.location.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${application.kafka.topics.location-events}")
    private String locationTopic;

    @Bean
    public NewTopic locationTopic() {
        return TopicBuilder.name(locationTopic)
                .partitions(6)
                .replicas(3)
                .build();
    }
}
