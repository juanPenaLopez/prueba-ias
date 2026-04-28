package com.ias.transfer.application.port.out;

import com.ias.transfer.domain.model.Transfer;
import com.ias.transfer.domain.model.TransferStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransferRepository {

    Mono<Transfer> save(Transfer transfer);

    Mono<Transfer> findById(UUID transferId);

    Mono<Transfer> findByIdempotencyKey(String idempotencyKey);

    Flux<Transfer> findAll(UUID clientId, TransferStatus status, int page, int size);

    Mono<Transfer> update(Transfer transfer);
}
