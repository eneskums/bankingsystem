package com.banking.bankingsystem.account.repository;

import com.banking.bankingsystem.account.data.Account;
import com.banking.bankingsystem.account.data.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

	boolean existsByIdentityNoAndAccountType(BigDecimal identityNo, AccountType accountType);
}
