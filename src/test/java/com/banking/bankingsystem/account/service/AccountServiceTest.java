package com.banking.bankingsystem.account.service;

import com.banking.bankingsystem.account.data.Account;
import com.banking.bankingsystem.account.data.AccountType;
import com.banking.bankingsystem.account.dto.AccountDto;
import com.banking.bankingsystem.account.dto.CreateAccountRequestDto;
import com.banking.bankingsystem.account.dto.UpdateAccountRequestDto;
import com.banking.bankingsystem.account.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class AccountServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@InjectMocks
	private AccountService accountService;

	private Account account;

	private CreateAccountRequestDto createAccountRequestDto;

	private UpdateAccountRequestDto updateAccountRequestDto;

	private String firstName;

	private String lastName;

	@BeforeEach
	void setUp() {
		UUID accountId = UUID.randomUUID();
		BigDecimal identityNo = new BigDecimal("12345678901");
		firstName = "first name";
		lastName = "last name";

		account = new Account();
		account.setId(accountId);
		account.setIdentityNo(identityNo);
		account.setFirstName(firstName);
		account.setLastName(lastName);
		account.setAccountType(AccountType.TL);

		createAccountRequestDto = new CreateAccountRequestDto();
		createAccountRequestDto.setIdentityNo(identityNo);
		createAccountRequestDto.setFirstName(firstName);
		createAccountRequestDto.setLastName(lastName);
		createAccountRequestDto.setAccountType(AccountType.TL);

		updateAccountRequestDto = new UpdateAccountRequestDto();
		updateAccountRequestDto.setFirstName("updated first name");
		updateAccountRequestDto.setLastName("updated last name");
	}

	@Test
	void createAccount_ShouldCreateAccount_WhenValidDataProvided() {
		when(accountRepository.existsByIdentityNoAndAccountType(any(), any())).thenReturn(false);
		when(accountRepository.save(any())).thenReturn(account);

		AccountDto accountDto = accountService.createAccount(createAccountRequestDto);

		assertNotNull(accountDto);
		assertEquals(firstName, accountDto.getFirstName());
		assertEquals(lastName, accountDto.getLastName());
		verify(accountRepository, times(1)).save(any());
	}

	@Test
	void createAccount_ShouldThrowException_WhenAccountAlreadyExists() {
		when(accountRepository.existsByIdentityNoAndAccountType(any(), any())).thenReturn(true);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
														  () -> accountService.createAccount(createAccountRequestDto));
		assertEquals("Bu kimlik numarasına sahip bu türde bir hesap bulunmaktadır.", exception.getMessage());
	}

	@Test
	void getAllAccounts_ShouldReturnAccountList() {
		when(accountRepository.findAll()).thenReturn(List.of(account));

		List<AccountDto> accountDtos = accountService.getAllAccounts();

		assertFalse(accountDtos.isEmpty());
		assertEquals(1, accountDtos.size());
		verify(accountRepository, times(1)).findAll();
	}

	@Test
	void getAccountById_ShouldReturnAccount_WhenAccountExists() {
		when(accountRepository.findById(any(UUID.class))).thenReturn(Optional.of(account));

		AccountDto accountDto = accountService.getAccountById(account.getId());

		assertNotNull(accountDto);
		assertEquals(account.getFirstName(), accountDto.getFirstName());
		assertEquals(account.getLastName(), accountDto.getLastName());
		verify(accountRepository, times(1)).findById(any(UUID.class));
	}

	@Test
	void getAccountById_ShouldThrowException_WhenAccountNotFound() {
		when(accountRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> accountService.getAccountById(UUID.randomUUID()));
		assertEquals("Hesap bulunamadı.", exception.getMessage());
	}

	@Test
	void updateAccount_ShouldUpdateAccount_WhenValidDataProvided() {
		when(accountRepository.findById(any(UUID.class))).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenReturn(account);

		AccountDto accountDto = accountService.updateAccount(account.getId(), updateAccountRequestDto);

		assertNotNull(accountDto);
		assertEquals("updated first name", accountDto.getFirstName());
		assertEquals("updated last name", accountDto.getLastName());
		verify(accountRepository, times(1)).save(any());
	}

	@Test
	void updateAccount_ShouldThrowException_WhenAccountNotFound() {
		when(accountRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
														 () -> accountService.updateAccount(UUID.randomUUID(), updateAccountRequestDto));
		assertEquals("Güncellenecek hesap bulunamadı.", exception.getMessage());
	}

	@Test
	void deleteAccount_ShouldDeleteAccount_WhenAccountExists() {
		when(accountRepository.existsById(any(UUID.class))).thenReturn(true);

		accountService.deleteAccountById(account.getId());

		verify(accountRepository, times(1)).deleteById(any(UUID.class));
	}

	@Test
	void deleteAccount_ShouldThrowException_WhenAccountNotFound() {
		when(accountRepository.existsById(any(UUID.class))).thenReturn(false);

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> accountService.deleteAccountById(UUID.randomUUID()));
		assertEquals("Silinecek hesap bulunamadı.", exception.getMessage());
	}
}