package com.mani.payment_transfer_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mani.payment_transfer_system.exception.AccountNotFoundException;
import com.mani.payment_transfer_system.exception.InsufficientBalanceException;
import com.mani.payment_transfer_system.exception.InvalidAmountException;
import com.mani.payment_transfer_system.dto.TransactionRequest;
import com.mani.payment_transfer_system.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void testSubmitTransaction_Success() throws Exception {
        TransactionRequest request = new TransactionRequest(123L, 456L, new BigDecimal("100.12345"));

        when(transactionService.processTransaction(any(TransactionRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.message").value("Transaction processed successfully"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(transactionService).processTransaction(any(TransactionRequest.class));
    }

    @Test
    void testSubmitTransaction_ValidationError() throws Exception {
        TransactionRequest request = new TransactionRequest(null, 456L, new BigDecimal("-10.00000"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).processTransaction(any(TransactionRequest.class));
    }

    @Test
    void testSubmitTransaction_AccountNotFound() throws Exception {
        TransactionRequest request = new TransactionRequest(123L, 456L, new BigDecimal("100.12345"));

        doThrow(new AccountNotFoundException(123L)).when(transactionService).processTransaction(any(TransactionRequest.class));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        verify(transactionService).processTransaction(any(TransactionRequest.class));
    }

    @Test
    void testSubmitTransaction_InsufficientBalance() throws Exception {
        TransactionRequest request = new TransactionRequest(123L, 456L, new BigDecimal("100.12345"));

        doThrow(new InsufficientBalanceException(123L, new BigDecimal("50.00000"), new BigDecimal("100.12345")))
                .when(transactionService).processTransaction(any(TransactionRequest.class));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(transactionService).processTransaction(any(TransactionRequest.class));
    }

    @Test
    void testSubmitTransaction_InvalidAmount() throws Exception {
        TransactionRequest request = new TransactionRequest(123L, 456L, new BigDecimal("100.12345"));

        doThrow(new InvalidAmountException("Transaction amount must be positive"))
                .when(transactionService).processTransaction(any(TransactionRequest.class));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(transactionService).processTransaction(any(TransactionRequest.class));
    }

    @Test
    void testSubmitTransaction_UnexpectedException() throws Exception {
        TransactionRequest request = new TransactionRequest(123L, 456L, new BigDecimal("100.12345"));

        doThrow(new RuntimeException("Unexpected error")).when(transactionService).processTransaction(any(TransactionRequest.class));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());

        verify(transactionService).processTransaction(any(TransactionRequest.class));
    }

    @Test
    void testGetAllTransactions_Success() throws Exception {
        com.mani.payment_transfer_system.dto.TransactionResponse response1 = 
            new com.mani.payment_transfer_system.dto.TransactionResponse(
                1L, 123L, 456L, new BigDecimal("50.00000"), java.time.LocalDateTime.now());
        com.mani.payment_transfer_system.dto.TransactionResponse response2 = 
            new com.mani.payment_transfer_system.dto.TransactionResponse(
                2L, 456L, 789L, new BigDecimal("100.00000"), java.time.LocalDateTime.now());

        when(transactionService.getAllTransactions())
            .thenReturn(java.util.List.of(response1, response2));

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].source_account_id").value(123))
                .andExpect(jsonPath("$[0].destination_account_id").value(456))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(transactionService).getAllTransactions();
    }

    @Test
    void testGetAllTransactions_EmptyList() throws Exception {
        when(transactionService.getAllTransactions()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(transactionService).getAllTransactions();
    }
}

