package com.banking.bankingsystem.transaction.mapper;

import com.banking.bankingsystem.transaction.data.AccountTransaction;
import com.banking.bankingsystem.transaction.dto.AccountTransactionDto;
import com.banking.bankingsystem.transaction.dto.TransactionRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Mapper
public interface AccountTransactionMapper {

	AccountTransactionMapper INSTANCE = Mappers.getMapper(AccountTransactionMapper.class);

	AccountTransactionDto accountTransactionToAccountTransactionDto(AccountTransaction accountTransaction);

	List<AccountTransactionDto> accountTransactionsToAccountTransactionDtoList(List<AccountTransaction> accountTransactions);
}
