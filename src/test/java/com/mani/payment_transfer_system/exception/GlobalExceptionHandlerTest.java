package com.mani.payment_transfer_system.exception;

import com.mani.payment_transfer_system.service.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        metricsService = mock(MetricsService.class);
        globalExceptionHandler = new GlobalExceptionHandler(metricsService);
    }

    @Test
    void testHandleAccountNotFoundException() {
        AccountNotFoundException ex = new AccountNotFoundException(123L);
        jakarta.servlet.http.HttpServletRequest request = mock(jakarta.servlet.http.HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/accounts/123");
        ResponseEntity<com.mani.payment_transfer_system.dto.ErrorResponse> response = 
                globalExceptionHandler.handleAccountNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Account with ID 123 not found", response.getBody().getError());
        assertEquals("/accounts/123", response.getBody().getPath());
    }

    @Test
    void testHandleInsufficientBalanceException() {
        InsufficientBalanceException ex = new InsufficientBalanceException(123L, 
                new java.math.BigDecimal("50.00"), new java.math.BigDecimal("100.00"));
        jakarta.servlet.http.HttpServletRequest request = mock(jakarta.servlet.http.HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/transactions");
        ResponseEntity<com.mani.payment_transfer_system.dto.ErrorResponse> response = 
                globalExceptionHandler.handleInsufficientBalanceException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getError().contains("insufficient balance"));
        assertEquals("/transactions", response.getBody().getPath());
    }

    @Test
    void testHandleInvalidAmountException() {
        InvalidAmountException ex = new InvalidAmountException("Invalid amount");
        jakarta.servlet.http.HttpServletRequest request = mock(jakarta.servlet.http.HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/transactions");
        ResponseEntity<com.mani.payment_transfer_system.dto.ErrorResponse> response = 
                globalExceptionHandler.handleInvalidAmountException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid amount", response.getBody().getError());
        assertEquals("/transactions", response.getBody().getPath());
    }

    @Test
    void testHandleAccountAlreadyExistsException() {
        AccountAlreadyExistsException ex = new AccountAlreadyExistsException(123L);
        jakarta.servlet.http.HttpServletRequest request = mock(jakarta.servlet.http.HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/accounts");
        ResponseEntity<com.mani.payment_transfer_system.dto.ErrorResponse> response = 
                globalExceptionHandler.handleAccountAlreadyExistsException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Account with ID 123 already exists", response.getBody().getError());
        assertEquals("/accounts", response.getBody().getPath());
    }

    @Test
    void testHandleValidationExceptions() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        List<org.springframework.validation.ObjectError> errors = new ArrayList<>();
        
        FieldError fieldError1 = new FieldError("accountRequest", "accountId", "accountId is required");
        FieldError fieldError2 = new FieldError("accountRequest", "initialBalance", "initialBalance is required");
        errors.add(fieldError1);
        errors.add(fieldError2);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(errors);

        jakarta.servlet.http.HttpServletRequest request = mock(jakarta.servlet.http.HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/accounts");
        ResponseEntity<com.mani.payment_transfer_system.dto.ErrorResponse> response = 
                globalExceptionHandler.handleValidationExceptions(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getError().contains("Validation failed"));
        assertEquals("/accounts", response.getBody().getPath());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        jakarta.servlet.http.HttpServletRequest request = mock(jakarta.servlet.http.HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/accounts");
        ResponseEntity<com.mani.payment_transfer_system.dto.ErrorResponse> response = 
                globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid argument", response.getBody().getError());
        assertEquals("/accounts", response.getBody().getPath());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected error");
        jakarta.servlet.http.HttpServletRequest request = mock(jakarta.servlet.http.HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/accounts");
        ResponseEntity<com.mani.payment_transfer_system.dto.ErrorResponse> response = 
                globalExceptionHandler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getError().contains("unexpected error"));
        assertEquals("/accounts", response.getBody().getPath());
    }
}

