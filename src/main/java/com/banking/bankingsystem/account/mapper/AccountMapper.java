package com.banking.bankingsystem.account.mapper;

import com.banking.bankingsystem.account.data.Account;
import com.banking.bankingsystem.account.dto.AccountDto;
import com.banking.bankingsystem.account.dto.CreateAccountRequestDto;
import com.banking.bankingsystem.account.dto.UpdateAccountRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Mapper
public interface AccountMapper {

	AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

	AccountDto accountToAccountDto(Account account);

	List<AccountDto> accountsToAccountDtoList(List<Account> accounts);

	Account createAccountFromCreateAccountRequestDto(CreateAccountRequestDto createAccountRequestDto);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "identityNo", ignore = true)
	@Mapping(target = "accountType", ignore = true)
	@Mapping(target = "balance", ignore = true)
	@Mapping(target = "transactions", ignore = true)
	void updateAccountFromUpdateAccountRequestDto(UpdateAccountRequestDto updateAccountRequestDto, @MappingTarget Account account);
}
