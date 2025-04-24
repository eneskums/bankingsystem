package com.banking.bankingsystem.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Data
public class UpdateAccountRequestDto {

	@NotBlank
	@Schema(description = "Kullanıcının yeni adı", example = "Enes", required = true)
	private String firstName;

	@NotBlank
	@Schema(description = "Kullanıcının yeni soyadı", example = "Kumaş", required = true)
	private String lastName;
}
