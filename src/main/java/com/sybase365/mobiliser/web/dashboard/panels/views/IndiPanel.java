package com.sybase365.mobiliser.web.dashboard.panels.views;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;

public class IndiPanel extends ViewPanel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(IndiPanel.class);

    public IndiPanel(String id, TrackerBean trackerBean,
	    MobiliserBasePage mobBasePage, FeedbackPanel feedbackPanel) {

	super(id, trackerBean, mobBasePage, feedbackPanel);

	LOG.info("Created new IndiPanel");

	constructPanel();
    }

    @SuppressWarnings("unchecked")
    private void constructPanel() {

	add(new Label("name", this.trackerBean.getName()));

	add(new Label("trackerHolder")
		.setOutputMarkupId(true)
		.setMarkupId("trackerHolder-"+trackerBean.getId()));

	add(new Link("refresh") {
		    @Override
		    public void onClick() {
			// no-op
		    }
	    });

	add(getTrackerChart("chartPanel"));

	add(getTrackerSettings("settingsPanel"));

    }

}
