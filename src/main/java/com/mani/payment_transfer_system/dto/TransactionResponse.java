package com.mani.payment_transfer_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for transaction response.
 * Contains complete transaction information including ID, accounts, amount, and timestamp.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionResponse {

    private Long id;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}

