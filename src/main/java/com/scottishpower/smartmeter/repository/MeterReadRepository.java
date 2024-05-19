package com.scottishpower.smartmeter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.scottishpower.smartmeter.models.entities.MeterRead;

import java.util.List;

public interface MeterReadRepository extends JpaRepository<MeterRead, Long> {
    List<MeterRead> findByAccountAccountNumberAndType(String accountNumber, String type);
}
