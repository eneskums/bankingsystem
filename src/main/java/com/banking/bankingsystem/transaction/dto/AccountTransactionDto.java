package com.banking.bankingsystem.transaction.dto;

import com.banking.bankingsystem.transaction.data.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@Data
@Schema(description = "Hesap hareketi bilgileri")
public class AccountTransactionDto {

	@Schema(description = "İşlem id", example = "f5dc28e1-62ad-47b4-bc18-c9a37c82d429")
	private UUID id;

	@Schema(description = "İşlem yapan hesap id", example = "e8b2bc1d-7c5b-4c29-8d9d-3f07b6527cd4")
	private UUID accountId;

	@Schema(description = "İşlem tarihi", example = "2025-01-01 18:30:00")
	private LocalDateTime transactionDate;

	@Schema(description = "İşlem türü", example = "DEPOSIT")
	private TransactionType transactionType;

	@Schema(description = "İşlem tutarı", example = "500.00")
	private BigDecimal amount;
}
