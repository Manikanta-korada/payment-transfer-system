package com.mani.payment_transfer_system.exception;

import com.mani.payment_transfer_system.dto.ErrorResponse;
import com.mani.payment_transfer_system.service.MetricsService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for centralized error handling across all controllers.
 * Provides consistent error response format for all exceptions thrown in the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MetricsService metricsService;

    public GlobalExceptionHandler(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    /**
     * Handles AccountNotFoundException.
     * Returns HTTP 404 Not Found status.
     *
     * @param ex the AccountNotFoundException that was thrown
     * @param request the HTTP request
     * @return ResponseEntity with error message and HTTP 404 status
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex, HttpServletRequest request) {
        // Metrics already recorded in service layer
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles TransactionNotFoundException.
     * Returns HTTP 404 Not Found status.
     *
     * @param ex the TransactionNotFoundException that was thrown
     * @param request the HTTP request
     * @return ResponseEntity with error message and HTTP 404 status
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(
            TransactionNotFoundException ex, HttpServletRequest request) {
        // Metrics already recorded in service layer
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles InsufficientBalanceException.
     * Returns HTTP 400 Bad Request status.
     *
     * @param ex the InsufficientBalanceException that was thrown
     * @param request the HTTP request
     * @return ResponseEntity with error message and HTTP 400 status
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException ex, HttpServletRequest request) {
        // Metrics already recorded in service layer
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles InvalidAmountException.
     * Returns HTTP 400 Bad Request status.
     *
     * @param ex the InvalidAmountException that was thrown
     * @param request the HTTP request
     * @return ResponseEntity with error message and HTTP 400 status
     */
    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAmountException(InvalidAmountException ex, HttpServletRequest request) {
        // Metrics already recorded in service layer
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles AccountAlreadyExistsException.
     * Returns HTTP 409 Conflict status.
     *
     * @param ex the AccountAlreadyExistsException that was thrown
     * @param request the HTTP request
     * @return ResponseEntity with error message and HTTP 409 status
     */
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyExistsException(AccountAlreadyExistsException ex, HttpServletRequest request) {
        // Metrics already recorded in service layer
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles validation exceptions from request body validation.
     * Returns HTTP 400 Bad Request status with field-level error details.
     *
     * @param ex the MethodArgumentNotValidException that was thrown
     * @param request the HTTP request
     * @return ResponseEntity with validation errors and HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String message = error.getDefaultMessage();
            if (errorMessage.length() > "Validation failed: ".length()) {
                errorMessage.append("; ");
            }
            errorMessage.append(message);
        });
        ErrorResponse errorResponse = new ErrorResponse(errorMessage.toString(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles IllegalArgumentException.
     * Returns HTTP 400 Bad Request status.
     *
     * @param ex the IllegalArgumentException that was thrown
     * @param request the HTTP request
     * @return ResponseEntity with error message and HTTP 400 status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles all other unhandled exceptions.
     * Returns HTTP 500 Internal Server Error status.
     * This is a catch-all handler for any exception not handled by specific handlers.
     * Logs detailed error information server-side but returns generic message to client
     * to prevent information leakage.
     *
     * @param ex the Exception that was thrown
     * @param request the HTTP request
     * @return ResponseEntity with generic error message and HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        // Log the full exception details server-side for debugging
        logger.error("An unexpected error occurred", ex);
        
        // Record generic error metric
        metricsService.recordError();
        
        ErrorResponse errorResponse = new ErrorResponse(
                "An unexpected error occurred. Please contact support if the problem persists.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

