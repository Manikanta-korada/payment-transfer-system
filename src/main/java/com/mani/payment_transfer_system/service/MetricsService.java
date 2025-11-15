package com.mani.payment_transfer_system.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for collecting application metrics.
 * Provides methods to record various business and operational metrics.
 */
@Service
public class MetricsService {

    private final Counter transactionCounter;
    private final Counter transactionAmountCounter;
    private final Counter accountCreationCounter;
    private final Counter accountQueryCounter;
    private final Counter transactionQueryCounter;
    private final Counter errorCounter;
    private final Counter insufficientBalanceCounter;
    private final Counter accountNotFoundCounter;
    private final Counter invalidAmountCounter;
    private final Counter accountAlreadyExistsCounter;
    private final Timer transactionProcessingTime;
    private final Timer accountCreationTime;

    public MetricsService(MeterRegistry meterRegistry) {
        // Transaction metrics
        this.transactionCounter = Counter.builder("payment.transactions.total")
                .description("Total number of transactions processed")
                .register(meterRegistry);

        this.transactionAmountCounter = Counter.builder("payment.transactions.amount.total")
                .description("Total amount of all transactions")
                .baseUnit("currency")
                .register(meterRegistry);

        this.transactionProcessingTime = Timer.builder("payment.transactions.processing.time")
                .description("Time taken to process transactions")
                .register(meterRegistry);

        // Account metrics
        this.accountCreationCounter = Counter.builder("payment.accounts.created.total")
                .description("Total number of accounts created")
                .register(meterRegistry);

        this.accountQueryCounter = Counter.builder("payment.accounts.queried.total")
                .description("Total number of account queries")
                .register(meterRegistry);

        this.accountCreationTime = Timer.builder("payment.accounts.creation.time")
                .description("Time taken to create accounts")
                .register(meterRegistry);

        // Query metrics
        this.transactionQueryCounter = Counter.builder("payment.transactions.queried.total")
                .description("Total number of transaction queries")
                .register(meterRegistry);

        // Error metrics
        this.errorCounter = Counter.builder("payment.errors.total")
                .description("Total number of errors")
                .register(meterRegistry);

        this.insufficientBalanceCounter = Counter.builder("payment.errors.insufficient_balance")
                .description("Number of insufficient balance errors")
                .register(meterRegistry);

        this.accountNotFoundCounter = Counter.builder("payment.errors.account_not_found")
                .description("Number of account not found errors")
                .register(meterRegistry);

        this.invalidAmountCounter = Counter.builder("payment.errors.invalid_amount")
                .description("Number of invalid amount errors")
                .register(meterRegistry);

        this.accountAlreadyExistsCounter = Counter.builder("payment.errors.account_already_exists")
                .description("Number of account already exists errors")
                .register(meterRegistry);
    }

    /**
     * Records a successful transaction.
     *
     * @param amount the transaction amount
     */
    public void recordTransaction(BigDecimal amount) {
        transactionCounter.increment();
        transactionAmountCounter.increment(amount.doubleValue());
    }

    /**
     * Records transaction processing time.
     *
     * @return Timer.Sample to be stopped after processing
     */
    public Timer.Sample startTransactionTimer() {
        return Timer.start();
    }

    /**
     * Stops the transaction timer and records the duration.
     *
     * @param sample the timer sample started earlier
     */
    public void stopTransactionTimer(Timer.Sample sample) {
        sample.stop(transactionProcessingTime);
    }

    /**
     * Records a successful account creation.
     */
    public void recordAccountCreation() {
        accountCreationCounter.increment();
    }

    /**
     * Records account creation time.
     *
     * @return Timer.Sample to be stopped after creation
     */
    public Timer.Sample startAccountCreationTimer() {
        return Timer.start();
    }

    /**
     * Stops the account creation timer and records the duration.
     *
     * @param sample the timer sample started earlier
     */
    public void stopAccountCreationTimer(Timer.Sample sample) {
        sample.stop(accountCreationTime);
    }

    /**
     * Records an account query.
     */
    public void recordAccountQuery() {
        accountQueryCounter.increment();
    }

    /**
     * Records a transaction query.
     */
    public void recordTransactionQuery() {
        transactionQueryCounter.increment();
    }

    /**
     * Records an error occurrence.
     */
    public void recordError() {
        errorCounter.increment();
    }

    /**
     * Records an insufficient balance error.
     */
    public void recordInsufficientBalanceError() {
        insufficientBalanceCounter.increment();
        recordError();
    }

    /**
     * Records an account not found error.
     */
    public void recordAccountNotFoundError() {
        accountNotFoundCounter.increment();
        recordError();
    }

    /**
     * Records an invalid amount error.
     */
    public void recordInvalidAmountError() {
        invalidAmountCounter.increment();
        recordError();
    }

    /**
     * Records an account already exists error.
     */
    public void recordAccountAlreadyExistsError() {
        accountAlreadyExistsCounter.increment();
        recordError();
    }
}

