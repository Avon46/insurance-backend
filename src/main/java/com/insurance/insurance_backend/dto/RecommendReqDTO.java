package com.insurance.insurance_backend.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RecommendReqDTO {
    private String userGroup;// 身分

    @Min(value = 0, message = "age 必須為正整數")
    private Integer age;// 使用者真實年齡

    private String schedule;// 保單有效期

    @Min(value = 1, message = "budgetLimit 必須大於 0")
    private Integer budgetLimit;//預算

    @NotEmpty(message = "primaryNeeds 不可為空，請至少選擇一項險種")
    private List<String> primaryNeeds;//險種

}
