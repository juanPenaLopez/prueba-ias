package com.ias.transfer.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Client(
        UUID clientId,
        String documentType,
        String documentNumber,
        String fullName,
        String email,
        ClientStatus status,
        Instant createdAt
) {

    public Client {
        if (clientId == null) {
            throw new BusinessException("El clientId es obligatorio");
        }
        if (fullName == null || fullName.length() > 80) {
            throw new BusinessException("El nombre completo no es valido");
        }
        if (status == null) {
            throw new BusinessException("El estado es obligatorio");
        }
    }

    public boolean isActive() {
        return status == ClientStatus.ACTIVE;
    }
}
