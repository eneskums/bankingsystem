package com.banking.bankingsystem.account.service;

import com.banking.bankingsystem.account.data.Account;
import com.banking.bankingsystem.account.data.AccountType;
import com.banking.bankingsystem.account.dto.AccountDto;
import com.banking.bankingsystem.account.dto.CreateAccountRequestDto;
import com.banking.bankingsystem.account.dto.UpdateAccountRequestDto;
import com.banking.bankingsystem.account.mapper.AccountMapper;
import com.banking.bankingsystem.account.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;

	public AccountDto createAccount(CreateAccountRequestDto request) {
		final BigDecimal identityNumber = request.getIdentityNo();
		final AccountType accountType = request.getAccountType();

		final boolean exists = accountRepository.existsByIdentityNoAndAccountType(identityNumber, accountType);

		if (exists) {
			throw new IllegalArgumentException("Bu kimlik numarasına sahip bu türde bir hesap bulunmaktadır.");
		}

		final Account account = AccountMapper.INSTANCE.createAccountFromCreateAccountRequestDto(request);

		return AccountMapper.INSTANCE.accountToAccountDto(accountRepository.save(account));
	}

	public List<AccountDto> getAllAccounts() {
		return AccountMapper.INSTANCE.accountsToAccountDtoList(accountRepository.findAll());
	}

	public AccountDto getAccountById(UUID id) {
		return AccountMapper.INSTANCE.accountToAccountDto(
				accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Hesap bulunamadı.")));
	}

	public AccountDto updateAccount(UUID id, UpdateAccountRequestDto request) {
		final Account account = accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Güncellenecek hesap bulunamadı."));

		AccountMapper.INSTANCE.updateAccountFromUpdateAccountRequestDto(request, account);

		return AccountMapper.INSTANCE.accountToAccountDto(accountRepository.save(account));
	}

	public void deleteAccountById(UUID id) {
		if (!accountRepository.existsById(id)) {
			throw new EntityNotFoundException("Silinecek hesap bulunamadı.");
		}
		accountRepository.deleteById(id);
	}
}
