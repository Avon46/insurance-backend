package com.insurance.insurance_backend.rules;

import lombok.Data;

@Data
public class UserGroupRule {
    private String groupName;

    private int minAge;
    private int maxAge;

    private int recommendBudgetMin;
    private int recommendBudgetMax;

    private String defaultNeed;
}
