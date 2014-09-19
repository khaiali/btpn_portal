package com.sybase365.mobiliser.web.dashboard.pages.trackers.dao;

public interface ITrackersDataSeriesDao {

    public String sample(String server, String objectName, String attribute, 
	    String keyName, String keyValue, String valueName);

}