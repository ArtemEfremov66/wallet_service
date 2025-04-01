package com.example.wallet_service.controller;

import com.example.wallet_service.dto.WalletOperationRequest;
import com.example.wallet_service.dto.WalletResponse;
import com.example.wallet_service.entity.OperationType;
import com.example.wallet_service.exception.GlobalExceptionHandler;
import com.example.wallet_service.exception.WalletNotFoundException;
import com.example.wallet_service.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class WalletControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UUID testWalletId = UUID.fromString("8a8a8a8a-8a8a-8a8a-8a8a-8a8a8a8a8a8a");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(walletController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deposit_ShouldReturnWalletResponse() throws Exception {
        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(testWalletId);
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(1000L);

        WalletResponse response = WalletResponse.builder()
                .id(testWalletId)
                .balance(11000L)
                .build();

        when(walletService.processTransaction(eq(testWalletId), eq(OperationType.DEPOSIT), eq(1000L)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testWalletId.toString()))
                .andExpect(jsonPath("$.balance").value(11000L));
    }


    @Test
    void withdraw_ShouldReturnWalletResponse() throws Exception {
        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(testWalletId);
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(500L);

        WalletResponse response = WalletResponse.builder()
                .id(testWalletId)
                .balance(9500L)
                .build();

        given(walletService.processTransaction(eq(testWalletId), eq(OperationType.WITHDRAW), eq(500L)))
                .willReturn(response);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testWalletId.toString()))
                .andExpect(jsonPath("$.balance").value(9500L));
    }

    @Test
    void getBalance_ShouldReturnWalletResponse() throws Exception {
        WalletResponse response = WalletResponse.builder()
                .id(testWalletId)
                .balance(10000L)
                .build();

        given(walletService.getWalletBalance(testWalletId))
                .willReturn(response);

        mockMvc.perform(get("/api/v1/wallets/{walletId}", testWalletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testWalletId.toString()))
                .andExpect(jsonPath("$.balance").value(10000L));
    }

    @Test
    void processTransaction_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        WalletOperationRequest invalidRequest = new WalletOperationRequest();
        invalidRequest.setWalletId(null);
        invalidRequest.setOperationType(null);
        invalidRequest.setAmount(-100L);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void getBalance_ShouldReturnNotFound_WhenWalletNotExists() throws Exception {
        UUID nonExistentWalletId = UUID.randomUUID();

        given(walletService.getWalletBalance(nonExistentWalletId))
                .willThrow(new WalletNotFoundException("Wallet not found"));

        mockMvc.perform(get("/api/v1/wallets/{walletId}", nonExistentWalletId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }
}