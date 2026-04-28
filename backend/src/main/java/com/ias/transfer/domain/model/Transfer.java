package com.ias.transfer.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Transfer(
        UUID transferId,
        String sourceAccountId,
        String targetAccountId,
        BigDecimal amount,
        String currency,
        String description,
        UUID clientId,
        TransferStatus status,
        Instant createdAt,
        Instant updatedAt,
        Instant processedAt,
        String idempotencyKey
) {
    public static Transfer create(
            UUID transferId,
            String sourceAccountId,
            String targetAccountId,
            BigDecimal amount,
            String currency,
            String description,
            UUID clientId,
            String idempotencyKey
    ) {
        return new Transfer(
                transferId,
                sourceAccountId,
                targetAccountId,
                amount,
                currency,
                description,
                clientId,
                TransferStatus.PENDING,
                Instant.now(),
                Instant.now(),
                null,
                idempotencyKey
        );
    }

    public Transfer validate(Client client) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto debe ser mayor que cero");
        }
        if (sourceAccountId.equals(targetAccountId)) {
            throw new BusinessException("La cuenta origen y destino no pueden ser la misma");
        }
        if (!client.isActive()) {
            throw new BusinessException("Solo los clientes activos pueden crear transferencias");
        }
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BusinessException("La clave de idempotencia es obligatoria");
        }
        return this;
    }

    public Transfer changeStatus(TransferStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new BusinessException(
                    "Transicion de estado invalida de " + status + " a " + newStatus
            );
        }

        return new Transfer(
                transferId,
                sourceAccountId,
                targetAccountId,
                amount,
                currency,
                description,
                clientId,
                newStatus,
                createdAt,
                Instant.now(),
                newStatus.isFinal() ? Instant.now() : processedAt,
                idempotencyKey
        );
    }
}
