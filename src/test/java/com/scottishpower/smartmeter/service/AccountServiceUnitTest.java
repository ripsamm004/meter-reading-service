package com.scottishpower.smartmeter.service;

import com.scottishpower.smartmeter.exception.Exceptions;
import com.scottishpower.smartmeter.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.scottishpower.smartmeter.exception.DuplicateResource;
import com.scottishpower.smartmeter.models.entities.Account;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceUnitTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        accountService = new AccountService(accountRepository);
    }

    @Test
    public void testSaveAccount() {
        Account account = new Account();
        account.setAccountNumber("12345");

        when(accountRepository.findByAccountNumber(any(String.class))).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account savedAccount = accountService.saveAccount(account);

        verify(accountRepository).findByAccountNumber("12345");
        verify(accountRepository).save(account);
        assertNotNull(savedAccount);
        assertEquals("12345", savedAccount.getAccountNumber());
    }

    @Test
    public void testSaveAccount_AlreadyExists() {
        Account account = new Account();
        account.setAccountNumber("12345");

        when(accountRepository.findByAccountNumber(any(String.class))).thenReturn(Optional.of(account));

        Exception exception = assertThrows(DuplicateResource.class, () -> {
            accountService.saveAccount(account);
        });

        verify(accountRepository).findByAccountNumber("12345");
        assertEquals(Exceptions.API_ERROR_ACCOUNT_ALREADY_EXIST, exception);
    }

    @Test
    public void testGetAccountByNumber() {
        Account account = new Account();
        account.setAccountNumber("12345");

        when(accountRepository.findByAccountNumber(any(String.class))).thenReturn(Optional.of(account));

        Optional<Account> foundAccount = accountService.getAccountByNumber("12345");

        verify(accountRepository).findByAccountNumber("12345");
        assertTrue(foundAccount.isPresent());
        assertEquals("12345", foundAccount.get().getAccountNumber());
    }

    @Test
    public void testGetAccountByNumber_NotFound() {
        when(accountRepository.findByAccountNumber(any(String.class))).thenReturn(Optional.empty());

        Optional<Account> foundAccount = accountService.getAccountByNumber("12345");

        verify(accountRepository).findByAccountNumber("12345");
        assertFalse(foundAccount.isPresent());
    }
}
