package com.avillarreal.exercise.recruiting.service;

import com.avillarreal.exercise.recruiting.entity.Application;
import com.avillarreal.exercise.recruiting.entity.ApplicationStatus;
import com.avillarreal.exercise.recruiting.entity.Offer;
import com.avillarreal.exercise.recruiting.repository.ApplicationRepository;
import com.avillarreal.exercise.recruiting.repository.OfferRepository;
import com.avillarreal.exercise.recruiting.request.ApplicationRequest;
import com.avillarreal.exercise.recruiting.response.ApplicationResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.avillarreal.exercise.recruiting.entity.ApplicationStatus.APPLIED;
import static com.avillarreal.exercise.recruiting.entity.ApplicationStatus.HIRED;
import static com.avillarreal.exercise.recruiting.entity.ApplicationStatus.INVITED;
import static com.avillarreal.exercise.recruiting.entity.ApplicationStatus.REJECTED;
import static com.avillarreal.exercise.recruiting.service.NotificationAction.CREATED;
import static com.avillarreal.exercise.recruiting.service.NotificationAction.UPDATED;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Service
public class ApplicationService {
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private OfferRepository offerRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private ModelMapper modelMapper;
    
    private final static Map<ApplicationStatus, List<ApplicationStatus>> VALID_STATUSES = new HashMap<>();
    
    @PostConstruct
    public void init() {
        VALID_STATUSES.put(APPLIED, Arrays.asList(INVITED, REJECTED));
        VALID_STATUSES.put(INVITED, Arrays.asList(HIRED, REJECTED));
        VALID_STATUSES.put(REJECTED, Collections.emptyList());
        VALID_STATUSES.put(HIRED, Collections.emptyList());
    }

    @Transactional(readOnly = true)
    public long countByOffer(Long id) {
        return applicationRepository.countByOfferId(id);
    }

    @Transactional
    public Application create(ApplicationRequest applicationRequest) {
        if (StringUtils.isEmpty(StringUtils.trimWhitespace(applicationRequest.getEmail()))) {
            throw new ServiceException("empty_email", UNPROCESSABLE_ENTITY);
        }
        if (StringUtils.isEmpty(StringUtils.trimWhitespace(applicationRequest.getResumeText()))) {
            throw new ServiceException("empty_resume_text", UNPROCESSABLE_ENTITY);
        }
        
        Offer offer = offerRepository.findOne(applicationRequest.getOfferId());
        if (offer == null) {
            throw new ServiceException("offer_not_found", NOT_FOUND);
        }
        
        Application existingApplication = applicationRepository.findOneByEmailAndOfferId(applicationRequest.getEmail(), applicationRequest.getOfferId());
        if (existingApplication != null) {
            throw new ServiceException("application_exists", CONFLICT);
        }
        
        Application application = new Application();
        application.setEmail(applicationRequest.getEmail());
        application.setResumeText(applicationRequest.getResumeText());
        application.setOffer(offer);
        application.setStatus(APPLIED);

        application = applicationRepository.save(application);
        notificationService.notifyEvent(getClass().getSimpleName(), application.getId(), CREATED);
        return application;
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> list(Pageable pageable) {
        return applicationRepository.findAll(pageable).map(this::convert);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse findOne(long id) {
        Application application = applicationRepository.findOne(id);
        if (application == null) {
            throw new ServiceException("application_not_found", NOT_FOUND);
        }
        return convert(application);
    }

    @Transactional
    public Application progressStatus(long id, ApplicationStatus status) {
        Application application = applicationRepository.findOne(id);
        if (application == null) {
            throw new ServiceException("application_not_found", NOT_FOUND);
        }
        ApplicationStatus prevStatus = application.getStatus();
        validateStatus(prevStatus, status);
        
        application.setStatus(status);
        application = applicationRepository.save(application);

        notificationService.notifyEvent(getClass().getSimpleName(), application.getId(), UPDATED, "status changed from " + prevStatus + " to " + application.getStatus());
        return application;
    }

    public Page<ApplicationResponse> listByOffer(long id, Pageable pageable) {
        return applicationRepository.findAllByOfferId(id, pageable).map(this::convert);
    }
    
    private void validateStatus(ApplicationStatus current, ApplicationStatus next) {
        if (!VALID_STATUSES.get(current).contains(next)) {
            throw new ServiceException("invalid_status", UNPROCESSABLE_ENTITY);
        }
    }

    private ApplicationResponse convert(Application application) {
        ApplicationResponse applicationResponse = modelMapper.map(application, ApplicationResponse.class);
        applicationResponse.getOffer().setNumberOfApplications(countByOffer(applicationResponse.getOffer().getId()));
        return applicationResponse;
    }
}
