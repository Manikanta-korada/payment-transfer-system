package com.mani.payment_transfer_system.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void testDefaultConstructor() {
        Account account = new Account();
        assertNull(account.getAccountId());
        assertNull(account.getBalance());
    }

    @Test
    void testParameterizedConstructor() {
        Account account = new Account(123L, new BigDecimal("100.50"));
        assertEquals(123L, account.getAccountId());
        assertEquals(new BigDecimal("100.50"), account.getBalance());
    }

    @Test
    void testGettersAndSetters() {
        Account account = new Account();
        account.setAccountId(456L);
        account.setBalance(new BigDecimal("200.75"));

        assertEquals(456L, account.getAccountId());
        assertEquals(new BigDecimal("200.75"), account.getBalance());
    }

    @Test
    void testEquals_SameInstance() {
        Account account = new Account(123L, new BigDecimal("100.00"));
        assertEquals(account, account);
    }

    @Test
    void testEquals_SameAccountId() {
        Account account1 = new Account(123L, new BigDecimal("100.00"));
        Account account2 = new Account(123L, new BigDecimal("200.00"));
        assertEquals(account1, account2);
    }

    @Test
    void testEquals_DifferentAccountId() {
        Account account1 = new Account(123L, new BigDecimal("100.00"));
        Account account2 = new Account(456L, new BigDecimal("100.00"));
        assertNotEquals(account1, account2);
    }

    @Test
    void testEquals_Null() {
        Account account = new Account(123L, new BigDecimal("100.00"));
        assertNotEquals(account, null);
        assertNotEquals(null, account);
    }

    @Test
    void testEquals_DifferentClass() {
        Account account = new Account(123L, new BigDecimal("100.00"));
        assertNotEquals(account, "not an account");
    }

    @Test
    void testHashCode() {
        Account account1 = new Account(123L, new BigDecimal("100.00"));
        Account account2 = new Account(123L, new BigDecimal("200.00"));
        assertEquals(account1.hashCode(), account2.hashCode());
    }

    @Test
    void testToString() {
        Account account = new Account(123L, new BigDecimal("100.50"));
        String toString = account.toString();
        assertTrue(toString.contains("123"));
        assertTrue(toString.contains("100.50"));
    }
}

