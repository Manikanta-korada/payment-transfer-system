package com.mani.payment_transfer_system.dto;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

/**
 * Data Transfer Object for error responses.
 * Provides a consistent error response format across all API endpoints.
 * Timestamp is in UTC timezone (ISO-8601 format) for global compatibility.
 */
@Getter
@ToString
public class ErrorResponse {

    private final String error;
    private final String timestamp;
    private final String path;

    /**
     * Constructs a new ErrorResponse with the specified error message and path.
     * The timestamp is automatically set to the current UTC time in ISO-8601 format.
     *
     * @param error the error message
     * @param path the request path where the error occurred
     */
    public ErrorResponse(String error, String path) {
        this.error = error;
        this.path = path;
        this.timestamp = Instant.now().toString(); // Automatically uses UTC
    }

    /**
     * Convenience factory method for creating ErrorResponse instances.
     *
     * @param error the error message
     * @param path the request path where the error occurred
     * @return new ErrorResponse instance
     */
    public static ErrorResponse of(String error, String path) {
        return new ErrorResponse(error, path);
    }
}