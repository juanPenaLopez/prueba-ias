package com.ias.transfer.infrastructure.adapter.out.persistence.repository;

import com.ias.transfer.infrastructure.adapter.out.persistence.entity.ClientEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ReactiveClientRepository extends ReactiveCrudRepository<ClientEntity, UUID> {
}