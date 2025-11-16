package com.mani.payment_transfer_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Data Transfer Object for transaction submission request.
 * Contains source account ID, destination account ID, and the amount to transfer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionRequest {

    @NotNull(message = "Source account ID is required")
    @JsonProperty("source_account_id")
    private Long sourceAccountId;

    @NotNull(message = "Destination account ID is required")
    @JsonProperty("destination_account_id")
    private Long destinationAccountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00001", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;
}

