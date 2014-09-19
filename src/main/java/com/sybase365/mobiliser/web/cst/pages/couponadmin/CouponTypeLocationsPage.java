package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.behavior.SimpleAttributeModifier;
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

import com.sybase365.mobiliser.money.contract.v5_0.coupon.DeleteCouponTypeLocationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.DeleteCouponTypeLocationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponTypeLocation;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.CouponLocationDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CouponTypeLocationsPage extends CouponTypeMenuGroup {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypeDescriptionPage.class);
    private CouponLocationDataProvider dataProvider;
    private List<CouponTypeLocation> couponTypeLocationList;

    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;
    private int rowIndex = 1;
    private static final String WICKET_ID_editAction = "editLink";
    private static final String WICKET_ID_removeAction = "removeLink";
    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByName = "orderByName";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_longitude = "longitude";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new FeedbackPanel("errorMessages"));

	createCouponTypeLocationsDataView();

    }

    private void createCouponTypeLocationsDataView() {

	addOrReplace(new Link("couponTypeLocationAddLink") {
	    @Override
	    public void onClick() {
		setResponsePage(new CreateCouponTypeLocationsPage());
	    }
	});

	dataProvider = new CouponLocationDataProvider("longitude", this, true);
	couponTypeLocationList = new ArrayList<CouponTypeLocation>();

	final DataView<CouponTypeLocation> dataView = new DataView<CouponTypeLocation>(
		WICKET_ID_pageable, dataProvider) {
	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadCouponLocation(forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading Coupon locations",
			    dple);
		    error(getLocalizer().getString(
			    "coupontype.location.load.error", this));
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
	    protected void populateItem(final Item<CouponTypeLocation> item) {
		final CouponTypeLocation entry = item.getModelObject();
		couponTypeLocationList.add(entry);
		item.add(new Label("longitude", entry.getLongitude().toString()));
		item.add(new Label("latitude", entry.getLatitude().toString()));
		item.add(new Label("radius", PortalUtils.exists(entry
			.getRadius()) ? entry.getRadius().toString() : ""));
		Link<CouponTypeLocation> editLink = new Link<CouponTypeLocation>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			CouponTypeLocation entry = (CouponTypeLocation) getModelObject();
			setResponsePage(new EditCouponTypeLocationsPage(entry));
		    }
		};
		item.add(editLink);

		Link<CouponTypeLocation> removeLink = new Link<CouponTypeLocation>(
			WICKET_ID_removeAction, item.getModel()) {
		    @Override
		    public void onClick() {
			CouponTypeLocation entry = (CouponTypeLocation) getModelObject();
			deleteCouponLocation(entry);
		    }
		};
		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"location.remove.confirm", this)
				+ "');"));
		item.add(removeLink);
	    }
	};
	dataView.setItemsPerPage(10);
	addOrReplace(dataView);

	addOrReplace(new OrderByBorder(WICKET_ID_orderByName,
		WICKET_ID_longitude, dataProvider) {
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
		.getString("couponTypeLocation.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString(
			"couponTypeLocation.addCouponLocationHelp", this)) {
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

    private void deleteCouponLocation(
	    final CouponTypeLocation couponTypeLocation) {
	DeleteCouponTypeLocationRequest request;
	try {
	    request = getNewMobiliserRequest(DeleteCouponTypeLocationRequest.class);
	    request.setLocationId(couponTypeLocation.getId());
	    DeleteCouponTypeLocationResponse response = wsCouponsClient
		    .deleteCouponTypeLocation(request);
	    if (!evaluateMobiliserResponse(response))
		return;
	    this.forceReload = true;
	    createCouponTypeLocationsDataView();
	    LOG.info("# Coupon Location deleted successfully");
	} catch (Exception e) {
	    LOG.error("# An error occurred while deleting Coupon Location", e);
	    error(getLocalizer().getString("delete.location.error", this));
	}
    }
}
