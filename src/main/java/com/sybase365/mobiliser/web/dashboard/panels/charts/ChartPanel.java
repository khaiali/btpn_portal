package com.sybase365.mobiliser.web.dashboard.panels.charts;

import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerDataSeriesBean;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerDataSeriesBean.DataPointMap;

public class ChartPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ChartPanel.class);

    protected final TrackerBean trackerBean;
    protected final MobiliserBasePage mobBasePage;
    protected final FeedbackPanel feedBackPanel;

    protected long minValue = 0;
    protected long maxValue = 1;

    public ChartPanel(String id, TrackerBean trackerBean,
	    MobiliserBasePage mobBasePage, FeedbackPanel feedBackPanel) {

	super(id);

	this.trackerBean = trackerBean;
	this.mobBasePage = mobBasePage;
	this.feedBackPanel = feedBackPanel;
    }

    private long updateRange(long value) {
	if (value < minValue) {
	    minValue = value;
	}
	if (value > maxValue) {
	    maxValue = value;
	}
	return value;
    }

    protected long convertDataPoint(String dataPointValue, double factor) {

	// Bar Panel expects only numeric values - ensure all values are numeric

	if (dataPointValue == null) {
	    return updateRange(0L);
	}

	try {
	    return updateRange(Math.round(Long.valueOf(dataPointValue)*factor));
	}
	catch (Exception e) {
	    return updateRange(0L);
	}

    }

    protected String renderDataPoints(long[] dataPoints) {

	StringBuilder dataPointsStr = new StringBuilder();
	dataPointsStr.append("[");

	for (int i=0; i<dataPoints.length; i++) {

	    if (i > 0) {
		dataPointsStr.append(",");
	    }

	    dataPointsStr.append(dataPoints[i]);
	}

	dataPointsStr.append("]");

	LOG.debug("DataPoints: {}", dataPointsStr.toString());

	return dataPointsStr.toString();
    }

    protected String renderDataSets() {

    	List<TrackerDataSeriesBean> dataSeriesList = 
		trackerBean.getDataSeries();

	int dataSeriesIndex = 1;

	final StringBuilder dataSeriesSet = new StringBuilder();
	dataSeriesSet.append("[");

	for (TrackerDataSeriesBean dataSeriesBean : dataSeriesList) {

	    if (dataSeriesIndex++ > 1) {
		dataSeriesSet.append(",");
	    }

	    // the map contains all the data points we have stored and 
	    // held onto
	    DataPointMap dataPointsMap = dataSeriesBean.getDataPoints();

	    // this array contains the data points we will render
	    long[] dataPoints = new long[trackerBean.getPointsToDisplay()];

	    // if we have more data points than we want to display
	    if (dataPointsMap.size() > trackerBean.getPointsToDisplay()) {

		// start filling render array from the data points added most recently
		int startFrom = dataPointsMap.size() - trackerBean.getPointsToDisplay();

		// convert the data points to an array, order is important that's why
		// DataPointMap extends LinkedHashMap
		String[] dataPointsArray = new String[dataPointsMap.size()];
		dataPointsArray = dataPointsMap.values().toArray(dataPointsArray);

		// iterate from starting point, until we've filled all data points to render
		int dataPointIndex = 0;
		for (int i = startFrom; dataPointIndex < trackerBean.getPointsToDisplay(); i++) {
		    dataPoints[dataPointIndex++] = convertDataPoint(dataPointsArray[i], dataSeriesBean.getFactor());
		}
	    }
	    // otherwise just render the data points we have
	    else {

		// iterate through all points we have
		int dataPointIndex = 0;

		// fill in for all the data points we don't have
		for (int i = 0; i < (trackerBean.getPointsToDisplay()- dataPointsMap.size()); i++) {
		    dataPoints[dataPointIndex++] = convertDataPoint(null, dataSeriesBean.getFactor());
		}

		// now fill in the remainder with the data points we do have
		for (Map.Entry<Long,String> entry : dataPointsMap.entrySet()) {
		    dataPoints[dataPointIndex++] = convertDataPoint(entry.getValue(), dataSeriesBean.getFactor());
		}
	    }

	    dataSeriesSet.append(renderDataPoints(dataPoints));
	}

	dataSeriesSet.append("]");
	
	LOG.debug("DataSeriesSet: {}", dataSeriesSet.toString());

	return dataSeriesSet.toString();
    }

    protected long getMinValue() {
	return this.minValue;
    }

    protected long getMaxValue() {
	return this.maxValue;
    }
}