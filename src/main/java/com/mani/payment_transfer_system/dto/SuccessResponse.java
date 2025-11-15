package com.mani.payment_transfer_system.dto;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

/**
 * Data Transfer Object for success response messages.
 * Used to return success messages for operations like account creation and transaction processing.
 * Timestamp is in UTC timezone (ISO-8601 format) for global compatibility.
 */
@Getter
@ToString
public class SuccessResponse {

    private final String message;
    private final String timestamp;

    /**
     * Constructs a new SuccessResponse with the specified message.
     * The timestamp is automatically set to the current UTC time in ISO-8601 format.
     *
     * @param message the success message
     */
    public SuccessResponse(String message) {
        this.message = message;
        this.timestamp = Instant.now().toString(); // Automatically uses UTC
    }

    public static SuccessResponse of(String message) {
        return new SuccessResponse(message);
    }
}