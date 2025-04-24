package com.banking.bankingsystem.account.dto;

import com.banking.bankingsystem.account.data.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Data
@Schema(description = "Hesap oluşturmak için gerekli bilgiler")
public class CreateAccountRequestDto {

	@NotNull
	@Digits(integer = 11, fraction = 0)
	@Schema(description = "Kullanıcının kimlik numarası", example = "12345678901", required = true)
	private BigDecimal identityNo;

	@NotBlank
	@Schema(description = "Kullanıcının adı", example = "Enes", required = true)
	private String firstName;

	@NotBlank
	@Schema(description = "Kullanıcının soyadı", example = "Kumaş", required = true)
	private String lastName;

	@NotNull
	@Schema(description = "Hesap türü", example = "TL", required = true)
	private AccountType accountType;
}
