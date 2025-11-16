package com.mani.payment_transfer_system.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mani.payment_transfer_system.dto.AccountRequest;
import com.mani.payment_transfer_system.dto.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testTransaction_EndToEnd() throws Exception {
        // Create source account
        AccountRequest sourceRequest = new AccountRequest(111L, new BigDecimal("200.00000"));
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sourceRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        // Create destination account
        AccountRequest destRequest = new AccountRequest(222L, new BigDecimal("100.00000"));
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(destRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        // Process transaction
        TransactionRequest transactionRequest = new TransactionRequest(111L, 222L, new BigDecimal("50.12345"));
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Transaction processed successfully"));

        // Verify source account balance
        mockMvc.perform(get("/accounts/111"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("149.87655"));

        // Verify destination account balance
        mockMvc.perform(get("/accounts/222"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("150.12345"));
    }

    @Test
    void testTransaction_InsufficientBalance() throws Exception {
        // Create source account with low balance
        AccountRequest sourceRequest = new AccountRequest(333L, new BigDecimal("10.00000"));
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sourceRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        // Create destination account
        AccountRequest destRequest = new AccountRequest(444L, new BigDecimal("100.00000"));
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(destRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        // Try to transfer more than available
        TransactionRequest transactionRequest = new TransactionRequest(333L, 444L, new BigDecimal("50.00000"));
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testTransaction_AccountNotFound() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(999L, 888L, new BigDecimal("50.00000"));
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetAllTransactions_EndToEnd() throws Exception {
        // Create accounts
        AccountRequest sourceRequest = new AccountRequest(555L, new BigDecimal("200.00000"));
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sourceRequest)))
                .andExpect(status().isCreated());

        AccountRequest destRequest = new AccountRequest(666L, new BigDecimal("100.00000"));
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(destRequest)))
                .andExpect(status().isCreated());

        // Process first transaction
        TransactionRequest transactionRequest1 = new TransactionRequest(555L, 666L, new BigDecimal("50.00000"));
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest1)))
                .andExpect(status().isCreated());

        // Process second transaction
        TransactionRequest transactionRequest2 = new TransactionRequest(666L, 555L, new BigDecimal("25.00000"));
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest2)))
                .andExpect(status().isCreated());

        // Get all transactions
        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$[?(@.source_account_id == 555 && @.destination_account_id == 666)]").exists())
                .andExpect(jsonPath("$[?(@.source_account_id == 666 && @.destination_account_id == 555)]").exists())
                .andExpect(jsonPath("$[*].source_account_id").exists())
                .andExpect(jsonPath("$[*].destination_account_id").exists())
                .andExpect(jsonPath("$[*].amount").exists())
                .andExpect(jsonPath("$[*].timestamp").exists());
    }
}

