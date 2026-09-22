package com.example.wt.calculation;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "calculation_results")
public class CalculationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wind_speed", nullable = false, precision = 12, scale = 4)
    private BigDecimal windSpeed;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal height;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal diameter;

    @Column(name = "air_density", nullable = false, precision = 12, scale = 4)
    private BigDecimal airDensity;

    @Column(name = "power_coefficient", nullable = false, precision = 12, scale = 4)
    private BigDecimal powerCoefficient;

    @Column(name = "swept_area", nullable = false, precision = 16, scale = 4)
    private BigDecimal sweptArea;

    @Column(name = "raw_power_kw", nullable = false, precision = 16, scale = 4)
    private BigDecimal rawPowerKw;

    @Column(name = "net_power_kw", nullable = false, precision = 16, scale = 4)
    private BigDecimal netPowerKw;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected CalculationResult() {
        // Required by JPA.
    }

    CalculationResult(
            BigDecimal windSpeed,
            BigDecimal height,
            BigDecimal diameter,
            BigDecimal airDensity,
            BigDecimal powerCoefficient,
            BigDecimal sweptArea,
            BigDecimal rawPowerKw,
            BigDecimal netPowerKw,
            OffsetDateTime createdAt) {
        this.windSpeed = windSpeed;
        this.height = height;
        this.diameter = diameter;
        this.airDensity = airDensity;
        this.powerCoefficient = powerCoefficient;
        this.sweptArea = sweptArea;
        this.rawPowerKw = rawPowerKw;
        this.netPowerKw = netPowerKw;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getWindSpeed() {
        return windSpeed;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public BigDecimal getDiameter() {
        return diameter;
    }

    public BigDecimal getAirDensity() {
        return airDensity;
    }

    public BigDecimal getPowerCoefficient() {
        return powerCoefficient;
    }

    public BigDecimal getSweptArea() {
        return sweptArea;
    }

    public BigDecimal getRawPowerKw() {
        return rawPowerKw;
    }

    public BigDecimal getNetPowerKw() {
        return netPowerKw;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
