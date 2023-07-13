package com.avillarreal.exercise.recruiting.request;

import java.util.Date;

public class OfferRequest {
    private String jobTitle;
    private Date startDate;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
