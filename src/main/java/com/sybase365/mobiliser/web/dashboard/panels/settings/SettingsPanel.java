package com.sybase365.mobiliser.web.dashboard.panels.settings;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerDataSeriesBean;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.dataproviders.TrackerDataSeriesDataProvider;
import com.sybase365.mobiliser.web.util.Constants;

public class SettingsPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(SettingsPanel.class);

    private TrackerBean trackerBean;
    private MobiliserBasePage mobBasePage;
    private FeedbackPanel feedBackPanel;

    // Data Model for table list data view
    private TrackerDataSeriesDataProvider dataProvider;

    private boolean forceReload = true;

    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_server = "server";
    private static final String WICKET_ID_objectName = "objectName";
    private static final String WICKET_ID_attribute = "attribute";
    private static final String WICKET_ID_removeAction = "removeAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg"; 

    public SettingsPanel(String id, TrackerBean trackerBean,
	    MobiliserBasePage mobBasePage, FeedbackPanel feedBackPanel) {

	super(id);

	this.trackerBean = trackerBean;
	this.mobBasePage = mobBasePage;
	this.feedBackPanel = feedBackPanel;

	LOG.info("Created new SettingsPanel");

	Form settingsForm = new Form("settingsForm",
		new CompoundPropertyModel<TrackerBean>(trackerBean));

	createTrackerDataSeriesListDataView(settingsForm);

	settingsForm.add(new Label("name", 
		trackerBean.getName()));

	settingsForm.add(new Label("type", 
		trackerBean.getType().toString()));

	settingsForm.add(new Label("sampleInterval", 
		String.valueOf(trackerBean.getSampleInterval())));

	settingsForm.add(new Label("sampleIntervalTimeUnit", 
		trackerBean.getSampleIntervalTimeUnit().toString()));

	settingsForm.add(new Label("pointsToDisplay", 
		String.valueOf(trackerBean.getPointsToDisplay())));

	add(settingsForm);
    }

    private WebMarkupContainer createTrackerDataSeriesListDataView(
	    final WebMarkupContainer parent) {

	dataProvider = new TrackerDataSeriesDataProvider(WICKET_ID_objectName, mobBasePage);

	final DataView<TrackerDataSeriesBean> dataView = 
		new DataView<TrackerDataSeriesBean>(WICKET_ID_pageable, dataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		dataProvider.setTrackerDataSeries(trackerBean.getDataSeries());

		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}

		super.onBeforeRender();
	    }

	    private String showPartialString(String original, int noOfChars) {

		if (original.length() > (noOfChars - 3)) {
		    return "..." + original.substring(original.length()-(noOfChars-3));
		}

		return original;
	    }

	    @Override
	    protected void populateItem(final Item<TrackerDataSeriesBean> item) {

		final TrackerDataSeriesBean entry = item.getModelObject();
		item.add(new Label(WICKET_ID_server, entry.getServer()));
		item.add(new Label(WICKET_ID_objectName, 
			showPartialString(entry.getObjectName(), 60)));
		item.add(new Label(WICKET_ID_attribute, 
			showPartialString(entry.getAttribute(), 30)));

		/* TODO - readonly at the moment
		// Remove Action
		Link removeLink = new Link<TrackerDataSeriesBean>(WICKET_ID_removeAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			TrackerDataSeriesBean entry = (TrackerDataSeriesBean) getModelObject();
			deleteTrackerDataSeries(entry);
		    }
		};

		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"trackerDataSeriesList.remove.confirm", this)
				+ "');"));
		removeLink.add(new PrivilegedBehavior(mobBasePage,
			Constants.PRIV_CUST_WRITE));
		item.add(removeLink);
		 */

		// set items in even/odd rows to different css style classes
		item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
			true, new AbstractReadOnlyModel<String>() {

			    @Override
			    public String getObject() {
				return (item.getIndex() % 2 == 1) ? Constants.CSS_STYLE_ODD
					: Constants.CSS_STYLE_EVEN;
			    }
			}));
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
		.getString("addTrackerDataSeries.table.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString(
			"addTrackerDataSeries.table.addTrackerDataSeriesHelp", this)) {

	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return false;
		} else {
		    return super.isVisible();
		}
	    }
	});

	return parent;
    }

    private void deleteTrackerDataSeries(TrackerDataSeriesBean entry) {


    }

}
