package com.ias.transfer.application.port.in;

import com.ias.transfer.domain.model.Transfer;
import com.ias.transfer.domain.model.TransferStatus;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UpdateTransferStatusUseCase {
    Mono<Transfer> updateStatus(UUID transferId, TransferStatus newStatus);
}
