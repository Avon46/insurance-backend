package com.insurance.insurance_backend.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.insurance.insurance_backend.dto.PlanQueryCondition;
import com.insurance.insurance_backend.dto.RecommendReqDTO;
import com.insurance.insurance_backend.dto.RecommendRespDTO;
import com.insurance.insurance_backend.entity.InsurancePlan;
import com.insurance.insurance_backend.mapper.InsurancePlanMapper;
import com.insurance.insurance_backend.rules.ScoreCalculator;
import com.insurance.insurance_backend.service.RecommendService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private static final int TOP_N = 3;

    private final InsurancePlanMapper insurancePlanMapper;
    private final ScoreCalculator scoreCalculator;

    @Override
    public List<RecommendRespDTO> recommend(RecommendReqDTO req) {

        log.info("===== 開始執行保單推薦 =====");
        log.info("收到推薦請求: {}", req);

        // 1. 查詢條件
        PlanQueryCondition cond = PlanQueryCondition.builder()
                .age(req.getAge())
                .budgetLimit(req.getBudgetLimit())
                .categories(req.getPrimaryNeeds())
                .schedule(req.getSchedule())
                .build();

        log.info("查詢條件: {}", cond);

        // 2. 查詢候選保單
        List<InsurancePlan> candidates = insurancePlanMapper.selectCandidates(cond);

        log.info("查詢到 {} 筆候選保單", candidates.size());

        if (candidates.isEmpty()) {
            log.warn("找不到符合條件的保單");
            return List.of();
        }

        candidates.forEach(plan ->
                log.info("候選保單 => id={}, name={}, category={}, premium={}",
                        plan.getId(),
                        plan.getName(),
                        plan.getCategory(),
                        plan.getBasePremium()));

        // 3. 計算分數（只剩兩項）
        List<RecommendRespDTO> result = candidates.stream()
                .map(plan -> buildScoredResp(req, plan))
                .sorted(Comparator.comparingInt(RecommendRespDTO::getScore).reversed())
                .limit(TOP_N)
                .collect(Collectors.toList());

        // 4. 排名
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setRank(i + 1);
        }

        log.info("===== 推薦結果 =====");

        result.forEach(r ->
                log.info("Rank={}, Plan={}, Score={}, AgeScore={}, BudgetScore={}",
                        r.getRank(),
                        r.getName(),
                        r.getScore(),
                        r.getAgeScore(),
                        r.getBudgetScore()));

        log.info("===== 保單推薦完成 =====");

        return result;
    }

    private RecommendRespDTO buildScoredResp(RecommendReqDTO req, InsurancePlan plan) {

        // ✅ 只剩兩個分數
        int ageScore = scoreCalculator.calcAgeScore(req.getAge(), plan);
        int budgetScore = scoreCalculator.calcBudgetScore(req.getBudgetLimit(), plan);

        int totalScore = scoreCalculator.calcTotalScore(
                ageScore,
                budgetScore
        );

        String reason = scoreCalculator.buildReason(
                req,
                plan,
                ageScore,
                budgetScore
        );

        log.info(
                "保單評分 => id={}, name={}, ageScore={}, budgetScore={}, totalScore={}",
                plan.getId(),
                plan.getName(),
                ageScore,
                budgetScore,
                totalScore);

        RecommendRespDTO dto = new RecommendRespDTO();

        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setCategory(plan.getCategory());
        dto.setCoveragePeriod(plan.getCoveragePeriod());
        dto.setHighlight(buildHighlight(plan));
        dto.setBasePremium(plan.getBasePremium());
        dto.setTags(buildTags(plan));

        dto.setScore(totalScore);
        dto.setBudgetScore(budgetScore);
        dto.setAgeScore(ageScore);

        dto.setReason(reason);

        return dto;
    }

    /**
     * highlight 目前使用 description 切分
     */
    private List<String> buildHighlight(InsurancePlan plan) {

        if (plan.getDescription() == null || plan.getDescription().isBlank()) {
            return List.of();
        }

        return List.of(plan.getDescription().split("[,，;；]"));
    }

    /**
     * tags 由 DB 查詢
     */
    private List<String> buildTags(InsurancePlan plan) {

        List<String> tags = insurancePlanMapper.findTagsByPlanId(plan.getId());

        log.debug("保單 {} Tags: {}", plan.getId(), tags);

        return tags;
    }
}