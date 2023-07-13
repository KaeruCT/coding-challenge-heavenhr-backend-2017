package com.avillarreal.exercise.recruiting.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This class is used to keep track of all the events that took place in the application.
 * For now it just logs the event, but it could communicate with external services.
 */
@Service
public class NotificationService {
    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void notifyEvent(String object, long id, NotificationAction action, String description) {
        if (description != null) {
            log.info("{} [id={}] {}: {}", object, id, action, description);
        } else {
            log.info("{} [id={}] {}", object, id, action);
        }
    }

    public void notifyEvent(String object, long id, NotificationAction action) {
        notifyEvent(object, id, action, null);
    }
}
