package com.insurance.insurance_backend.converter;

import org.springframework.stereotype.Component;

import com.insurance.insurance_backend.dto.PremiumCalculationRequest;
import com.insurance.insurance_backend.dto.PremiumCalculationResponse;
import com.insurance.insurance_backend.entity.InsurancePlan;
import com.insurance.insurance_backend.entity.OccupationRiskCoefficient;
import com.insurance.insurance_backend.entity.AgeCoefficient;

@Component
public class PremiumCalculationConverter {

    public PremiumCalculationResponse toResponse(
            InsurancePlan plan,
            PremiumCalculationRequest request,
            AgeCoefficient ageCoefficient,
            OccupationRiskCoefficient riskCoefficient,
            Integer estimatedPremium) {
        PremiumCalculationResponse response = new PremiumCalculationResponse();

        response.setPlanId(plan.getId());
        response.setPlanName(plan.getName());
        response.setBasePremium(plan.getBasePremium());
        response.setAge(request.getAge());
        response.setAgeCoefficient(ageCoefficient.getAgeCoefficient());
        response.setRiskLevel(riskCoefficient.getRiskLevel());
        response.setRiskCoefficient(riskCoefficient.getRiskCoefficient());
        response.setEstimatedPremium(estimatedPremium);

        return response;
    }
}