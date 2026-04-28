package com.ias.transfer.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransferCommand(
        UUID clientId,
        String sourceAccountId,
        String targetAccountId,
        BigDecimal amount,
        String currency,
        String description,
        String idempotencyKey
) {}
