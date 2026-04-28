package com.ias.transfer.application.port.in;

import com.ias.transfer.domain.model.Transfer;
import reactor.core.publisher.Mono;

public interface CreateTransferUseCase {
    Mono<Transfer> create(CreateTransferCommand command);
}
