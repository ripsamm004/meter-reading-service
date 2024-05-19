package com.scottishpower.smartmeter.controller;

import com.scottishpower.smartmeter.exception.ErrorDetails;
import com.scottishpower.smartmeter.exception.Exceptions;
import com.scottishpower.smartmeter.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.scottishpower.smartmeter.models.dto.AccountDTO;
import com.scottishpower.smartmeter.models.entities.Account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AccountControllerIntegrationTest {

    private static final String BASE_ACCOUNT_NUMBER = "30000";
    private static final String AUTH_USER = "user";
    private static final String AUTH_PASSWORD = "password";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountRepository accountRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/accounts";
    }

    @Test
    public void testCreateAccount() {
        String accountNumber = generateUniqueAccountNumber();
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber(accountNumber);

        HttpEntity<AccountDTO> request = createHttpEntity(accountDTO);

        ResponseEntity<Account> response = restTemplate.withBasicAuth(AUTH_USER, AUTH_PASSWORD)
            .postForEntity(getBaseUrl(), request, Account.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Account responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(accountNumber, responseBody.getAccountNumber());

        // Verify that the account is saved in the database
        Account savedAccount = accountRepository.findByAccountNumber(accountNumber).orElse(null);
        assertNotNull(savedAccount);
        assertEquals(accountNumber, savedAccount.getAccountNumber());
    }

    @Test
    public void testCreateAccount_AlreadyExists() {
        String accountNumber = generateUniqueAccountNumber();
        Account account = createAndSaveAccount(accountNumber);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber(accountNumber);

        HttpEntity<AccountDTO> request = createHttpEntity(accountDTO);

        ResponseEntity<ErrorDetails> response = restTemplate.withBasicAuth(AUTH_USER, AUTH_PASSWORD)
            .postForEntity(getBaseUrl(), request, ErrorDetails.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorDetails responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(Exceptions.API_ERROR_ACCOUNT_ALREADY_EXIST.getApiError().getCode(), responseBody.getCode());
        assertEquals(Exceptions.API_ERROR_ACCOUNT_ALREADY_EXIST.getApiError().getMessage(), responseBody.getMessage());
    }

    private String generateUniqueAccountNumber() {
        return BASE_ACCOUNT_NUMBER + System.currentTimeMillis();
    }

    private Account createAndSaveAccount(String accountNumber) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        return accountRepository.save(account);
    }

    private HttpEntity<AccountDTO> createHttpEntity(AccountDTO accountDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(accountDTO, headers);
    }
}
