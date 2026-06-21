package com.insurance.insurance_backend.rules;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserGroupRuleFactory {

    private static final Map<String, UserGroupRule> RULES = new HashMap<>();

    static {

        UserGroupRule student = new UserGroupRule();
        student.setGroupName("STUDENT");
        student.setMinAge(18);
        student.setMaxAge(25);
        student.setRecommendBudgetMin(3000);
        student.setRecommendBudgetMax(8000);
        student.setDefaultNeed("ACCIDENT");

        UserGroupRule young = new UserGroupRule();
        young.setGroupName("YOUNG_ADULT");
        young.setMinAge(25);
        young.setMaxAge(35);
        young.setRecommendBudgetMin(8000);
        young.setRecommendBudgetMax(20000);
        young.setDefaultNeed("MEDICAL");

        UserGroupRule family = new UserGroupRule();
        family.setGroupName("FAMILY");
        family.setMinAge(30);
        family.setMaxAge(50);
        family.setRecommendBudgetMin(15000);
        family.setRecommendBudgetMax(40000);
        family.setDefaultNeed("LIFE");

        UserGroupRule senior = new UserGroupRule();
        senior.setGroupName("SENIOR");
        senior.setMinAge(50);
        senior.setMaxAge(80);
        senior.setRecommendBudgetMin(10000);
        senior.setRecommendBudgetMax(30000);
        senior.setDefaultNeed("MEDICAL");

        RULES.put("STUDENT", student);
        RULES.put("YOUNG_ADULT", young);
        RULES.put("FAMILY", family);
        RULES.put("SENIOR", senior);
    }

    public UserGroupRule getRule(String group) {
        return RULES.getOrDefault(group, RULES.get("YOUNG_ADULT"));
    }
}