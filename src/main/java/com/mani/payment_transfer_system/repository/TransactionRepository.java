package com.mani.payment_transfer_system.repository;

import com.mani.payment_transfer_system.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Transaction entity operations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}

