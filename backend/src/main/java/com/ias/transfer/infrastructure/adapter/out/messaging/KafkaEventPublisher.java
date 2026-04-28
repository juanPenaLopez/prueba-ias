package com.ias.transfer.infrastructure.adapter.out.messaging;

import com.ias.transfer.application.port.out.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(Object event) {
        log.info("Publicando evento en Kafka | eventType={} | event={}", event.getClass().getSimpleName(), event);
        kafkaTemplate.send("transfer-events", event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error publicando evento en Kafka | eventType={} | error={}",
                                event.getClass().getSimpleName(), ex.getMessage(), ex);
                    } else {
                        log.debug("Evento publicado correctamente en Kafka | eventType={} | partition={} | offset={}",
                                event.getClass().getSimpleName(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
