package com.sybase365.mobiliser.web.dashboard.pages.trackers;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerGroupBean;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.dataproviders.TrackerGroupDataProvider;
import com.sybase365.mobiliser.web.dashboard.panels.views.MaxiPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_TRACKERS)
public class AllTrackersPage extends TrackersMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AllTrackersPage.class);

    private final FeedbackPanel feedbackPanel = new FeedbackPanel(
	    "errorMessages");

    private TrackerGroupDataProvider dataProvider;

    private static int TRACKERS_PER_PAGE = 4;

    private static final String WICKET_ID_repeater = "repeater";
    private static final String WICKET_ID_sort_by = "id";
    private static final String WICKET_ID_slot_prefix = "slot";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public AllTrackersPage() {
	super();
	initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {

	add(feedbackPanel);

	createTrackerGroupDataView(this);
    }

    private void createTrackerGroupDataView(final WebPage parent) {

	dataProvider = new TrackerGroupDataProvider(TRACKERS_PER_PAGE,
		WICKET_ID_sort_by, this);

	final DataView<TrackerGroupBean> dataView = new DataView<TrackerGroupBean>(
		WICKET_ID_repeater, dataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadTrackerGroups(true);
		} catch (DataProviderLoadException e) {
		    LOG.warn("Can not load trackers:", e);
		}

		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<TrackerGroupBean> item) {

		final TrackerGroupBean entry = item.getModelObject();

		for (int slotNo = 0; slotNo < TRACKERS_PER_PAGE; slotNo++) {
		    item.add(createPanel(slotNo, entry.getTrackerBean(slotNo)));
		}
	    }

	    @Override
	    public boolean isVisible() {
		if (getItemCount() > 0) {
		    return true;
		} else {
		    return super.isVisible();
		}
	    }

	};

	dataView.setOutputMarkupPlaceholderTag(true);

	parent.add(dataView);

	parent.add(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer()
		.getString("allTrackers.table.noItemsMsg", this)) {

	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return false;
		} else {
		    return super.isVisible();
		}
	    }
	});

	return;
    }

    private Component createPanel(int slotNo, TrackerBean trackerBean) {
	return new MaxiPanel(WICKET_ID_slot_prefix + slotNo, trackerBean, this,
		feedbackPanel);
    }
}
