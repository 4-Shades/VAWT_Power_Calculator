package com.example.wt.calculation;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CalculationResponse(
        Long id,
        BigDecimal windSpeed,
        BigDecimal height,
        BigDecimal diameter,
        BigDecimal airDensity,
        BigDecimal powerCoefficient,
        BigDecimal sweptArea,
        BigDecimal rawPowerKw,
        BigDecimal netPowerKw,
        OffsetDateTime createdAt) {

    static CalculationResponse from(CalculationResult result) {
        return new CalculationResponse(
                result.getId(),
                result.getWindSpeed(),
                result.getHeight(),
                result.getDiameter(),
                result.getAirDensity(),
                result.getPowerCoefficient(),
                result.getSweptArea(),
                result.getRawPowerKw(),
                result.getNetPowerKw(),
                result.getCreatedAt());
    }
}
