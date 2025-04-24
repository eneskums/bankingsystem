package com.banking.bankingsystem.transaction.service;

import com.banking.bankingsystem.account.data.Account;
import com.banking.bankingsystem.account.repository.AccountRepository;
import com.banking.bankingsystem.transaction.data.AccountTransaction;
import com.banking.bankingsystem.transaction.data.TransactionType;
import com.banking.bankingsystem.transaction.dto.AccountTransactionDto;
import com.banking.bankingsystem.transaction.dto.AccountTransactionSearchRequest;
import com.banking.bankingsystem.transaction.dto.TransactionRequestDto;
import com.banking.bankingsystem.transaction.repository.AccountTransactionRepository;
import com.querydsl.core.BooleanBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@ExtendWith(MockitoExtension.class)
class AccountTransactionServiceTest {

	@Mock
	private AccountTransactionRepository accountTransactionRepository;

	@Mock
	private AccountRepository accountRepository;

	@InjectMocks
	private AccountTransactionService accountTransactionService;

	private Account account;

	private UUID accountId;

	private TransactionRequestDto transactionRequestDto;

	private AccountTransactionSearchRequest searchRequest;

	@BeforeEach
	void setUp() {
		accountId = UUID.randomUUID();
		account = new Account();
		account.setId(accountId);
		account.setBalance(BigDecimal.valueOf(5000));

		transactionRequestDto = new TransactionRequestDto();
		transactionRequestDto.setAmount(BigDecimal.valueOf(1000));
		searchRequest = new AccountTransactionSearchRequest();
		searchRequest.setAccountId(null);
		searchRequest.setFromDate(LocalDateTime.now().minusDays(1));
		searchRequest.setToDate(LocalDateTime.now());
		searchRequest.setTransactionType(TransactionType.DEPOSIT);
		searchRequest.setMinAmount(BigDecimal.valueOf(100));
		searchRequest.setMaxAmount(BigDecimal.valueOf(5000));
	}

	@Test
	void deposit_ShouldIncreaseBalance_WhenValidDataProvided() {
		when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
		when(accountTransactionRepository.save(any())).thenReturn(new AccountTransaction());

		AccountTransactionDto result = accountTransactionService.deposit(accountId, transactionRequestDto);

		assertNotNull(result);
		assertEquals(BigDecimal.valueOf(6000), account.getBalance());
		verify(accountRepository, times(1)).save(account);
	}

	@Test
	void deposit_ShouldThrowException_WhenBalanceExceedsMaxLimit() {
		account.setBalance(BigDecimal.valueOf(9_999_000));

		when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			accountTransactionService.deposit(accountId, transactionRequestDto);
		});

		assertEquals("Hesap bakiyesi 9.999.999'dan fazla olamaz.", exception.getMessage());
	}

	@Test
	void withdraw_ShouldDecreaseBalance_WhenValidDataProvided() {
		when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
		when(accountTransactionRepository.save(any())).thenReturn(new AccountTransaction());

		AccountTransactionDto result = accountTransactionService.withdraw(accountId, transactionRequestDto);

		assertNotNull(result);
		assertEquals(BigDecimal.valueOf(4000), account.getBalance());
		verify(accountRepository, times(1)).save(account);
	}

	@Test
	void withdraw_ShouldThrowException_WhenInsufficientBalance() {
		account.setBalance(BigDecimal.valueOf(500));

		when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			accountTransactionService.withdraw(accountId, transactionRequestDto);
		});

		assertEquals("Yetersiz bakiye", exception.getMessage());
	}

	@Test
	void getTransactionsByAccountId_ShouldReturnTransactionList() {
		AccountTransaction transaction = new AccountTransaction();
		transaction.setAmount(BigDecimal.valueOf(1000));

		when(accountTransactionRepository.findByAccountId(accountId)).thenReturn(List.of(transaction));

		List<AccountTransactionDto> transactions = accountTransactionService.getTransactionsByAccountId(accountId);

		assertNotNull(transactions);
		assertEquals(1, transactions.size());
		assertEquals(BigDecimal.valueOf(1000), transactions.getFirst().getAmount());
		verify(accountTransactionRepository, times(1)).findByAccountId(accountId);
	}

	@Test
	void getTransactionsByAccountId_ShouldReturnEmptyList_WhenNoTransactionsExist() {
		when(accountTransactionRepository.findByAccountId(accountId)).thenReturn(List.of());

		List<AccountTransactionDto> transactions = accountTransactionService.getTransactionsByAccountId(accountId);

		assertNotNull(transactions);
		assertTrue(transactions.isEmpty());
	}

	@Test
	void deposit_ShouldThrowException_WhenAccountNotFound() {
		when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			accountTransactionService.deposit(accountId, transactionRequestDto);
		});

		assertEquals("İşlem yapılacak hesap bulunamadı.", exception.getMessage());
	}

	@Test
	void withdraw_ShouldThrowException_WhenAccountNotFound() {
		when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			accountTransactionService.withdraw(accountId, transactionRequestDto);
		});

		assertEquals("İşlem yapılacak hesap bulunamadı.", exception.getMessage());
	}

	@Test
	void searchTransactions_ShouldReturnTransactionList_WhenValidDataProvided() {
		AccountTransaction transaction = new AccountTransaction();
		transaction.setAmount(BigDecimal.valueOf(500));
		transaction.setTransactionType(TransactionType.DEPOSIT);

		Page<AccountTransaction> page = new PageImpl<>(List.of(transaction), PageRequest.of(0, 10), 1);

		when(accountTransactionRepository.findAll(any(BooleanBuilder.class), any(Pageable.class))).thenReturn(page);

		Page<AccountTransactionDto> result = accountTransactionService.searchTransactions(searchRequest);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.getTotalElements());
		assertEquals(transaction.getTransactionType(), result.getContent().getFirst().getTransactionType());
		assertEquals(transaction.getAmount(), result.getContent().getFirst().getAmount());

		verify(accountTransactionRepository, times(1)).findAll(any(BooleanBuilder.class), any(Pageable.class));
	}

	@Test
	void searchTransactions_ShouldReturnEmptyList_WhenNoTransactionsFound() {
		Page<AccountTransaction> emptyPage = Page.empty();

		when(accountTransactionRepository.findAll(any(BooleanBuilder.class), any(Pageable.class))).thenReturn(emptyPage);

		Page<AccountTransactionDto> result = accountTransactionService.searchTransactions(searchRequest);

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(accountTransactionRepository, times(1)).findAll(any(BooleanBuilder.class), any(Pageable.class));
	}

	@Test
	void testSearchTransactions_ShouldThrowException_WhenFromDateIsAfterToDate() {
		searchRequest.setFromDate(LocalDateTime.now().plusDays(1));
		searchRequest.setToDate(LocalDateTime.now());

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			accountTransactionService.searchTransactions(searchRequest);
		});

		assertEquals("Başlangıç tarihi bilgisi bitiş tarihinden küçük ya da eşit olmalıdır.", exception.getMessage());
	}

	@Test
	void testSearchTransactions_ShouldThrowException_WhenMinAmountGreaterThanMaxAmount() {
		searchRequest.setMinAmount(BigDecimal.valueOf(2000));
		searchRequest.setMaxAmount(BigDecimal.valueOf(1500));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			accountTransactionService.searchTransactions(searchRequest);
		});

		assertEquals("Minumum işlem tutarı bilgisi maksimum işlem tutarı bilgisinden küçük ya da eşit olmalıdır.", exception.getMessage());
	}
}