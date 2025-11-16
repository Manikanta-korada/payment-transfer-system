package com.mani.payment_transfer_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mani.payment_transfer_system.exception.AccountAlreadyExistsException;
import com.mani.payment_transfer_system.exception.AccountNotFoundException;
import com.mani.payment_transfer_system.dto.AccountRequest;
import com.mani.payment_transfer_system.dto.AccountResponse;
import com.mani.payment_transfer_system.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @Test
    void testCreateAccount_Success() throws Exception {
        AccountRequest request = new AccountRequest(123L, new BigDecimal("100.23344"));

        doNothing().when(accountService).createAccount(any(AccountRequest.class));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        verify(accountService).createAccount(any(AccountRequest.class));
    }

    @Test
    void testCreateAccount_ValidationError() throws Exception {
        AccountRequest request = new AccountRequest(null, new BigDecimal("-10.00000"));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).createAccount(any(AccountRequest.class));
    }

    @Test
    void testCreateAccount_AccountAlreadyExists() throws Exception {
        AccountRequest request = new AccountRequest(123L, new BigDecimal("100.23344"));

        doThrow(new AccountAlreadyExistsException(123L)).when(accountService).createAccount(any(AccountRequest.class));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());

        verify(accountService).createAccount(any(AccountRequest.class));
    }

    @Test
    void testGetAccount_Success() throws Exception {
        AccountResponse response = new AccountResponse(123L, new BigDecimal("100.23344"));

        when(accountService.getAccount(123L)).thenReturn(response);

        mockMvc.perform(get("/accounts/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account_id").value(123))
                .andExpect(jsonPath("$.balance").value("100.23344"));

        verify(accountService).getAccount(123L);
    }

    @Test
    void testGetAccount_NotFound() throws Exception {
        when(accountService.getAccount(123L)).thenThrow(new AccountNotFoundException(123L));

        mockMvc.perform(get("/accounts/123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        verify(accountService).getAccount(123L);
    }

    @Test
    void testCreateAccount_UnexpectedException() throws Exception {
        AccountRequest request = new AccountRequest(123L, new BigDecimal("100.23344"));

        doThrow(new RuntimeException("Unexpected error")).when(accountService).createAccount(any(AccountRequest.class));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());

        verify(accountService).createAccount(any(AccountRequest.class));
    }

    @Test
    void testGetAccount_UnexpectedException() throws Exception {
        when(accountService.getAccount(123L)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/accounts/123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());

        verify(accountService).getAccount(123L);
    }
}

