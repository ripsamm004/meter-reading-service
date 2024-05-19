package com.scottishpower.smartmeter.models.dto;

import com.scottishpower.smartmeter.enums.MeterReadType;
import com.scottishpower.smartmeter.models.entities.MeterRead;
import com.scottishpower.smartmeter.validators.ValidAccount;
import com.scottishpower.smartmeter.validators.ValidValueOfEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class MeterReadRequest {

    @NotEmpty(message = "Account number is required")
    @ValidAccount(message = "Account does not exist")
    private String accountNumber;

    @NotNull(message = "Meter ID is required")
    private Long meterId;

    @NotNull(message = "Reading is required")
    private Double reading;

    @NotNull(message = "Read date is required")
    private LocalDate readDate;

    @ValidValueOfEnum(enumClass = MeterReadType.class, field = "type", message = "Invalid '{field}'. Accepted values are: {acceptedValues}")
    private String type;

    public MeterRead toMeterRead() {
        MeterRead meterRead = new MeterRead();
        meterRead.setMeterId(this.meterId);
        meterRead.setReading(this.reading);
        meterRead.setReadDate(this.readDate);
        meterRead.setType(this.type);
        return meterRead;
    }

}
