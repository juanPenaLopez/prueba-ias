package com.ias.transfer.domain.model;

import java.util.Set;

public enum TransferStatus {

    PENDING,
    PROCESSING,
    APPROVED,
    REJECTED;

    public boolean canTransitionTo(TransferStatus target) {
        return switch (this) {
            case PENDING -> Set.of(PROCESSING, REJECTED).contains(target);
            case PROCESSING -> Set.of(APPROVED, REJECTED).contains(target);
            default -> false;
        };
    }

    public boolean isFinal() {
        return this == APPROVED || this == REJECTED;
    }
}
