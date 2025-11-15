package com.mani.payment_transfer_system.service;

import com.mani.payment_transfer_system.exception.AccountAlreadyExistsException;
import com.mani.payment_transfer_system.exception.AccountNotFoundException;
import com.mani.payment_transfer_system.entity.Account;
import com.mani.payment_transfer_system.dto.AccountRequest;
import com.mani.payment_transfer_system.dto.AccountResponse;
import com.mani.payment_transfer_system.repository.AccountRepository;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for account-related operations.
 * Handles business logic for account creation and retrieval.
 */
@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;
    private final MetricsService metricsService;

    /**
     * Constructs a new AccountService with the given repository and metrics service.
     *
     * @param accountRepository the account repository for data access
     * @param metricsService the metrics service for recording metrics
     */
    public AccountService(AccountRepository accountRepository, MetricsService metricsService) {
        this.accountRepository = accountRepository;
        this.metricsService = metricsService;
    }

    /**
     * Creates a new account with the specified initial balance.
     *
     * @param request the account creation request
     * @throws AccountAlreadyExistsException if an account with the same ID already exists
     */
    @Transactional
    public void createAccount(AccountRequest request) {
        Timer.Sample timer = metricsService.startAccountCreationTimer();
        try {
            logger.debug("Creating new account with ID: {} and initial balance: {}", 
                    request.getAccountId(), request.getInitialBalance());
            // 1. Check with pessimistic lock (prevents race conditions)
            if (accountRepository.findByAccountIdWithLock(request.getAccountId()).isPresent()) {
                metricsService.recordAccountAlreadyExistsError();
                throw new AccountAlreadyExistsException(request.getAccountId());
            }
            Account account = new Account(request.getAccountId(), request.getInitialBalance());

            try {
                accountRepository.save(account);
                metricsService.recordAccountCreation();
                logger.debug("Account saved successfully with ID: {}", request.getAccountId());
            } catch (DataIntegrityViolationException e) {
                metricsService.recordAccountAlreadyExistsError();
                throw new AccountAlreadyExistsException(request.getAccountId());
            }
        } finally {
            metricsService.stopAccountCreationTimer(timer);
        }
    }


    /**
     * Retrieves account information by account ID.
     *
     * @param accountId the account ID
     * @return AccountResponse containing account ID and balance
     * @throws AccountNotFoundException if the account is not found
     */
    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long accountId) {
        logger.debug("Retrieving account with ID: {}", accountId);
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    logger.warn("Account not found with ID: {}", accountId);
                    metricsService.recordAccountNotFoundError();
                    return new AccountNotFoundException(accountId);
                });

        metricsService.recordAccountQuery();
        logger.debug("Account retrieved successfully with ID: {} and balance: {}", 
                accountId, account.getBalance());
        return new AccountResponse(account.getAccountId(), account.getBalance());
    }
}

