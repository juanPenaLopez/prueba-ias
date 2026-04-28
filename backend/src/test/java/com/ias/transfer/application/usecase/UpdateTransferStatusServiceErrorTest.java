package com.ias.transfer.application.usecase;

import com.ias.transfer.application.port.out.EventPublisher;
import com.ias.transfer.application.port.out.TransferRepository;
import com.ias.transfer.domain.model.TransferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

class UpdateTransferStatusServiceErrorTest {

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
    void shouldReturnErrorWhenTransferNotFound() {
        UUID transferId = UUID.randomUUID();

        when(repository.findById(transferId)).thenReturn(Mono.empty());

        StepVerifier.create(service.updateStatus(transferId, TransferStatus.APPROVED))
                .expectErrorMatches(error -> error.getMessage().contains("Transferencia no encontrada"))
                .verify();

        verify(repository, never()).update(any());
        verify(eventPublisher, never()).publish(any());
    }
}
