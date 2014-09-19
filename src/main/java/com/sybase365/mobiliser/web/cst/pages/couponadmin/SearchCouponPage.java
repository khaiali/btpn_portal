package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetCouponTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetCouponTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.SearchCouponsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.SearchCouponsResponseType.Result;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Identifier;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.CouponDataProvider;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class SearchCouponPage extends BaseCouponAdminPage {
    private static final Logger LOG = LoggerFactory
	    .getLogger(SearchCouponPage.class);

    private String customerIdentification;
    private String couponSerialNumber;
    private CouponDataProvider dataProvider;
    private List<Result> notesList;

    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;
    private int rowIndex = 1;
    private boolean forceReload = true;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderById = "orderById";
    private static final String WICKET_ID_LidLink = "LidLink";
    private static final String WICKET_ID_Lid = "Lid";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_couponType = "couponType";
    private static final String WICKET_ID_serialNumber = "serialNumber";
    private static final String WICKET_ID_issueDate = "issueDate";
    private static final String WICKET_ID_issuedTo = "issuedTo";
    private static final String WICKET_ID_couponId = "couponId";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	final Form<?> form = new Form("searchCouponForm",
		new CompoundPropertyModel<SearchCouponPage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new TextField<String>("customerIdentification")
		.add(new ErrorIndicator()));
	form.add(new TextField<String>("couponSerialNumber")
		.add(new ErrorIndicator()));
	form.add(new Button("searchCoupon") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    }
	});
	add(form);
	createCouponSearchTable(false);
    }

    private void createCouponSearchTable(boolean isVisible) {

	WebMarkupContainer couponsDataViewContainer = new WebMarkupContainer(
		"couponsDataViewContainer");
	couponsDataViewContainer.setVisible(isVisible);
	dataProvider = new CouponDataProvider(WICKET_ID_pageable, this, true);
	notesList = new ArrayList<Result>();
	final DataView<Result> dataView = new DataView<Result>(
		WICKET_ID_pageable, dataProvider) {
	    @Override
	    protected void onBeforeRender() {

		try {
		    // create the search request
		    SearchCouponsRequest req = getNewMobiliserRequest(SearchCouponsRequest.class);
		    if (PortalUtils.exists(customerIdentification)) {
			Identifier identifier = new Identifier();
			identifier.setType(Constants.IDENT_TYPE_CUST_ID);
			identifier.setValue(customerIdentification);
			req.setCustomer(identifier);
		    } else {
			req.setSerialNumber(couponSerialNumber);
		    }
		    dataProvider.searchCoupons(req, forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (Exception e) {
		    LOG.error("An error occurred while searching for coupons",
			    e);
		    error(getLocalizer().getString("search.coupon.error", this));
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
		totalItemString = String.valueOf(dataProvider.size());
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

	    protected void populateItem(final Item<Result> item) {
		final Result entry = item.getModelObject();
		notesList.add(entry);
		Link idLink = new Link<Result>(WICKET_ID_LidLink,
			item.getModel()) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {
			Result cModel = (Result) item.getModelObject();
			setResponsePage(new EditCouponPage(cModel.getCoupon(),
				cModel.getInstance(), getCouponTypeName(cModel
					.getCoupon().getCouponType())));
		    }
		};
		item.add(idLink);
		idLink.add(new Label(WICKET_ID_Lid, String.valueOf(entry
			.getCoupon().getId())));
		item.add(new Label(WICKET_ID_couponType,
			getCouponTypeName(entry.getCoupon().getCouponType())));
		item.add(new Label(WICKET_ID_serialNumber, entry.getCoupon()
			.getSerialNumber()));
		// TODO set the proper field for issue date
		item.add(new Label(WICKET_ID_issueDate, ""));
		item.add(new Label(WICKET_ID_issuedTo, String.valueOf(entry
			.getInstance().getCustomer())));

	    };

	};

	dataView.setItemsPerPage(10);
	couponsDataViewContainer.addOrReplace(dataView);
	couponsDataViewContainer.addOrReplace(new OrderByBorder(
		WICKET_ID_orderById, WICKET_ID_orderById, dataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the
		// page is
		// loading
		// and the sort is clicked (clicking the name header),
		// so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	couponsDataViewContainer.addOrReplace(new MultiLineLabel(
		WICKET_ID_noItemsMsg, getLocalizer().getString(
			"search.coupon.noItemMsg", this)) {
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
	couponsDataViewContainer.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	couponsDataViewContainer.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	couponsDataViewContainer.addOrReplace(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	couponsDataViewContainer.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));
	couponsDataViewContainer.addOrReplace(dataView);
	addOrReplace(couponsDataViewContainer);

    }

    protected String getCouponTypeName(long couponType) {
	String couponTypeName = "";
	try {
	    GetCouponTypeRequest request = getNewMobiliserRequest(GetCouponTypeRequest.class);
	    request.setId(couponType);
	    GetCouponTypeResponse response = wsCouponsClient
		    .getCouponType(request);
	    if (PortalUtils.exists(response)
		    && PortalUtils.exists(response.getStatus())) {
		if (response.getStatus().getCode() == 0) {
		    if (PortalUtils.exists(response.getType()))
			couponTypeName = response.getType().getName();
		}
	    }
	} catch (Exception e) {
	    LOG.error("An error occurred in getting the name of coupon type", e);
	}
	return couponTypeName;
    }

    protected void handleSubmit() {
	if (!PortalUtils.exists(couponSerialNumber)
		&& !PortalUtils.exists(customerIdentification)) {
	    error(getLocalizer().getString("search.coupon.compulsory", this));
	    createCouponSearchTable(false);
	    return;
	}
	createCouponSearchTable(true);
    }

    public String getCustomerIdentification() {
	return customerIdentification;
    }

    public void setCustomerIdentification(String customerIdentification) {
	this.customerIdentification = customerIdentification;
    }

    public String getCouponSerialNumber() {
	return couponSerialNumber;
    }

    public void setCouponSerialNumber(String couponSerialNumber) {
	this.couponSerialNumber = couponSerialNumber;
    }
}
