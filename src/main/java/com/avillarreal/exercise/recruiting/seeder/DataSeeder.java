package com.avillarreal.exercise.recruiting.seeder;

import com.avillarreal.exercise.recruiting.entity.Offer;
import com.avillarreal.exercise.recruiting.request.ApplicationRequest;
import com.avillarreal.exercise.recruiting.request.OfferRequest;
import com.avillarreal.exercise.recruiting.service.ApplicationService;
import com.avillarreal.exercise.recruiting.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

/**
 * This class initializes the application with some sample data
 */
@Component
public class DataSeeder {
    @Autowired private ApplicationService applicationService;
    @Autowired private OfferService offerService;
    
    @EventListener
    public void seed(ContextRefreshedEvent event) {
        Random rand = new Random();
        String[] titles = new String[]{
            "Java Engineer",
            "Frontend Engineer",
            "PHP Developer",
            "DevOps Engineer",
            "Game Developer",
            "Mobile Engineer",
            "Software Architect",
        };
        
        // initialize the application with some useful data
        for (String title : titles) {
            OfferRequest offerRequest = new OfferRequest();
            offerRequest.setStartDate(Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            offerRequest.setJobTitle(title);
            Offer offer = offerService.create(offerRequest);
            
            for (int i = 1; i <= rand.nextInt(3); i++) {
                ApplicationRequest applicationRequest = new ApplicationRequest();
                String email = "candidate_email_" + i + "@gmail.com";
                applicationRequest.setOfferId(offer.getId());
                applicationRequest.setEmail(email);
                applicationRequest.setResumeText("Resume text for candidate " + email);
                applicationService.create(applicationRequest);
            }
        }
    }

}
