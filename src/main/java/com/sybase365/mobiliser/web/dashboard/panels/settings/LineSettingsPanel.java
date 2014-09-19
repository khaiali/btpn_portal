package com.sybase365.mobiliser.web.dashboard.panels.settings;

import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;

public class LineSettingsPanel extends SettingsPanel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(LineSettingsPanel.class);

    public LineSettingsPanel(String id, TrackerBean trackerBean,
	    MobiliserBasePage mobBasePage, FeedbackPanel feedbackPanel) {

	super(id, trackerBean, mobBasePage, feedbackPanel);

	LOG.info("Created new LineSettingsPanel");

	constructPanel();
    }

    @SuppressWarnings("unchecked")
    private void constructPanel() {

    }
}
