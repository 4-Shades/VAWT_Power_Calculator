package com.example.wt.calculation;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CalculationRequest(
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal windSpeed,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal height,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal diameter,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal airDensity,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal powerCoefficient) {
}
