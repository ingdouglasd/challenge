package com.drdg.challenge.service;

import com.drdg.challenge.business.*;
import com.drdg.challenge.web.model.ApplicationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class ApplicationServiceImpl implements ApplicationService{

    @Autowired
    CountRequestHelper countRequestHelper;
    // class variable
    private CreditStatus creditStatus = null;

    // Fetches the recommendedCredit
    @Override
    public CreditStatus processApplication(ApplicationDto applicationDto, long timeRequested){
        applicationDto.setSystemRequestedDate(new Date(timeRequested));
        // Rule. If the system receives 3 or more requests within two minutes return code 429
        // Rule. If the application is accepted, the application credit will be the same regardless of the inputs
        if(creditStatus!=null && creditStatus.getStatus().equals(CreditStatus.STATUS_ACCEPTED)) {
            // The application was already Accepted, accessing per second or third time.
            countRequestHelper.addAppRequest(applicationDto);
            // Timeframe of two mins between this request and the buffer first request.
            if(countRequestHelper.isTwoMinutesRuleBroken(timeRequested)){
                creditStatus.setOverloadFlag(true);
                return creditStatus;
            }else{
                creditStatus.setOverloadFlag(false);
            }
        }
        else {
            // Still the credit has not been accepted, need to calculate the recommended credit line
            BusinessType businessType = getBusinessType(applicationDto.getFoundingType());
            Double recommendedCreditLine = businessType.getRecommendedLine(applicationDto.getCashBalance(), applicationDto.getMonthlyRevenue());
            log.debug("rcl:"+recommendedCreditLine+" applicationDto:"+applicationDto.toString());
            // Once the recommended credit line has been calculated, the next step is to determine whether the
            // application is accepted or not.
            // If the recommended credit line is higher, then the application is accepted; otherwise, it is rejected
            if(recommendedCreditLine>applicationDto.getRequestedCreditLine()){
                log.debug("application ACCEPTED:" +System.currentTimeMillis());
                // The accepted credit line will be the same regardless of the inputs.
                creditStatus = CreditStatus.getInstance(CreditStatus.STATUS_ACCEPTED,recommendedCreditLine);
                // Once accepted we need to restart count till 3
                CountRequestHelper.clearRequestBuffer();
            }
        }
        return creditStatus;
    }

    private BusinessType getBusinessType(String foundingType){
        if(foundingType.equals("SME"))
            return new SME();
        if(foundingType.equals("Startup"))
            return new Startup();
        return new NoneType();
    }
}
