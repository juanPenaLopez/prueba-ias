package com.ias.transfer.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransferTest {

    private final Client activeClient = new Client(
            UUID.randomUUID(),
            "CC",
            "123",
            "Juan Perez",
            "juan@test.com",
            ClientStatus.ACTIVE,
            java.time.Instant.now()
    );

    @Test
    void shouldCreateTransferWithPendingStatus() {
        Transfer transfer = Transfer.create(
                UUID.randomUUID(),
                "ACC1",
                "ACC2",
                BigDecimal.TEN,
                "USD",
                "test",
                activeClient.clientId(),
                "idem-123"
        );

        assertEquals(TransferStatus.PENDING, transfer.status());
    }

    @Test
    void shouldFailWhenAmountIsZeroOrNegative() {
        Transfer transfer = Transfer.create(
                UUID.randomUUID(),
                "ACC1",
                "ACC2",
                BigDecimal.ZERO,
                "USD",
                null,
                activeClient.clientId(),
                "idem-123"
        );

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> transfer.validate(activeClient)
        );

        assertEquals("El monto debe ser mayor que cero", ex.getMessage());
    }

    @Test
    void shouldFailWhenSourceAndTargetAccountAreSame() {
        Transfer transfer = Transfer.create(
                UUID.randomUUID(),
                "ACC1",
                "ACC1",
                BigDecimal.TEN,
                "USD",
                null,
                activeClient.clientId(),
                "idem-123"
        );

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> transfer.validate(activeClient)
        );

        assertTrue(ex.getMessage().contains("no pueden ser la misma"));
    }

    @Test
    void shouldFailWhenClientIsInactive() {
        Client inactiveClient = new Client(
                UUID.randomUUID(),
                "CC",
                "123",
                "Juan Perez",
                "juan@test.com",
                ClientStatus.INACTIVE,
                java.time.Instant.now()
        );

        Transfer transfer = Transfer.create(
                UUID.randomUUID(),
                "ACC1",
                "ACC2",
                BigDecimal.TEN,
                "USD",
                null,
                inactiveClient.clientId(),
                "idem-123"
        );

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> transfer.validate(inactiveClient)
        );

        assertEquals("Solo los clientes activos pueden crear transferencias", ex.getMessage());
    }
}
