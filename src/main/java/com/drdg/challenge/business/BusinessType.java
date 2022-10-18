package com.drdg.challenge.business;

/**
 * Risk management is a key aspect at Tribal, therefore to define the recommended credit line of a customer is
 * defined using the following rules:
 * One third of the cash balance (3:1 ratio)
 * One fifth of the monthly revenue (5:1 ratio)
 * And depending on the founding type the rules are:
 * When it is SME, it's only needed to calculate the recommended credit line based one the monthly revenue.
 * And when it is Startup, the recommended credit line is the maximum value between the rule based on the
 * cash balance and the monthly revenue.
 */
public abstract class BusinessType {
    public abstract Double getRecommendedLine(Double cashBalance, Double monthRevenue);
}
