package com.ias.transfer.infrastructure.adapter.in.web;

import com.ias.transfer.application.port.in.CreateTransferUseCase;
import com.ias.transfer.application.port.in.ListTransfersUseCase;
import com.ias.transfer.application.port.in.UpdateTransferStatusUseCase;
import com.ias.transfer.domain.model.Transfer;
import com.ias.transfer.domain.model.TransferStatus;
import com.ias.transfer.infrastructure.adapter.in.web.dto.CreateTransferRequest;
import com.ias.transfer.infrastructure.adapter.in.web.dto.UpdateTransferStatusRequest;
import com.ias.transfer.infrastructure.adapter.in.web.mapper.TransferWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name = "Transferencias", description = "API de gestion de transferencias bancarias")
@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final CreateTransferUseCase createUseCase;
    private final UpdateTransferStatusUseCase updateStatusUseCase;
    private final ListTransfersUseCase listUseCase;

    public TransferController(
            CreateTransferUseCase createUseCase,
            UpdateTransferStatusUseCase updateStatusUseCase,
            ListTransfersUseCase listUseCase
    ) {
        this.createUseCase = createUseCase;
        this.updateStatusUseCase = updateStatusUseCase;
        this.listUseCase = listUseCase;
    }

    @Operation(
            summary = "Crear nueva transferencia",
            description = "Crea una nueva transferencia bancaria. Requiere header 'Idempotency-Key' para prevenir duplicados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferencia creada exitosamente",
                    content = @Content(schema = @Schema(implementation = Transfer.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "409", description = "Transferencia duplicada (Idempotency-Key repetida)")
    })
    @PostMapping
    public Mono<com.ias.transfer.infrastructure.adapter.in.web.dto.ApiResponse<Transfer>> create(
            @Valid @RequestBody CreateTransferRequest request,
            @Parameter(description = "Clave de idempotencia para prevenir duplicados", required = true)
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestAttribute("traceId") String traceId
    ) {
        return createUseCase
                .create(TransferWebMapper.toCommand(request, idempotencyKey))
                .map(t -> com.ias.transfer.infrastructure.adapter.in.web.dto.ApiResponse.ok(t, traceId));
    }

    @Operation(
            summary = "Actualizar estado de transferencia",
            description = "Actualiza el estado de una transferencia existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = Transfer.class))),
            @ApiResponse(responseCode = "404", description = "Transferencia no encontrada"),
            @ApiResponse(responseCode = "400", description = "Estado invalido o transicion no permitida")
    })
    @PatchMapping("/{id}/status")
    public Mono<com.ias.transfer.infrastructure.adapter.in.web.dto.ApiResponse<Transfer>> updateStatus(
            @Parameter(description = "ID de la transferencia", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTransferStatusRequest request,
            @RequestAttribute("traceId") String traceId
    ) {
        return updateStatusUseCase
                .updateStatus(id, request.status())
                .map(t -> com.ias.transfer.infrastructure.adapter.in.web.dto.ApiResponse.ok(t, traceId));
    }

    @Operation(
            summary = "Listar transferencias",
            description = "Lista las transferencias de un cliente con filtros opcionales de estado y paginacion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transferencias",
                    content = @Content(schema = @Schema(implementation = Transfer.class)))
    })
    @GetMapping
    public Flux<Transfer> list(
            @Parameter(description = "ID del cliente", required = true)
            @RequestParam UUID clientId,
            @Parameter(description = "Filtrar por estado de transferencia")
            @RequestParam(required = false) TransferStatus status,
            @Parameter(description = "Numero de pagina (inicia en 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamano de pagina")
            @RequestParam(defaultValue = "10") int size
    ) {
        return listUseCase.list(clientId, status, page, size);
    }
}
