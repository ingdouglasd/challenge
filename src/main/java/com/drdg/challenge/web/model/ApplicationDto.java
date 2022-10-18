package com.drdg.challenge.web.model;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/*
* The credit line model has 5 inputs:
Business type:  It can be: Startup SME
Cash balance - The customer's bank account balance
Monthly revenue - The total sales revenue for the month
Requested credit line
Requested date - Represents when request was made
*/
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class ApplicationDto implements Serializable {
    @Null
    private UUID id;
    @NotNull
    @NotEmpty(message = "foundingType' field was empty")
    private String foundingType;
    @Positive
    private Double cashBalance;
    @NotNull
    @Positive
    private Double monthlyRevenue;
    @NotNull
    @Positive
    private Integer requestedCreditLine;
    @NotNull
    @NotNull
    private Date requestedDate;
    @Null
    private transient Date systemRequestedDate;

}
