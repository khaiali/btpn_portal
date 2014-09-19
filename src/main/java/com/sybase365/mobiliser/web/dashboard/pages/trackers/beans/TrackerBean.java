package com.sybase365.mobiliser.web.dashboard.pages.trackers.beans;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.sybase365.mobiliser.web.dashboard.base.SpringAdapter;

public class TrackerBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TrackerBean.class);

    private String id;
    private String name;
    private TrackerChartType type;
    private int sampleInterval;
    private TimeUnit sampleIntervalTimeUnit;
    private int pointsToDisplay;
    private List<TrackerDataSeriesBean> dataSeries;

    public static enum TrackerChartType { LINE, BAR, CANDLESTICK, GAUGE };

    public TrackerBean() {
    }

    public TrackerBean(String id, String name, TrackerChartType type, 
	    int sampleInterval, TimeUnit sampleIntervalTimeUnit,
	    int pointsToDisplay) {
	this.id = id;
	this.name = name;
	this.type = type;
	this.sampleInterval = sampleInterval;
	this.sampleIntervalTimeUnit = sampleIntervalTimeUnit;
	this.pointsToDisplay = pointsToDisplay;

    }

    protected void init() {
	scheduleSampler();
    }

    protected void destroy() {
    }

    public String getId() {
	return this.id;
    }

    public void setId(String value) {
	this.id = value;
    }

    public String getName() {
	return this.name;
    }

    public void setName(String value) {
	this.name = value;
    }

    public TrackerChartType getType() {
	return this.type;
    }

    public void setType(TrackerChartType value) {
	this.type = value;
    }

    public int getSampleInterval() {
	return this.sampleInterval;
    }

    public void setSampleInterval(int value) {
	this.sampleInterval = value;
    }

    public TimeUnit getSampleIntervalTimeUnit() {
	return this.sampleIntervalTimeUnit;
    }

    public void setSampleIntervalTimeUnit(TimeUnit value) {
	this.sampleIntervalTimeUnit = value;
    }

    public int getPointsToDisplay() {
	return this.pointsToDisplay;
    }

    public void setPointsToDisplay(int value) {
	this.pointsToDisplay = value;
    }

    public List<TrackerDataSeriesBean> getDataSeries() {
	return this.dataSeries;
    }

    public void setDataSeries(List<TrackerDataSeriesBean> listValue) {
	this.dataSeries = listValue;
    }

    protected void sampleDataSeries() {
	for (TrackerDataSeriesBean dataSeriesBean : this.dataSeries) {
	    dataSeriesBean.doSample();
	}
    }

    private void scheduleSampler() {

	LOG.debug("Scheduling data series sampler for {} to run every {} {}",
		new Object[] {getName(), getSampleInterval(), 
		    getSampleIntervalTimeUnit()});

	ScheduledExecutorService executor = getScheduledExecutorService();

        final ScheduledFuture<?> samplerHandle = 
		executor.scheduleAtFixedRate(new Sampler(), 1L, 
			this.sampleInterval, this.sampleIntervalTimeUnit);
    }

    public class Sampler implements Runnable, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void run() {
	    try {
	    	sampleDataSeries();
	    }
	    catch (Exception e) {
		LOG.error("Error sampling data series", e);
	    }
	}

    }

    private ScheduledThreadPoolExecutor getScheduledExecutorService() {
    	return (ScheduledThreadPoolExecutor) SpringAdapter.getContext()
		.getBean("scheduledExecutorFactory", 
			ScheduledThreadPoolExecutor.class);
    }
}