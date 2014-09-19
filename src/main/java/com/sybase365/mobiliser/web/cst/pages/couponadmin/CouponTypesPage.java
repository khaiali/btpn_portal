package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponType;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.CouponTypeDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.Constants;

public class CouponTypesPage extends BaseCouponAdminPage {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypesPage.class);
    private CouponTypeDataProvider dataProvider;
    private List<CouponType> couponTypeList;

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
    private static final String WICKET_ID_fromCurrency = "fromCurrency";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new FeedbackPanel("errorMessages"));
	final WebMarkupContainer addCouponTypeContainer = new WebMarkupContainer(
		"addCoupontypesContainer");

	createCouponTypesDataView();
	createAddCouponTypesForm();
    }

    private void createCouponTypesDataView() {

	dataProvider = new CouponTypeDataProvider("name", this, true);
	couponTypeList = new ArrayList<CouponType>();

	final DataView<CouponType> dataView = new DataView<CouponType>(
		WICKET_ID_pageable, dataProvider) {
	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadCouponTypes(forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error("# An error occurred while loading Coupon types",
			    dple);
		    error(getLocalizer().getString("coupontype.load.error",
			    this));
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
	    protected void populateItem(final Item<CouponType> item) {
		final CouponType entry = item.getModelObject();
		couponTypeList.add(entry);
		item.add(new Label("name", entry.getName()));
		item.add(new Label("active", entry.isIsActive() ? "Yes" : "No"));
		Link<CouponType> editLink = new Link<CouponType>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			CouponType entry = (CouponType) getModelObject();
			getMobiliserWebSession().setCouponType(entry);
			setResponsePage(new EditCouponTypePage(entry));
		    }
		};
		item.add(editLink);
	    }
	};
	dataView.setItemsPerPage(10);
	add(dataView);

	add(new OrderByBorder(WICKET_ID_orderByName, WICKET_ID_fromCurrency,
		dataProvider) {
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

	add(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer().getString(
		"couponType.noItemsMsg", this)
		+ "\n"
		+ getLocalizer()
			.getString("couponType.addCouponTypeHelp", this)) {
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
	add(new CustomPagingNavigator(WICKET_ID_navigator, dataView));

	add(new Label(WICKET_ID_totalItems, new PropertyModel<String>(this,
		"totalItemString")));

	add(new Label(WICKET_ID_startIndex, new PropertyModel(this,
		"startIndex")));

	add(new Label(WICKET_ID_endIndex, new PropertyModel(this, "endIndex")));

	// End of Pagenation
    }

    private void createAddCouponTypesForm() {

	add(new Link("couponTypeAddLink") {
	    @Override
	    public void onClick() {
		setResponsePage(new CreateCouponTypePage());
	    }
	}.add(new PrivilegedBehavior(this, Constants.PRIV_CREATE_CPN_TYPE)));

    }
}
