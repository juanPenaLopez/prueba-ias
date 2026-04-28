package com.ias.transfer.infrastructure.adapter.in.web.mapper;

import com.ias.transfer.application.port.in.CreateTransferCommand;
import com.ias.transfer.infrastructure.adapter.in.web.dto.CreateTransferRequest;

public class TransferWebMapper {

    public static CreateTransferCommand toCommand(
            CreateTransferRequest request,
            String idempotencyKey
    ) {
        return new CreateTransferCommand(
                request.clientId(),
                request.sourceAccountId(),
                request.targetAccountId(),
                request.amount(),
                request.currency(),
                request.description(),
                idempotencyKey
        );
    }
}
