package com.avillarreal.exercise.recruiting.response;

import java.util.Date;

public class OfferResponse {
    private long id;
    private String jobTitle;
    private Date startDate;
    private long numberOfApplications;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public long getNumberOfApplications() {
        return numberOfApplications;
    }

    public void setNumberOfApplications(long numberOfApplications) {
        this.numberOfApplications = numberOfApplications;
    }
}
