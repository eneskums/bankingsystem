package com.banking.bankingsystem.api;

import com.banking.bankingsystem.account.data.Account;
import com.banking.bankingsystem.account.data.AccountType;
import com.banking.bankingsystem.account.repository.AccountRepository;
import com.banking.bankingsystem.transaction.data.TransactionType;
import com.banking.bankingsystem.transaction.dto.AccountTransactionSearchRequest;
import com.banking.bankingsystem.transaction.dto.TransactionRequestDto;
import com.banking.bankingsystem.transaction.repository.AccountTransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created on April, 2025
 *
 * @author Enes Kumaş
 */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountTransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountTransactionRepository transactionRepository;

	private UUID testAccountId;

	private AccountTransactionSearchRequest searchRequest;

	@BeforeEach
	void setUp() {
		transactionRepository.deleteAll();
		accountRepository.deleteAll();

		Account account = new Account();
		account.setFirstName("Test");
		account.setLastName("User");
		account.setBalance(BigDecimal.valueOf(5000));
		account.setIdentityNo(BigDecimal.valueOf(12345678901L));
		account.setAccountType(AccountType.TL);
		testAccountId = accountRepository.save(account).getId();

		searchRequest = new AccountTransactionSearchRequest();
		searchRequest.setAccountId(null);
		searchRequest.setFromDate(LocalDateTime.now().minusDays(1));
		searchRequest.setToDate(LocalDateTime.now().plusDays(1));
		searchRequest.setTransactionType(TransactionType.DEPOSIT);
		searchRequest.setMinAmount(BigDecimal.valueOf(100));
		searchRequest.setMaxAmount(BigDecimal.valueOf(1500));
	}

	@AfterEach
	void tearDown() {
		transactionRepository.deleteAll();
		accountRepository.deleteAll();
	}

	@Test
	@Order(1)
	void testDepositSuccess() throws Exception {
		TransactionRequestDto request = new TransactionRequestDto();
		request.setAmount(BigDecimal.valueOf(500));

		mockMvc
				.perform(post("/api/v1/transactions/{accountId}/deposit", testAccountId)
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.transactionType", is("DEPOSIT")))
				.andExpect(jsonPath("$.amount", is(500)));
	}

	@Test
	@Order(2)
	void testWithdrawSuccess() throws Exception {
		TransactionRequestDto request = new TransactionRequestDto();
		request.setAmount(BigDecimal.valueOf(1000));

		mockMvc
				.perform(post("/api/v1/transactions/{accountId}/withdraw", testAccountId)
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.transactionType", is("WITHDRAW")))
				.andExpect(jsonPath("$.amount", is(1000)));
	}

	@Test
	@Order(3)
	void testWithdrawFail_InsufficientBalance() throws Exception {
		TransactionRequestDto request = new TransactionRequestDto();
		request.setAmount(BigDecimal.valueOf(10_000));

		mockMvc
				.perform(post("/api/v1/transactions/{accountId}/withdraw", testAccountId)
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(4)
	void testGetTransactionsByAccountId() throws Exception {
		TransactionRequestDto request = new TransactionRequestDto();
		request.setAmount(BigDecimal.valueOf(1000));

		mockMvc
				.perform(post("/api/v1/transactions/{accountId}/deposit", testAccountId)
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());

		mockMvc
				.perform(get("/api/v1/transactions/{accountId}", testAccountId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
				.andExpect(jsonPath("$[0].transactionType", is("DEPOSIT")));
	}

	@Test
	@Order(5)
	void testDepositFail_AccountNotFound() throws Exception {
		TransactionRequestDto request = new TransactionRequestDto();
		request.setAmount(BigDecimal.valueOf(100));

		mockMvc
				.perform(post("/api/v1/transactions/{accountId}/deposit", UUID.randomUUID())
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(6)
	void testSearchTransactions() throws Exception {
		TransactionRequestDto request = new TransactionRequestDto();
		request.setAmount(BigDecimal.valueOf(500));

		mockMvc
				.perform(post("/api/v1/transactions/{accountId}/deposit", testAccountId)
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());

		mockMvc
				.perform(post("/api/v1/transactions/search")
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(searchRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content[0].transactionType").value("DEPOSIT"))
				.andExpect(jsonPath("$.content[0].amount").value(500));
	}

	@Test
	@Order(7)
	void testSearchTransactions_ShouldThrowException_WhenFromDateIsAfterToDate() throws Exception {
		searchRequest.setFromDate(LocalDateTime.now().plusDays(1));
		searchRequest.setToDate(LocalDateTime.now());

		mockMvc
				.perform(post("/api/v1/transactions/search")
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(searchRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(8)
	void testSearchTransactions_ShouldThrowException_WhenMinAmountGreaterThanMaxAmount() throws Exception {
		searchRequest.setMinAmount(BigDecimal.valueOf(2000));
		searchRequest.setMaxAmount(BigDecimal.valueOf(1500));

		mockMvc
				.perform(post("/api/v1/transactions/search")
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(searchRequest)))
				.andExpect(status().isBadRequest());
	}
}