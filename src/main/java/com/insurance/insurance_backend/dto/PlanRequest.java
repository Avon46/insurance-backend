package com.insurance.insurance_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增 / 修改保單時，前端送入的資料。
 * 刻意不含 id、createdAt、updatedAt —— 這些由 DB / 後端控制，避免前端竄改。
 * DTO -> Entity 的轉換寫在 InsurancePlanService。
 */
@Data
public class PlanRequest {

    @NotBlank(message = "保單名稱不可為空")
    @Size(max = 100, message = "保單名稱長度不可超過 100 字")
    private String name;

    @NotBlank(message = "分類不可為空")
    @Pattern(regexp = "MEDICAL|ACCIDENT|LIFE",
            message = "分類僅能為 MEDICAL、ACCIDENT、LIFE")
    private String category;

    @Size(max = 255, message = "保單簡介長度不可超過 255 字")
    private String description;

    @NotNull(message = "基本保費不可為空")
    @Min(value = 0, message = "基本保費不可為負數")
    private Integer basePremium;

    @NotNull(message = "最高保障額度不可為空")
    @Min(value = 0, message = "最高保障額度不可為負數")
    private Integer maxCoverage;

    @Min(value = 0, message = "最低承保年齡不可為負數")
    private Integer minAge;

    @Min(value = 0, message = "最高承保年齡不可為負數")
    private Integer maxAge;

    @Pattern(regexp = "ACTIVE|INACTIVE",
            message = "狀態僅能為 ACTIVE 或 INACTIVE")
    private String status;

    /** 保障年期（如：一年期 / 十年期 / 二十年期 / 終身），供推薦模組配對 */
    @Size(max = 20, message = "保障年期長度不可超過 20 字")
    private String coveragePeriod;
}
