package com.ias.transfer.application.usecase;

import com.ias.transfer.application.port.in.ListTransfersUseCase;
import com.ias.transfer.application.port.out.TransferRepository;
import com.ias.transfer.domain.model.Transfer;
import com.ias.transfer.domain.model.TransferStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
public class ListTransfersService implements ListTransfersUseCase {

    private final TransferRepository repository;

    public ListTransfersService(TransferRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<Transfer> list(UUID clientId, TransferStatus status, int page, int size) {
        return repository.findAll(clientId, status, page, size);
    }
}
