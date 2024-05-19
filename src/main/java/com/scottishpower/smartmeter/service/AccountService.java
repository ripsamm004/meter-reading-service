package com.scottishpower.smartmeter.service;

import com.scottishpower.smartmeter.exception.Exceptions;
import com.scottishpower.smartmeter.repository.AccountRepository;
import org.springframework.stereotype.Service;
import com.scottishpower.smartmeter.models.entities.Account;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Account saveAccount(Account account) {
        if (accountRepository.findByAccountNumber(account.getAccountNumber()).isPresent()) {
            throw Exceptions.API_ERROR_ACCOUNT_ALREADY_EXIST;
        }
        return accountRepository.save(account);
    }
}
