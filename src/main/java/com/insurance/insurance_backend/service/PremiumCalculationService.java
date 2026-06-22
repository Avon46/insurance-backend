package com.insurance.insurance_backend.service;

import com.insurance.insurance_backend.converter.PremiumCalculationConverter;
import com.insurance.insurance_backend.dto.PremiumCalculationRequest;
import com.insurance.insurance_backend.dto.PremiumCalculationResponse;
import com.insurance.insurance_backend.entity.AgeCoefficient;
import com.insurance.insurance_backend.entity.InsurancePlan;
import com.insurance.insurance_backend.entity.OccupationRiskCoefficient;
import com.insurance.insurance_backend.exception.BusinessException;
import com.insurance.insurance_backend.exception.ResourceNotFoundException;
import com.insurance.insurance_backend.mapper.AgeCoefficientMapper;
import com.insurance.insurance_backend.mapper.InsurancePlanMapper;
import com.insurance.insurance_backend.mapper.OccupationRiskCoefficientMapper;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@AllArgsConstructor
public class PremiumCalculationService {

    private final InsurancePlanMapper insurancePlanMapper;
    private final AgeCoefficientMapper ageCoefficientMapper;
    private final OccupationRiskCoefficientMapper occupationRiskCoefficientMapper;
    private final PremiumCalculationConverter premiumCalculationConverter;

    public PremiumCalculationResponse calculate(PremiumCalculationRequest request) {

        // 1. 基本欄位檢查：屬於前端傳入參數錯誤
        if (request == null) {
            throw new IllegalArgumentException("試算資料不得為空");
        }

        if (request.getPlanId() == null) {
            throw new IllegalArgumentException("保單 ID 不得為空");
        }

        if (request.getAge() == null) {
            throw new IllegalArgumentException("年齡不得為空");
        }

        if (request.getAge() < 0) {
            throw new IllegalArgumentException("年齡不得小於 0");
        }

        if (request.getRiskLevel() == null || request.getRiskLevel().isBlank()) {
            throw new IllegalArgumentException("職業風險等級不得為空");
        }

        // 避免前端傳 high / High 導致查不到
        String riskLevel = request.getRiskLevel().trim().toUpperCase();

        // 2. 查保單
        InsurancePlan plan = insurancePlanMapper.findById(request.getPlanId());

        if (plan == null) {
            throw new ResourceNotFoundException("查無此保單", "PLAN_NOT_FOUND");
        }

        // 3. 業務規則：保單必須上架
        if (!"ACTIVE".equals(plan.getStatus())) {
            throw new BusinessException("此保單目前未上架，無法試算", "PLAN_INACTIVE");
        }

        // 4. 業務規則：年齡必須符合保單承保範圍
        if (request.getAge() < plan.getMinAge() || request.getAge() > plan.getMaxAge()) {
            throw new BusinessException("年齡不符合此保單承保範圍", "AGE_NOT_ALLOWED");
        }

        // 5. 查年齡級距係數
        AgeCoefficient ageCoefficient = ageCoefficientMapper.findByAge(request.getAge());

        if (ageCoefficient == null) {
            throw new ResourceNotFoundException("查無對應的年齡級距係數", "AGE_COEFFICIENT_NOT_FOUND");
        }

        // 6. 查職業風險係數
        OccupationRiskCoefficient riskCoefficient = occupationRiskCoefficientMapper.findByRiskLevel(riskLevel);

        if (riskCoefficient == null) {
            throw new ResourceNotFoundException("查無對應的職業風險係數", "RISK_COEFFICIENT_NOT_FOUND");
        }

        // 7. 計算保費
        BigDecimal basePremium = BigDecimal.valueOf(plan.getBasePremium());

        BigDecimal estimatedPremiumDecimal = basePremium
                .multiply(ageCoefficient.getAgeCoefficient())
                .multiply(riskCoefficient.getRiskCoefficient());

        Integer estimatedPremium = estimatedPremiumDecimal
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();

        // 8. 防呆：避免計算結果異常
        if (estimatedPremium < 0) {
            throw new BusinessException("保費計算結果異常", "PREMIUM_CALCULATION_ERROR");
        }

        // 9. 組 Response
        return premiumCalculationConverter.toResponse(
                plan,
                request,
                ageCoefficient,
                riskCoefficient,
                estimatedPremium);
    }
}
