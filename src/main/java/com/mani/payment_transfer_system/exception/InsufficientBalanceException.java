package com.mani.payment_transfer_system.exception;

import java.math.BigDecimal;

/**
 * Exception thrown when an account has insufficient balance for a transaction.
 * This exception is thrown when attempting to transfer an amount that exceeds
 * the current balance of the source account.
 */
public class InsufficientBalanceException extends RuntimeException {

    /**
     * Constructs a new InsufficientBalanceException with the specified message.
     *
     * @param message the detail message
     */
    public InsufficientBalanceException(String message) {
        super(message);
    }

    /**
     * Constructs a new InsufficientBalanceException with account details.
     *
     * @param accountId the account ID with insufficient balance
     * @param balance the current balance of the account
     * @param requestedAmount the amount that was requested for transfer
     */
    public InsufficientBalanceException(Long accountId, BigDecimal balance, BigDecimal requestedAmount) {
        super(String.format("Account %d has insufficient balance. Current balance: %s, Requested amount: %s",
                accountId, balance, requestedAmount));
    }
}

