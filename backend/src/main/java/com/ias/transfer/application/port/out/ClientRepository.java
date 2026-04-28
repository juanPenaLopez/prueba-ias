package com.ias.transfer.application.port.out;

import com.ias.transfer.domain.model.Client;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClientRepository {
    Mono<Client> findById(UUID clientId);
}
