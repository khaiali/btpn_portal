package com.sybase365.mobiliser.web.dashboard.panels.views;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;
import com.sybase365.mobiliser.web.dashboard.panels.charts.BarPanel;
import com.sybase365.mobiliser.web.dashboard.panels.charts.CandlestickPanel;
import com.sybase365.mobiliser.web.dashboard.panels.charts.EmptyPanel;
import com.sybase365.mobiliser.web.dashboard.panels.charts.GaugePanel;
import com.sybase365.mobiliser.web.dashboard.panels.charts.LinePanel;
import com.sybase365.mobiliser.web.dashboard.panels.settings.BarSettingsPanel;
import com.sybase365.mobiliser.web.dashboard.panels.settings.CandlestickSettingsPanel;
import com.sybase365.mobiliser.web.dashboard.panels.settings.GaugeSettingsPanel;
import com.sybase365.mobiliser.web.dashboard.panels.settings.LineSettingsPanel;

public class ViewPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ViewPanel.class);

    protected TrackerBean trackerBean;
    protected MobiliserBasePage mobBasePage;
    protected FeedbackPanel feedbackPanel;

    public ViewPanel(String id, TrackerBean trackerBean,
	    MobiliserBasePage mobBasePage, FeedbackPanel feedbackPanel) {

	super(id);

	this.trackerBean = trackerBean;
	this.mobBasePage = mobBasePage;
	this.feedbackPanel = feedbackPanel;

    }

    protected Panel getTrackerChart(String id) {

	if (trackerBean == null) {
	    return new EmptyPanel(id, trackerBean, mobBasePage, feedbackPanel);
	}
	else if (trackerBean.getType() == TrackerBean.TrackerChartType.LINE) {
	    return new LinePanel(id, trackerBean, mobBasePage, feedbackPanel);
	}
	else if (trackerBean.getType() == TrackerBean.TrackerChartType.BAR) {
	    return new BarPanel(id, trackerBean, mobBasePage, feedbackPanel);
	}
	else if (trackerBean.getType() == TrackerBean.TrackerChartType.CANDLESTICK) {
	    return new CandlestickPanel(id, trackerBean, mobBasePage, feedbackPanel);
	}
	else if (trackerBean.getType() == TrackerBean.TrackerChartType.GAUGE) {
	    return new GaugePanel(id, trackerBean, mobBasePage, feedbackPanel);
	}

	return null;
    }

    protected Panel getTrackerSettings(String id) {

	if (trackerBean.getType() == TrackerBean.TrackerChartType.LINE) {
	    return new LineSettingsPanel(id, trackerBean, mobBasePage, feedbackPanel);
	}
	else if (trackerBean.getType() == TrackerBean.TrackerChartType.BAR) {
	    return new BarSettingsPanel(id, trackerBean, mobBasePage, feedbackPanel);
	}
	else if (trackerBean.getType() == TrackerBean.TrackerChartType.CANDLESTICK) {
	    return new CandlestickSettingsPanel(id, trackerBean, mobBasePage, feedbackPanel);
	}
	else if (trackerBean.getType() == TrackerBean.TrackerChartType.GAUGE) {
	    return new GaugeSettingsPanel(id, trackerBean, mobBasePage, feedbackPanel);
	}

	return null;
    }
}
