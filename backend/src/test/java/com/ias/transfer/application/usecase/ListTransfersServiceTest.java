package com.ias.transfer.application.usecase;

import com.ias.transfer.application.port.out.TransferRepository;
import com.ias.transfer.domain.model.Transfer;
import com.ias.transfer.domain.model.TransferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ListTransfersServiceTest {

    private TransferRepository repository;
    private ListTransfersService service;

    @BeforeEach
    void setup() {
        repository = mock(TransferRepository.class);
        service = new ListTransfersService(repository);
    }

    @Test
    void shouldDelegateListWithFiltersAndPagination() {
        UUID clientId = UUID.randomUUID();
        Transfer transfer = new Transfer(
                UUID.randomUUID(), "ACC1", "ACC2", BigDecimal.TEN, "USD", null, clientId,
                TransferStatus.PENDING, Instant.now(), Instant.now(), null, "idem-1"
        );

        when(repository.findAll(clientId, TransferStatus.PENDING, 1, 5))
                .thenReturn(Flux.just(transfer));

        StepVerifier.create(service.list(clientId, TransferStatus.PENDING, 1, 5))
                .expectNext(transfer)
                .verifyComplete();

        verify(repository).findAll(clientId, TransferStatus.PENDING, 1, 5);
    }
}
