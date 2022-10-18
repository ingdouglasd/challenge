package com.drdg.challenge.service;

import com.drdg.challenge.business.CreditStatus;
import com.drdg.challenge.web.model.ApplicationDto;

public interface ApplicationService {
    CreditStatus processApplication(ApplicationDto applicationDto, long timeRequested);
}
