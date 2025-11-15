package com.mani.payment_transfer_system.exception;

/**
 * Exception thrown when an invalid amount is provided for a transaction.
 * This exception is thrown when the transaction amount is zero, negative,
 * or when source and destination accounts are the same.
 */
public class InvalidAmountException extends RuntimeException {

    /**
     * Constructs a new InvalidAmountException with the specified message.
     *
     * @param message the detail message describing why the amount is invalid
     */
    public InvalidAmountException(String message) {
        super(message);
    }
}

