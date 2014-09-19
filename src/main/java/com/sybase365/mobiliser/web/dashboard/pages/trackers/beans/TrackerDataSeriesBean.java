package com.sybase365.mobiliser.web.dashboard.pages.trackers.beans;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.sybase365.mobiliser.web.dashboard.pages.trackers.dao.ITrackersDataSeriesDao;

public class TrackerDataSeriesBean implements Serializable, InitializingBean {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TrackerDataSeriesBean.class);

    private String server;
    private String objectName;
    private String attribute;
    private String keyName;
    private String keyValue;
    private String valueName;
    private int numberOfDataPoints = -1;
    private double factor = 1;
    private DataPointMap dataPoints;
    private ITrackersDataSeriesDao dataSeriesDao;

    public TrackerDataSeriesBean() {

    }

    public TrackerDataSeriesBean(String server, 
	    String objectName, String attribute, int numberOfDataPoints) {
	this.server = server;
	this.objectName = objectName;
	this.attribute = attribute;
	this.numberOfDataPoints = numberOfDataPoints;
    }

    public TrackerDataSeriesBean(String server, 
	    String objectName, String attribute, String keyName, 
	    String keyValue, String valueName, int numberOfDataPoints) {
	this.server = server;
	this.objectName = objectName;
	this.attribute = attribute;
	this.keyName = keyName;
	this.keyValue = keyValue;
	this.valueName = valueName;
	this.numberOfDataPoints = numberOfDataPoints;
    }
    @Override
    public void afterPropertiesSet() {
	if (this.dataSeriesDao == null) {
	    throw new IllegalStateException("Configuration requires data series dao");
	}
	if (this.server == null) {
	    throw new IllegalStateException("Configuration requires server location");
	}
	if (this.objectName == null) {
	    throw new IllegalStateException("Configuration requires jmx object name");
	}
	if (this.attribute == null) {
	    throw new IllegalStateException("Configuration requires jmx attribute name");
	}
	if (this.numberOfDataPoints == -1) {
	    throw new IllegalStateException("Configuration requires number of data points");
	}
    }

    public String getServer() {
	return this.server;
    }

    public void setServer(String value) {
	this.server = value;
    }

    public String getObjectName() {
	return this.objectName;
    }

    public void setObjectName(String value) {
	this.objectName = value;
    }

    public String getAttribute() {
	return this.attribute;
    }

    public void setAttribute(String value) {
	this.attribute = value;
    }

    public String getKeyName() {
	return this.keyName;
    }

    public void setKeyName(String value) {
	this.keyName = value;
    }

    public String getKeyValue() {
	return this.keyValue;
    }

    public void setKeyValue(String value) {
	this.keyValue = value;
    }

    public String getValueName() {
	return this.valueName;
    }

    public void setValueName(String value) {
	this.valueName = value;
    }

    public int getNumberOfDataPoints() {
	return this.numberOfDataPoints;
    }

    public void setNumberOfDataPoints(int value) {
	this.numberOfDataPoints = value;
	this.dataPoints = new DataPointMap(this.numberOfDataPoints);
    }

    public double getFactor() {
	return this.factor;
    }

    public void setFactor(double value) {
	this.factor = value;
    }

    public ITrackersDataSeriesDao getDataSeriesDao() {
	return this.dataSeriesDao;
    }

    public void setDataSeriesDao(ITrackersDataSeriesDao value) {
	this.dataSeriesDao = value;
    }

    public DataPointMap getDataPoints() {
	return this.dataPoints;
    }

    public void addDataPoint(String value) {
	this.dataPoints.put(new Long(System.nanoTime()), value);
    }

    @Override
    public String toString() {
	return server+":"+objectName+":"+attribute;
    }

    public void doSample() {
	addDataPoint(dataSeriesDao.sample(server, objectName, 
		attribute, keyName, keyValue, valueName));
    }

    // this is a map that guarantees to hold only a fixed max number of entries
    // with the eldest being automatically evicted when at the max size and 
    // anther entry is added 
    public class DataPointMap extends LinkedHashMap<Long, String> {

	private int maxDataPoints;

	public DataPointMap(int value) {
	    super();
	    this.maxDataPoints = value;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry eldest) {
	    return size() > maxDataPoints;
	}
    }
}