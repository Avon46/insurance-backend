package com.insurance.insurance_backend.mapper;

import com.insurance.insurance_backend.dto.PlanQueryCondition;
import com.insurance.insurance_backend.entity.InsurancePlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 保單資料存取。SQL 寫在 resources/mapper/InsurancePlanMapper.xml。
 */
@Mapper
public interface InsurancePlanMapper {

    /**
     * 取得保單列表。category 為 null 時回傳全部。
     * 預設排序：id DESC（依 serial 流水號）。
     */
    List<InsurancePlan> findAll(@Param("category") String category);

    InsurancePlan findById(@Param("id") Integer id);

    /** 新增後 id 會回填到傳入的 plan 物件。 */
    int insert(InsurancePlan plan);

    int update(InsurancePlan plan);

    int deleteById(@Param("id") Integer id);

    List<InsurancePlan> findEligiblePlans(
        @Param("budgetLimit") int budgetLimit,
        @Param("age") int age,
        @Param("categories") List<String> categories);// 查詢推薦保險方案

    List<String> findTagsByPlanId(Integer planId);// 查詢保險方案tags

    /**
     * 依條件粗篩候選保單：
     * - status = ACTIVE
     * - age 落在 [minAge, maxAge]
     * - category 命中 categories 其中之一
     * - basePremium <= budgetLimit
     */
    List<InsurancePlan> selectCandidates(@Param("cond") PlanQueryCondition cond);
}
