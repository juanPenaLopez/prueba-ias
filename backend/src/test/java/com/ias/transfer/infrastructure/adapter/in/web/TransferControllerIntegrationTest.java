package com.ias.transfer.infrastructure.adapter.in.web;

import com.ias.transfer.application.port.in.CreateTransferUseCase;
import com.ias.transfer.application.port.in.ListTransfersUseCase;
import com.ias.transfer.application.port.in.UpdateTransferStatusUseCase;
import com.ias.transfer.bootstrap.TransferApplication;
import com.ias.transfer.domain.model.Transfer;
import com.ias.transfer.domain.model.TransferStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = TransferApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"api.security.enabled=false"}
)
@AutoConfigureWebTestClient
class TransferControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateTransferUseCase createTransferUseCase;

    @MockBean
    private UpdateTransferStatusUseCase updateTransferStatusUseCase;

    @MockBean
    private ListTransfersUseCase listTransfersUseCase;

    @Test
    void shouldCreateTransfer() {
        UUID transferId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        Transfer transfer = buildTransfer(transferId, clientId, TransferStatus.PENDING);

        when(createTransferUseCase.create(any())).thenReturn(Mono.just(transfer));

        webTestClient.post()
                .uri("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Idempotency-Key", "idem-123")
                .attribute("traceId", "trace-001")
                .bodyValue("""
                        {
                          "clientId": "%s",
                          "sourceAccountId": "ACC-001",
                          "targetAccountId": "ACC-002",
                          "amount": 150.25,
                          "currency": "USD",
                          "description": "Pago proveedor"
                        }
                        """.formatted(clientId))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.traceId").exists()
                .jsonPath("$.data.transferId").isEqualTo(transferId.toString())
                .jsonPath("$.data.status").isEqualTo("PENDING");

        verify(createTransferUseCase).create(any());
    }

    @Test
    void shouldListTransfersWithFiltersAndPagination() {
        UUID clientId = UUID.randomUUID();
        Transfer transfer = buildTransfer(UUID.randomUUID(), clientId, TransferStatus.PROCESSING);

        when(listTransfersUseCase.list(clientId, TransferStatus.PROCESSING, 1, 5))
                .thenReturn(Flux.just(transfer));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/transfers")
                        .queryParam("clientId", clientId)
                        .queryParam("status", "PROCESSING")
                        .queryParam("page", 1)
                        .queryParam("size", 5)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].clientId").isEqualTo(clientId.toString())
                .jsonPath("$[0].status").isEqualTo("PROCESSING");

        verify(listTransfersUseCase).list(clientId, TransferStatus.PROCESSING, 1, 5);
    }

    @Test
    void shouldUpdateTransferStatus() {
        UUID transferId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        Transfer transfer = buildTransfer(transferId, clientId, TransferStatus.APPROVED);

        when(updateTransferStatusUseCase.updateStatus(transferId, TransferStatus.APPROVED))
                .thenReturn(Mono.just(transfer));

        webTestClient.patch()
                .uri("/api/v1/transfers/{id}/status", transferId)
                .contentType(MediaType.APPLICATION_JSON)
                .attribute("traceId", "trace-002")
                .bodyValue("""
                        {
                          "status": "APPROVED"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.traceId").exists()
                .jsonPath("$.data.transferId").isEqualTo(transferId.toString())
                .jsonPath("$.data.status").isEqualTo("APPROVED");

        verify(updateTransferStatusUseCase).updateStatus(transferId, TransferStatus.APPROVED);
    }

    @Test
    void shouldReturnHealthOk() {
        webTestClient.get()
                .uri("/api/v1/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("OK");
    }

    private Transfer buildTransfer(UUID transferId, UUID clientId, TransferStatus status) {
        Instant now = Instant.now();
        return new Transfer(
                transferId,
                "ACC-001",
                "ACC-002",
                new BigDecimal("150.25"),
                "USD",
                "Pago proveedor",
                clientId,
                status,
                now,
                now,
                status.isFinal() ? now : null,
                "idem-123"
        );
    }
}
