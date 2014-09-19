package com.sybase365.mobiliser.web.dashboard.pages.trackers.beans;

import java.io.Serializable;

public class TrackerGroupBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private TrackerBean[] trackerGroup;

    public TrackerGroupBean(String id, int groupSize) {
	this.id = id;
	this.trackerGroup = new TrackerBean[groupSize];
    }

    public String getId() {
	return this.id;
    }

    public TrackerBean[] getAllTrackerBeans() {
	return this.trackerGroup != null ? this.trackerGroup.clone() : null;
    }

    public TrackerBean getTrackerBean(int index)
	    throws IndexOutOfBoundsException {

	if (index > trackerGroup.length || index < 0) {
	    throw new IndexOutOfBoundsException();
	}

	return this.trackerGroup[index];
    }

    public TrackerBean getTrackerHolder() {
	return null;
    }

    public void addTrackerBeanToGroup(int index, TrackerBean trackerBean)
	    throws IndexOutOfBoundsException {

	if (index > trackerGroup.length || index < 0) {
	    throw new IndexOutOfBoundsException();
	}

	trackerGroup[index] = trackerBean;
    }
}