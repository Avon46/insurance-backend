package com.insurance.insurance_backend.dto;

import lombok.Data;

@Data
public class PremiumCalculationRequest {

    private Integer planId;

    private Integer age;

    private String riskLevel;
}
