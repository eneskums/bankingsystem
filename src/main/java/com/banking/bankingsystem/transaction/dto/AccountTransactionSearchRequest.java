package com.banking.bankingsystem.transaction.dto;

import com.banking.bankingsystem.transaction.data.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class AccountTransactionSearchRequest {

	@Schema(description = "Hesap id", example = "e8b2bc1d-7c5b-4c29-8d9d-3f07b6527cd4")
	private UUID accountId;

	@Schema(description = "İşlem tarihi", example = "2025-01-01 18:30:00")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fromDate;

	@Schema(description = "İşlem tarihi", example = "2025-01-01 18:30:00")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime toDate;

	@Schema(description = "İşlem türü", example = "WITHDRAW")
	private TransactionType transactionType;

	@Schema(description = "İşlem tutarı", example = "500.00")
	private BigDecimal minAmount;

	@Schema(description = "İşlem tutarı", example = "1000.00")
	private BigDecimal maxAmount;

	@Schema(description = "Başlangıç sayfası", example = "0")
	private int page = 0;

	@Schema(description = "Sayfadaki veri miktarı", example = "10")
	private int size = 10;
}
