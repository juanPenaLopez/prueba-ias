package com.ias.transfer.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferStatusTest {

    @Test
    void shouldAllowValidTransition() {
        assertTrue(
                TransferStatus.PENDING.canTransitionTo(TransferStatus.PROCESSING)
        );
    }

    @Test
    void shouldRejectInvalidTransition() {
        assertFalse(
                TransferStatus.PENDING.canTransitionTo(TransferStatus.APPROVED)
        );
    }

    @Test
    void approvedIsFinalState() {
        assertTrue(TransferStatus.APPROVED.isFinal());
    }
}
