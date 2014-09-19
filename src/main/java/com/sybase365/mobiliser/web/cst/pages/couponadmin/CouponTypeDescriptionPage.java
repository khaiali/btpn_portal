package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponTypeDescription;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.CouponDescriptionDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.Constants;

public class CouponTypeDescriptionPage extends CouponTypeMenuGroup {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypeDescriptionPage.class);
    private CouponDescriptionDataProvider dataProvider;
    private List<CouponTypeDescription> couponTypeDescriptionList;

    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;
    private int rowIndex = 1;
    private static final String WICKET_ID_editAction = "editLink";
    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByName = "orderByName";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_mimeType = "mimeType";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new FeedbackPanel("errorMessages"));

	createCouponTypeDescriptionDataView();

    }

    private void createCouponTypeDescriptionDataView() {

	add(new Link("couponTypeDescriptionAddLink") {
	    @Override
	    public void onClick() {
		setResponsePage(new CreateCouponTypeDescriptionPage());
	    }
	});

	dataProvider = new CouponDescriptionDataProvider("locale", this, true);
	couponTypeDescriptionList = new ArrayList<CouponTypeDescription>();

	final DataView<CouponTypeDescription> dataView = new DataView<CouponTypeDescription>(
		WICKET_ID_pageable, dataProvider) {
	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadCouponDescription(forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading Coupon type description",
			    dple);
		    error(getLocalizer().getString(
			    "couponDescription.load.error", this));
		}
		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}
		refreshTotalItemCount();
		super.onBeforeRender();
	    }

	    private void refreshTotalItemCount() {
		totalItemString = Integer.toString(dataProvider.size());
		int total = getItemCount();
		if (total > 0) {
		    startIndex = getCurrentPage() * getItemsPerPage() + 1;
		    endIndex = startIndex + getItemsPerPage() - 1;
		    if (endIndex > total)
			endIndex = total;
		} else {
		    startIndex = 0;
		    endIndex = 0;
		}
	    }

	    @Override
	    protected void populateItem(final Item<CouponTypeDescription> item) {
		final CouponTypeDescription entry = item.getModelObject();
		couponTypeDescriptionList.add(entry);
		item.add(new Label("locale", getDisplayValue(entry.getLocale(),
			Constants.RESOURCE_BUNDLE_SUPPORTED_LOCALE)));
		item.add(new Label("mimeType", entry.getMimeType()));
		Link<CouponTypeDescription> editLink = new Link<CouponTypeDescription>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			CouponTypeDescription entry = (CouponTypeDescription) getModelObject();
			setResponsePage(new EditCouponTypeDescriptionPage(entry));
		    }
		};
		item.add(editLink);
	    }
	};
	dataView.setItemsPerPage(10);
	addOrReplace(dataView);

	addOrReplace(new OrderByBorder(WICKET_ID_orderByName,
		WICKET_ID_mimeType, dataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the page is
		// loading
		// and the sort is clicked (clicking the name header), so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer()
		.getString("couponTypeDescription.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString(
			"couponTypeDescription.addCouponDescriptionHelp", this)) {
	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return false;
		} else {
		    return super.isVisible();
		}
	    }
	});

	// Navigator example: << < 1 2 > >>
	addOrReplace(new CustomPagingNavigator(WICKET_ID_navigator, dataView));

	addOrReplace(new Label(WICKET_ID_totalItems, new PropertyModel<String>(
		this, "totalItemString")));

	addOrReplace(new Label(WICKET_ID_startIndex, new PropertyModel(this,
		"startIndex")));

	addOrReplace(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));

	// End of Pagenation
    }
}
