package com.ias.transfer.application.usecase;

import com.ias.transfer.application.port.in.UpdateTransferStatusUseCase;
import com.ias.transfer.application.port.out.EventPublisher;
import com.ias.transfer.application.port.out.TransferRepository;
import com.ias.transfer.domain.event.TransferProcessedEvent;
import com.ias.transfer.domain.model.BusinessException;
import com.ias.transfer.domain.model.Transfer;
import com.ias.transfer.domain.model.TransferStatus;
import com.ias.transfer.infrastructure.util.TraceIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UpdateTransferStatusService implements UpdateTransferStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateTransferStatusService.class);

    private final TransferRepository repository;
    private final EventPublisher eventPublisher;

    public UpdateTransferStatusService(
            TransferRepository repository,
            EventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Mono<Transfer> updateStatus(UUID transferId, TransferStatus newStatus) {

        log.info("Actualizando estado de transferencia | transferId={} | newStatus={}", transferId, newStatus);

        return repository.findById(transferId)
                .switchIfEmpty(Mono.error(
                        new BusinessException("Transferencia no encontrada")
                ))
                .doOnNext(transfer -> log.debug("Transferencia encontrada | transferId={} | currentStatus={} | newStatus={}",
                        transfer.transferId(), transfer.status(), newStatus))
                .map(transfer -> transfer.changeStatus(newStatus))
                .flatMap(repository::update)
                .flatMap(transfer ->
                        TraceIdUtil.getTraceId().map(traceId -> {
                            log.info("Estado de transferencia actualizado correctamente | transferId={} | newStatus={} | clientId={}",
                                    transfer.transferId(), transfer.status(), transfer.clientId());

                            if (transfer.status().isFinal()) {
                                log.info("La transferencia llego a estado final, publicando evento | transferId={} | status={}",
                                        transfer.transferId(), transfer.status());
                                eventPublisher.publish(
                                        TransferProcessedEvent.from(
                                                transfer.transferId(),
                                                transfer.clientId(),
                                                transfer.amount(),
                                                transfer.status().name(),
                                                traceId
                                        )
                                );
                            }
                            return transfer;
                        })
                )
                .doOnError(error -> log.error("Error al actualizar estado de transferencia | transferId={} | newStatus={} | error={}",
                        transferId, newStatus, error.getMessage(), error));
    }
}
