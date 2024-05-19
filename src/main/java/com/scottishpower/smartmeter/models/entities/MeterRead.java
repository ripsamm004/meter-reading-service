package com.scottishpower.smartmeter.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "meter_reads")
@NoArgsConstructor
@Data
public class MeterRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonBackReference
    private Account account;

    @Column(name = "meter_id", nullable = false)
    private Long meterId;

    @Column(name = "reading", nullable = false)
    private Double reading;

    @Column(name = "read_date", nullable = false)
    private LocalDate readDate;

    @Column(name = "type", nullable = false)
    private String type;

    @Transient
    private Double usageSinceLastRead;

    @Transient
    private Long periodSinceLastRead;

}
