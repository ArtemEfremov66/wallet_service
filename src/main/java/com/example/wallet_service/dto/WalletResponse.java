package com.example.wallet_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class WalletResponse {
    private UUID id;
    private long balance;
}