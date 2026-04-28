package com.ias.transfer.infrastructure.adapter.out.persistence.repository;

import com.ias.transfer.infrastructure.adapter.out.persistence.entity.ProcessedEventEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends ReactiveCrudRepository<ProcessedEventEntity, UUID> {
}
