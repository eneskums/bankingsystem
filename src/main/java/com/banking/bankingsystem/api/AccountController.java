package com.banking.bankingsystem.api;

import com.banking.bankingsystem.account.dto.AccountDto;
import com.banking.bankingsystem.account.dto.CreateAccountRequestDto;
import com.banking.bankingsystem.account.dto.UpdateAccountRequestDto;
import com.banking.bankingsystem.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Controller", description = "Hesap yönetimi işlemleri")
public class AccountController {

	private final AccountService accountService;

	@Operation(summary = "Hesap oluştur", description = "Kullanıcının kimlik numarası, adı, soyadı ve hesap türü ile yeni hesap oluşturur")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Hesap başarıyla oluşturuldu"),
			@ApiResponse(responseCode = "400", description = "Geçersiz giriş verisi") })
	@PostMapping
	public ResponseEntity<AccountDto> createAccount(
			@RequestBody @Valid @Parameter(description = "Oluşturulacak hesap verisi") CreateAccountRequestDto request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
	}

	@Operation(summary = "Tüm hesapları getir", description = "Sistemdeki tüm hesapları listeler")
	@GetMapping
	public ResponseEntity<List<AccountDto>> getAllAccounts() {
		return ResponseEntity.ok(accountService.getAllAccounts());
	}

	@Operation(summary = "Id ile hesap getir", description = "Belirtilen id'ye sahip hesabı getirir")
	@GetMapping("/{id}")
	public ResponseEntity<AccountDto> getAccountById(@PathVariable @Parameter(description = "Görüntülenecek hesap id") UUID id) {
		return ResponseEntity.ok(accountService.getAccountById(id));
	}

	@Operation(summary = "Hesap güncelle", description = "Belirtilen id'ye sahip hesabın ad ve soyad bilgilerini günceller")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Hesap başarıyla güncellendi"),
			@ApiResponse(responseCode = "404", description = "Hesap bulunamadı"),
			@ApiResponse(responseCode = "400", description = "Geçersiz giriş verisi") })
	@PutMapping("/{id}")
	public ResponseEntity<AccountDto> updateAccount(@PathVariable @Parameter(description = "Güncellenecek hesap id") UUID id,
			@RequestBody @Valid @Parameter(description = "Güncelleme verileri") UpdateAccountRequestDto request) {
		return ResponseEntity.ok(accountService.updateAccount(id, request));
	}

	@Operation(summary = "Hesap sil", description = "Belirtilen id'ye sahip hesabı siler")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAccountById(@PathVariable @Parameter(description = "Silinecek hesap id") UUID id) {
		accountService.deleteAccountById(id);
		return ResponseEntity.noContent().build();
	}
}
