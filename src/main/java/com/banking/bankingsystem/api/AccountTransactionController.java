package com.banking.bankingsystem.api;

import com.banking.bankingsystem.transaction.dto.AccountTransactionDto;
import com.banking.bankingsystem.transaction.dto.AccountTransactionSearchRequest;
import com.banking.bankingsystem.transaction.dto.TransactionRequestDto;
import com.banking.bankingsystem.transaction.service.AccountTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Hesap Hareketleri", description = "Para yatırma / çekme işlemleri")
public class AccountTransactionController {

	private final AccountTransactionService accountTransactionService;

	@PostMapping("/{accountId}/deposit")
	@Operation(summary = "Hesaba para yatırma işlemi", description = "Belirtilen hesaba para yatırma işlemi gerçekleştirir.")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "İşlem başarıyla gerçekleştirildi"),
			@ApiResponse(responseCode = "400", description = "Geçersiz işlem veya yetersiz bakiye"),
			@ApiResponse(responseCode = "404", description = "Hesap bulunamadı") })
	public ResponseEntity<AccountTransactionDto> deposit(@PathVariable @Parameter(description = "İşlem yapılacak hesap id") UUID accountId,
			@Valid @RequestBody TransactionRequestDto request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(accountTransactionService.deposit(accountId, request));
	}

	@PostMapping("/{accountId}/withdraw")
	@Operation(summary = "Hesaptan para çekme işlemi", description = "Belirtilen hesaptan para çekme işlemi gerçekleştirir.")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "İşlem başarıyla gerçekleştirildi"),
			@ApiResponse(responseCode = "400", description = "Geçersiz işlem veya yetersiz bakiye"),
			@ApiResponse(responseCode = "404", description = "Hesap bulunamadı") })
	public ResponseEntity<AccountTransactionDto> withdraw(@PathVariable @Parameter(description = "İşlem yapılacak hesap id") UUID accountId,
			@Valid @RequestBody TransactionRequestDto request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(accountTransactionService.withdraw(accountId, request));
	}

	@GetMapping("/{accountId}")
	@Operation(summary = "Hesap id ile tüm işlemlerini getir",
			   description = "Belirli bir hesap için geçmişte yapılan tüm para yatırma/çekme işlemlerini getirir.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "İşlemler başarıyla getirildi"),
			@ApiResponse(responseCode = "404", description = "Hesap bulunamadı") })
	public ResponseEntity<List<AccountTransactionDto>> getTransactionsByAccountId(
			@PathVariable @Parameter(description = "İşlem yapılacak hesap id") UUID accountId) {
		return ResponseEntity.ok(accountTransactionService.getTransactionsByAccountId(accountId));
	}

	@PostMapping("/search")
	public ResponseEntity<Page<AccountTransactionDto>> searchTransactions(@RequestBody AccountTransactionSearchRequest request) {
		return ResponseEntity.ok(accountTransactionService.searchTransactions(request));
	}
}
