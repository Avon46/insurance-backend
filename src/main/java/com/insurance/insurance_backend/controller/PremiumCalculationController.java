package com.insurance.insurance_backend.controller;

import com.insurance.insurance_backend.dto.PremiumCalculationRequest;
import com.insurance.insurance_backend.dto.PremiumCalculationResponse;
import com.insurance.insurance_backend.service.PremiumCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PremiumCalculationController {

    private final PremiumCalculationService premiumCalculationService;

    public PremiumCalculationController(PremiumCalculationService premiumCalculationService) {
        this.premiumCalculationService = premiumCalculationService;
    }

    @PostMapping("/calculate-premium")
    public ResponseEntity<PremiumCalculationResponse> calculatePremium(
            @RequestBody PremiumCalculationRequest request) {
        PremiumCalculationResponse response = premiumCalculationService.calculate(request);
        return ResponseEntity.ok(response);
    }
}