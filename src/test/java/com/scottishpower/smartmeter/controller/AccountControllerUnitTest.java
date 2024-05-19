package com.scottishpower.smartmeter.controller;

import com.scottishpower.smartmeter.exception.Exceptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.scottishpower.smartmeter.models.dto.AccountDTO;
import com.scottishpower.smartmeter.models.entities.Account;
import com.scottishpower.smartmeter.service.AccountService;
import com.scottishpower.smartmeter.enums.Error;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class AccountControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    private static final String BASE_URL = "/api/accounts";
    private static final String AUTH_USER = "user";
    private static final String ACCOUNT_NUMBER = "12345";
    private static final String ACCOUNT_JSON = "{\"accountNumber\":\"" + ACCOUNT_NUMBER + "\"}";

    @Test
    @WithMockUser(username = AUTH_USER, roles = "USER")
    public void testCreateAccount() throws Exception {
        AccountDTO accountDTO = createAccountDTO(ACCOUNT_NUMBER);
        Account account = createAccount(ACCOUNT_NUMBER);

        when(accountService.saveAccount(any(Account.class))).thenReturn(account);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ACCOUNT_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().json(ACCOUNT_JSON));
    }

    @Test
    @WithMockUser(username = AUTH_USER, roles = "USER")
    public void testCreateAccount_AlreadyExists() throws Exception {
        when(accountService.saveAccount(any())).thenThrow(Exceptions.API_ERROR_ACCOUNT_ALREADY_EXIST);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ACCOUNT_JSON))
            .andExpect(status().isConflict())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code", is(Error.API_ERROR_ACCOUNT_ALREADY_EXIST.getCode())))
            .andExpect(jsonPath("$.message", is(Error.API_ERROR_ACCOUNT_ALREADY_EXIST.getMessage())));
    }

    @Test
    @WithMockUser(username = AUTH_USER, roles = "USER")
    public void testCreateAccount_InvalidPayload() throws Exception {
        String invalidJson = "{\"accountssNumber\":\"12345\"}";

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code", is(Error.BAD_REQUEST.getCode())));
    }

    private AccountDTO createAccountDTO(String accountNumber) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber(accountNumber);
        return accountDTO;
    }

    private Account createAccount(String accountNumber) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        return account;
    }
}
