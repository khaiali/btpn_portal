package com.sybase365.mobiliser.web.dashboard.pages.servers.beans;

public class EventTaskHandlerBean implements java.io.Serializable {

    String handlerName;
    String statusDescription;
    String eventName;

    String avgProcessTime;
    String configuredMaxActiveThreads;
    String configuredMaxIdleThreads;
    String currentActiveThreads;
    String currentIdleThreads;

    String lastFail;
    String lastRun;
    String noCatchup;
    String noExpire;
    String noFail;
    String noProcessed;
    String noRuns;
    String noSuccess;

    public String getHandlerName() {
	return handlerName;
    }

    public void setHandlerName(String handlerName) {
	this.handlerName = handlerName;
    }

    public String getStatusDescription() {
	return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
	this.statusDescription = statusDescription;
    }

    public String getEventName() {
	return eventName;
    }

    public void setEventName(String eventName) {
	this.eventName = eventName;
    }

    public String getAvgProcessTime() {
	return avgProcessTime;
    }

    public void setAvgProcessTime(String avgProcessTime) {
	this.avgProcessTime = avgProcessTime;
    }

    public String getConfiguredMaxActiveThreads() {
	return configuredMaxActiveThreads;
    }

    public void setConfiguredMaxActiveThreads(String configuredMaxActiveThreads) {
	this.configuredMaxActiveThreads = configuredMaxActiveThreads;
    }

    public String getConfiguredMaxIdleThreads() {
	return configuredMaxIdleThreads;
    }

    public void setConfiguredMaxIdleThreads(String configuredMaxIdleThreads) {
	this.configuredMaxIdleThreads = configuredMaxIdleThreads;
    }

    public String getCurrentActiveThreads() {
	return currentActiveThreads;
    }

    public void setCurrentActiveThreads(String currentActiveThreads) {
	this.currentActiveThreads = currentActiveThreads;
    }

    public String getCurrentIdleThreads() {
	return currentIdleThreads;
    }

    public void setCurrentIdleThreads(String currentIdleThreads) {
	this.currentIdleThreads = currentIdleThreads;
    }

    public String getLastFail() {
	return lastFail;
    }

    public void setLastFail(String lastFail) {
	this.lastFail = lastFail;
    }

    public String getLastRun() {
	return lastRun;
    }

    public void setLastRun(String lastRun) {
	this.lastRun = lastRun;
    }

    public String getNoCatchup() {
	return noCatchup;
    }

    public void setNoCatchup(String noCatchup) {
	this.noCatchup = noCatchup;
    }

    public String getNoExpire() {
	return noExpire;
    }

    public void setNoExpire(String noExpire) {
	this.noExpire = noExpire;
    }

    public String getNoFail() {
	return noFail;
    }

    public void setNoFail(String noFail) {
	this.noFail = noFail;
    }

    public String getNoProcessed() {
	return noProcessed;
    }

    public void setNoProcessed(String noProcessed) {
	this.noProcessed = noProcessed;
    }

    public String getNoRuns() {
	return noRuns;
    }

    public void setNoRuns(String noRuns) {
	this.noRuns = noRuns;
    }

    public String getNoSuccess() {
	return noSuccess;
    }

    public void setNoSuccess(String noSuccess) {
	this.noSuccess = noSuccess;
    }

}
