package com.mani.payment_transfer_system.service;

import com.mani.payment_transfer_system.exception.AccountAlreadyExistsException;
import com.mani.payment_transfer_system.exception.AccountNotFoundException;
import com.mani.payment_transfer_system.entity.Account;
import com.mani.payment_transfer_system.dto.AccountRequest;
import com.mani.payment_transfer_system.dto.AccountResponse;
import com.mani.payment_transfer_system.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private AccountRequest accountRequest;
    private Account account;

    @BeforeEach
    void setUp() {
        accountRequest = new AccountRequest(123L, new BigDecimal("100.23344"));
        account = new Account(123L, new BigDecimal("100.23344"));
    }

    @Test
    void testCreateAccount_Success() {
        when(accountRepository.existsByAccountId(123L)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        assertDoesNotThrow(() -> accountService.createAccount(accountRequest));
        verify(accountRepository).existsByAccountId(123L);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void testCreateAccount_AccountAlreadyExists() {
        when(accountRepository.existsByAccountId(123L)).thenReturn(true);

        assertThrows(AccountAlreadyExistsException.class, () -> accountService.createAccount(accountRequest));
        verify(accountRepository).existsByAccountId(123L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void testGetAccount_Success() {
        when(accountRepository.findByAccountId(123L)).thenReturn(Optional.of(account));

        AccountResponse response = accountService.getAccount(123L);

        assertNotNull(response);
        assertEquals(123L, response.getAccountId());
        assertEquals(new BigDecimal("100.23344"), response.getBalance());
        verify(accountRepository).findByAccountId(123L);
    }

    @Test
    void testGetAccount_NotFound() {
        when(accountRepository.findByAccountId(123L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(123L));
        verify(accountRepository).findByAccountId(123L);
    }

    @Test
    void testCreateAccount_UnexpectedException() {
        when(accountRepository.existsByAccountId(123L)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.createAccount(accountRequest));
        assertEquals("Database error", exception.getMessage());
        verify(accountRepository).existsByAccountId(123L);
    }

    @Test
    void testGetAccount_UnexpectedException() {
        when(accountRepository.findByAccountId(123L)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.getAccount(123L));
        assertEquals("Database error", exception.getMessage());
        verify(accountRepository).findByAccountId(123L);
    }
}

