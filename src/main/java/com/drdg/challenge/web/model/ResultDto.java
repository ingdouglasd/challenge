package com.drdg.challenge.web.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * When accepted, the result should indicate that it was accepted and the credit line authorized
 */

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Data
public class ResultDto implements Serializable {
    @NotNull
    private String status;
    @NotNull
    private Double creditLineAuthorized;
    @Null
    private String message;

}
