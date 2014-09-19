package com.sybase365.mobiliser.web.dashboard.panels.views;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.ViewTrackerPage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class MiniPanel extends ViewPanel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(MiniPanel.class);

    public MiniPanel(String id, TrackerBean trackerBean,
	    MobiliserBasePage mobBasePage, FeedbackPanel feedbackPanel) {

	super(id, trackerBean, mobBasePage, feedbackPanel);

	LOG.info("Created new MiniPanel");

	constructPanel();
    }

    @SuppressWarnings("unchecked")
    private void constructPanel() {
	
	if (PortalUtils.exists(trackerBean)) {
	    add(new Label("name", this.trackerBean.getName()));

	    add(new Label("trackerHolder")
		    .setOutputMarkupId(true)
		    .setMarkupId("trackerHolder-"+trackerBean.getId()));

	    add(new Link("edit") {
		    @Override
		    public void onClick() {
			setResponsePage(new ViewTrackerPage(trackerBean));
		    }
	    });
    	}
	else {
	    add(new Label("name", "No Tracker"));

	    add(new Label("trackerHolder", "")
		    .setOutputMarkupId(true));

	    add(new Link("edit") {
		    @Override
		    public void onClick() {
			// no-op
		    }
	    });
	}

	add(getTrackerChart("chartPanel"));

    }

}
