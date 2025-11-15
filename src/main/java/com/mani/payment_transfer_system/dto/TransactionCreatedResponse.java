package com.mani.payment_transfer_system.dto;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

/**
 * Data Transfer Object for transaction creation response.
 * Contains the transaction ID, success message, and timestamp.
 * Timestamp is in UTC timezone (ISO-8601 format) for global compatibility.
 */
@Getter
@ToString
public class TransactionCreatedResponse {

    private final Long transactionId;
    private final String message;
    private final String timestamp;

    /**
     * Constructs a new TransactionCreatedResponse with the specified transaction ID and message.
     * The timestamp is automatically set to the current UTC time in ISO-8601 format.
     *
     * @param transactionId the unique transaction identifier
     * @param message the success message
     */
    public TransactionCreatedResponse(Long transactionId, String message) {
        this.transactionId = transactionId;
        this.message = message;
        this.timestamp = Instant.now().toString(); // Automatically uses UTC
    }

    public static TransactionCreatedResponse of(Long transactionId, String message) {
        return new TransactionCreatedResponse(transactionId, message);
    }
}

