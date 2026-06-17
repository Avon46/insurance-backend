package com.insurance.insurance_backend.entity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgeCoefficient {

    private Integer id;

    private Integer minAge;

    private Integer maxAge;

    private BigDecimal ageCoefficient;

    private Integer ageScore;

    private String description;
}
