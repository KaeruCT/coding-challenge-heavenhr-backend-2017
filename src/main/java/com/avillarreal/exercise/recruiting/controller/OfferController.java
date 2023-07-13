package com.avillarreal.exercise.recruiting.controller;


import com.avillarreal.exercise.recruiting.request.OfferRequest;
import com.avillarreal.exercise.recruiting.response.ApplicationResponse;
import com.avillarreal.exercise.recruiting.response.OfferResponse;
import com.avillarreal.exercise.recruiting.service.ApplicationService;
import com.avillarreal.exercise.recruiting.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/offers")
public class OfferController {
    @Autowired private OfferService offerService;
    @Autowired private ApplicationService applicationService;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = POST)
    public void create(@RequestBody OfferRequest offerRequest) {
        offerService.create(offerRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = GET, path = "/{id}")
    public OfferResponse findOne(@PathVariable long id) {
        return offerService.findOne(id);
    }
    
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = GET, path = "/{id}/applications")
    public Page<ApplicationResponse> listApplications(@PathVariable long id, Pageable pageable) {
        return applicationService.listByOffer(id, pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = GET)
    public Page<OfferResponse> list(Pageable pageable) {
        return offerService.list(pageable);
    }
}
