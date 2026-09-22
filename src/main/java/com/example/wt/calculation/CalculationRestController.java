package com.example.wt.calculation;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/calculations")
@Validated
public class CalculationRestController {

    private final CalculationService calculationService;

    public CalculationRestController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @PostMapping
    public ResponseEntity<CalculationResponse> calculate(@Valid @RequestBody CalculationRequest request) {
        CalculationResponse result = calculationService.calculateAndSave(request);
        return ResponseEntity.created(URI.create("/api/calculations/" + result.id())).body(result);
    }

    @GetMapping
    public List<CalculationResponse> getRecent(
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int limit) {
        return calculationService.getRecent(limit);
    }

    @GetMapping("/{id}")
    public CalculationResponse getById(@PathVariable Long id) {
        return calculationService.getById(id);
    }
}
