package com.banking.bankingsystem.account.dto;

import com.banking.bankingsystem.account.data.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Data
@Schema(description = "Hesap bilgileri")
public class AccountDto {

	@Schema(description = "Hesap id", example = "e8b2bc1d-7c5b-4c29-8d9d-3f07b6527cd4")
	private UUID id;

	@Schema(description = "Kullanıcının kimlik numarası", example = "12345678901")
	private BigDecimal identityNo;

	@Schema(description = "Kullanıcının adı", example = "Enes")
	private String firstName;

	@Schema(description = "Kullanıcının soyadı", example = "Kumaş")
	private String lastName;

	@Schema(description = "Hesap türü", example = "TL")
	private AccountType accountType;

	@Schema(description = "Hesap bakiyesi", example = "1000.00")
	private BigDecimal balance;
}
