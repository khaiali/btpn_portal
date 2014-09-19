package com.sybase365.mobiliser.web.dashboard.pages.trackers;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;
import com.sybase365.mobiliser.web.dashboard.panels.views.IndiPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;
import org.apache.wicket.PageParameters;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_TRACKERS)
public class ViewTrackerPage extends TrackersMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ViewTrackerPage.class);

    private List<TrackerBean> trackers;
    private TrackerBean selectedTracker;

    final FeedbackPanel feedbackPanel = new FeedbackPanel("errorMessages");

    public ViewTrackerPage() {
        super();
        initPageComponents();
    }

    public ViewTrackerPage(TrackerBean trackerBean) {
	super();
	this.selectedTracker = trackerBean;
        initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {

	Form viewTrackerForm = new Form("viewTrackerForm",
		new CompoundPropertyModel<ViewTrackerPage>(this));

	viewTrackerForm.add(feedbackPanel);

	List<KeyValue<TrackerBean, String>> trackerSelectOptions = 
		new ArrayList<KeyValue<TrackerBean, String>>();

	trackers = this.trackersDao.getTrackers();

	if (PortalUtils.exists(trackers)) {
	    for (TrackerBean tracker : trackers) {
		    trackerSelectOptions.add(
			    new KeyValue<TrackerBean,String>(
				    tracker,
				    tracker.getName()));
	    }
	}

	final Component trackerSelect = 
		new KeyValueDropDownChoice<TrackerBean, String>( 
			"selectedTracker", trackerSelectOptions) {

			    @Override
			    protected boolean wantOnSelectionChangedNotifications() {
				return Boolean.TRUE;
			    }

			    @Override 
			    protected void onSelectionChanged(TrackerBean newSelection) {
				LOG.debug("New tracker selected: {}", 
					newSelection.getName());
				setResponsePage(new ViewTrackerPage(newSelection));
			    }

			}.setRequired(true)
			 .add(new ErrorIndicator())
			 .setOutputMarkupId(true);

	viewTrackerForm.add(trackerSelect);

	if (selectedTracker == null && PortalUtils.exists(trackers.isEmpty())) {
		selectedTracker = trackers.get(0);
	}

	add(viewTrackerForm);

	add(new IndiPanel("trackerPanel", selectedTracker, 
		this, feedbackPanel)
		.setOutputMarkupId(true));
    }

    private void handleViewAction() {
	addOrReplace(new IndiPanel("trackerPanel", selectedTracker, 
		this, feedbackPanel)
		.setOutputMarkupId(true));

    }
}
