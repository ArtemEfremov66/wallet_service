package com.example.wallet_service.controller;

import com.example.wallet_service.dto.WalletOperationRequest;
import com.example.wallet_service.dto.WalletResponse;
import com.example.wallet_service.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponse> processTransaction(@RequestBody @Valid WalletOperationRequest request) {
        WalletResponse response = walletService.processTransaction(
                request.getWalletId(),
                request.getOperationType(),
                request.getAmount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWalletBalance(@PathVariable UUID walletId) {
        WalletResponse response = walletService.getWalletBalance(walletId);
        return ResponseEntity.ok(response);
    }
}
