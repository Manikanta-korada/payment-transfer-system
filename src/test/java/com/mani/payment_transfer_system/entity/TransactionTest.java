package com.mani.payment_transfer_system.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testDefaultConstructor() {
        Transaction transaction = new Transaction();
        assertNull(transaction.getId());
        assertNull(transaction.getSourceAccountId());
        assertNull(transaction.getDestinationAccountId());
        assertNull(transaction.getAmount());
        assertNull(transaction.getTimestamp());
    }

    @Test
    void testParameterizedConstructor() {
        Transaction transaction = new Transaction(123L, 456L, new BigDecimal("100.50"));
        assertEquals(123L, transaction.getSourceAccountId());
        assertEquals(456L, transaction.getDestinationAccountId());
        assertEquals(new BigDecimal("100.50"), transaction.getAmount());
        assertNotNull(transaction.getTimestamp());
    }

    @Test
    void testGettersAndSetters() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setSourceAccountId(123L);
        transaction.setDestinationAccountId(456L);
        transaction.setAmount(new BigDecimal("100.50"));
        LocalDateTime timestamp = LocalDateTime.now();
        transaction.setTimestamp(timestamp);

        assertEquals(1L, transaction.getId());
        assertEquals(123L, transaction.getSourceAccountId());
        assertEquals(456L, transaction.getDestinationAccountId());
        assertEquals(new BigDecimal("100.50"), transaction.getAmount());
        assertEquals(timestamp, transaction.getTimestamp());
    }

    @Test
    void testOnCreate_SetsTimestamp() {
        Transaction transaction = new Transaction();
        assertNull(transaction.getTimestamp());
        transaction.onCreate();
        assertNotNull(transaction.getTimestamp());
    }

    @Test
    void testOnCreate_DoesNotOverrideExistingTimestamp() {
        Transaction transaction = new Transaction();
        LocalDateTime existingTimestamp = LocalDateTime.now().minusHours(1);
        transaction.setTimestamp(existingTimestamp);
        transaction.onCreate();
        assertEquals(existingTimestamp, transaction.getTimestamp());
    }

    @Test
    void testOnCreate_WithNullTimestamp() {
        Transaction transaction = new Transaction(123L, 456L, new BigDecimal("100.00"));
        transaction.setTimestamp(null);
        transaction.onCreate();
        assertNotNull(transaction.getTimestamp());
    }

    @Test
    void testEquals_SameInstance() {
        Transaction transaction = new Transaction(123L, 456L, new BigDecimal("100.00"));
        transaction.setId(1L);
        assertEquals(transaction, transaction);
    }

    @Test
    void testEquals_SameId() {
        Transaction transaction1 = new Transaction(123L, 456L, new BigDecimal("100.00"));
        transaction1.setId(1L);
        Transaction transaction2 = new Transaction(789L, 12L, new BigDecimal("200.00"));
        transaction2.setId(1L);
        assertEquals(transaction1, transaction2);
    }

    @Test
    void testEquals_DifferentId() {
        Transaction transaction1 = new Transaction(123L, 456L, new BigDecimal("100.00"));
        transaction1.setId(1L);
        Transaction transaction2 = new Transaction(123L, 456L, new BigDecimal("100.00"));
        transaction2.setId(2L);
        assertNotEquals(transaction1, transaction2);
    }

    @Test
    void testEquals_Null() {
        Transaction transaction = new Transaction(123L, 456L, new BigDecimal("100.00"));
        transaction.setId(1L);
        assertNotEquals(transaction, null);
        assertNotEquals(null, transaction);
    }

    @Test
    void testEquals_DifferentClass() {
        Transaction transaction = new Transaction(123L, 456L, new BigDecimal("100.00"));
        transaction.setId(1L);
        assertNotEquals(transaction, "not a transaction");
    }

    @Test
    void testHashCode() {
        Transaction transaction1 = new Transaction(123L, 456L, new BigDecimal("100.00"));
        transaction1.setId(1L);
        Transaction transaction2 = new Transaction(789L, 12L, new BigDecimal("200.00"));
        transaction2.setId(1L);
        assertEquals(transaction1.hashCode(), transaction2.hashCode());
    }

    @Test
    void testToString() {
        Transaction transaction = new Transaction(123L, 456L, new BigDecimal("100.50"));
        transaction.setId(1L);
        LocalDateTime timestamp = LocalDateTime.now();
        transaction.setTimestamp(timestamp);
        String toString = transaction.toString();
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("123"));
        assertTrue(toString.contains("456"));
        assertTrue(toString.contains("100.50"));
    }
}

