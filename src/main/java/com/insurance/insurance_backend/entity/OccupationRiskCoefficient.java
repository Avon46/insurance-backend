package com.insurance.insurance_backend.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OccupationRiskCoefficient {

    private Integer occupationId;

    private String occupationName;

    private String riskLevel;

    private BigDecimal riskCoefficient;

    private Integer riskScore;

    private String description;
}
