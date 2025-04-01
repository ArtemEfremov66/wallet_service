package com.example.wallet_service.service;

import com.example.wallet_service.dto.WalletResponse;
import com.example.wallet_service.entity.OperationType;
import com.example.wallet_service.entity.Wallet;
import com.example.wallet_service.entity.WalletTransaction;
import com.example.wallet_service.exception.InsufficientFundsException;
import com.example.wallet_service.exception.WalletNotFoundException;
import com.example.wallet_service.repository.WalletRepository;
import com.example.wallet_service.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Transactional
    @Retryable(maxAttempts = 3, retryFor = {Exception.class})
    public WalletResponse processTransaction(UUID walletId, OperationType operationType, long amount) {
        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Кошелек не найден по ID: " + walletId));

        long newBalance = operationType == OperationType.DEPOSIT ?
                wallet.getBalance() + amount :
                wallet.getBalance() - amount;

        if (newBalance < 0) {
            throw new InsufficientFundsException("Недостаточно средств для вывода");
        }

        wallet.setBalance(newBalance);
        Wallet savedWallet = walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .id(UUID.randomUUID())
                .wallet(wallet)
                .operationType(operationType)
                .amount(amount)
                .build();

        walletTransactionRepository.save(transaction);

        return WalletResponse.builder()
                .id(savedWallet.getId())
                .balance(savedWallet.getBalance())
                .build();
    }

    public WalletResponse getWalletBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Кошелек не найден по ID: " + walletId));

        return WalletResponse.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .build();
    }
}
