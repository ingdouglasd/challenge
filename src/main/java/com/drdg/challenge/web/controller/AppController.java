package com.drdg.challenge.web.controller;

import com.drdg.challenge.business.CreditStatus;
import com.drdg.challenge.service.ApplicationService;
import com.drdg.challenge.web.model.ApplicationDto;
import com.drdg.challenge.web.model.ResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.*;


@Slf4j
@RequestMapping("/api/v1/challenge")
@RestController
public class AppController {

    @Autowired
    private final ApplicationService appService;

    @Autowired
    ObjectMapper objectMapper;

    private static int timesRejected = 0;
    private static long lastTimeRequested = 0;

    @PostMapping(value="/processApp", consumes = {"application/xml","application/json"})
    public @ResponseBody ResponseEntity<ResultDto> processApp(@Valid @RequestBody ApplicationDto applicationDto){
        long timeRequested = System.currentTimeMillis();
        try {
            if (applicationDto != null && !(applicationDto.getFoundingType().equals("SME") || applicationDto.getFoundingType().equals("Startup"))) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
             // None functional requirements:
            // When the application has been rejected wait 30 seconds to process
            //After failing 3 times, return the message "A sales agent will contact you".
            if (timesRejected >= 3) {
                HttpStatus status = HttpStatus.OK;
                String message = "A sales agent will contact you.";
                return new ResponseEntity<>(new ResultDto(CreditStatus.STATUS_REJECTED, 0.0, message), status);
            }
            if (timesRejected > 0
                    && (timeRequested - lastTimeRequested) < 30000) {
                // 30 seconds X 1000  converting to millis
                timesRejected++;
                HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
                return new ResponseEntity<>(status);
            }
            log.debug("processApplication");
            CreditStatus creditStatus = appService.processApplication(applicationDto, timeRequested);
            // When the credit line is accepted - If the system receives 3 or more requests within two minutes, return the http code 429.
            if (creditStatus != null && creditStatus.getStatus().equals(CreditStatus.STATUS_ACCEPTED)) {
                timesRejected = 0;
                if (creditStatus.getOverloadFlag()) {
                    HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
                    return new ResponseEntity<>(status);
                }
                HttpStatus status = HttpStatus.OK;
                //When accepted, the result should indicate that it was accepted and the credit line authorized
                return new ResponseEntity<>(new ResultDto(creditStatus.getStatus(), creditStatus.getRecommendedCredit(), "Credit line authorized"), status);
            }

            // REJECTED
            HttpStatus status = HttpStatus.OK;
            timesRejected++;
            return new ResponseEntity<>(new ResultDto(CreditStatus.STATUS_REJECTED, 0.0, ""), status);
        } finally {
            lastTimeRequested = timeRequested;
        }
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> validationErrorHandler(ConstraintViolationException e){
        List<String> errors = new ArrayList<>(e.getConstraintViolations().size());
        e.getConstraintViolations().forEach(constraintViolation -> {
            errors.add(constraintViolation.getPropertyPath() + " : " + constraintViolation.getMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    public AppController(ApplicationService appService){
        this.appService = appService;
    }

    public AppController() {
        appService = null;
    }

}
