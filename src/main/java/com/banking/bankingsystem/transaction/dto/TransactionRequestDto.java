package com.banking.bankingsystem.transaction.dto;

import com.banking.bankingsystem.transaction.data.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Data
public class TransactionRequestDto {

	@NotNull
	@DecimalMin(value = "0.01", message = "Tutar 0.01'den büyük olmalı")
	@Schema(description = "İşlem tutarı", example = "500.00", required = true)
	private BigDecimal amount;
}
