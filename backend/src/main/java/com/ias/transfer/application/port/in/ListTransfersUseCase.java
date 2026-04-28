package com.ias.transfer.application.port.in;

import com.ias.transfer.domain.model.Transfer;
import com.ias.transfer.domain.model.TransferStatus;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ListTransfersUseCase {
    Flux<Transfer> list(UUID clientId, TransferStatus status, int page, int size);
}
