package com.example.wt;

import java.math.BigDecimal;

import com.example.wt.calculation.CalculationRequest;
import com.example.wt.calculation.CalculationResponse;
import com.example.wt.calculation.CalculationService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
@Controller
public class WindTurbineApplication {

    private final CalculationService calculationService;

    public WindTurbineApplication(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(WindTurbineApplication.class, args);
    }

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("windSpeed", "12.0");
        model.addAttribute("height", "2.0");
        model.addAttribute("diameter", "2.0");
        model.addAttribute("density", "1.16");
        model.addAttribute("cp", "0.35");
        model.addAttribute("recentCalculations", calculationService.getRecent(5));
        return "index";
    }

    @PostMapping("/calculate")
    public String calculatePower(
            @RequestParam("windSpeed") String windSpeedStr,
            @RequestParam("height") String heightStr,
            @RequestParam("diameter") String diameterStr,
            @RequestParam("density") String densityStr,
            @RequestParam("cp") String cpStr,
            Model model) {

        model.addAttribute("windSpeed", windSpeedStr);
        model.addAttribute("height", heightStr);
        model.addAttribute("diameter", diameterStr);
        model.addAttribute("density", densityStr);
        model.addAttribute("cp", cpStr);

        try {
            // Number Parsing & Type Validation
            BigDecimal windSpeed = new BigDecimal(windSpeedStr);
            BigDecimal height = new BigDecimal(heightStr);
            BigDecimal diameter = new BigDecimal(diameterStr);
            BigDecimal density = new BigDecimal(densityStr);
            BigDecimal cp = new BigDecimal(cpStr);

            // Physical Constraints Checking
            if (windSpeed.signum() < 0 || height.signum() < 0 || diameter.signum() < 0
                    || density.signum() < 0 || cp.signum() < 0) {
                throw new IllegalArgumentException("Parameters cannot be negative values.");
            }

            if (cp.compareTo(new BigDecimal("0.593")) > 0) {
                model.addAttribute("warningMessage", "Warning: Cp value exceeds the theoretical Betz Limit (0.593).");
            }

            CalculationResponse result = calculationService.calculateAndSave(
                    new CalculationRequest(windSpeed, height, diameter, density, cp));

            model.addAttribute("sweptArea", String.format("%.2f", result.sweptArea()));
            model.addAttribute("rawPowerKw", String.format("%.2f", result.rawPowerKw()));
            model.addAttribute("netPowerKw", String.format("%.2f", result.netPowerKw()));
            model.addAttribute("hasResult", true);

        } catch (NumberFormatException e) {
            model.addAttribute("errorMessage", "Input Error: Non-numeric format or empty fields detected.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Constraint Error: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Unexpected Calculation Error: " + e.getMessage());
        }

        model.addAttribute("recentCalculations", calculationService.getRecent(5));
        return "index";
    }
}
