package com.insurance.insurance_backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class RecommendRespDTO {
    private Integer id;
    private String name;
    private String category;
    private String coveragePeriod;
    private List<String> highlight;

    private Integer basePremium;

    private List<String> tags;

    private Integer score;
    private Integer budgetScore;
    private Integer ageScore;

    private Integer rank;

    private String reason;
}
