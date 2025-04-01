package com.example.wallet_service.dto;


import com.example.wallet_service.entity.OperationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class WalletOperationRequest {

    @NotNull(message = "Требуется ID кошелька")
    private UUID walletId;

    @NotNull(message = "Требуется тип операции")
    private OperationType operationType;

    @Min(value = 1, message = "Сумма должна быть положительной")
    private long amount;
}
