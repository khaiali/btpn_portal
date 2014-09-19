package com.sybase365.mobiliser.web.dashboard.pages.servers.beans;

public class EventQueueBean implements java.io.Serializable {
    String name;
    String size;
    String maxSize;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getSize() {
	return size;
    }

    public void setSize(String size) {
	this.size = size;
    }

    public String getMaxSize() {
	return maxSize;
    }

    public void setMaxSize(String maxSize) {
	this.maxSize = maxSize;
    }

}
