package com.scottishpower.smartmeter.service;

import com.scottishpower.smartmeter.exception.Exceptions;
import com.scottishpower.smartmeter.repository.MeterReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.scottishpower.smartmeter.models.dto.MeterReadRequest;
import com.scottishpower.smartmeter.models.entities.Account;
import com.scottishpower.smartmeter.models.entities.MeterRead;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class MeterReadService {

    private final MeterReadRepository meterReadRepository;
    private final AccountService accountService;

    public MeterReadService(MeterReadRepository meterReadRepository, AccountService accountService) {
        this.meterReadRepository = meterReadRepository;
        this.accountService = accountService;
    }

    @Transactional
    public List<MeterRead> saveMeterReads(List<MeterReadRequest> meterReadRequests) {
        if (meterReadRequests.isEmpty()) {
            throw Exceptions.INVALID_METER_READING_EMPTY;
        }

        List<MeterRead> meterReads = meterReadRequests.stream()
            .map(this::processMeterReadRequest)
            .collect(Collectors.toList());

        return meterReadRepository.saveAll(meterReads);
    }

    private MeterRead processMeterReadRequest(MeterReadRequest request) {
        Account account = accountService.getAccountByNumber(request.getAccountNumber()).get();
        validateMeterRead(request, account);
        MeterRead meterRead = request.toMeterRead();
        meterRead.setAccount(account);
        return meterRead;
    }

    private void validateMeterRead(MeterReadRequest request, Account account) {
        List<MeterRead> existingReads = meterReadRepository.findByAccountAccountNumberAndType(account.getAccountNumber(), request.getType());

        // Check for duplicate readings with the same date and reading value
        boolean duplicateExists = existingReads.stream()
            .anyMatch(read -> read.getReadDate().isEqual(request.getReadDate())
                && read.getReading().equals(request.getReading()));
        if (duplicateExists) {
            throw Exceptions.INVALID_METER_READING_DUPLICATE;
        }

        // Check for valid dates (not past dates)
        boolean pastDateExists = existingReads.stream()
            .anyMatch(read -> read.getReadDate().isAfter(request.getReadDate()));
        if (pastDateExists) {
            throw Exceptions.INVALID_METER_READING_PAST_DATE;
        }

        // Find the maximum reading on or before the requested date
        Optional<MeterRead> latestRead = existingReads.stream()
            .filter(read -> !read.getReadDate().isAfter(request.getReadDate())) // Filter to include only readings on or before the request date
            .max(Comparator.comparing(MeterRead::getReadDate) // First compare by date
                .thenComparing(MeterRead::getReading)); // Then compare by reading

        // Check if the new reading is not lower than or equal to the latest reading on or before the requested date
        if (latestRead.isPresent() && request.getReading() <= latestRead.get().getReading()) {
            throw Exceptions.INVALID_METER_READING_LOWER;
        }
    }

    public List<MeterRead> getMeterReadsByAccountNumberAndType(String accountNumber, String type) {
        Account account = accountService.getAccountByNumber(accountNumber)
            .orElseThrow(() -> Exceptions.API_ERROR_ACCOUNT_NOT_FOUND);
        List<MeterRead> reads = meterReadRepository.findByAccountAccountNumberAndType(accountNumber, type);
        calculateAdditionalFields(reads);
        return reads;
    }

    private void calculateAdditionalFields(List<MeterRead> reads) {
        reads.sort(Comparator.comparing(MeterRead::getReadDate)); // Ensure reads are sorted by date

        double totalUsage = 0;
        long totalDays = 0;

        for (int i = 1; i < reads.size(); i++) {
            MeterRead currentRead = reads.get(i);
            MeterRead previousRead = reads.get(i - 1);

            double usageSinceLastRead = currentRead.getReading() - previousRead.getReading();
            long daysSinceLastRead = ChronoUnit.DAYS.between(previousRead.getReadDate(), currentRead.getReadDate());

            currentRead.setUsageSinceLastRead(usageSinceLastRead);
            currentRead.setPeriodSinceLastRead(daysSinceLastRead);

            totalUsage += usageSinceLastRead;
            totalDays += daysSinceLastRead;
        }

        double avgDailyUsage = totalDays > 0 ? totalUsage / totalDays : 0;
        reads.forEach(read -> read.setAvgDailyUsage(avgDailyUsage));
    }

}
