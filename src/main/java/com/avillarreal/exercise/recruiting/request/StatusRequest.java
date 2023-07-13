package com.avillarreal.exercise.recruiting.request;

import com.avillarreal.exercise.recruiting.entity.ApplicationStatus;

public class StatusRequest {
    private ApplicationStatus status;

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}
