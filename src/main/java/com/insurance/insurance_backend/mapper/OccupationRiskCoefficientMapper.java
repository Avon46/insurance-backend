package com.insurance.insurance_backend.mapper;

import com.insurance.insurance_backend.entity.OccupationRiskCoefficient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OccupationRiskCoefficientMapper {

    @Select("""
            SELECT
                occupation_id AS occupationId,
                occupation_name AS occupationName,
                risk_level AS riskLevel,
                risk_coefficient AS riskCoefficient,
                risk_score AS riskScore,
                description
            FROM occupation_risk_coefficients
            WHERE risk_level = #{riskLevel}
            ORDER BY risk_coefficient DESC
            LIMIT 1
            """)
    OccupationRiskCoefficient findByRiskLevel(String riskLevel);
}