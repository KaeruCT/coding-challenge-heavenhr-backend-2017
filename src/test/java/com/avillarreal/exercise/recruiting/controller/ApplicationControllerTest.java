package com.avillarreal.exercise.recruiting.controller;

import com.avillarreal.exercise.recruiting.entity.Application;
import com.avillarreal.exercise.recruiting.entity.ApplicationStatus;
import com.avillarreal.exercise.recruiting.request.ApplicationRequest;
import com.avillarreal.exercise.recruiting.response.ApplicationResponse;
import com.avillarreal.exercise.recruiting.response.OfferResponse;
import com.avillarreal.exercise.recruiting.service.ApplicationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ApplicationController.class)
@EnableSpringDataWebSupport
public class ApplicationControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ApplicationService applicationService;
    
    @Test
    public void test_create() throws Exception {
        Application application = new Application();
        when(applicationService.create(any(ApplicationRequest.class))).thenReturn(application);
        
        ApplicationRequest applicationRequest = new ApplicationRequest();
    
        mockMvc.perform(post("/applications")
            .contentType(APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(applicationRequest)))
            .andExpect(status().isCreated());

        verify(applicationService, times(1)).create(any(ApplicationRequest.class));
        verifyNoMoreInteractions(applicationService);
    }

    @Test
    public void test_list() throws Exception {
        ApplicationResponse applicationResponse = getApplicationResponse();
        List<ApplicationResponse> applications = Arrays.asList(applicationResponse, new ApplicationResponse(), new ApplicationResponse());
        when(applicationService.list(any(Pageable.class))).thenReturn(new PageImpl<>(applications));
        
        mockMvc.perform(get("/applications"))
            .andExpect(jsonPath("$.content", hasSize(applications.size())))
            .andExpect(jsonPath("$.content[0].id", is((int) applications.get(0).getId())))
            .andExpect(jsonPath("$.content[0].email", is(applications.get(0).getEmail())))
            .andExpect(jsonPath("$.content[0].resumeText", is(applications.get(0).getResumeText())))
            .andExpect(jsonPath("$.content[0].status", is(applications.get(0).getStatus().name())))
            .andExpect(jsonPath("$.content[0].offer", is(notNullValue())))
            .andExpect(status().isOk());

        verify(applicationService, times(1)).list(any(Pageable.class));
        verifyNoMoreInteractions(applicationService);
    }

    @Test
    public void test_findOne() throws Exception {
        ApplicationResponse applicationResponse = getApplicationResponse();
        when(applicationService.findOne(applicationResponse.getId())).thenReturn(applicationResponse);

        mockMvc.perform(get("/applications/{id}", applicationResponse.getId()))
            .andExpect(jsonPath("$.id", is((int) applicationResponse.getId())))
            .andExpect(jsonPath("$.email", is(applicationResponse.getEmail())))
            .andExpect(jsonPath("$.resumeText", is(applicationResponse.getResumeText())))
            .andExpect(jsonPath("$.status", is(applicationResponse.getStatus().name())))
            .andExpect(jsonPath("$.offer", is(notNullValue())))
            .andExpect(status().isOk());

        verify(applicationService, times(1)).findOne(applicationResponse.getId());
        verifyNoMoreInteractions(applicationService);
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