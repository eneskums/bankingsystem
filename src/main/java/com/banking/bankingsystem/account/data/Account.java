package com.banking.bankingsystem.account.data;

import com.banking.bankingsystem.transaction.data.AccountTransaction;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Entity
@Table(name = "account", uniqueConstraints = @UniqueConstraint(columnNames = { "identity_no", "account_type" }))
@Data
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private UUID id;

	@Column(name = "identity_no", nullable = false, precision = 11)
	private BigDecimal identityNo;

	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	@Enumerated(EnumType.STRING)
	@Column(name = "account_type", nullable = false)
	private AccountType accountType;

	@Column(name = "balance", nullable = false, precision = 9, scale = 2)
	private BigDecimal balance = BigDecimal.ZERO;

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AccountTransaction> transactions = new ArrayList<>();
}
