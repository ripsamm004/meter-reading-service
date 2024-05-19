package com.scottishpower.smartmeter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.scottishpower.smartmeter.models.entities.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
}
