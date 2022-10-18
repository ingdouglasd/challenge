package com.drdg.challenge.business;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SME extends BusinessType {
    //the recommended credit line of a customer is  defined using the following rules:
    // One fifth of the monthly revenue (5:1 ratio)
    // Rule: SME only needed to calculate the recommended credit line base on the monthly revenue
    @Override
    public Double getRecommendedLine(Double cashBalance, Double monthRevenue){
        log.debug("getRecommendedLine:cashBalance["+cashBalance+"], monthRevenue["+ monthRevenue+"]");
        if (monthRevenue == 0)
            return 0.0;
        log.debug(".20*(monthRevenue)="+.20*(monthRevenue));
        return (.20*(monthRevenue));
    }
}
