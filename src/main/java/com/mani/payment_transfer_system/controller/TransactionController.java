package com.mani.payment_transfer_system.controller;

import com.mani.payment_transfer_system.dto.TransactionCreatedResponse;
import com.mani.payment_transfer_system.dto.TransactionRequest;
import com.mani.payment_transfer_system.dto.TransactionResponse;
import com.mani.payment_transfer_system.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for transaction-related operations.
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Processes a transaction between two accounts.
     * Transfers the specified amount from source account to destination account.
     *
     * @param request the transaction request containing source account ID, destination account ID, and amount
     * @return ResponseEntity with transaction ID, success message, and timestamp
     * @throws AccountNotFoundException if source or destination account is not found
     * @throws InsufficientBalanceException if source account has insufficient balance
     * @throws InvalidAmountException if the transaction amount is invalid
     */
    @PostMapping
    public ResponseEntity<TransactionCreatedResponse> submitTransaction(@Valid @RequestBody TransactionRequest request) {
        logger.info("Processing transaction from account {} to account {} with amount {}",
                request.getSourceAccountId(), request.getDestinationAccountId(), request.getAmount());
        Long transactionId = transactionService.processTransaction(request);
        logger.info("Transaction processed successfully with ID: {} from account {} to account {} with amount {}",
                transactionId, request.getSourceAccountId(), request.getDestinationAccountId(), request.getAmount());
        TransactionCreatedResponse response = new TransactionCreatedResponse(
                transactionId,
                "Transaction processed successfully"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a transaction by its unique identifier.
     *
     * @param transactionId the unique transaction identifier
     * @return ResponseEntity containing transaction details including ID, source account,
     *         destination account, amount, and timestamp
     * @throws TransactionNotFoundException if the transaction is not found
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long transactionId) {
        logger.info("Retrieving transaction with ID: {}", transactionId);
        TransactionResponse transaction = transactionService.getTransactionById(transactionId);
        logger.info("Transaction retrieved successfully with ID: {}", transactionId);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Retrieves all transactions from the system.
     *
     * @return ResponseEntity containing a list of all transactions with their details
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        logger.info("Retrieving all transactions");
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        logger.info("Retrieved {} transactions", transactions.size());
        return ResponseEntity.ok(transactions);
    }
}

