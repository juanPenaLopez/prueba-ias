package com.ias.transfer.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransferRequest(

        @NotNull UUID clientId,

        @NotBlank String sourceAccountId,

        @NotBlank String targetAccountId,

        @NotNull @Positive BigDecimal amount,

        @NotBlank String currency,

        String description
) {}
