package com.mani.payment_transfer_system.exception;

/**
 * Exception thrown when attempting to create an account that already exists.
 * This exception is thrown when trying to create an account with an ID
 * that is already in use.
 */
public class AccountAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new AccountAlreadyExistsException with the specified message.
     *
     * @param message the detail message
     */
    public AccountAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructs a new AccountAlreadyExistsException for the specified account ID.
     *
     * @param accountId the account ID that already exists
     */
    public AccountAlreadyExistsException(Long accountId) {
        super("Account with ID " + accountId + " already exists");
    }
}

