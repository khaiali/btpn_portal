package com.sybase365.mobiliser.web.dashboard.pages.servers.beans;

import java.io.Serializable;

public class AuditStatisticsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestType;
    private String requestCount;
    private String successCount;
    private String failureCount;
    private String averageTime;

    public String getRequestType() {
	return requestType;
    }

    public void setRequestType(String requestType) {
	this.requestType = requestType;
    }

    public String getRequestCount() {
	return requestCount;
    }

    public void setRequestCount(String requestCount) {
	this.requestCount = requestCount;
    }

    public String getSuccessCount() {
	return successCount;
    }

    public void setSuccessCount(String successCount) {
	this.successCount = successCount;
    }

    public String getFailureCount() {
	return failureCount;
    }

    public void setFailureCount(String failureCount) {
	this.failureCount = failureCount;
    }

    public String getAverageTime() {
	return averageTime;
    }

    public void setAverageTime(String averageTime) {
	this.averageTime = averageTime;
    }

}
