package com.sybase365.mobiliser.web.dashboard.panels.charts;

import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;

public class EmptyPanel extends ChartPanel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EmptyPanel.class);

    public EmptyPanel(String id, TrackerBean trackerBean,
	    MobiliserBasePage mobBasePage, FeedbackPanel feedbackPanel) {

	super(id, trackerBean, mobBasePage, feedbackPanel);

	LOG.debug("Created new EmptyPanel");

	constructPanel();
    }

    @SuppressWarnings("unchecked")
    private void constructPanel() {

    }

}