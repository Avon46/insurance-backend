package com.insurance.insurance_backend.controller;

import com.insurance.insurance_backend.dto.PremiumCalculationRequest;
import com.insurance.insurance_backend.dto.PremiumCalculationResponse;
import com.insurance.insurance_backend.entity.InsurancePlan;
import com.insurance.insurance_backend.mapper.InsurancePlanMapper;
import com.insurance.insurance_backend.service.PremiumCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1")
public class PremiumCalculationController {

    private final PremiumCalculationService premiumCalculationService;
    private final InsurancePlanMapper insurancePlanMapper;

    public PremiumCalculationController(PremiumCalculationService premiumCalculationService,
            InsurancePlanMapper insurancePlanMapper) {
        this.premiumCalculationService = premiumCalculationService;
        this.insurancePlanMapper = insurancePlanMapper;
    }

    @GetMapping("/plans")
    public ResponseEntity<List<InsurancePlan>> getPlans() {
        List<InsurancePlan> plans = insurancePlanMapper.findAll(null);
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/calculate-premium")
    public ResponseEntity<PremiumCalculationResponse> calculatePremium(
            @RequestBody PremiumCalculationRequest request) {
        PremiumCalculationResponse response = premiumCalculationService.calculate(request);
        return ResponseEntity.ok(response);
    }
}