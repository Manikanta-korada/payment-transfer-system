package com.mani.payment_transfer_system.controller;

import com.mani.payment_transfer_system.dto.AccountRequest;
import com.mani.payment_transfer_system.dto.AccountResponse;
import com.mani.payment_transfer_system.service.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for account-related operations.
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Creates a new account with the specified initial balance.
     *
     * @param request the account creation request containing account ID and initial balance
     * @return ResponseEntity with empty body (201 Created) on success
     * @throws AccountAlreadyExistsException if an account with the same ID already exists
     */
    @PostMapping
    public ResponseEntity<Void> createAccount(@Valid @RequestBody AccountRequest request) {
        logger.info("Creating account with ID: {}", request.getAccountId());
        accountService.createAccount(request);
        logger.info("Account created successfully with ID: {}", request.getAccountId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Retrieves account information by account ID.
     *
     * @param accountId the account ID to retrieve
     * @return ResponseEntity containing account information including account ID and balance
     * @throws AccountNotFoundException if the account with the given ID is not found
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long accountId) {
        logger.info("Retrieving account with ID: {}", accountId);
        AccountResponse response = accountService.getAccount(accountId);
        logger.info("Account retrieved successfully with ID: {}", accountId);
        return ResponseEntity.ok(response);
    }
}

