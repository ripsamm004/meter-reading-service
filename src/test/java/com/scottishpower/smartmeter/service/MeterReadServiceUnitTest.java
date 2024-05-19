package com.scottishpower.smartmeter.service;

import com.scottishpower.smartmeter.exception.BadRequest;
import com.scottishpower.smartmeter.exception.Exceptions;
import com.scottishpower.smartmeter.exception.NotFound;
import com.scottishpower.smartmeter.repository.MeterReadRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.scottishpower.smartmeter.exception.DuplicateResource;
import com.scottishpower.smartmeter.models.dto.MeterReadRequest;
import com.scottishpower.smartmeter.models.entities.Account;
import com.scottishpower.smartmeter.models.entities.MeterRead;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeterReadServiceUnitTest {

    @InjectMocks
    private MeterReadService meterReadService;

    @Mock
    private MeterReadRepository meterReadRepository;

    @Mock
    private AccountService accountService;

    private Account account;
    private MeterReadRequest meterReadRequest;
    private MeterRead meterRead;

    @BeforeEach
    public void setUp() {
        account = new Account();
        account.setAccountNumber("12345");

        meterReadRequest = new MeterReadRequest();
        meterReadRequest.setAccountNumber("12345");
        meterReadRequest.setReadDate(LocalDate.now());
        meterReadRequest.setReading(100.0);
        meterReadRequest.setType("GAS");

        meterRead = new MeterRead();
        meterRead.setAccount(account);
        meterRead.setReadDate(meterReadRequest.getReadDate());
        meterRead.setReading(meterReadRequest.getReading());
        meterRead.setType(meterReadRequest.getType());
    }

    @Test
    public void testSaveMeterReads() {
        when(meterReadRepository.saveAll(any())).thenReturn(Collections.singletonList(meterRead));
        when(accountService.getAccountByNumber("12345")).thenReturn(Optional.of(account));

        List<MeterRead> savedMeterReads = meterReadService.saveMeterReads(Collections.singletonList(meterReadRequest));

        verify(accountService).getAccountByNumber("12345");
        verify(meterReadRepository).saveAll(any());
        assertNotNull(savedMeterReads);
        assertEquals(1, savedMeterReads.size());
        assertEquals(meterRead.getReading(), savedMeterReads.get(0).getReading());
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
        when(meterReadRepository.findByAccountAccountNumberAndType("12345", "GAS"))
                .thenReturn(Collections.singletonList(meterRead));
        when(accountService.getAccountByNumber("12345")).thenReturn(Optional.of(account));

        Exception exception = assertThrows(DuplicateResource.class, () -> {
            meterReadService.saveMeterReads(Collections.singletonList(meterReadRequest));
        });

        assertEquals(Exceptions.INVALID_METER_READING_DUPLICATE, exception);
    }

    @Test
    public void testSaveMeterReads_PastDate() {
        MeterRead pastMeterRead = new MeterRead();
        pastMeterRead.setAccount(account);
        pastMeterRead.setReadDate(meterReadRequest.getReadDate().plusDays(1));
        pastMeterRead.setReading(50.0);
        pastMeterRead.setType("GAS");

        when(meterReadRepository.findByAccountAccountNumberAndType("12345", "GAS"))
                .thenReturn(Collections.singletonList(pastMeterRead));
        when(accountService.getAccountByNumber("12345")).thenReturn(Optional.of(account));
        Exception exception = assertThrows(BadRequest.class, () -> {
            meterReadService.saveMeterReads(Collections.singletonList(meterReadRequest));
        });

        Assertions.assertEquals(Exceptions.INVALID_METER_READING_PAST_DATE, exception);
    }

    @Test
    public void testSaveMeterReads_LowerReading() {
        MeterRead higherMeterRead = new MeterRead();
        higherMeterRead.setAccount(account);
        higherMeterRead.setReadDate(meterReadRequest.getReadDate().minusDays(1));
        higherMeterRead.setReading(150.0);
        higherMeterRead.setType("GAS");

        when(meterReadRepository.findByAccountAccountNumberAndType("12345", "GAS"))
                .thenReturn(Collections.singletonList(higherMeterRead));
        when(accountService.getAccountByNumber("12345")).thenReturn(Optional.of(account));
        Exception exception = assertThrows(BadRequest.class, () -> {
            meterReadService.saveMeterReads(Collections.singletonList(meterReadRequest));
        });

        Assertions.assertEquals(Exceptions.INVALID_METER_READING_LOWER, exception);
    }

    @Test
    public void testGetMeterReadsByAccountNumberAndType() {
        when(meterReadRepository.findByAccountAccountNumberAndType("12345", "GAS"))
                .thenReturn(Collections.singletonList(meterRead));
        when(accountService.getAccountByNumber("12345")).thenReturn(Optional.of(account));
        List<MeterRead> meterReads = meterReadService.getMeterReadsByAccountNumberAndType("12345", "GAS");

        assertNotNull(meterReads);
        assertEquals(1, meterReads.size());
        assertEquals(meterRead.getReading(), meterReads.get(0).getReading());
    }

    @Test
    public void testGetMeterReadsByAccountNumberAndType_AccountNotExist() {
        when(accountService.getAccountByNumber("12345")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFound.class, () -> {
            meterReadService.getMeterReadsByAccountNumberAndType("12345", "GAS");
        });

        Assertions.assertEquals(Exceptions.API_ERROR_ACCOUNT_NOT_FOUND, exception);
    }
}
