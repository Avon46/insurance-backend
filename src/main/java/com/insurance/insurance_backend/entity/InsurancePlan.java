package com.insurance.insurance_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InsurancePlan {

    private Integer id;

    private String name;

    private String category;

    private String description;

    private Integer basePremium;

    private Integer maxCoverage;

    private Integer minAge;

    private Integer maxAge;

    private String status;

    private Integer sortOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}