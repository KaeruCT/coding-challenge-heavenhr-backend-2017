package com.avillarreal.exercise.recruiting.service;

import com.avillarreal.exercise.recruiting.entity.Offer;
import com.avillarreal.exercise.recruiting.repository.OfferRepository;
import com.avillarreal.exercise.recruiting.request.OfferRequest;
import com.avillarreal.exercise.recruiting.response.OfferResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.avillarreal.exercise.recruiting.service.NotificationAction.CREATED;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OfferService {
    @Autowired private OfferRepository offerRepository;
    @Autowired private ApplicationService applicationService;
    @Autowired private NotificationService notificationService;
    @Autowired private ModelMapper modelMapper;

    @Transactional
    public Offer create(OfferRequest offerRequest) {
        Offer existingOffer = offerRepository.findOneByJobTitle(offerRequest.getJobTitle());
        if (existingOffer != null) {
            throw new ServiceException("offer_exists", CONFLICT);
        }
        Offer offer = new Offer();
        offer.setJobTitle(offerRequest.getJobTitle());
        offer.setStartDate(offerRequest.getStartDate());
        offer = offerRepository.save(offer);

        notificationService.notifyEvent(getClass().getSimpleName(), offer.getId(), CREATED);
        
        return offer;
    }

    @Transactional(readOnly = true)
    public Page<OfferResponse> list(Pageable pageable) {
        return offerRepository.findAll(pageable).map(this::convert);
    }

    @Transactional(readOnly = true)
    public OfferResponse findOne(long id) {
        Offer offer = offerRepository.findOne(id);
        if (offer == null) {
            throw new ServiceException("offer_not_found", NOT_FOUND);
        }
        return convert(offer);
    }
    
    private OfferResponse convert(Offer offer) {
        OfferResponse offerResponse = modelMapper.map(offer, OfferResponse.class);
        offerResponse.setNumberOfApplications(applicationService.countByOffer(offer.getId()));
        return offerResponse;
    }
}
