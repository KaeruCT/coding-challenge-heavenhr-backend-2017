package com.avillarreal.exercise.recruiting.controller;

import com.avillarreal.exercise.recruiting.entity.Application;
import com.avillarreal.exercise.recruiting.entity.Offer;
import com.avillarreal.exercise.recruiting.repository.ApplicationRepository;
import com.avillarreal.exercise.recruiting.request.ApplicationRequest;
import com.avillarreal.exercise.recruiting.request.OfferRequest;
import com.avillarreal.exercise.recruiting.request.StatusRequest;
import com.avillarreal.exercise.recruiting.service.OfferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static com.avillarreal.exercise.recruiting.entity.ApplicationStatus.APPLIED;
import static com.avillarreal.exercise.recruiting.entity.ApplicationStatus.HIRED;
import static com.avillarreal.exercise.recruiting.entity.ApplicationStatus.INVITED;
import static java.lang.Math.toIntExact;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@SpringBootTest
public class ApplicationControllerIntegrationTest {
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OfferService offerService;
    @Autowired private ApplicationRepository applicationRepository;
    private MockMvc mockMvc;
    
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    public void test_createAndHire() throws Exception {
        applicationRepository.deleteAll();
        
        OfferRequest offerRequest = new OfferRequest();
        offerRequest.setJobTitle("Job title for integration test");
        offerRequest.setStartDate(new Date());
        Offer offer = offerService.create(offerRequest);
        
        ApplicationRequest applicationRequest = new ApplicationRequest();
        applicationRequest.setOfferId(offer.getId());
        applicationRequest.setEmail("test_email@gmail.com");
        applicationRequest.setResumeText("sample text");

        // create application
        mockMvc.perform(post("/applications")
            .contentType(APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(applicationRequest)))
            .andExpect(status().isCreated());

        // verify our created application exists
        mockMvc.perform(get("/applications"))
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].email", is(applicationRequest.getEmail())))
            .andExpect(jsonPath("$.content[0].resumeText", is(applicationRequest.getResumeText())))
            .andExpect(jsonPath("$.content[0].status", is(APPLIED.name())))
            .andExpect(jsonPath("$.content[0].offer.id", is(toIntExact(offer.getId()))))
            .andExpect(status().isOk());
        
        Application application = applicationRepository.findOneByEmailAndOfferId(applicationRequest.getEmail(), offer.getId());
        
        StatusRequest statusRequest = new StatusRequest();
        statusRequest.setStatus(INVITED);
        // progress the application to "INVITED"
        mockMvc.perform(patch("/applications/{id}", application.getId())
            .contentType(APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(statusRequest)))
            .andExpect(status().isOk());

        statusRequest.setStatus(HIRED);
        // progress the application to "HIRED"
        mockMvc.perform(patch("/applications/{id}", application.getId())
            .contentType(APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(statusRequest)))
            .andExpect(status().isOk());
        
        // verify our application has the "HIRED" status
        mockMvc.perform(get("/applications/{id}", application.getId()))
            .andExpect(jsonPath("$.status", is(HIRED.name())))
            .andExpect(status().isOk());
    }

    // imagine there are more tests that cover all the other ugly cases
}
