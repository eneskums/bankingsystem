package com.banking.bankingsystem.transaction.repository;

import com.banking.bankingsystem.transaction.data.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, UUID>, QuerydslPredicateExecutor<AccountTransaction> {

	List<AccountTransaction> findByAccountId(UUID accountId);
}
