package com.insurance.insurance_backend.rules;

import org.springframework.stereotype.Component;

import com.insurance.insurance_backend.dto.RecommendReqDTO;
import com.insurance.insurance_backend.entity.InsurancePlan;

@Component
public class ScoreCalculator {
    private static final int AGE_SCORE_MAX = 40;
    private static final int BUDGET_SCORE_MAX = 40;
    private static final int SCHEDULE_SCORE_MAX = 20;
    private static final int SCHEDULE_SCORE_BASE = 10; // 不匹配時的基礎分
 
    /**
     * 年齡分：年齡落在 [minAge, maxAge] 內，越接近區間中點分數越高。
     * 落在區間外（理論上 DB 已過濾掉，這裡做防禦性處理）給予低分而非直接 0，
     * 避免極端情況下候選池為空。
     */
    public int calcAgeScore(Integer age, InsurancePlan plan) {
        if (age == null || plan.getMinAge() == null || plan.getMaxAge() == null) {
            return AGE_SCORE_MAX / 2;
        }
        int min = plan.getMinAge();
        int max = plan.getMaxAge();
        if (age < min || age > max) {
            return 0;
        }
        if (max == min) {
            return AGE_SCORE_MAX;
        }
        double mid = (min + max) / 2.0;
        double halfRange = (max - min) / 2.0;
        double distanceRatio = Math.abs(age - mid) / halfRange; // 0(中心) ~ 1(邊界)
        double score = AGE_SCORE_MAX * (1 - distanceRatio * 0.3); // 邊界最多扣 30%
        return (int) Math.round(score);
    }
 
    /**
     * 預算分：basePremium 越接近 budgetLimit（且不超過）分數越高，代表「預算利用率」。
     * 注意：超過預算的方案應在 DB 層或 Service 層先被排除，這裡假設輸入已合法。
     */
    public int calcBudgetScore(Integer budgetLimit, InsurancePlan plan) {
        if (budgetLimit == null || budgetLimit <= 0 || plan.getBasePremium() == null) {
            return BUDGET_SCORE_MAX / 2;
        }
        if (plan.getBasePremium() > budgetLimit) {
            return 0;
        }
        double ratio = (double) plan.getBasePremium() / budgetLimit; // 0~1
        return (int) Math.round(BUDGET_SCORE_MAX * ratio);
    }
 
    /**
     * 保障期間分：與用戶期望的 schedule 完全相符給滿分，否則給基礎分（仍可入選，只是分數較低）。
     */
    public int calcScheduleScore(String schedule, InsurancePlan plan) {
        if (schedule == null || schedule.isBlank() || plan.getCoveragePeriod() == null) {
            return SCHEDULE_SCORE_BASE;
        }
        if (schedule.trim().equals(plan.getCoveragePeriod().trim())) {
            return SCHEDULE_SCORE_MAX;
        }
        return SCHEDULE_SCORE_BASE;
    }
 
    /**
     * 組合三個分數為總分。
     */
    public int calcTotalScore(int ageScore, int budgetScore, int scheduleScore) {
        return ageScore + budgetScore + scheduleScore;
    }
 
    /**
     * 依分數明細產生推薦理由文案。
     */
    public String buildReason(RecommendReqDTO req, InsurancePlan plan, int ageScore, int budgetScore, int scheduleScore) {
        StringBuilder sb = new StringBuilder();
        sb.append(plan.getName()).append(" 適合您，因為：");
        if (ageScore >= AGE_SCORE_MAX * 0.8) {
            sb.append("年齡與保障對象高度吻合；");
        } else if (ageScore > 0) {
            sb.append("符合年齡投保範圍；");
        }
        if (budgetScore >= BUDGET_SCORE_MAX * 0.8) {
            sb.append("保費貼近您設定的預算上限；");
        } else if (budgetScore > 0) {
            sb.append("保費在您可負擔範圍內；");
        }
        if (scheduleScore == SCHEDULE_SCORE_MAX) {
            sb.append("保障期間符合您的規劃。");
        } else {
            sb.append("提供 ").append(plan.getCoveragePeriod()).append(" 的保障規劃。");
        }
        return sb.toString();
    }
}
