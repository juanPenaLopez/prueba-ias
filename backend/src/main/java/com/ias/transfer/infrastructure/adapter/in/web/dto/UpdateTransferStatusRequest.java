package com.ias.transfer.infrastructure.adapter.in.web.dto;

import com.ias.transfer.domain.model.TransferStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTransferStatusRequest(
        @NotNull TransferStatus status
) {}
