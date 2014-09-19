package com.sybase365.mobiliser.web.dashboard.pages.trackers.dao;

import java.util.List;

import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;

public interface ITrackersDao {

    public void setTrackers(List<TrackerBean> value);

    public List<TrackerBean> getTrackers();

}