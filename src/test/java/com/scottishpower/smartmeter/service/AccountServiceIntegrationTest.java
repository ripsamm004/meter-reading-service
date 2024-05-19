package com.scottishpower.smartmeter.service;

import com.scottishpower.smartmeter.exception.Exceptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import com.scottishpower.smartmeter.exception.DuplicateResource;
import com.scottishpower.smartmeter.models.entities.Account;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AccountServiceIntegrationTest {

    public static final String ACCOUNT_NUMBER = "10000";

    @Autowired
    private AccountService accountService;

    @Test
    public void testSaveAccount() {
        String accountNumber = ACCOUNT_NUMBER + "1";
        Account account = new Account();
        account.setAccountNumber(accountNumber);

        Account savedAccount = accountService.saveAccount(account);

        assertNotNull(savedAccount);
        assertEquals(accountNumber, savedAccount.getAccountNumber());
    }

    @Test
    public void testSaveAccount_AlreadyExists() {
        String accountNumber = ACCOUNT_NUMBER + "2";
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        accountService.saveAccount(account);

        Account duplicateAccount = new Account();
        duplicateAccount.setAccountNumber(accountNumber);

        Exception exception = assertThrows(DuplicateResource.class, () -> {
            accountService.saveAccount(duplicateAccount);
        });

        assertEquals(Exceptions.API_ERROR_ACCOUNT_ALREADY_EXIST, exception);
    }

    @Test
    public void testGetAccountByNumber() {
        String accountNumber = ACCOUNT_NUMBER + "3";
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        accountService.saveAccount(account);

        Optional<Account> foundAccount = accountService.getAccountByNumber(accountNumber);

        assertTrue(foundAccount.isPresent());
        assertEquals(accountNumber, foundAccount.get().getAccountNumber());
    }

    @Test
    public void testGetAccountByNumber_NotFound() {
        String accountNumber = ACCOUNT_NUMBER + "4";
        Optional<Account> foundAccount = accountService.getAccountByNumber(accountNumber );
        assertFalse(foundAccount.isPresent());
    }
}
