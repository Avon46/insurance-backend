package com.insurance.insurance_backend.mapper;

import com.insurance.insurance_backend.entity.AgeCoefficient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AgeCoefficientMapper {

    @Select("""
            SELECT
                id,
                min_age AS minAge,
                max_age AS maxAge,
                age_coefficient AS ageCoefficient,
                age_score AS ageScore,
                description
            FROM age_coefficients
            WHERE #{age} BETWEEN min_age AND max_age
            LIMIT 1
            """)
    AgeCoefficient findByAge(Integer age);
}