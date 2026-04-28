package com.ias.transfer.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferProcessedEvent(
        UUID eventId,
        UUID transferId,
        UUID clientId,
        BigDecimal amount,
        String status,
        String traceId,
        Instant occurredAt
) {
    public static TransferProcessedEvent from(
            UUID transferId,
            UUID clientId,
            BigDecimal amount,
            String status,
            String traceId
    ) {
        return new TransferProcessedEvent(
                UUID.randomUUID(),
                transferId,
                clientId,
                amount,
                status,
                traceId,
                Instant.now()
        );
    }
}