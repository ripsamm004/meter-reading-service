package com.scottishpower.smartmeter.controller;

import com.scottishpower.smartmeter.exception.Exceptions;
import com.scottishpower.smartmeter.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.scottishpower.smartmeter.models.entities.Account;
import com.scottishpower.smartmeter.models.entities.MeterRead;
import com.scottishpower.smartmeter.service.MeterReadService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.scottishpower.smartmeter.enums.Error;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class MeterReadControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeterReadService meterReadService;

    @MockBean
    private AccountRepository accountRepository;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testGetMeterReadsByAccountNumber() throws Exception {
        String accountNumber = "5555";
        MeterRead meterRead = createMeterRead("GAS", 100.0, LocalDate.now());

        when(meterReadService.getMeterReadsByAccountNumberAndType(accountNumber, "GAS"))
            .thenReturn(Collections.singletonList(meterRead));
        when(meterReadService.getMeterReadsByAccountNumberAndType(accountNumber, "ELEC"))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/smart/reads/" + accountNumber)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountNumber", is(accountNumber)))
            .andExpect(jsonPath("$.gasReadings[0].reading", is(100.0)))
            .andExpect(jsonPath("$.elecReadings", is(Collections.emptyList())))
            .andReturn();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testSubmitMeterRead() throws Exception {
        MeterRead gasMeterRead = createMeterRead("GAS", 602.0, LocalDate.of(2023, 5, 11), 101L);
        MeterRead elecMeterRead = createMeterRead("ELEC", 342.0, LocalDate.of(2023, 5, 11), 102L);

        String accountNumber = "12345";
        Account account = createAccount(accountNumber);

        when(meterReadService.saveMeterReads(any())).thenReturn(Arrays.asList(gasMeterRead, elecMeterRead));
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        String inputJson = """
            [
              {
                "accountNumber": "12345",
                "meterId": 101,
                "reading": 602.0,
                "readDate": "2023-05-11",
                "type": "GAS"
              },
              {
                "accountNumber": "12345",
                "meterId": 102,
                "reading": 342.0,
                "readDate": "2023-05-11",
                "type": "ELEC"
              }
            ]
            """;

        mockMvc.perform(post("/api/smart/reads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$[0].meterId", is(101)))
            .andExpect(jsonPath("$[0].reading", is(602.0)))
            .andExpect(jsonPath("$[0].readDate", is("2023-05-11")))
            .andExpect(jsonPath("$[1].meterId", is(102)))
            .andExpect(jsonPath("$[1].reading", is(342.0)))
            .andExpect(jsonPath("$[1].readDate", is("2023-05-11")))
            .andReturn();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testIfAccountNotExist_SubmitMeterReadFail() throws Exception {
        String accountNumber = "12345";
        String inputJson = """
            [
              {
                "accountNumber": "12345",
                "meterId": 101,
                "reading": 602.0,
                "readDate": "2023-05-11",
                "type": "GAS"
              },
              {
                "accountNumber": "12345",
                "meterId": 102,
                "reading": 342.0,
                "readDate": "2023-05-11",
                "type": "ELEC"
              }
            ]
            """;

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/smart/reads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is(Error.BAD_REQUEST.getCode())))
            .andExpect(jsonPath("$.message", containsString("Account does not exist")))
            .andReturn();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testGetMeterReadsByAccountNumber_AccountNotExist() throws Exception {
        String accountNumber = "9999";

        when(meterReadService.getMeterReadsByAccountNumberAndType(accountNumber, "GAS"))
            .thenThrow(Exceptions.API_ERROR_ACCOUNT_NOT_FOUND);

        mockMvc.perform(get("/api/smart/reads/" + accountNumber)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is(Error.API_ERROR_ACCOUNT_NOT_FOUND.getCode())))
            .andExpect(jsonPath("$.message", is(Error.API_ERROR_ACCOUNT_NOT_FOUND.getMessage())))
            .andReturn();
    }

    private MeterRead createMeterRead(String type, double reading, LocalDate readDate) {
        MeterRead meterRead = new MeterRead();
        meterRead.setType(type);
        meterRead.setReading(reading);
        meterRead.setReadDate(readDate);
        return meterRead;
    }

    private MeterRead createMeterRead(String type, double reading, LocalDate readDate, Long meterId) {
        MeterRead meterRead = createMeterRead(type, reading, readDate);
        meterRead.setMeterId(meterId);
        return meterRead;
    }

    private Account createAccount(String accountNumber) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        return account;
    }
}
