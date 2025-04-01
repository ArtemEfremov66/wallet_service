package com.example.wallet_service.service;

import com.example.wallet_service.dto.WalletResponse;
import com.example.wallet_service.exception.InsufficientFundsException;
import com.example.wallet_service.exception.WalletNotFoundException;
import com.example.wallet_service.entity.OperationType;
import com.example.wallet_service.entity.Wallet;
import com.example.wallet_service.repository.WalletRepository;
import com.example.wallet_service.repository.WalletTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletTransactionRepository walletTransactionRepository;

    @InjectMocks
    private WalletService walletService;

    private final UUID walletId = UUID.fromString("8a8a8a8a-8a8a-8a8a-8a8a-8a8a8a8a8a8a");

    @Test
    void processTransaction_ShouldDepositSuccessfully() {
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(1000L);

        when(walletRepository.findByIdForUpdate(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WalletResponse response = walletService.processTransaction(walletId, OperationType.DEPOSIT, 500L);

        assertEquals(1500L, response.getBalance());
        verify(walletTransactionRepository, times(1)).save(any());
    }

    @Test
    void processTransaction_ShouldWithdrawSuccessfully() {
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(1000L);

        when(walletRepository.findByIdForUpdate(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WalletResponse response = walletService.processTransaction(walletId, OperationType.WITHDRAW, 500L);

        assertEquals(500L, response.getBalance());
    }

    @Test
    void processTransaction_ShouldThrowWhenInsufficientFunds() {
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(100L);

        when(walletRepository.findByIdForUpdate(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InsufficientFundsException.class, () ->
                walletService.processTransaction(walletId, OperationType.WITHDRAW, 500L));
    }

    @Test
    void processTransaction_ShouldThrowWhenWalletNotFound() {
        when(walletRepository.findByIdForUpdate(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () ->
                walletService.processTransaction(walletId, OperationType.DEPOSIT, 100L));
    }

    @Test
    void getWalletBalance_ShouldReturnCorrectBalance() {
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(1000L);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.getWalletBalance(walletId);

        assertEquals(1000L, response.getBalance());
    }
}
