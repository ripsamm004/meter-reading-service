package com.scottishpower.smartmeter.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.scottishpower.smartmeter.service.AccountService;

@Component
public class AccountValidator implements ConstraintValidator<ValidAccount, String> {

    @Autowired
    private AccountService accountService;

    @Override
    public boolean isValid(String accountNumber, ConstraintValidatorContext context) {
        if (accountNumber != null) {
            return accountService.getAccountByNumber(accountNumber).isPresent();
        }
        return true; // Assuming null account number is handled by @NotEmpty
    }
}
