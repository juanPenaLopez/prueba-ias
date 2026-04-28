package com.ias.transfer.domain.event;

import java.time.Instant;
import java.util.UUID;

public record TransferCreatedEvent(
        UUID eventId,
        UUID transferId,
        UUID clientId,
        String traceId,
        Instant occurredAt
) {
    public static TransferCreatedEvent from(UUID transferId, UUID clientId, String traceId) {
        return new TransferCreatedEvent(UUID.randomUUID(), transferId, clientId, traceId, Instant.now());
    }
}