package com.ias.transfer.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic transferEventsTopic() {
        return TopicBuilder.name("transfer-events")
                .partitions(1)
                .replicas(1)
                .build();
    }
}