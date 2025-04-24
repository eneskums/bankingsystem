package com.banking.bankingsystem.transaction.service;

import com.banking.bankingsystem.account.data.Account;
import com.banking.bankingsystem.account.repository.AccountRepository;
import com.banking.bankingsystem.transaction.data.AccountTransaction;
import com.banking.bankingsystem.transaction.data.QAccountTransaction;
import com.banking.bankingsystem.transaction.data.TransactionType;
import com.banking.bankingsystem.transaction.dto.AccountTransactionDto;
import com.banking.bankingsystem.transaction.dto.AccountTransactionSearchRequest;
import com.banking.bankingsystem.transaction.dto.TransactionRequestDto;
import com.banking.bankingsystem.transaction.mapper.AccountTransactionMapper;
import com.banking.bankingsystem.transaction.repository.AccountTransactionRepository;
import com.querydsl.core.BooleanBuilder;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

	private final AccountTransactionRepository accountTransactionRepository;

	private final AccountRepository accountRepository;

	private static final BigDecimal MAX_ACCOUNT_BALANCE = BigDecimal.valueOf(9_999_999);

	@Transactional
	public AccountTransactionDto deposit(UUID accountId, TransactionRequestDto request) {
		final Account account = accountRepository
				.findById(accountId)
				.orElseThrow(() -> new EntityNotFoundException("İşlem yapılacak hesap bulunamadı."));

		final BigDecimal amount = request.getAmount();
		final BigDecimal newBalance = account.getBalance().add(amount);

		if (newBalance.compareTo(MAX_ACCOUNT_BALANCE) > 0) {
			throw new IllegalArgumentException("Hesap bakiyesi 9.999.999'dan fazla olamaz.");
		}

		account.setBalance(newBalance);
		accountRepository.save(account);

		final AccountTransaction accountTransaction = new AccountTransaction();
		accountTransaction.setAccount(account);
		accountTransaction.setAmount(amount);
		accountTransaction.setTransactionType(TransactionType.DEPOSIT);
		accountTransaction.setTransactionDate(LocalDateTime.now());

		return AccountTransactionMapper.INSTANCE.accountTransactionToAccountTransactionDto(accountTransactionRepository.save(accountTransaction));
	}

	@Transactional
	public AccountTransactionDto withdraw(UUID accountId, TransactionRequestDto request) {
		final Account account = accountRepository
				.findById(accountId)
				.orElseThrow(() -> new EntityNotFoundException("İşlem yapılacak hesap bulunamadı."));

		final BigDecimal amount = request.getAmount();

		if (account.getBalance().compareTo(amount) < 0) {
			throw new IllegalArgumentException("Yetersiz bakiye");
		}

		account.setBalance(account.getBalance().subtract(amount));
		accountRepository.save(account);

		final AccountTransaction accountTransaction = new AccountTransaction();
		accountTransaction.setAccount(account);
		accountTransaction.setAmount(amount);
		accountTransaction.setTransactionType(TransactionType.WITHDRAW);
		accountTransaction.setTransactionDate(LocalDateTime.now());

		return AccountTransactionMapper.INSTANCE.accountTransactionToAccountTransactionDto(accountTransactionRepository.save(accountTransaction));
	}

	public List<AccountTransactionDto> getTransactionsByAccountId(UUID accountId) {
		return AccountTransactionMapper.INSTANCE.accountTransactionsToAccountTransactionDtoList(
				accountTransactionRepository.findByAccountId(accountId));
	}

	public Page<AccountTransactionDto> searchTransactions(AccountTransactionSearchRequest request) {
		final QAccountTransaction qAccountTransaction = QAccountTransaction.accountTransaction;

		final BooleanBuilder builder = new BooleanBuilder();

		if (Objects.nonNull(request.getFromDate()) && Objects.nonNull(request.getToDate()) && request.getFromDate().isAfter(request.getToDate())) {
			throw new IllegalArgumentException("Başlangıç tarihi bilgisi bitiş tarihinden küçük ya da eşit olmalıdır.");
		}

		if (Objects.nonNull(request.getMinAmount()) && Objects.nonNull(request.getMaxAmount())
				&& request.getMinAmount().compareTo(request.getMaxAmount()) > 0) {
			throw new IllegalArgumentException("Minumum işlem tutarı bilgisi maksimum işlem tutarı bilgisinden küçük ya da eşit olmalıdır.");
		}

		if (Objects.nonNull(request.getAccountId())) {
			builder.and(qAccountTransaction.account.id.eq(request.getAccountId()));
		}

		if (Objects.nonNull(request.getFromDate())) {
			builder.and(qAccountTransaction.transactionDate.goe(request.getFromDate()));
		}

		if (Objects.nonNull(request.getToDate())) {
			builder.and(qAccountTransaction.transactionDate.loe(request.getToDate()));
		}

		if (Objects.nonNull(request.getTransactionType())) {
			builder.and(qAccountTransaction.transactionType.eq(request.getTransactionType()));
		}

		if (Objects.nonNull(request.getMinAmount())) {
			builder.and(qAccountTransaction.amount.goe(request.getMinAmount()));
		}

		if (Objects.nonNull(request.getMaxAmount())) {
			builder.and(qAccountTransaction.amount.loe(request.getMaxAmount()));
		}

		final Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(Sort.Direction.DESC, "transactionDate"));

		final Page<AccountTransaction> resultPage = accountTransactionRepository.findAll(builder, pageable);

		return resultPage.map(AccountTransactionMapper.INSTANCE::accountTransactionToAccountTransactionDto);
	}
}
