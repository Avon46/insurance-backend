package com.insurance.insurance_backend.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanQueryCondition {
    private Integer age;
    private Integer budgetLimit;
    private String schedule;
    private List<String> categories; // 對應 primaryNeeds
}
