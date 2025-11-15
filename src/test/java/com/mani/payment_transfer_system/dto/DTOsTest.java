package com.mani.payment_transfer_system.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DTOsTest {

    @Test
    void testAccountRequest() {
        AccountRequest request = new AccountRequest();
        request.setAccountId(123L);
        request.setInitialBalance(new BigDecimal("100.50"));

        assertEquals(123L, request.getAccountId());
        assertEquals(new BigDecimal("100.50"), request.getInitialBalance());

        AccountRequest request2 = new AccountRequest(456L, new BigDecimal("200.75"));
        assertEquals(456L, request2.getAccountId());
        assertEquals(new BigDecimal("200.75"), request2.getInitialBalance());

        assertNotNull(request.toString());
    }

    @Test
    void testAccountResponse() {
        AccountResponse response = new AccountResponse();
        response.setAccountId(123L);
        response.setBalance(new BigDecimal("100.50"));

        assertEquals(123L, response.getAccountId());
        assertEquals(new BigDecimal("100.50"), response.getBalance());

        AccountResponse response2 = new AccountResponse(456L, new BigDecimal("200.75"));
        assertEquals(456L, response2.getAccountId());
        assertEquals(new BigDecimal("200.75"), response2.getBalance());

        assertNotNull(response.toString());
    }

    @Test
    void testTransactionRequest() {
        TransactionRequest request = new TransactionRequest();
        request.setSourceAccountId(123L);
        request.setDestinationAccountId(456L);
        request.setAmount(new BigDecimal("100.50"));

        assertEquals(123L, request.getSourceAccountId());
        assertEquals(456L, request.getDestinationAccountId());
        assertEquals(new BigDecimal("100.50"), request.getAmount());

        TransactionRequest request2 = new TransactionRequest(789L, 12L, new BigDecimal("200.75"));
        assertEquals(789L, request2.getSourceAccountId());
        assertEquals(12L, request2.getDestinationAccountId());
        assertEquals(new BigDecimal("200.75"), request2.getAmount());

        assertNotNull(request.toString());
    }

    @Test
    void testSuccessResponse() {
        SuccessResponse response = new SuccessResponse("Test message");

        assertEquals("Test message", response.getMessage());
        assertNotNull(response.getTimestamp());

        SuccessResponse response2 = new SuccessResponse("Another message");
        assertEquals("Another message", response2.getMessage());

        assertNotNull(response.toString());
        assertTrue(response.toString().contains("Test message"));
    }

    @Test
    void testTransactionResponse() {
        TransactionResponse response = new TransactionResponse();
        response.setId(1L);
        response.setSourceAccountId(123L);
        response.setDestinationAccountId(456L);
        response.setAmount(new BigDecimal("100.50"));
        java.time.LocalDateTime timestamp = java.time.LocalDateTime.now();
        response.setTimestamp(timestamp);

        assertEquals(1L, response.getId());
        assertEquals(123L, response.getSourceAccountId());
        assertEquals(456L, response.getDestinationAccountId());
        assertEquals(new BigDecimal("100.50"), response.getAmount());
        assertEquals(timestamp, response.getTimestamp());

        TransactionResponse response2 = new TransactionResponse(
            2L, 789L, 12L, new BigDecimal("200.75"), java.time.LocalDateTime.now());
        assertEquals(2L, response2.getId());
        assertEquals(789L, response2.getSourceAccountId());
        assertEquals(12L, response2.getDestinationAccountId());
        assertEquals(new BigDecimal("200.75"), response2.getAmount());

        assertNotNull(response.toString());
        assertTrue(response.toString().contains("1"));
        assertTrue(response.toString().contains("123"));
    }
}

