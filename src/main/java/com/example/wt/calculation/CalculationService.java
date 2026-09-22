package com.example.wt.calculation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;

@Service
public class CalculationService {

    private static final int RESULT_SCALE = 4;

    private final CalculationResultRepository repository;

    public CalculationService(CalculationResultRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CalculationResponse calculateAndSave(CalculationRequest request) {
        double windSpeed = request.windSpeed().doubleValue();
        double sweptAreaValue = request.height().doubleValue() * request.diameter().doubleValue();
        double rawPowerValue = (0.5 * request.airDensity().doubleValue() * sweptAreaValue
                * Math.pow(windSpeed, 3)) / 1000.0;
        double netPowerValue = rawPowerValue * request.powerCoefficient().doubleValue();

        BigDecimal sweptArea = rounded(sweptAreaValue);
        BigDecimal rawPowerKw = rounded(rawPowerValue);
        BigDecimal netPowerKw = rounded(netPowerValue);

        CalculationResult result = new CalculationResult(
                request.windSpeed(),
                request.height(),
                request.diameter(),
                request.airDensity(),
                request.powerCoefficient(),
                sweptArea,
                rawPowerKw,
                netPowerKw,
                OffsetDateTime.now(ZoneOffset.UTC));

        return CalculationResponse.from(repository.save(result));
    }

    @Transactional(readOnly = true)
    public CalculationResponse getById(Long id) {
        return repository.findById(id)
                .map(CalculationResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Calculation not found."));
    }

    @Transactional(readOnly = true)
    public List<CalculationResponse> getRecent(int limit) {
        return repository.findAllByOrderByCreatedAtDescIdDesc(PageRequest.of(0, limit)).stream()
                .map(CalculationResponse::from)
                .toList();
    }

    private BigDecimal rounded(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Calculation result is outside the supported range.");
        }
        return BigDecimal.valueOf(value).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
    }
}
