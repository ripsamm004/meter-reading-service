package com.scottishpower.smartmeter.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.scottishpower.smartmeter.models.entities.Account;
import com.scottishpower.smartmeter.service.AccountService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountValidatorTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountValidator accountValidator;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_whenAccountNumberIsNull_thenReturnTrue() {
        boolean isValid = accountValidator.isValid(null, null);
        assertTrue(isValid);
    }

    @Test
    void isValid_whenAccountExists_thenReturnTrue() {
        when(accountService.getAccountByNumber(anyString()))
            .thenReturn(Optional.of(new Account()));

        boolean isValid = accountValidator.isValid("validAccountNumber", null);
        assertTrue(isValid);
    }

    @Test
    void isValid_whenAccountDoesNotExist_thenReturnFalse() {
        when(accountService.getAccountByNumber(anyString()))
            .thenReturn(Optional.empty());

        boolean isValid = accountValidator.isValid("invalidAccountNumber", null);
        assertFalse(isValid);
    }
}
