package com.drdg.challenge.business;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Startup extends BusinessType {
    // Rule: The recommended credit line is the maximum value between the rule based on cash balance
    // and the monthly revenue.
    @Override
    public Double getRecommendedLine(Double cashBalance, Double monthRevenue){
        if(cashBalance == null || monthRevenue == null){
            log.error(" Error: cashBalance/monthRevenue should not be null");
            return 0.0;
        }
        // TODO: Ask if this is needed as it seems recomended line should  be always positive
        if(cashBalance <0 || monthRevenue <0)
            return 0.0;
        return Math.max(cashBalance, monthRevenue);
    }
}
