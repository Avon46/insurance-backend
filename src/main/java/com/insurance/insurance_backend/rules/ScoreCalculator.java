package com.insurance.insurance_backend.rules;

import org.springframework.stereotype.Component;

import com.insurance.insurance_backend.dto.RecommendReqDTO;
import com.insurance.insurance_backend.entity.InsurancePlan;

@Component
public class ScoreCalculator {

    /** 年齡分滿分（50） */
    private static final int AGE_SCORE_MAX = 50;

    /** 預算分滿分（50） */
    private static final int BUDGET_SCORE_MAX = 50;

    /**
     * 年齡分：
     * - 在區間內：依距離中點遠近給分
     * - 超出區間：0分
     */
    public int calcAgeScore(Integer age, InsurancePlan plan) {

        if (age == null || plan.getMinAge() == null || plan.getMaxAge() == null) {
            return AGE_SCORE_MAX / 2; // 防禦性：25分
        }

        int min = plan.getMinAge();
        int max = plan.getMaxAge();

        // 超出年齡範圍
        if (age < min || age > max) {
            return 0;
        }

        // 無區間（例如 min=max）
        if (max == min) {
            return AGE_SCORE_MAX;
        }

        // 區間中點
        double mid = (min + max) / 2.0;

        // 半區間
        double halfRange = (max - min) / 2.0;

        // 距離中心比例（0 = 中心，1 = 邊界）
        double distanceRatio = Math.abs(age - mid) / halfRange;

        // 邊界最多扣 30%
        double score = AGE_SCORE_MAX * (1 - distanceRatio * 0.3);

        return (int) Math.round(score);
    }

    /**
     * 預算分：
     * - 保費 <= 預算：依使用比例給分（越貼近上限越高）
     * - 超出預算：0分
     */
    public int calcBudgetScore(Integer budgetLimit, InsurancePlan plan) {

        if (budgetLimit == null
                || budgetLimit <= 0
                || plan.getBasePremium() == null) {
            return BUDGET_SCORE_MAX / 2; // 防禦性：25分
        }

        int premium = plan.getBasePremium();

        // 超出預算直接淘汰
        if (premium > budgetLimit) {
            return 0;
        }

        double ratio = (double) premium / budgetLimit;

        return (int) Math.round(BUDGET_SCORE_MAX * ratio);
    }

    /**
     * 總分（100分制）
     */
    public int calcTotalScore(int ageScore, int budgetScore) {
        return ageScore + budgetScore;
    }

    /**
     * 推薦理由（只基於年齡 + 預算）
     */
    public String buildReason(
            RecommendReqDTO req,
            InsurancePlan plan,
            int ageScore,
            int budgetScore) {

        StringBuilder sb = new StringBuilder();

        sb.append(plan.getName())
          .append(" 適合您，因為：");

        // 年齡理由
        if (ageScore >= AGE_SCORE_MAX * 0.8) {
            sb.append("年齡與保障對象高度吻合；");
        } else if (ageScore > 0) {
            sb.append("符合年齡投保範圍；");
        } else {
            sb.append("年齡略超出建議範圍；");
        }

        // 預算理由
        if (budgetScore >= BUDGET_SCORE_MAX * 0.8) {
            sb.append("保費非常貼近您的預算上限；");
        } else if (budgetScore > 0) {
            sb.append("保費在可接受範圍內；");
        } else {
            sb.append("保費超出預算範圍；");
        }

        return sb.toString();
    }
}