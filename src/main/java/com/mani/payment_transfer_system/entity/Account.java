package com.mani.payment_transfer_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Account entity representing a financial account with a balance.
 * Uses pessimistic locking for concurrent transaction safety.
 */
@Entity
@Table(name = "accounts", uniqueConstraints = {
    @UniqueConstraint(columnNames = "account_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Account {

    /**
     * The unique account identifier.
     */
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "account_id", nullable = false, unique = true)
    private Long accountId;

    /**
     * The current balance of the account.
     * Precision: 19 digits, Scale: 5 decimal places.
     */
    @Column(name = "balance", nullable = false, precision = 19, scale = 5)
    private BigDecimal balance;
}

