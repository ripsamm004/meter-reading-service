package com.scottishpower.smartmeter.models.response;

import com.scottishpower.smartmeter.models.entities.MeterRead;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class MeterReadResponse {

    private String accountNumber;
    private List<MeterReadDTO> gasReadings;
    private List<MeterReadDTO> elecReadings;

    public MeterReadResponse(String accountNumber, List<MeterRead> gasReads, List<MeterRead> elecReads) {
        this.accountNumber = accountNumber;
        this.gasReadings = gasReads.stream()
            .map(MeterReadDTO::new)
            .collect(Collectors.toList());
        this.elecReadings = elecReads.stream()
            .map(MeterReadDTO::new)
            .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    public static class MeterReadDTO {
        private Long id;
        private Long meterId;
        private Double reading;
        private LocalDate readDate;
        private Double usageSinceLastRead;
        private Long periodSinceLastRead;
        private Double avgDailyUsage;
        private Double gasComparison;
        private Double elecComparison;

        public MeterReadDTO(MeterRead meterRead) {
            this.id = meterRead.getId();
            this.meterId = meterRead.getMeterId();
            this.reading = meterRead.getReading();
            this.readDate = meterRead.getReadDate();
            // The following fields would need calculation logic
            this.usageSinceLastRead = null;
            this.periodSinceLastRead = null;
            this.avgDailyUsage = null;
            this.gasComparison = null;
            this.elecComparison = null;
        }
    }
}
