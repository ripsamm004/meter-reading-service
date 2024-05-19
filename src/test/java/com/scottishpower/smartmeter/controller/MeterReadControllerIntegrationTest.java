package com.scottishpower.smartmeter.controller;

import com.scottishpower.smartmeter.exception.ErrorDetails;
import com.scottishpower.smartmeter.exception.Exceptions;
import com.scottishpower.smartmeter.repository.AccountRepository;
import com.scottishpower.smartmeter.repository.MeterReadRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.scottishpower.smartmeter.models.dto.MeterReadRequest;
import com.scottishpower.smartmeter.models.entities.Account;
import com.scottishpower.smartmeter.models.entities.MeterRead;
import com.scottishpower.smartmeter.models.response.MeterReadResponse;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MeterReadControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MeterReadRepository meterReadRepository;

    @Autowired
    private AccountRepository accountRepository;

    private String baseUrl;

    private static final String BASE_ACCOUNT_NUMBER = "40000";

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/smart";
    }

    @Test
    public void testGetMeterReadsByAccountNumber() {
        String accountNumber = generateUniqueAccountNumber();
        Account account = createAccount(accountNumber);

        createMeterRead(account, "GAS", 100.0, LocalDate.now().minusDays(3));
        createMeterRead(account, "GAS", 150.0, LocalDate.now().minusDays(2));
        createMeterRead(account, "GAS", 200.0, LocalDate.now().minusDays(1));
        createMeterRead(account, "ELEC", 200.0, LocalDate.now().minusDays(3));
        createMeterRead(account, "ELEC", 300.0, LocalDate.now().minusDays(2));
        createMeterRead(account, "ELEC", 400.0, LocalDate.now().minusDays(1));

        ResponseEntity<MeterReadResponse> response = restTemplate.withBasicAuth("user", "password")
            .getForEntity(baseUrl + "/reads/" + accountNumber, MeterReadResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        MeterReadResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(accountNumber, responseBody.getAccountNumber());
        assertEquals(3, responseBody.getGasReadings().size());
        assertEquals(3, responseBody.getElecReadings().size());

        // Verify usageSinceLastRead and daysSinceLastRead for GAS
        MeterReadResponse.MeterReadDTO gasRead1 = responseBody.getGasReadings().get(0);
        MeterReadResponse.MeterReadDTO gasRead2 = responseBody.getGasReadings().get(1);
        MeterReadResponse.MeterReadDTO gasRead3 = responseBody.getGasReadings().get(2);

        assertNull(gasRead1.getUsageSinceLastRead());
        assertNull(gasRead1.getPeriodSinceLastRead());

        assertEquals(50.0, gasRead2.getUsageSinceLastRead());
        assertEquals(1L, gasRead2.getPeriodSinceLastRead());
        assertEquals(50.0, gasRead3.getUsageSinceLastRead());
        assertEquals(1L, gasRead3.getPeriodSinceLastRead());

        // Verify usageSinceLastRead and daysSinceLastRead for ELEC
        MeterReadResponse.MeterReadDTO elecRead1 = responseBody.getElecReadings().get(0);
        MeterReadResponse.MeterReadDTO elecRead2 = responseBody.getElecReadings().get(1);
        MeterReadResponse.MeterReadDTO elecRead3 = responseBody.getElecReadings().get(2);

        assertNull(elecRead1.getUsageSinceLastRead());
        assertNull(elecRead1.getPeriodSinceLastRead());

        assertEquals(100.0, elecRead2.getUsageSinceLastRead());
        assertEquals(1L, elecRead2.getPeriodSinceLastRead());
        assertEquals(100.0, elecRead3.getUsageSinceLastRead());
        assertEquals(1L, elecRead3.getPeriodSinceLastRead());

        // Verify avgDailyUsage for GAS
        double expectedGasAvgDailyUsage = 50.0; // Total usage 100.0 over 2 days
        assertEquals(expectedGasAvgDailyUsage, responseBody.getGasReadings().get(0).getAvgDailyUsage());
        assertEquals(expectedGasAvgDailyUsage, responseBody.getGasReadings().get(1).getAvgDailyUsage());
        assertEquals(expectedGasAvgDailyUsage, responseBody.getGasReadings().get(2).getAvgDailyUsage());

        // Verify avgDailyUsage for ELEC
        double expectedElecAvgDailyUsage = 100.0; // Total usage 200.0 over 2 days
        assertEquals(expectedElecAvgDailyUsage, responseBody.getElecReadings().get(0).getAvgDailyUsage());
        assertEquals(expectedElecAvgDailyUsage, responseBody.getElecReadings().get(1).getAvgDailyUsage());
        assertEquals(expectedElecAvgDailyUsage, responseBody.getElecReadings().get(2).getAvgDailyUsage());
    }

    @Test
    public void testGetMeterReadsByAccountNumber_AccountNotExist() {
        String accountNumber = generateUniqueAccountNumber();
        ResponseEntity<ErrorDetails> response = restTemplate.withBasicAuth("user", "password")
            .getForEntity(baseUrl + "/reads/" + accountNumber, ErrorDetails.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
            Exceptions.API_ERROR_ACCOUNT_NOT_FOUND.getApiError().getCode(), response.getBody().getCode());
        Assertions.assertEquals(Exceptions.API_ERROR_ACCOUNT_NOT_FOUND.getApiError().getMessage(), response.getBody().getMessage());
    }

    @Test
    public void testSubmitMeterRead() {
        String accountNumber = generateUniqueAccountNumber();
        createAccount(accountNumber);

        MeterReadRequest gasMeterReadRequest = createMeterReadRequest(accountNumber, 101L, "GAS", 602.0, LocalDate.of(2023, 5, 11));
        MeterReadRequest elecMeterReadRequest = createMeterReadRequest(accountNumber, 102L, "ELEC", 342.0, LocalDate.of(2023, 5, 11));

        List<MeterReadRequest> meterReadRequests = Arrays.asList(gasMeterReadRequest, elecMeterReadRequest);
        HttpEntity<List<MeterReadRequest>> request = createHttpEntity(meterReadRequests);

        ResponseEntity<MeterRead[]> response = restTemplate.withBasicAuth("user", "password")
            .postForEntity(baseUrl + "/reads", request, MeterRead[].class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);

        verifyMeterRead(accountNumber, "GAS", 602.0);
        verifyMeterRead(accountNumber, "ELEC", 342.0);
    }

    @Test
    public void testSubmitMeterRead_EmptyRequest() {
        HttpEntity<List<MeterReadRequest>> request = createHttpEntity(Collections.emptyList());

        ResponseEntity<ErrorDetails> response = restTemplate.withBasicAuth("user", "password")
            .postForEntity(baseUrl + "/reads", request, ErrorDetails.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
            Exceptions.INVALID_METER_READING_EMPTY.getApiError().getCode(), response.getBody().getCode());
        Assertions.assertEquals(Exceptions.INVALID_METER_READING_EMPTY.getApiError().getMessage(), response.getBody().getMessage());
    }

    @Test
    public void testSubmitMeterRead_DuplicateRead() {
        String accountNumber = generateUniqueAccountNumber();
        Account account = createAccount(accountNumber);

        createMeterRead(account, "GAS", 602.0, LocalDate.of(2023, 5, 11));

        MeterReadRequest gasMeterReadRequest = createMeterReadRequest(accountNumber, 101L, "GAS", 602.0, LocalDate.of(2023, 5, 11));
        HttpEntity<List<MeterReadRequest>> request = createHttpEntity(Collections.singletonList(gasMeterReadRequest));

        ResponseEntity<ErrorDetails> response = restTemplate.withBasicAuth("user", "password")
            .postForEntity(baseUrl + "/reads", request, ErrorDetails.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
            Exceptions.INVALID_METER_READING_DUPLICATE.getApiError().getCode(), response.getBody().getCode());
        assertEquals(Exceptions.INVALID_METER_READING_DUPLICATE.getApiError().getMessage(), response.getBody().getMessage());
    }

    @Test
    public void testSubmitMeterRead_PastDate() {
        String accountNumber = generateUniqueAccountNumber();
        Account account = createAccount(accountNumber);

        createMeterRead(account, "GAS", 602.0, LocalDate.of(2023, 5, 12));

        MeterReadRequest meterReadRequest = createMeterReadRequest(accountNumber, 101L, "GAS", 602.0, LocalDate.of(2023, 5, 11));
        HttpEntity<List<MeterReadRequest>> request = createHttpEntity(Collections.singletonList(meterReadRequest));

        ResponseEntity<ErrorDetails> response = restTemplate.withBasicAuth("user", "password")
            .postForEntity(baseUrl + "/reads", request, ErrorDetails.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
            Exceptions.INVALID_METER_READING_PAST_DATE.getApiError().getCode(), response.getBody().getCode());
        Assertions.assertEquals(Exceptions.INVALID_METER_READING_PAST_DATE.getApiError().getMessage(), response.getBody().getMessage());
    }

    @Test
    public void testSubmitMeterRead_LowerReading() {
        String accountNumber = generateUniqueAccountNumber();
        Account account = createAccount(accountNumber);

        createMeterRead(account, "GAS", 650.0, LocalDate.of(2023, 5, 10));

        MeterReadRequest meterReadRequest = createMeterReadRequest(accountNumber, 101L, "GAS", 602.0, LocalDate.of(2023, 5, 11));
        HttpEntity<List<MeterReadRequest>> request = createHttpEntity(Collections.singletonList(meterReadRequest));

        ResponseEntity<ErrorDetails> response = restTemplate.withBasicAuth("user", "password")
            .postForEntity(baseUrl + "/reads", request, ErrorDetails.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
            Exceptions.INVALID_METER_READING_LOWER.getApiError().getCode(), response.getBody().getCode());
        Assertions.assertEquals(Exceptions.INVALID_METER_READING_LOWER.getApiError().getMessage(), response.getBody().getMessage());
    }

    private Account createAccount(String accountNumber) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        return accountRepository.save(account);
    }

    private MeterRead createMeterRead(Account account, String type, double reading, LocalDate readDate) {
        MeterRead meterRead = new MeterRead();
        meterRead.setAccount(account);
        meterRead.setType(type);
        meterRead.setReading(reading);
        meterRead.setReadDate(readDate);
        return meterReadRepository.save(meterRead);
    }

    private MeterReadRequest createMeterReadRequest(String accountNumber, Long meterId, String type, double reading, LocalDate readDate) {
        MeterReadRequest request = new MeterReadRequest();
        request.setAccountNumber(accountNumber);
        request.setMeterId(meterId);
        request.setType(type);
        request.setReading(reading);
        request.setReadDate(readDate);
        return request;
    }

    private HttpEntity<List<MeterReadRequest>> createHttpEntity(List<MeterReadRequest> meterReadRequests) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(meterReadRequests, headers);
    }

    private void verifyMeterRead(String accountNumber, String type, double reading) {
        MeterRead savedMeterRead = meterReadRepository.findByAccountAccountNumberAndType(accountNumber, type).get(0);
        assertNotNull(savedMeterRead);
        assertEquals(accountNumber, savedMeterRead.getAccount().getAccountNumber());
        assertEquals(type, savedMeterRead.getType());
        assertEquals(reading, savedMeterRead.getReading());
    }

    private String generateUniqueAccountNumber() {
        return BASE_ACCOUNT_NUMBER + System.currentTimeMillis();
    }
}
