package com.drdg.challenge.business;


import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public class CreditStatus {
    private final String status;
    private final Double recommendedCredit;
    private boolean overloadFlag;
    private Boolean waitFlag;
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_REJECTED = "REJECTED";

    private static CreditStatus creditStatus;
    // Singleton, constructor must be private
    private CreditStatus(String status, Double recommendedCredit ){
        this.status = status;
        this.recommendedCredit = recommendedCredit;
        overloadFlag = Boolean.FALSE;
    }
    // Singleton will always return the same instance.  Final values once set cannot be changed.
    public static CreditStatus getInstance(String status, double recommendedCredit){
        if(creditStatus == null) {
            log.debug("Creating Credit Status");
            return new CreditStatus(status, recommendedCredit);
        }
        return creditStatus;
    }

    public String getStatus(){
        return status;
    }

    public Double getRecommendedCredit(){
        return recommendedCredit;
    }

    public boolean getOverloadFlag() {
        return overloadFlag;
    }
    public void setOverloadFlag(boolean overloadFlag) {
        this.overloadFlag = overloadFlag;
    }

}
