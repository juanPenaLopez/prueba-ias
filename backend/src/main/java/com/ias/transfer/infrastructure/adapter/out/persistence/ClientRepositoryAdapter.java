package com.ias.transfer.infrastructure.adapter.out.persistence;

import com.ias.transfer.application.port.out.ClientRepository;
import com.ias.transfer.domain.model.Client;
import com.ias.transfer.domain.model.ClientStatus;
import com.ias.transfer.infrastructure.adapter.out.persistence.repository.ReactiveClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class ClientRepositoryAdapter implements ClientRepository {

    private static final Logger log = LoggerFactory.getLogger(ClientRepositoryAdapter.class);

    private final ReactiveClientRepository repository;

    public ClientRepositoryAdapter(ReactiveClientRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Client> findById(UUID clientId) {
        return repository.findById(clientId)
                .map(entity -> new Client(
                        entity.clientId,
                        entity.documentType,
                        entity.documentNumber,
                        entity.fullName,
                        entity.email,
                        ClientStatus.valueOf(entity.status),
                        entity.createdAt
                ))
                .doOnNext(c -> log.info("Cliente encontrado: {}", clientId))
                .doOnError(e -> log.error("Error buscando cliente: {}", clientId, e));
    }
}