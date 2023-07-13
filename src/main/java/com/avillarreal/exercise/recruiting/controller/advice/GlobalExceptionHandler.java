package com.avillarreal.exercise.recruiting.controller.advice;

import com.avillarreal.exercise.recruiting.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Map<String, String>> handle(ServiceException e) {
        log.error("ServiceException [{}]: {}", e.getStatus(), e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(response);
    }
}
