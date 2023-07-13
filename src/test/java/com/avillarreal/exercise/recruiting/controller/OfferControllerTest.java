package com.avillarreal.exercise.recruiting.controller;

import com.avillarreal.exercise.recruiting.entity.ApplicationStatus;
import com.avillarreal.exercise.recruiting.entity.Offer;
import com.avillarreal.exercise.recruiting.request.OfferRequest;
import com.avillarreal.exercise.recruiting.response.ApplicationResponse;
import com.avillarreal.exercise.recruiting.response.OfferResponse;
import com.avillarreal.exercise.recruiting.service.ApplicationService;
import com.avillarreal.exercise.recruiting.service.OfferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(OfferController.class)
@EnableSpringDataWebSupport
public class OfferControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private OfferService offerService;
    @MockBean private ApplicationService applicationService;
    
    @Test
    public void test_create() throws Exception {
        Offer offer = new Offer();
        when(offerService.create(any(OfferRequest.class))).thenReturn(offer);
        
        OfferRequest offerRequest = new OfferRequest();
    
        mockMvc.perform(post("/offers")
            .contentType(APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(offerRequest)))
            .andExpect(status().isCreated());

        verify(offerService, times(1)).create(any(OfferRequest.class));
        verifyNoMoreInteractions(offerService);
    }

    @Test
    public void test_list() throws Exception {
        OfferResponse offerResponse = getOfferResponse();
        List<OfferResponse> offers = Arrays.asList(offerResponse, new OfferResponse(), new OfferResponse());
        when(offerService.list(any(Pageable.class))).thenReturn(new PageImpl<>(offers));
        
        mockMvc.perform(get("/offers"))
            .andExpect(jsonPath("$.content", hasSize(offers.size())))
            .andExpect(jsonPath("$.content[0].id", is((int) offers.get(0).getId())))
            .andExpect(jsonPath("$.content[0].jobTitle", is(offers.get(0).getJobTitle())))
            .andExpect(jsonPath("$.content[0].numberOfApplications", is((int)offers.get(0).getNumberOfApplications())))
            .andExpect(jsonPath("$.content[0].startDate", is(objectMapper.writeValueAsString(offers.get(0).getStartDate()).replace("\"", ""))))
            .andExpect(status().isOk());

        verify(offerService, times(1)).list(any(Pageable.class));
        verifyNoMoreInteractions(offerService);
    }

    @Test
    public void test_findOne() throws Exception {
        OfferResponse offerResponse = getOfferResponse();
        when(offerService.findOne(offerResponse.getId())).thenReturn(offerResponse);

        mockMvc.perform(get("/offers/{id}", offerResponse.getId()))
            .andExpect(jsonPath("$.id", is((int) offerResponse.getId())))
            .andExpect(jsonPath("$.jobTitle", is(offerResponse.getJobTitle())))
            .andExpect(jsonPath("$.numberOfApplications", is((int)offerResponse.getNumberOfApplications())))
            .andExpect(jsonPath("$.startDate", is(objectMapper.writeValueAsString(offerResponse.getStartDate()).replace("\"", ""))))
            .andExpect(status().isOk());

        verify(offerService, times(1)).findOne(offerResponse.getId());
        verifyNoMoreInteractions(offerService);
    }
    
    @Test
    public void test_listApplicationsByOffer() throws Exception {
        long id = 1;
        ApplicationResponse applicationResponse = getApplicationResponse();
        List<ApplicationResponse> applications = Arrays.asList(applicationResponse, new ApplicationResponse(), new ApplicationResponse());
        when(applicationService.listByOffer(eq(id), any(Pageable.class))).thenReturn(new PageImpl<>(applications));

        mockMvc.perform(get("/offers/1/applications", id))
            .andExpect(jsonPath("$.content", hasSize(applications.size())))
            .andExpect(jsonPath("$.content[0].id", is((int) applications.get(0).getId())))
            .andExpect(jsonPath("$.content[0].email", is(applications.get(0).getEmail())))
            .andExpect(jsonPath("$.content[0].resumeText", is(applications.get(0).getResumeText())))
            .andExpect(jsonPath("$.content[0].status", is(applications.get(0).getStatus().name())))
            .andExpect(jsonPath("$.content[0].offer", is(notNullValue())))
            .andExpect(status().isOk());

        verify(applicationService, times(1)).listByOffer(eq(id), any(Pageable.class));
        verifyNoMoreInteractions(applicationService);
    }
    
    private OfferResponse getOfferResponse() {
        OfferResponse offerResponse = new OfferResponse();
        offerResponse.setId(1);
        offerResponse.setJobTitle("Java Engineer");
        offerResponse.setNumberOfApplications(4);
        offerResponse.setStartDate(new Date());
        return offerResponse;
    }

    private ApplicationResponse getApplicationResponse() {
        ApplicationResponse applicationResponse = new ApplicationResponse();

        OfferResponse offerResponse = new OfferResponse();
        offerResponse.setId(1);
        offerResponse.setJobTitle("Java Engineer");
        offerResponse.setNumberOfApplications(4);
        offerResponse.setStartDate(new Date());

        applicationResponse.setId(1);
        applicationResponse.setResumeText("resume text");
        applicationResponse.setEmail("test_email@gmail.com");
        applicationResponse.setStatus(ApplicationStatus.APPLIED);
        applicationResponse.setOffer(offerResponse);
        return applicationResponse;
    }
}