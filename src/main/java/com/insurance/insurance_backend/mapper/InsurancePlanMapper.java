package com.insurance.insurance_backend.mapper;

import com.insurance.insurance_backend.entity.InsurancePlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InsurancePlanMapper {

    @Select("""
            SELECT
                id,
                name,
                category,
                description,
                base_premium AS basePremium,
                max_coverage AS maxCoverage,
                min_age AS minAge,
                max_age AS maxAge,
                status,
                sort_order AS sortOrder,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM insurance_plans
            WHERE id = #{planId}
            """)
    InsurancePlan findById(Integer planId);

    @Select("""
            SELECT
                id,
                name,
                category,
                description,
                base_premium AS basePremium,
                max_coverage AS maxCoverage,
                min_age AS minAge,
                max_age AS maxAge,
                status,
                sort_order AS sortOrder,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM insurance_plans
            WHERE status = 'ACTIVE'
            ORDER BY sort_order ASC
            """)
    List<InsurancePlan> findAllActive();
}