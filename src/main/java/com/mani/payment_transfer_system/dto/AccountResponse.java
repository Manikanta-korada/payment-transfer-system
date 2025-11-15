package com.mani.payment_transfer_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Data Transfer Object for account query response.
 * Contains the account ID and current balance information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountResponse {

    private Long accountId;
    private BigDecimal balance;
}

