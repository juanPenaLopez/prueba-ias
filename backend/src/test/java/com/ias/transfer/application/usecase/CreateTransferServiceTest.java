package com.ias.transfer.application.usecase;

import com.ias.transfer.application.port.in.CreateTransferCommand;
import com.ias.transfer.application.port.out.ClientRepository;
import com.ias.transfer.application.port.out.EventPublisher;
import com.ias.transfer.application.port.out.TransferRepository;
import com.ias.transfer.domain.model.Client;
import com.ias.transfer.domain.model.ClientStatus;
import com.ias.transfer.domain.model.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class CreateTransferServiceTest {

    private TransferRepository transferRepository;
    private ClientRepository clientRepository;
    private EventPublisher eventPublisher;

    private CreateTransferService service;

    @BeforeEach
    void setup() {
        transferRepository = mock(TransferRepository.class);
        clientRepository = mock(ClientRepository.class);
        eventPublisher = mock(EventPublisher.class);

        service = new CreateTransferService(
                transferRepository,
                clientRepository,
                eventPublisher
        );
    }

    @Test
    void shouldCreateTransferAndPublishEvent() {

        UUID clientId = UUID.randomUUID();

        Client client = new Client(
                clientId,
                "CC",
                "123",
                "Juan Perez",
                "juan@test.com",
                ClientStatus.ACTIVE,
                Instant.now()
        );

        CreateTransferCommand command = new CreateTransferCommand(
                clientId,
                "ACC1",
                "ACC2",
                BigDecimal.TEN,
                "USD",
                "test",
                "idem-1"
        );

        when(transferRepository.findByIdempotencyKey("idem-1"))
                .thenReturn(Mono.empty());

        when(clientRepository.findById(clientId))
                .thenReturn(Mono.just(client));

        when(transferRepository.save(any(Transfer.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.create(command))
                .expectNextMatches(t ->
                        t.clientId().equals(clientId) &&
                                t.status().name().equals("PENDING")
                )
                .verifyComplete();

        verify(eventPublisher, times(1)).publish(any());
    }
}
