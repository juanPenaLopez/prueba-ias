package com.ias.transfer.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.UUID;

@Table("processed_events")
public class ProcessedEventEntity implements Persistable<UUID> {

    @Id
    public UUID eventId;
    public String eventType;
    public Instant processedAt;

    @Transient
    private boolean isNew = false;

    @Override
    public UUID getId() {
        return eventId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public ProcessedEventEntity markAsNew() {
        this.isNew = true;
        return this;
    }
}
