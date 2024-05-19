package com.scottishpower.smartmeter.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.scottishpower.smartmeter.models.dto.MeterReadRequest;
import com.scottishpower.smartmeter.models.entities.MeterRead;
import com.scottishpower.smartmeter.service.MeterReadService;
import com.scottishpower.smartmeter.models.response.MeterReadResponse;


import java.util.List;

@RestController
@RequestMapping("/api/smart")
@Validated
public class MeterReadController {

    private final MeterReadService meterReadService;

    public MeterReadController(MeterReadService meterReadService) {
        this.meterReadService = meterReadService;
    }

    @GetMapping("/reads/{accountNumber}")
    public ResponseEntity<?> getMeterReadsByAccountNumber(@PathVariable String accountNumber) {
        List<MeterRead> gasReads = meterReadService.getMeterReadsByAccountNumberAndType(accountNumber, "GAS");
        List<MeterRead> elecReads = meterReadService.getMeterReadsByAccountNumberAndType(accountNumber, "ELEC");
        MeterReadResponse response = new MeterReadResponse(accountNumber, gasReads, elecReads);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reads")
    public ResponseEntity<?> submitMeterRead(@Valid @RequestBody List<MeterReadRequest> meterReadRequests) {
        List<MeterRead> savedMeterReads = meterReadService.saveMeterReads(meterReadRequests);
        return new ResponseEntity<>(savedMeterReads, HttpStatus.CREATED);
    }
}
