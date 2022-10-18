package com.drdg.challenge;

import com.drdg.challenge.service.ApplicationService;
import com.drdg.challenge.web.model.ApplicationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ChallengeApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ApplicationService applicationService;

	@Test void processRejectedMissingFoundingType() throws Exception {
		LocalDateTime currentDateTime = LocalDateTime.of(2022, Month.OCTOBER, 15, 12, 45);
		ApplicationDto applicationDto = ApplicationDto.builder()
				.cashBalance(2000.00)
				.monthlyRevenue(1000.00)
				.requestedCreditLine(1250)
				.requestedDate(java.sql.Timestamp.valueOf(currentDateTime)).build();

		String applicationToJson = objectMapper.writeValueAsString(applicationDto);

		mockMvc.perform(post("/api/v1/challenge/processApp")
				.contentType(MediaType.APPLICATION_JSON)
				.content(applicationToJson))
				.andExpect(status().isBadRequest());
	}

	@Test void processBadFoundingType() throws Exception {
		LocalDateTime currentDateTime = LocalDateTime.of(2022, Month.OCTOBER, 15, 12, 45);
		ApplicationDto applicationDto = ApplicationDto.builder()
				.foundingType("Start")
				.cashBalance(2000.00)
				.cashBalance(2000.00)
				.monthlyRevenue(1000.00)
				.requestedCreditLine(1250)
				.requestedDate(java.sql.Timestamp.valueOf(currentDateTime)).build();

		String applicationToJson = objectMapper.writeValueAsString(applicationDto);

		mockMvc.perform(post("/api/v1/challenge/processApp")
				.contentType(MediaType.APPLICATION_JSON)
				.content(applicationToJson))
				.andExpect(status().isBadRequest());
	}
	//When it is SME, it's only needed to calculate the recommended credit line based one the monthly revenue.
	//Requested credit line is more than monthly revenue and less than cashBalance
	@Test void processRejectedSME() throws Exception {
		//"requestedDate": "2021-07-19T16:42:03.860Z"
		//One fifth of the monthly revenue (5:1 ratio)
		LocalDateTime currentDateTime = LocalDateTime.of(2022, Month.OCTOBER, 15, 12, 45);
		ApplicationDto applicationDto = ApplicationDto.builder()
				.foundingType("SME")
				.cashBalance(2000.00)
				.monthlyRevenue(1000.00)
				.requestedCreditLine(1250)
				.requestedDate(java.sql.Timestamp.valueOf(currentDateTime)).build();

		String applicationToJson = objectMapper.writeValueAsString(applicationDto);

		mockMvc.perform(post("/api/v1/challenge/processApp")
				.contentType(MediaType.APPLICATION_JSON)
				.content(applicationToJson))
				.andExpect(status().is(HttpStatus.OK.value()))
		.andReturn().getResponse().getContentAsString().contains("REJECTED");
	}

	//And when it is Startup, the recommended credit line is the maximum value between the rule based on the
	//cash balance and the monthly revenue.
	//Requested credit line is more than monthly revenue and less than cashBalance
	@Test void processRejectedStartup() throws Exception {
		//"requestedDate": "2021-07-19T16:42:03.860Z"
		LocalDateTime currentDateTime = LocalDateTime.of(2022, Month.OCTOBER, 15, 12, 45);
		ApplicationDto applicationDto = ApplicationDto.builder()
				.foundingType("SME")
				.cashBalance(435.30)
				.monthlyRevenue(4235.45)
				.requestedCreditLine(4236)
				.requestedDate(java.sql.Timestamp.valueOf(currentDateTime)).build();

		String applicationToJson = objectMapper.writeValueAsString(applicationDto);

		mockMvc.perform(post("/api/v1/challenge/processApp")
				.contentType(MediaType.APPLICATION_JSON)
				.content(applicationToJson))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andReturn().getResponse().getContentAsString().contains("REJECTED");

	}

	@Test void processRejectedTwoRequestsInLessThan30Seconds() throws Exception {
		//"requestedDate": "2021-07-19T16:42:03.860Z"
		//One fifth of the monthly revenue (5:1 ratio)
		LocalDateTime currentDateTime = LocalDateTime.of(2022, Month.OCTOBER, 15, 12, 45);
		ApplicationDto applicationDto = ApplicationDto.builder()
				.foundingType("SME")
				.cashBalance(2000.00)
				.monthlyRevenue(1000.00)
				.requestedCreditLine(1250)
				.requestedDate(java.sql.Timestamp.valueOf(currentDateTime)).build();

		String applicationToJson = objectMapper.writeValueAsString(applicationDto);
		mockMvc.perform(post("/api/v1/challenge/processApp")
				.contentType(MediaType.APPLICATION_JSON)
				.content(applicationToJson))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andReturn().getResponse().getContentAsString().contains("REJECTED");

		mockMvc.perform(post("/api/v1/challenge/processApp")
				.contentType(MediaType.APPLICATION_JSON)
				.content(applicationToJson))
				.andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()));
	}

	@Test void processAppAcceptValidSME() throws Exception {
		//"requestedDate": "2021-07-19T16:42:03.860Z"
		LocalDateTime currentDateTime = LocalDateTime.of(2022, Month.OCTOBER, 15, 12, 45);
		ApplicationDto applicationDto = ApplicationDto.builder()
				.foundingType("SME")
				.cashBalance(435.30)
				.monthlyRevenue(4235.45)
				.requestedCreditLine(100)
				.requestedDate(java.sql.Timestamp.valueOf(currentDateTime)).build();

		String applicationToJson = objectMapper.writeValueAsString(applicationDto);

		mockMvc.perform(post("/api/v1/challenge/processApp")
				.contentType(MediaType.APPLICATION_JSON)
				.content(applicationToJson))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andReturn().getResponse().getContentAsString().contains("ACCEPTED");
	}

	@Test void processAppAcceptValidStartup() throws Exception {
		//"requestedDate": "2021-07-19T16:42:03.860Z"
		LocalDateTime currentDateTime = LocalDateTime.of(2022, Month.OCTOBER, 15, 12, 45);
		ApplicationDto applicationDto = ApplicationDto.builder()
				.foundingType("Startup")
				.cashBalance(435.30)
				.monthlyRevenue(4235.45)
				.requestedCreditLine(100)
				.requestedDate(java.sql.Timestamp.valueOf(currentDateTime)).build();

		String applicationToJson = objectMapper.writeValueAsString(applicationDto);

		mockMvc.perform(post("/api/v1/challenge/processApp")
				.contentType(MediaType.APPLICATION_JSON)
				.content(applicationToJson))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andReturn().getResponse().getContentAsString().contains("ACCEPTED");
	}

}
