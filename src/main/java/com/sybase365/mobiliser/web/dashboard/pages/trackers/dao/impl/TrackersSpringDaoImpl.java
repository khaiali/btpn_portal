package com.sybase365.mobiliser.web.dashboard.pages.trackers.dao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.InitializingBean;

import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.dao.ITrackersDao;

public class TrackersSpringDaoImpl 
	implements ITrackersDao, Serializable, InitializingBean {

    private static final long serialVersionUID = 1L;

    private List<TrackerBean> trackers;

    public TrackersSpringDaoImpl() {

    }

    @Override
    public void afterPropertiesSet() {
	if (this.trackers == null) {
	    throw new IllegalStateException("Configuration requires trackers list");
	}
    }

    @Override
    public void setTrackers(List<TrackerBean> value) {
	this.trackers = value;
	// allocate a unique id to the spring injected tracker beans
	for (TrackerBean tracker: trackers) {
		tracker.setId(UUID.randomUUID().toString());
	}
    }

    @Override
    public List<TrackerBean> getTrackers() {
	return this.trackers;
    }

}