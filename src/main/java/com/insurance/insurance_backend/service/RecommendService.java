package com.insurance.insurance_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.insurance.insurance_backend.dto.RecommendReqDTO;
import com.insurance.insurance_backend.dto.RecommendRespDTO;
import com.insurance.insurance_backend.entity.InsurancePlan;
import com.insurance.insurance_backend.mapper.InsurancePlanMapper;
import com.insurance.insurance_backend.rules.UserGroupRule;
import com.insurance.insurance_backend.rules.UserGroupRuleFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private static final Logger log = LoggerFactory.getLogger(RecommendService.class);

    // 評分權重
    private static final Integer BUDGET_MAX_SCORE = 40;
    private static final Integer AGE_MAX_SCORE = 20;
    private static final Integer TOTAL_MAX_SCORE = BUDGET_MAX_SCORE + AGE_MAX_SCORE;
    private static final Integer TOP_N = 3;

    private final InsurancePlanMapper mapper;
    private final UserGroupRuleFactory ruleFactory;

    public List<RecommendRespDTO> recommend(RecommendReqDTO req) {
        System.out.println("開始檢查DTO格式 -------------------");
        UserGroupRule rule = ruleFactory.getRule(req.getUserGroup());

        log.info("recommend request: budgetLimit={}, age={}, primaryNeeds={}",
                req.getBudgetLimit(), req.getAge(), req.getPrimaryNeeds());

        List<InsurancePlan> plans = mapper.findEligiblePlans(
                req.getBudgetLimit(),
                req.getAge(),
                req.getPrimaryNeeds());

        if (plans.isEmpty()) {
            return List.of();
        }

        List<RecommendRespDTO> result = new ArrayList<>();

        for (InsurancePlan plan : plans) {

            Integer budgetScore = calcBudgetScore(
                    req.getBudgetLimit(),
                    plan.getBasePremium());

            Integer ageScore = calcAgeScore(
                    plan,
                    req.getAge());

            Integer rawScore = budgetScore + ageScore;

            Integer totalScore = (int) Math.round(rawScore * 100.0 / TOTAL_MAX_SCORE);

            RecommendRespDTO dto = new RecommendRespDTO();

            dto.setId(plan.getId());
            dto.setName(plan.getName());
            dto.setCategory(plan.getCategory());
            dto.setCoveragePeriod(plan.getCoveragePeriod());

            dto.setBasePremium(plan.getBasePremium());

            dto.setHighlight(buildHighlight(plan));

            dto.setBudgetScore((int) Math.round(budgetScore * 100.0 / BUDGET_MAX_SCORE));
            dto.setAgeScore((int) Math.round(ageScore * 100.0 / AGE_MAX_SCORE));

            dto.setScore(totalScore);

            dto.setReason(buildReason(rule, budgetScore, ageScore));

            result.add(dto);
        }

        result.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        List<RecommendRespDTO> topN = result.stream()
                .limit(TOP_N)
                .toList();

        for (int i = 0; i < topN.size(); i++) {
            RecommendRespDTO dto = topN.get(i);
            dto.setRank(i + 1);
            dto.setTags(mapper.findTagsByPlanId(dto.getId()));
        }

        return topN;
    }

    /**
     * 預算匹配分數
     */
    private Integer calcBudgetScore(Integer budget, Integer premium) {

        if (budget == null || budget <= 0) {
            return 0;
        }

        if (premium > budget) {
            return 0;
        }

        double ratio = (double) premium / budget;

        if (ratio >= 0.8) {
            return 40;
        }

        if (ratio >= 0.5) {
            return 30;
        }

        return 20;
    }

    /**
     * 年齡匹配分數
     */
    private Integer calcAgeScore(InsurancePlan plan, Integer age) {

        if (age < plan.getMinAge() || age > plan.getMaxAge()) {
            return 0;
        }

        Integer range = plan.getMaxAge() - plan.getMinAge();

        if (range <= 0) {
            return AGE_MAX_SCORE;
        }

        Integer distFromMin = age - plan.getMinAge();
        Integer distFromMax = plan.getMaxAge() - age;
        Integer distToEdge = Math.min(distFromMin, distFromMax);

        double centerRatio = distToEdge / (range / 2.0);

        if (centerRatio >= 0.6) {
            return 20;
        }

        if (centerRatio >= 0.3) {
            return 15;
        }

        return 10;
    }

    /**
     * 保單亮點
     */
    private List<String> buildHighlight(InsurancePlan plan) {

        List<String> list = new ArrayList<>();

        if (plan.getDescription() != null && !plan.getDescription().isBlank()) {
            list.add(plan.getDescription());
        }

        if (plan.getCoveragePeriod() != null) {
            list.add("保障期間：" + plan.getCoveragePeriod());
        }

        list.add("投保年齡：" + plan.getMinAge() + "~" + plan.getMaxAge() + "歲");

        return list;
    }

    /**
     * 推薦原因
     */
    private String buildReason(UserGroupRule rule,
                               Integer budgetScore,
                               Integer ageScore) {

        List<String> reasons = new ArrayList<>();

        reasons.add("適合" + rule.getGroupName() + "族群");

        if (budgetScore >= 30) {
            reasons.add("符合預算規劃");
        }

        if (ageScore >= 15) {
            reasons.add("適合目前年齡階段");
        }

        return String.join("、", reasons);
    }
}
