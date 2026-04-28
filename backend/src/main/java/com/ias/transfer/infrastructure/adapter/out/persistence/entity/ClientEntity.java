package com.ias.transfer.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.UUID;

@Table("clients")
public class ClientEntity implements Persistable<UUID> {

    @Id
    public UUID clientId;
    public String documentType;
    public String documentNumber;
    public String fullName;
    public String email;
    public String status;
    public Instant createdAt;

    @Nullable
    @Override
    public UUID getId() {
        return null;
    }

    @Override
    public boolean isNew() {
        return false;
    }
}