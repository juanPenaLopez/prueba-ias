package com.ias.transfer.infrastructure.adapter.out.persistence;

import com.ias.transfer.application.port.out.TransferRepository;
import com.ias.transfer.domain.model.Transfer;
import com.ias.transfer.domain.model.TransferStatus;
import com.ias.transfer.infrastructure.adapter.out.persistence.entity.TransferEntity;
import com.ias.transfer.infrastructure.adapter.out.persistence.repository.ReactiveTransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class TransferRepositoryAdapter implements TransferRepository {

    private static final Logger log = LoggerFactory.getLogger(TransferRepositoryAdapter.class);

    private final ReactiveTransferRepository repository;

    public TransferRepositoryAdapter(ReactiveTransferRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Transfer> save(Transfer transfer) {
        TransferEntity entity = toEntity(transfer);
        entity.markAsNew();
        return repository.save(entity)
                .map(this::toDomain)
                .doOnNext(t -> log.info("Transferencia guardada | transferId={}", t.transferId()))
                .doOnError(e -> log.error("Error al guardar transferencia | transferId={} | error={}",
                        transfer.transferId(), e.getMessage()));
    }

    @Override
    public Mono<Transfer> findById(UUID transferId) {
        log.debug("Buscando transferencia por ID | transferId={}", transferId);
        return repository.findById(transferId)
                .map(this::toDomain)
                .doOnSuccess(transfer -> log.debug("Transferencia encontrada | transferId={}", transferId))
                .doOnError(error -> log.error("Error al buscar transferencia | transferId={} | error={}",
                        transferId, error.getMessage(), error));
    }

    @Override
    public Mono<Transfer> findByIdempotencyKey(String key) {
        log.debug("Buscando transferencia por clave de idempotencia | key={}", key);
        return repository.findByIdempotencyKey(key)
                .map(this::toDomain)
                .doOnNext(transfer -> log.debug("Transferencia encontrada por clave de idempotencia | transferId={} | key={}",
                        transfer.transferId(), key));
    }

    @Override
    public Flux<Transfer> findAll(UUID clientId, TransferStatus status, int page, int size) {
        log.debug("Buscando transferencias | clientId={} | status={} | page={} | size={}",
                clientId, status, page, size);
        Flux<TransferEntity> query = status == null
                ? repository.findAllByClientId(clientId)
                : repository.findAllByClientIdAndStatus(clientId, status.name());

        long skip = (long) Math.max(page, 0) * Math.max(size, 0);

        return query
                .skip(skip)
                .take(Math.max(size, 0))
                .map(this::toDomain)
                .doOnComplete(() -> log.debug("Consulta de transferencias finalizada | clientId={}", clientId))
                .doOnError(error -> log.error("Error al buscar transferencias | clientId={} | error={}",
                        clientId, error.getMessage(), error));
    }

    private TransferEntity toEntity(Transfer t) {
        TransferEntity e = new TransferEntity();
        e.transferId = t.transferId();
        e.sourceAccountId = t.sourceAccountId();
        e.targetAccountId = t.targetAccountId();
        e.amount = t.amount();
        e.currency = t.currency();
        e.description = t.description();
        e.clientId = t.clientId();
        e.status = t.status().name();
        e.createdAt = t.createdAt();
        e.updatedAt = t.updatedAt();
        e.processedAt = t.processedAt();
        e.idempotencyKey = t.idempotencyKey();
        return e;
    }

    private Transfer toDomain(TransferEntity e) {
        return new Transfer(
                e.transferId,
                e.sourceAccountId,
                e.targetAccountId,
                e.amount,
                e.currency,
                e.description,
                e.clientId,
                TransferStatus.valueOf(e.status),
                e.createdAt,
                e.updatedAt,
                e.processedAt,
                e.idempotencyKey
        );
    }

    @Override
    public Mono<Transfer> update(Transfer transfer) {
        TransferEntity entity = toEntity(transfer);
        return repository.save(entity)
                .map(this::toDomain)
                .doOnNext(t -> log.info("Transferencia actualizada | transferId={}", t.transferId()))
                .doOnError(e -> log.error("Error al actualizar transferencia | transferId={} | error={}",
                        transfer.transferId(), e.getMessage()));
    }
}
