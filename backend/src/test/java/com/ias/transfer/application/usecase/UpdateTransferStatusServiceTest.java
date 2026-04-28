package com.ias.transfer.application.usecase;

import com.ias.transfer.application.port.out.EventPublisher;
import com.ias.transfer.application.port.out.TransferRepository;
import com.ias.transfer.domain.model.Transfer;
import com.ias.transfer.domain.model.TransferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class UpdateTransferStatusServiceTest {

    private TransferRepository repository;
    private EventPublisher eventPublisher;
    private UpdateTransferStatusService service;

    @BeforeEach
    void setup() {
        repository = mock(TransferRepository.class);
        eventPublisher = mock(EventPublisher.class);
        service = new UpdateTransferStatusService(repository, eventPublisher);
    }

    @Test
    void shouldPublishEventWhenTransferIsApproved() {

        UUID transferId = UUID.randomUUID();

        Transfer pending = new Transfer(
                transferId,
                "ACC1",
                "ACC2",
                BigDecimal.TEN,
                "USD",
                null,
                UUID.randomUUID(),
                TransferStatus.PROCESSING,
                Instant.now(),
                Instant.now(),
                null,
                "idem"
        );

        when(repository.findById(transferId))
                .thenReturn(Mono.just(pending));

        when(repository.update(any()))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(
                        service.updateStatus(transferId, TransferStatus.APPROVED)
                )
                .expectNextMatches(t ->
                        t.status() == TransferStatus.APPROVED
                )
                .verifyComplete();

        verify(eventPublisher, times(1)).publish(any());
    }
}
