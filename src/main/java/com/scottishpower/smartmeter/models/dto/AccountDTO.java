package com.scottishpower.smartmeter.models.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountDTO {

    private Long id;

    @NotEmpty(message = "Account number is required")
    @Size(max = 50, message = "Account number must be at most 50 characters long")
    private String accountNumber;

    public AccountDTO(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
