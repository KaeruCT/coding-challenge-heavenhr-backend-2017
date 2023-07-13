package com.avillarreal.exercise.recruiting.service;

import com.avillarreal.exercise.recruiting.entity.Application;
import com.avillarreal.exercise.recruiting.entity.Offer;
import com.avillarreal.exercise.recruiting.repository.ApplicationRepository;
import com.avillarreal.exercise.recruiting.repository.OfferRepository;
import com.avillarreal.exercise.recruiting.request.ApplicationRequest;
import com.avillarreal.exercise.recruiting.response.ApplicationResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static com.avillarreal.exercise.recruiting.entity.ApplicationStatus.APPLIED;
import static com.avillarreal.exercise.recruiting.entity.ApplicationStatus.HIRED;
import static com.avillarreal.exercise.recruiting.entity.ApplicationStatus.INVITED;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class ApplicationServiceTest {
    
    @MockBean private ApplicationRepository applicationRepository;
    @MockBean private OfferRepository offerRepository;
    @MockBean private NotificationService notificationService;
    @Rule public ExpectedException thrown = ExpectedException.none();
    @Autowired private ApplicationService applicationService;

    @TestConfiguration
    static class Config {
        @Bean
        public ApplicationService applicationService() {
            return new ApplicationService();
        }
        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }

        @Test
    public void test_countByOffer() {
        long id = 1;
        long count = 5;
        when(applicationRepository.countByOfferId(id)).thenReturn(count);
        
        assertThat(applicationRepository.countByOfferId(id), is(count));
    }
    
    @Test
    public void test_create() {
        long id = 1;
        Offer offer = new Offer();
        
        when(offerRepository.findOne(id)).thenReturn(offer);
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> {
            Application application = (Application)i.getArguments()[0];
            application.setId(1l);
            return application;
        });
        
        ApplicationRequest applicationRequest = new ApplicationRequest();
        applicationRequest.setResumeText("resume text");
        applicationRequest.setEmail("test_email@gmail.com");
        applicationRequest.setOfferId(id);
        
        Application application = applicationService.create(applicationRequest);
        assertThat(application.getEmail(), is(applicationRequest.getEmail()));
        assertThat(application.getResumeText(), is(applicationRequest.getResumeText()));
        assertThat(application.getStatus(), is(APPLIED));
        assertThat(application.getOffer(), is(offer));
    }
    
    @Test
    public void test_create_emptyEmail() {
        ApplicationRequest applicationRequest = new ApplicationRequest();
        applicationRequest.setResumeText("resume text");
        applicationRequest.setOfferId(1l);
        
        thrown.expect(ServiceException.class);
        
        applicationService.create(applicationRequest);

        thrown.expectMessage("empty_resume_text");
    }

    @Test
    public void test_create_existingOffer() {
        long id = 1;
        Offer offer = new Offer();

        when(offerRepository.findOne(id)).thenReturn(offer);
        
        ApplicationRequest applicationRequest = new ApplicationRequest();
        applicationRequest.setResumeText("resume text");
        applicationRequest.setEmail("test_email@gmail.com");
        applicationRequest.setOfferId(id);

        Application application = getApplication();
        when(applicationRepository.findOneByEmailAndOfferId(applicationRequest.getEmail(), applicationRequest.getOfferId())).thenReturn(application);

        thrown.expect(ServiceException.class);

        applicationService.create(applicationRequest);

        thrown.expectMessage("existing_offer");
    }
    
    // imagine there are more tests that cover all the other validation cases
    
    @Test
    public void test_list() {
        Page<Application> page = new PageImpl<>(Arrays.asList(getApplication(), getApplication()));
        when(applicationRepository.findAll(any(Pageable.class))).thenReturn(page);
        
        Page<ApplicationResponse> result = applicationService.list(new PageRequest(0, 25));
        assertThat(result.getNumberOfElements(), is(page.getNumberOfElements()));
    }
    
    @Test
    public void test_findOne() {
        long id = 1;
        Application application = getApplication();
        application.setId(id);
        when(applicationRepository.findOne(id)).thenReturn(application);
        
        assertThat(applicationService.findOne(id).getId(), is(application.getId()));
    }
    
    @Test
    public void test_progressStatus() {
        long id = 1;
        Application application = getApplication();
        application.setStatus(APPLIED);
        application.setId(id);
        when(applicationRepository.findOne(id)).thenReturn(application);
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArguments()[0]);
        
        application = applicationService.progressStatus(id, INVITED);
        assertThat(application.getStatus(), is(INVITED));

        application.setStatus(INVITED);
        application = applicationService.progressStatus(id, HIRED);
        assertThat(application.getStatus(), is(HIRED));
    }

    @Test
    public void test_progressStatus_invalid_hired() {
        long id = 1;
        Application application = getApplication();
        application.setOffer(new Offer());
        application.setStatus(APPLIED);
        when(applicationRepository.findOne(id)).thenReturn(application);
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArguments()[0]);

        thrown.expect(ServiceException.class);
        
        applicationService.progressStatus(id, HIRED);
        thrown.expectMessage("invalid_status");
    }

    @Test
    public void test_progressStatus_invalid_invited() {
        long id = 1;
        Application application = getApplication();
        application.setOffer(new Offer());
        application.setStatus(INVITED);
        when(applicationRepository.findOne(id)).thenReturn(application);
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArguments()[0]);

        thrown.expect(ServiceException.class);

        applicationService.progressStatus(id, APPLIED);
        thrown.expectMessage("invalid_status");
    }

    // imagine there are more tests that cover all the other validation cases
    
    private Application getApplication() {
        Application application = new Application();
        Offer offer = new Offer();
        offer.setId(1l);
        application.setOffer(offer);
        return application;
    }
}