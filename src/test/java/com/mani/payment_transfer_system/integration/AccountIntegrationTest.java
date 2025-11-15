package com.mani.payment_transfer_system.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mani.payment_transfer_system.dto.AccountRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndGetAccount_EndToEnd() throws Exception {
        // Create account
        AccountRequest createRequest = new AccountRequest(999L, new BigDecimal("100.23344"));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Account created successfully"));

        // Get account
        mockMvc.perform(get("/accounts/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(999))
                .andExpect(jsonPath("$.balance").value("100.23344"));
    }

    @Test
    void testCreateAccount_Duplicate() throws Exception {
        AccountRequest request = new AccountRequest(888L, new BigDecimal("50.00000"));

        // Create first account
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Account created successfully"));

        // Try to create duplicate
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetAccount_NotFound() throws Exception {
        mockMvc.perform(get("/accounts/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}

