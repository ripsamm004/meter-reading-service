package com.scottishpower.smartmeter.service;

import com.scottishpower.smartmeter.exception.BadRequest;
import com.scottishpower.smartmeter.exception.Exceptions;
import com.scottishpower.smartmeter.exception.NotFound;
import com.scottishpower.smartmeter.repository.AccountRepository;
import com.scottishpower.smartmeter.repository.MeterReadRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import com.scottishpower.smartmeter.exception.DuplicateResource;
import com.scottishpower.smartmeter.models.dto.MeterReadRequest;
import com.scottishpower.smartmeter.models.entities.Account;
import com.scottishpower.smartmeter.models.entities.MeterRead;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MeterReadServiceIntegrationTest {

    public static final String ACCOUNT_NUMBER = "20000";
    @Autowired
    private MeterReadService meterReadService;

    @Autowired
    private MeterReadRepository meterReadRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testSaveMeterReads() {
        String accountNumber = ACCOUNT_NUMBER+"1";
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        accountRepository.save(account);

        MeterReadRequest meterReadRequest = new MeterReadRequest();
        meterReadRequest.setAccountNumber(accountNumber);
        meterReadRequest.setReadDate(LocalDate.now());
        meterReadRequest.setReading(100.0);
        meterReadRequest.setType("GAS");

        List<MeterRead> savedMeterReads = meterReadService.saveMeterReads(Collections.singletonList(meterReadRequest));

        assertNotNull(savedMeterReads);
        assertEquals(1, savedMeterReads.size());
        MeterRead savedMeterRead = savedMeterReads.get(0);
        assertEquals(meterReadRequest.getAccountNumber(), savedMeterRead.getAccount().getAccountNumber());
        assertEquals(meterReadRequest.getReadDate(), savedMeterRead.getReadDate());
        assertEquals(meterReadRequest.getReading(), savedMeterRead.getReading());
        assertEquals(meterReadRequest.getType(), savedMeterRead.getType());
    }

    @Test
    public void testSaveMeterReads_EmptyRequest() {
        Exception exception = assertThrows(BadRequest.class, () -> {
            meterReadService.saveMeterReads(Collections.emptyList());
        });
        Assertions.assertEquals(Exceptions.INVALID_METER_READING_EMPTY, exception);
    }

    @Test
    public void testSaveMeterReads_DuplicateRead() {
        String accountNumber = ACCOUNT_NUMBER+"2";
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        accountRepository.save(account);

        MeterReadRequest meterReadRequest = new MeterReadRequest();
        meterReadRequest.setAccountNumber(accountNumber);
        meterReadRequest.setReadDate(LocalDate.now());
        meterReadRequest.setReading(100.0);
        meterReadRequest.setType("GAS");

        MeterRead existingRead = new MeterRead();
        existingRead.setAccount(account);
        existingRead.setReadDate(meterReadRequest.getReadDate());
        existingRead.setReading(meterReadRequest.getReading());
        existingRead.setType(meterReadRequest.getType());
        meterReadRepository.save(existingRead);

        Exception exception = assertThrows(DuplicateResource.class, () -> {
            meterReadService.saveMeterReads(Collections.singletonList(meterReadRequest));
        });

        assertEquals(Exceptions.INVALID_METER_READING_DUPLICATE, exception);
    }

    @Test
    public void testSaveMeterReads_PastDate() {
        String accountNumber = ACCOUNT_NUMBER+"3";
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        accountRepository.save(account);

        MeterReadRequest meterReadRequest = new MeterReadRequest();
        meterReadRequest.setAccountNumber(accountNumber);
        meterReadRequest.setReadDate(LocalDate.now());
        meterReadRequest.setReading(100.0);
        meterReadRequest.setType("GAS");

        MeterRead futureRead = new MeterRead();
        futureRead.setAccount(account);
        futureRead.setReadDate(meterReadRequest.getReadDate().plusDays(1));
        futureRead.setReading(50.0);
        futureRead.setType(meterReadRequest.getType());
        meterReadRepository.save(futureRead);

        Exception exception = assertThrows(BadRequest.class, () -> {
            meterReadService.saveMeterReads(Collections.singletonList(meterReadRequest));
        });

        Assertions.assertEquals(Exceptions.INVALID_METER_READING_PAST_DATE, exception);
    }

    @Test
    public void testSaveMeterReads_LowerReading() {
        String accountNumber = ACCOUNT_NUMBER+"4";
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        accountRepository.save(account);

        MeterReadRequest meterReadRequest = new MeterReadRequest();
        meterReadRequest.setAccountNumber(accountNumber);
        meterReadRequest.setReadDate(LocalDate.now());
        meterReadRequest.setReading(100.0);
        meterReadRequest.setType("GAS");

        MeterRead higherRead = new MeterRead();
        higherRead.setAccount(account);
        higherRead.setReadDate(meterReadRequest.getReadDate().minusDays(1));
        higherRead.setReading(150.0);
        higherRead.setType(meterReadRequest.getType());
        meterReadRepository.save(higherRead);

        Exception exception = assertThrows(BadRequest.class, () -> {
            meterReadService.saveMeterReads(Collections.singletonList(meterReadRequest));
        });

        Assertions.assertEquals(Exceptions.INVALID_METER_READING_LOWER, exception);
    }

    @Test
    public void testGetMeterReadsByAccountNumberAndType() {
        String accountNumber = ACCOUNT_NUMBER+"5";
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        accountRepository.save(account);

        MeterReadRequest meterReadRequest = new MeterReadRequest();
        meterReadRequest.setAccountNumber(accountNumber);
        meterReadRequest.setReadDate(LocalDate.now());
        meterReadRequest.setReading(100.0);
        meterReadRequest.setType("GAS");

        MeterRead meterRead = new MeterRead();
        meterRead.setAccount(account);
        meterRead.setReadDate(meterReadRequest.getReadDate());
        meterRead.setReading(meterReadRequest.getReading());
        meterRead.setType(meterReadRequest.getType());
        meterReadRepository.save(meterRead);

        List<MeterRead> meterReads = meterReadService.getMeterReadsByAccountNumberAndType(accountNumber, "GAS");

        assertNotNull(meterReads);
        assertEquals(1, meterReads.size());
        MeterRead retrievedMeterRead = meterReads.get(0);
        assertEquals(meterRead.getAccount().getAccountNumber(), retrievedMeterRead.getAccount().getAccountNumber());
        assertEquals(meterRead.getReadDate(), retrievedMeterRead.getReadDate());
        assertEquals(meterRead.getReading(), retrievedMeterRead.getReading());
        assertEquals(meterRead.getType(), retrievedMeterRead.getType());
    }

    @Test
    public void testGetMeterReadsByAccountNumberAndType_AccountNotExist() {
        String accountNumber = ACCOUNT_NUMBER+"6";
        Exception exception = assertThrows(NotFound.class, () -> {
            meterReadService.getMeterReadsByAccountNumberAndType(accountNumber, "GAS");
        });

        Assertions.assertEquals(Exceptions.API_ERROR_ACCOUNT_NOT_FOUND, exception);
    }
}
