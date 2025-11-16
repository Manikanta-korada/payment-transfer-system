package com.mani.payment_transfer_system.service;

import com.mani.payment_transfer_system.exception.AccountNotFoundException;
import com.mani.payment_transfer_system.exception.InsufficientBalanceException;
import com.mani.payment_transfer_system.exception.InvalidAmountException;
import com.mani.payment_transfer_system.entity.Account;
import com.mani.payment_transfer_system.entity.Transaction;
import com.mani.payment_transfer_system.dto.TransactionRequest;
import com.mani.payment_transfer_system.repository.AccountRepository;
import com.mani.payment_transfer_system.repository.TransactionRepository;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionRequest transactionRequest;
    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        transactionRequest = new TransactionRequest(123L, 456L, new BigDecimal("50.12345"));
        sourceAccount = new Account(123L, new BigDecimal("100.00000"));
        destinationAccount = new Account(456L, new BigDecimal("200.00000"));
        
        // Mock MetricsService methods with lenient stubbing (not all tests use all methods)
        Timer.Sample timerSample = Timer.start();
        lenient().when(metricsService.startTransactionTimer()).thenReturn(timerSample);
        lenient().doNothing().when(metricsService).stopTransactionTimer(any(Timer.Sample.class));
        lenient().doNothing().when(metricsService).recordTransaction(any(BigDecimal.class));
        lenient().doNothing().when(metricsService).recordTransactionQuery();
        lenient().doNothing().when(metricsService).recordAccountNotFoundError();
        lenient().doNothing().when(metricsService).recordInsufficientBalanceError();
        lenient().doNothing().when(metricsService).recordInvalidAmountError();
        lenient().doNothing().when(metricsService).recordError();
    }

    @Test
    void testProcessTransaction_Success() {
        when(accountRepository.findByAccountIdWithLock(123L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountIdWithLock(456L)).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        Long transactionId = transactionService.processTransaction(transactionRequest);
        assertNotNull(transactionId);
        assertEquals(1L, transactionId);

        // Verify locks are acquired in sorted order (123 first, then 456)
        var inOrder = inOrder(accountRepository);
        inOrder.verify(accountRepository).findByAccountIdWithLock(123L);
        inOrder.verify(accountRepository).findByAccountIdWithLock(456L);
        
        verify(accountRepository).saveAll(anyList());
        verify(transactionRepository).save(any(Transaction.class));
        verify(metricsService).recordTransaction(any(BigDecimal.class));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Account>> accountListCaptor = ArgumentCaptor.forClass(List.class);
        verify(accountRepository).saveAll(accountListCaptor.capture());

        List<Account> savedAccounts = accountListCaptor.getValue();
        assertEquals(2, savedAccounts.size());
        assertEquals(new BigDecimal("49.87655"), savedAccounts.get(0).getBalance());
        assertEquals(new BigDecimal("250.12345"), savedAccounts.get(1).getBalance());
    }

    @Test
    void testProcessTransaction_SourceAccountNotFound() {
        when(accountRepository.findByAccountIdWithLock(123L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> transactionService.processTransaction(transactionRequest));
        verify(accountRepository).findByAccountIdWithLock(123L);
        verify(accountRepository, never()).saveAll(anyList());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(metricsService).recordAccountNotFoundError();
    }

    @Test
    void testProcessTransaction_DestinationAccountNotFound() {
        // With sorted locking, we lock accounts in ascending order (123 first, then 456)
        when(accountRepository.findByAccountIdWithLock(123L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountIdWithLock(456L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> transactionService.processTransaction(transactionRequest));
        
        // Verify locks are acquired in sorted order
        var inOrder = inOrder(accountRepository);
        inOrder.verify(accountRepository).findByAccountIdWithLock(123L);
        inOrder.verify(accountRepository).findByAccountIdWithLock(456L);
        
        verify(accountRepository, never()).saveAll(anyList());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(metricsService).recordAccountNotFoundError();
    }

    @Test
    void testProcessTransaction_InsufficientBalance() {
        sourceAccount.setBalance(new BigDecimal("30.00000"));
        // With sorted locking, we lock accounts in ascending order (123, then 456)
        when(accountRepository.findByAccountIdWithLock(123L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountIdWithLock(456L)).thenReturn(Optional.of(destinationAccount));

        assertThrows(InsufficientBalanceException.class, () -> transactionService.processTransaction(transactionRequest));
        
        // Verify locks are acquired in sorted order
        var inOrder = inOrder(accountRepository);
        inOrder.verify(accountRepository).findByAccountIdWithLock(123L);
        inOrder.verify(accountRepository).findByAccountIdWithLock(456L);
        
        verify(accountRepository, never()).saveAll(anyList());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(metricsService).recordInsufficientBalanceError();
    }

    @Test
    void testProcessTransaction_InvalidAmount_Zero() {
        transactionRequest.setAmount(BigDecimal.ZERO);

        assertThrows(InvalidAmountException.class, () -> transactionService.processTransaction(transactionRequest));
        verify(accountRepository, never()).findByAccountIdWithLock(any());
        verify(accountRepository, never()).saveAll(anyList());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(metricsService).recordInvalidAmountError();
    }

    @Test
    void testProcessTransaction_InvalidAmount_ExactlyZero() {
        transactionRequest.setAmount(new BigDecimal("0.00000"));

        assertThrows(InvalidAmountException.class, () -> transactionService.processTransaction(transactionRequest));
        verify(accountRepository, never()).findByAccountIdWithLock(any());
        verify(accountRepository, never()).saveAll(anyList());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(metricsService).recordInvalidAmountError();
    }

    @Test
    void testProcessTransaction_InvalidAmount_Negative() {
        transactionRequest.setAmount(new BigDecimal("-10.00000"));

        assertThrows(InvalidAmountException.class, () -> transactionService.processTransaction(transactionRequest));
        verify(accountRepository, never()).findByAccountIdWithLock(any());
        verify(accountRepository, never()).saveAll(anyList());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(metricsService).recordInvalidAmountError();
    }

    @Test
    void testProcessTransaction_SameSourceAndDestination() {
        transactionRequest.setDestinationAccountId(123L);

        assertThrows(InvalidAmountException.class, () -> transactionService.processTransaction(transactionRequest));
        verify(accountRepository, never()).findByAccountIdWithLock(any());
        verify(accountRepository, never()).saveAll(anyList());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(metricsService).recordInvalidAmountError();
    }

    @Test
    void testProcessTransaction_ExactBalance() {
        sourceAccount.setBalance(new BigDecimal("50.12345"));
        when(accountRepository.findByAccountIdWithLock(123L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountIdWithLock(456L)).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        Long transactionId = transactionService.processTransaction(transactionRequest);
        assertNotNull(transactionId);
        assertEquals(1L, transactionId);
        
        // Verify locks are acquired in sorted order
        var inOrder = inOrder(accountRepository);
        inOrder.verify(accountRepository).findByAccountIdWithLock(123L);
        inOrder.verify(accountRepository).findByAccountIdWithLock(456L);
        
        verify(accountRepository).saveAll(anyList());
    }

    @Test
    void testProcessTransaction_SortedLocking_ReverseOrder() {
        // Test that locks are acquired in sorted order even when dest < source
        // Transfer from 456 to 123 - should lock 123 first, then 456
        TransactionRequest reverseRequest = new TransactionRequest(456L, 123L, new BigDecimal("25.00000"));
        Account account123 = new Account(123L, new BigDecimal("100.00000"));
        Account account456 = new Account(456L, new BigDecimal("200.00000"));

        when(accountRepository.findByAccountIdWithLock(123L)).thenReturn(Optional.of(account123));
        when(accountRepository.findByAccountIdWithLock(456L)).thenReturn(Optional.of(account456));
        when(accountRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        Long transactionId = transactionService.processTransaction(reverseRequest);
        assertNotNull(transactionId);
        assertEquals(1L, transactionId);
        
        // Verify locks are acquired in sorted order (123 first, then 456)
        verify(accountRepository, times(1)).findByAccountIdWithLock(123L);
        verify(accountRepository, times(1)).findByAccountIdWithLock(456L);
        
        // Verify the order of lock acquisition
        var inOrder = inOrder(accountRepository);
        inOrder.verify(accountRepository).findByAccountIdWithLock(123L);
        inOrder.verify(accountRepository).findByAccountIdWithLock(456L);
        
        verify(accountRepository).saveAll(anyList());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testProcessTransaction_UnexpectedException() {
        when(accountRepository.findByAccountIdWithLock(123L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountIdWithLock(456L)).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.saveAll(anyList())).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionService.processTransaction(transactionRequest));
        assertEquals("Database error", exception.getMessage());
        
        // Verify locks are acquired in sorted order
        var inOrder = inOrder(accountRepository);
        inOrder.verify(accountRepository).findByAccountIdWithLock(123L);
        inOrder.verify(accountRepository).findByAccountIdWithLock(456L);
    }

    @Test
    void testGetAllTransactions_Success() {
        Transaction transaction1 = new Transaction(123L, 456L, new BigDecimal("50.00000"));
        transaction1.setId(1L);
        transaction1.setTimestamp(java.time.LocalDateTime.now());
        
        Transaction transaction2 = new Transaction(456L, 789L, new BigDecimal("100.00000"));
        transaction2.setId(2L);
        transaction2.setTimestamp(java.time.LocalDateTime.now());

        when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2));

        List<com.mani.payment_transfer_system.dto.TransactionResponse> result = 
            transactionService.getAllTransactions();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(123L, result.get(0).getSourceAccountId());
        assertEquals(456L, result.get(0).getDestinationAccountId());
        assertEquals(new BigDecimal("50.00000"), result.get(0).getAmount());
        
        verify(transactionRepository).findAll();
        verify(metricsService).recordTransactionQuery();
    }

    @Test
    void testGetAllTransactions_EmptyList() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        List<com.mani.payment_transfer_system.dto.TransactionResponse> result = 
            transactionService.getAllTransactions();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(transactionRepository).findAll();
        verify(metricsService).recordTransactionQuery();
    }
}

