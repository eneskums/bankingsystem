package com.banking.bankingsystem.transaction.data;

import com.banking.bankingsystem.account.data.Account;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Entity
@Table(name = "account_transaction")
@Data
public class AccountTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	@Column(name = "transaction_date", nullable = false)
	private LocalDateTime transactionDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", nullable = false)
	private TransactionType transactionType;

	@Column(name = "amount", nullable = false, precision = 9, scale = 2)
	private BigDecimal amount;
}
