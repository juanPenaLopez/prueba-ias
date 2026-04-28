package com.ias.transfer.infrastructure.adapter.out.persistence.repository;

import com.ias.transfer.infrastructure.adapter.out.persistence.entity.TransferEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReactiveTransferRepository
        extends ReactiveCrudRepository<TransferEntity, UUID> {

    Mono<TransferEntity> findByIdempotencyKey(String idempotencyKey);

    Flux<TransferEntity> findAllByClientId(UUID clientId);

    Flux<TransferEntity> findAllByClientIdAndStatus(UUID clientId, String status);
}
