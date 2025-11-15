package com.mani.payment_transfer_system.exception;

/**
 * Exception thrown when an account is not found.
 * This exception is thrown when attempting to access or operate on an account
 * that does not exist in the system.
 */
public class AccountNotFoundException extends RuntimeException {

    /**
     * Constructs a new AccountNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public AccountNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new AccountNotFoundException for the specified account ID.
     *
     * @param accountId the account ID that was not found
     */
    public AccountNotFoundException(Long accountId) {
        super("Account with ID " + accountId + " not found");
    }
}

