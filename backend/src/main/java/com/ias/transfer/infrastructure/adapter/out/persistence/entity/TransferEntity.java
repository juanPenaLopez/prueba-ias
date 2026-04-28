package com.ias.transfer.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table("transfers")
public class TransferEntity implements Persistable<UUID> {

    @Id
    public UUID transferId;
    public String sourceAccountId;
    public String targetAccountId;
    public BigDecimal amount;
    public String currency;
    public String description;
    public UUID clientId;
    public String status;
    public Instant createdAt;
    public Instant updatedAt;
    public Instant processedAt;
    public String idempotencyKey;

    @Transient
    private boolean isNew = false;

    @Override
    public UUID getId() {
        return transferId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public TransferEntity markAsNew() {
        this.isNew = true;
        return this;
    }
}