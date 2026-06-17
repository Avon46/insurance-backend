package com.insurance.insurance_backend.service;

import com.insurance.insurance_backend.dto.PlanRequest;
import com.insurance.insurance_backend.entity.InsurancePlan;
import com.insurance.insurance_backend.exception.BusinessException;
import com.insurance.insurance_backend.exception.ResourceNotFoundException;
import com.insurance.insurance_backend.mapper.InsurancePlanMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 保單 CRUD 商業邏輯。DTO <-> Entity 的轉換也寫在這層。
 */
@Service
public class InsurancePlanService {

    private final InsurancePlanMapper planMapper;

    public InsurancePlanService(InsurancePlanMapper planMapper) {
        this.planMapper = planMapper;
    }

    public List<InsurancePlan> findAll(String category) {
        return planMapper.findAll(category);
    }

    public InsurancePlan findById(Integer id) {
        InsurancePlan plan = planMapper.findById(id);
        if (plan == null) {
            throw new ResourceNotFoundException("找不到 id 為 " + id + " 的保單");
        }
        return plan;
    }

    public InsurancePlan create(PlanRequest request) {
        validateAgeRange(request);
        InsurancePlan plan = toEntity(request, new InsurancePlan());
        planMapper.insert(plan);
        return plan; // insert 後 id 已回填
    }

    public InsurancePlan update(Integer id, PlanRequest request) {
        InsurancePlan existing = findById(id); // 不存在會直接拋 404
        validateAgeRange(request);
        InsurancePlan plan = toEntity(request, existing);
        plan.setId(id);
        planMapper.update(plan);
        return findById(id); // 回傳含 updatedAt 的最新資料
    }

    public void delete(Integer id) {
        int affected = planMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("找不到 id 為 " + id + " 的保單");
        }
    }

    // ---- DTO -> Entity 轉換器 ----

    /**
     * 把 PlanRequest 的欄位搬到 target entity。
     * 刻意不碰 id / createdAt / updatedAt，交給 DB 控制。
     * status / 年齡採預設值（與 DB DEFAULT 一致）。
     */
    private InsurancePlan toEntity(PlanRequest req, InsurancePlan target) {
        target.setName(req.getName());
        target.setCategory(req.getCategory());
        target.setDescription(req.getDescription());
        target.setBasePremium(req.getBasePremium());
        target.setMaxCoverage(req.getMaxCoverage());
        target.setMinAge(req.getMinAge() != null ? req.getMinAge() : 0);
        target.setMaxAge(req.getMaxAge() != null ? req.getMaxAge() : 80);
        target.setStatus(req.getStatus() != null ? req.getStatus() : "ACTIVE");
        target.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
        return target;
    }

    private void validateAgeRange(PlanRequest req) {
        int min = req.getMinAge() != null ? req.getMinAge() : 0;
        int max = req.getMaxAge() != null ? req.getMaxAge() : 80;
        if (min > max) {
            throw new BusinessException("最低承保年齡不可大於最高承保年齡", "INVALID_AGE_RANGE");
        }
    }
}
