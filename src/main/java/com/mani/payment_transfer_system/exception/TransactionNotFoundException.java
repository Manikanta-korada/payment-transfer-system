package com.mani.payment_transfer_system.exception;

/**
 * Exception thrown when a transaction is not found.
 * This exception is thrown when attempting to access or operate on a transaction
 * that does not exist in the system.
 */
public class TransactionNotFoundException extends RuntimeException {

    /**
     * Constructs a new TransactionNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public TransactionNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new TransactionNotFoundException for the specified transaction ID.
     *
     * @param transactionId the transaction ID that was not found
     */
    public TransactionNotFoundException(Long transactionId) {
        super("Transaction with ID " + transactionId + " not found");
    }
}

