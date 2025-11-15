package com.mani.payment_transfer_system.repository;

import com.mani.payment_transfer_system.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

/**
 * Repository interface for Account entity operations.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find account by account ID.
     *
     * @param accountId the account ID
     * @return Optional containing the account if found
     */
    Optional<Account> findByAccountId(Long accountId);

    /**
     * Find account by account ID with pessimistic write lock for transaction processing.
     *
     * @param accountId the account ID
     * @return Optional containing the account if found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountId = :accountId")
    Optional<Account> findByAccountIdWithLock(@Param("accountId") Long accountId);

    /**
     * Check if account exists by account ID.
     *
     * @param accountId the account ID
     * @return true if account exists, false otherwise
     */
    boolean existsByAccountId(Long accountId);
}

