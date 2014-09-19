package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponCategoryDescriptionRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponCategoryDescriptionResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.DeleteCouponCategoryDescriptionRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.DeleteCouponCategoryDescriptionResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponCategoryDescription;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLookupsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLookupsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LookupEntity;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.CouponCategoryDescriptionDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CouponCategoriesDescriptionPage extends CouponCategoryMenuGroup {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypeDescriptionPage.class);
    private CouponCategoryDescriptionDataProvider dataProvider;
    private CouponCategoryDescription couponCategoryDescription;
    private List<CouponCategoryDescription> couponCategoryDescriptionList;
    private String caption;
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;
    private int rowIndex = 1;
    private static final String WICKET_ID_deleteAction = "deleteLink";
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
	createCouponcategoryDescriptionDataView();
    }

    private void createCouponcategoryDescriptionDataView() {

	dataProvider = new CouponCategoryDescriptionDataProvider("locale",
		this, true);

	final FeedbackPanel errorMessages = new FeedbackPanel("errorMessages");
	errorMessages.setOutputMarkupId(true);
	addOrReplace(errorMessages);
	final WebMarkupContainer addCouponCategoryDescriptionFormDiv = new WebMarkupContainer(
		"addCouponDescriptionFormDiv");
	addCouponCategoryDescriptionFormDiv.setOutputMarkupId(true);
	addCouponCategoryDescriptionFormDiv.setOutputMarkupPlaceholderTag(true);
	createAddCouponCategoryForm(addCouponCategoryDescriptionFormDiv, false);
	addOrReplace(addCouponCategoryDescriptionFormDiv);
	// Adding Link
	addOrReplace(new AjaxLink("couponCategoryDescriptionAddLink") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(final AjaxRequestTarget target) {
		target.addComponent(errorMessages);
		target.addComponent(addCouponCategoryDescriptionFormDiv);
		createAddCouponCategoryForm(
			addCouponCategoryDescriptionFormDiv, true);
	    }
	}.add(new PrivilegedBehavior(this, Constants.PRIV_CST_LOGIN)));
	couponCategoryDescriptionList = new ArrayList<CouponCategoryDescription>();

	final DataView<CouponCategoryDescription> dataView = new DataView<CouponCategoryDescription>(
		WICKET_ID_pageable, dataProvider) {
	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadCouponCategoryDescription(forceReload);
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
	    protected void populateItem(
		    final Item<CouponCategoryDescription> item) {
		final CouponCategoryDescription entry = item.getModelObject();
		couponCategoryDescriptionList.add(entry);
		item.add(new Label("locale", getDisplayValue(entry.getLocale(),
			Constants.RESOURCE_BUNDLE_SUPPORTED_LOCALE)));
		item.add(new Label("description", entry.getCaption()));

		Link<CouponCategoryDescription> removeLink = new Link<CouponCategoryDescription>(
			WICKET_ID_deleteAction, item.getModel()) {
		    @Override
		    public void onClick() {
			CouponCategoryDescription entry = (CouponCategoryDescription) getModelObject();
			deleteCouponDescription(entry);
		    }
		};
		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"description.remove.confirm", this)
				+ "');"));
		item.add(removeLink);
	    }
	};
	dataView.setItemsPerPage(10);
	addOrReplace(dataView);

	addOrReplace(new OrderByBorder(WICKET_ID_orderByName,
		WICKET_ID_fromCurrency, dataProvider) {
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
		.getString("couponCategoryDescription.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString(
			"couponCategory.addCategoryDescriptionHelp", this)) {
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

    private void deleteCouponDescription(
	    final CouponCategoryDescription couponCategoryDescription) {
	DeleteCouponCategoryDescriptionRequest request;
	try {
	    request = getNewMobiliserRequest(DeleteCouponCategoryDescriptionRequest.class);
	    request.setId(couponCategoryDescription.getId());
	    DeleteCouponCategoryDescriptionResponse response = wsCouponsClient
		    .deleteCouponCategoryDescription(request);
	    if (!evaluateMobiliserResponse(response))
		return;
	    this.forceReload = true;
	    createCouponcategoryDescriptionDataView();

	    LOG.info("# Description deleted successfully");
	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while deleting category description",
		    e);
	    error(getLocalizer().getString("delete.category.description.error",
		    this));
	}
    }

    private void createAddCouponCategoryForm(
	    final WebMarkupContainer addCouponCategoryDescriptionFormDiv,
	    boolean isVisible) {

	List<KeyValue<String, String>> localeList = new ArrayList<KeyValue<String, String>>();
	boolean nullExists = false;
	String modelObjectForLocale = null;
	try {
	    GetLookupsRequest request = getNewMobiliserRequest(GetLookupsRequest.class);
	    request.setEntityName(Constants.RESOURCE_BUNDLE_SUPPORTED_LOCALE);
	    GetLookupsResponse response = wsSystemConfClient
		    .getLookups(request);
	    if (evaluateMobiliserResponse(response)) {
		List<LookupEntity> localeEntities = response
			.getLookupEntities();
		if (!PortalUtils.exists(dataProvider))
		    dataProvider = new CouponCategoryDescriptionDataProvider(
			    "locale", this, true);

		List<CouponCategoryDescription> existingDescription = dataProvider
			.loadCouponCategoryDescription(forceReload);
		forceReload = false;
		for (Iterator<LookupEntity> iter = localeEntities.iterator(); iter
			.hasNext();) {
		    LookupEntity entity = iter.next();
		    for (CouponCategoryDescription desc : existingDescription) {
			if (PortalUtils.exists(desc.getLocale())) {
			    if (entity.getId().equals(desc.getLocale()))
				iter.remove();
			} else {
			    nullExists = true;
			}
		    }
		}
		if (PortalUtils.exists(localeEntities)) {
		    for (LookupEntity entity : localeEntities) {
			localeList.add(new KeyValue(entity.getId(), entity
				.getName()));
		    }
		    modelObjectForLocale = localeEntities.get(0).getId();
		} else if (nullExists) {
		    isVisible = false;
		    error(getLocalizer().getString("remove.desc.to.add.new",
			    this));
		}
	    }
	} catch (Exception e) {
	    LOG.error("an error occurred while loading supported languages ", e);
	    error(getLocalizer().getString("load.description.language.error",
		    this));
	}
	final Form<?> addCouponCategoryDescriptionForm = new Form(
		"addCouponCategoryDescriptionForm",
		new CompoundPropertyModel<CouponCategoriesDescriptionPage>(this));
	final KeyValueDropDownChoice<String, String> languageDropDown = new KeyValueDropDownChoice<String, String>(
		"couponCategoryDescription.locale", localeList);
	if (nullExists) {
	    if (!PortalUtils.exists(couponCategoryDescription))
		couponCategoryDescription = new CouponCategoryDescription();
	    couponCategoryDescription.setLocale(modelObjectForLocale);
	} else {
	    if (PortalUtils.exists(couponCategoryDescription))
		couponCategoryDescription.setLocale(null);
	}
	languageDropDown.setNullValid(false).setRequired(false)
		.setEnabled(true);
	addCouponCategoryDescriptionForm.addOrReplace(languageDropDown);

	addCouponCategoryDescriptionForm
		.addOrReplace(new RequiredTextField<String>(
			"couponCategoryDescription.caption").setRequired(true)
			.add(new ErrorIndicator()));

	addCouponCategoryDescriptionForm.addOrReplace(new Button("save") {
	    @Override
	    public void onSubmit() {
		try {
		    CreateCouponCategoryDescriptionRequest request = getNewMobiliserRequest(CreateCouponCategoryDescriptionRequest.class);
		    getCouponCategoryDescription().setCategory(
			    getMobiliserWebSession().getCouponCategory()
				    .getId());
		    request.setDescription(getCouponCategoryDescription());
		    CreateCouponCategoryDescriptionResponse response = wsCouponsClient
			    .createCouponCategoryDescription(request);
		    if (evaluateMobiliserResponse(response)) {
			forceReload = true;
			info(getLocalizer().getString(
				"create.category.description.success", this));
			setResponsePage(CouponCategoriesDescriptionPage.class);
		    }
		} catch (Exception e) {
		    LOG.error(
			    "An error occurred while adding category description",
			    e);
		    error(getLocalizer().getString(
			    "create.category.description.error", this));
		}
	    }

	});

	addCouponCategoryDescriptionForm.addOrReplace(new AjaxLink("cancel") {

	    @Override
	    public void onClick(final AjaxRequestTarget target) {
		addCouponCategoryDescriptionFormDiv.setVisible(false);
		target.addComponent(addCouponCategoryDescriptionFormDiv);

	    }
	});

	addCouponCategoryDescriptionFormDiv
		.addOrReplace(addCouponCategoryDescriptionForm);
	addCouponCategoryDescriptionFormDiv.setVisible(isVisible);
    }

    public CouponCategoryDescription getCouponCategoryDescription() {
	return couponCategoryDescription;
    }

    public void setCouponCategoryDescription(
	    CouponCategoryDescription couponCategoryDescription) {
	this.couponCategoryDescription = couponCategoryDescription;
    }

    public String getCaption() {
	return caption;
    }

    public void setCaption(String caption) {
	this.caption = caption;
    }

}
