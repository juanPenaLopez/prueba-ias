package com.ias.transfer.infrastructure.adapter.out.messaging;

import com.ias.transfer.application.service.AuditService;
import com.ias.transfer.domain.event.TransferCreatedEvent;
import com.ias.transfer.domain.model.TransferStatus;
import com.ias.transfer.infrastructure.adapter.out.persistence.entity.ProcessedEventEntity;
import com.ias.transfer.infrastructure.adapter.out.persistence.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class TransferEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransferEventConsumer.class);

    private final AuditService auditService;
    private final ProcessedEventRepository processedEventRepository;

    public TransferEventConsumer(
            AuditService auditService,
            ProcessedEventRepository processedEventRepository
    ) {
        this.auditService = auditService;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(
            topics = "transfer-events",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onTransferCreated(
                                   @Payload TransferCreatedEvent event,
                                   @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
                                   @Header(value = KafkaHeaders.OFFSET, required = false) Long offset
    ) {
        log.info("Evento recibido desde Kafka | eventId={} | transferId={} | traceId={} | partition={} | offset={}",
                event.eventId(), event.transferId(), event.traceId(), partition, offset);

        processedEventRepository.findById(event.eventId())
                .flatMap(existing -> {
                    log.warn("Evento ya procesado, se omite | eventId={} | transferId={}",
                            event.eventId(), event.transferId());
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.defer(() -> {
                    auditService.auditTransferProcessed(
                            event.eventId(),
                            event.transferId(),
                            event.clientId(),
                            null,
                            String.valueOf(TransferStatus.PENDING),
                            event.traceId()
                    );

                    ProcessedEventEntity processedEvent = new ProcessedEventEntity();
                    processedEvent.eventId = event.eventId();
                    processedEvent.eventType = "TransferCreatedEvent";
                    processedEvent.processedAt = Instant.now();
                    processedEvent.markAsNew();

                    return processedEventRepository.save(processedEvent);
                }))
                .doOnSuccess(saved ->
                        log.info("Procesamiento de evento completado | eventId={} | transferId={}",
                                event.eventId(), event.transferId())
                )
                .doOnError(error ->
                        log.error("Error procesando evento | eventId={} | transferId={} | error={}",
                                event.eventId(), event.transferId(), error.getMessage(), error)
                )
                .block();
    }
}