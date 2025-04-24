package com.banking.bankingsystem.api;

import com.banking.bankingsystem.account.data.AccountType;
import com.banking.bankingsystem.account.dto.CreateAccountRequestDto;
import com.banking.bankingsystem.account.dto.UpdateAccountRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
public class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static UUID createdAccountId;

	private CreateAccountRequestDto createRequest;

	private UpdateAccountRequestDto updateRequest;

	@BeforeEach
	void setUp() {
		createRequest = new CreateAccountRequestDto();
		createRequest.setIdentityNo(BigDecimal.valueOf(12345678901L));
		createRequest.setFirstName("first name");
		createRequest.setLastName("last name");
		createRequest.setAccountType(AccountType.TL);

		updateRequest = new UpdateAccountRequestDto();
		updateRequest.setFirstName("updated first name");
		updateRequest.setLastName("updated last name");
	}

	@Test
	@Order(1)
	void shouldCreateAccount() throws Exception {
		MvcResult result = mockMvc
				.perform(post("/api/v1/accounts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andReturn();

		createdAccountId = UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
	}

	@Test
	@Order(2)
	void shouldReturnBadRequestWhenDuplicateAccountIsCreated() throws Exception {
		mockMvc
				.perform(post("/api/v1/accounts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createRequest)))
				.andExpect(status().isBadRequest());
		;
	}

	@Test
	@Order(3)
	void shouldReturnBadRequestWhenMissingRequiredFields() throws Exception {
		CreateAccountRequestDto request = new CreateAccountRequestDto();
		request.setFirstName("first name");

		mockMvc
				.perform(post("/api/v1/accounts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(4)
	void shouldGetAllAccounts() throws Exception {
		mockMvc.perform(get("/api/v1/accounts")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	@Order(5)
	void shouldGetAccountById() throws Exception {
		mockMvc
				.perform(get("/api/v1/accounts/{id}", createdAccountId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value("first name"));
	}

	@Test
	@Order(6)
	void shouldReturnNotFoundForInvalidAccountId() throws Exception {
		UUID fakeId = UUID.randomUUID();

		mockMvc.perform(get("/api/v1/accounts/{id}", fakeId)).andExpect(status().isNotFound());
	}

	@Test
	@Order(7)
	void shouldUpdateAccount() throws Exception {
		mockMvc
				.perform(put("/api/v1/accounts/{id}", createdAccountId)
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value("updated first name"));
	}

	@Test
	@Order(8)
	void shouldReturnNotFoundWhenUpdatingNonExistentAccount() throws Exception {
		mockMvc
				.perform(put("/api/v1/accounts/{id}", UUID.randomUUID())
								 .contentType(MediaType.APPLICATION_JSON)
								 .content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(9)
	void shouldDeleteAccount() throws Exception {
		mockMvc.perform(delete("/api/v1/accounts/{id}", createdAccountId)).andExpect(status().isNoContent());
	}

	@Test
	@Order(10)
	void shouldReturnNotFoundWhenDeletingNonExistentAccount() throws Exception {
		mockMvc.perform(delete("/api/v1/accounts/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
	}
}