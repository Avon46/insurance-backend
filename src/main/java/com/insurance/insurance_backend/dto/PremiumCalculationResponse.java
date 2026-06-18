package com.insurance.insurance_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PremiumCalculationResponse {

    private Integer planId;

    private String planName;

    private Integer basePremium;

    private Integer age;

    private BigDecimal ageCoefficient;

    private String riskLevel;

    private BigDecimal riskCoefficient;

    private Integer estimatedPremium;
}