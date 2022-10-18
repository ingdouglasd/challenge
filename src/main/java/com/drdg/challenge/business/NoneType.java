package com.drdg.challenge.business;

public class NoneType extends BusinessType{
    // NoneType to avoid NPE
    @Override
    public Double getRecommendedLine(Double cashBalance, Double monthRevenue){
        return 0.0;
    }
}
