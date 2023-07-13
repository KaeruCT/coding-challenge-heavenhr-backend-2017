package com.avillarreal.exercise.recruiting.controller;

import com.avillarreal.exercise.recruiting.request.ApplicationRequest;
import com.avillarreal.exercise.recruiting.request.StatusRequest;
import com.avillarreal.exercise.recruiting.response.ApplicationResponse;
import com.avillarreal.exercise.recruiting.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(("/applications"))
public class ApplicationController {
    @Autowired private ApplicationService applicationService;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = POST)
    public void create(@RequestBody ApplicationRequest applicationRequest) {
        applicationService.create(applicationRequest);
    }
    
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = PATCH, path = "/{id}")
    public void progressStatus(@PathVariable long id, @RequestBody StatusRequest statusRequest) {
        applicationService.progressStatus(id, statusRequest.getStatus());
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = GET, path = "/{id}")
    public ApplicationResponse findOne(@PathVariable long id) {
        return applicationService.findOne(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = GET)
    public Page<ApplicationResponse> list(Pageable pageable) {
        return applicationService.list(pageable);
    }
}
