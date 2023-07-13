package com.avillarreal.exercise.recruiting.repository;

import com.avillarreal.exercise.recruiting.entity.Offer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository
    extends PagingAndSortingRepository<Offer, Long> {
    Offer findOneByJobTitle(String jobTitle);
}
