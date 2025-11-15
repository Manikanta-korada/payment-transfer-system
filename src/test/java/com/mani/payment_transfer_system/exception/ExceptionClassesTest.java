package com.mani.payment_transfer_system.exception;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionClassesTest {

    @Test
    void testAccountNotFoundException_WithMessage() {
        AccountNotFoundException ex = new AccountNotFoundException("Custom message");
        assertEquals("Custom message", ex.getMessage());
    }

    @Test
    void testAccountNotFoundException_WithAccountId() {
        AccountNotFoundException ex = new AccountNotFoundException(123L);
        assertEquals("Account with ID 123 not found", ex.getMessage());
    }

    @Test
    void testInsufficientBalanceException_WithMessage() {
        InsufficientBalanceException ex = new InsufficientBalanceException("Custom message");
        assertEquals("Custom message", ex.getMessage());
    }

    @Test
    void testInsufficientBalanceException_WithDetails() {
        InsufficientBalanceException ex = new InsufficientBalanceException(123L, 
                new BigDecimal("50.00"), new BigDecimal("100.00"));
        assertTrue(ex.getMessage().contains("123"));
        assertTrue(ex.getMessage().contains("50.00"));
        assertTrue(ex.getMessage().contains("100.00"));
    }

    @Test
    void testInvalidAmountException() {
        InvalidAmountException ex = new InvalidAmountException("Invalid amount");
        assertEquals("Invalid amount", ex.getMessage());
    }

    @Test
    void testAccountAlreadyExistsException_WithMessage() {
        AccountAlreadyExistsException ex = new AccountAlreadyExistsException("Custom message");
        assertEquals("Custom message", ex.getMessage());
    }

    @Test
    void testAccountAlreadyExistsException_WithAccountId() {
        AccountAlreadyExistsException ex = new AccountAlreadyExistsException(123L);
        assertEquals("Account with ID 123 already exists", ex.getMessage());
    }
}

