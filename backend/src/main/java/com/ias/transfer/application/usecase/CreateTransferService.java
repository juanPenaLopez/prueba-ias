package com.ias.transfer.application.usecase;

import com.ias.transfer.application.port.in.CreateTransferCommand;
import com.ias.transfer.application.port.in.CreateTransferUseCase;
import com.ias.transfer.application.port.out.ClientRepository;
import com.ias.transfer.application.port.out.EventPublisher;
import com.ias.transfer.application.port.out.TransferRepository;
import com.ias.transfer.domain.event.TransferCreatedEvent;
import com.ias.transfer.domain.model.BusinessException;
import com.ias.transfer.domain.model.Transfer;
import com.ias.transfer.infrastructure.util.TraceIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class CreateTransferService implements CreateTransferUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateTransferService.class);

    private final TransferRepository transferRepository;
    private final ClientRepository clientRepository;
    private final EventPublisher eventPublisher;

    public CreateTransferService(
            TransferRepository transferRepository,
            ClientRepository clientRepository,
            EventPublisher eventPublisher
    ) {
        this.transferRepository = transferRepository;
        this.clientRepository = clientRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Mono<Transfer> create(CreateTransferCommand command) {

        log.info("Creando transferencia | clientId={} | amount={} | currency={} | idempotencyKey={}",
                command.clientId(), command.amount(), command.currency(), command.idempotencyKey());

        return transferRepository
                .findByIdempotencyKey(command.idempotencyKey())
                .doOnNext(existing -> log.warn("Transferencia duplicada detectada | idempotencyKey={} | transferId={}",
                        command.idempotencyKey(), existing.transferId()))
                .switchIfEmpty(
                        clientRepository.findById(command.clientId())
                                .switchIfEmpty(Mono.error(
                                        new BusinessException("Cliente no encontrado")
                                ))
                                .doOnNext(client -> log.debug("Cliente validado | clientId={} | status={}",
                                        client.clientId(), client.status()))
                                .map(client ->
                                        Transfer.create(
                                                UUID.randomUUID(),
                                                command.sourceAccountId(),
                                                command.targetAccountId(),
                                                command.amount(),
                                                command.currency(),
                                                command.description(),
                                                command.clientId(),
                                                command.idempotencyKey()
                                        ).validate(client)
                                )
                                .flatMap(transferRepository::save)
                                .flatMap(transfer ->
                                        TraceIdUtil.getTraceId().map(traceId -> {
                                            log.info("Transferencia creada correctamente | transferId={} | clientId={} | amount={} | status={}",
                                                    transfer.transferId(), transfer.clientId(), transfer.amount(), transfer.status());
                                            eventPublisher.publish(
                                                    TransferCreatedEvent.from(
                                                            transfer.transferId(),
                                                            transfer.clientId(),
                                                            traceId
                                                    )
                                            );
                                            return transfer;
                                        })
                                )
                                .doOnError(error -> log.error("Error al crear transferencia | clientId={} | error={}",
                                        command.clientId(), error.getMessage(), error))
                );
    }
}