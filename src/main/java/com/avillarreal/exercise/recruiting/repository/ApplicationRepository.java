package com.avillarreal.exercise.recruiting.repository;

import com.avillarreal.exercise.recruiting.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository
     extends PagingAndSortingRepository<Application, Long> {
    long countByOfferId(long id);
    Application findOneByEmailAndOfferId(String email, long offerId);
    Page<Application> findAllByOfferId(long id, Pageable pageable);
}
