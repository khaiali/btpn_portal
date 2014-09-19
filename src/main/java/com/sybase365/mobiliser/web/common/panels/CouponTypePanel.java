package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.MinimumValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.UpdateCouponTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.UpdateCouponTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponType;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.util.tools.formatutils.FormatUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.CouponTypesPage;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.EditCouponTypePage;
import com.sybase365.mobiliser.web.util.AmountConverter;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CouponTypePanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypePanel.class);

    private CouponType couponType;
    private Date issueFromDate;
    private Date issueToDate;
    private MobiliserBasePage basePage;
    public static final int CUSTOMER_TYPE = 3;
    public static final int MAX_RECORDS = 500;

    public CouponTypePanel(String id, MobiliserBasePage basePage,
	    CouponType couponType) {
	super(id);
	if (PortalUtils.exists(couponType)) {
	    if (PortalUtils.exists(couponType.getIssueFrom()))
		issueFromDate = FormatUtils.getSaveDate(couponType
			.getIssueFrom());
	    if (PortalUtils.exists(couponType.getIssueTo()))
		issueToDate = FormatUtils.getSaveDate(couponType.getIssueTo());
	}
	this.basePage = basePage;
	this.couponType = couponType;

	final String chooseDtTxt = this.basePage.getLocalizer().getString(
		"datepicker.chooseDate", this.basePage);
	add(new HeaderContributor(new IHeaderContributor() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void renderHead(IHeaderResponse response) {

		// localize the jquery datepicker based on users locale setting
		// locale specific js includes for datepicker are available at
		// http://jquery-ui.googlecode.com/svn/trunk/ui/i18n/
		String localeLang = getLocale().getLanguage().toLowerCase();

		LOG.debug("Using DatePicker for locale language: {}",
			localeLang);

		if (PortalUtils.exists(localeLang)) {
		    response.renderJavascriptReference("scripts/jquery/i18n/jquery.ui.datepicker-"
			    + localeLang + ".js");
		}

		response.renderJavascript("\n"
			+ "jQuery(document).ready(function($) { \n"
			+ "  $('#issueFromDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'buttonOnlyImage': true} ); \n"

			+ "  $('#issueToDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");
	    }
	}));
	constructPanel();
    }

    private void constructPanel() {
	final Form<?> form = new Form("couponTypeForm",
		new CompoundPropertyModel<CouponTypePanel>(this));
	form.add(new FeedbackPanel("errorMessages"));
	WebMarkupContainer couponTypeIdDiv = new WebMarkupContainer(
		"couponTypeIdDiv");
	couponTypeIdDiv.setVisible(PortalUtils.exists(couponType)
		&& PortalUtils.exists(couponType.getId()));
	couponTypeIdDiv.add(new Label("couponType.id"));
	form.add(couponTypeIdDiv);
	form.add(new RequiredTextField<String>("couponType.name").setRequired(
		true).add(new ErrorIndicator()));
	form.add(new KeyValueDropDownChoice<Long, String>("couponType.issuer",
		getAllIssuersList()).setNullValid(true).setRequired(true)
		.add(new ErrorIndicator()));
	form.add(new CheckBox("couponType.isActive"));
	form.add(new DateTextField("issueFromDateField",
		new PropertyModel<Date>(this, "issueFromDate"),
		new PatternDateConverter(Constants.DATE_FORMAT_PATTERN_PARSE,
			false)).add(new ErrorIndicator()));
	form.add(new DateTextField("issueToDateField", new PropertyModel<Date>(
		this, "issueToDate"), new PatternDateConverter(
		Constants.DATE_FORMAT_PATTERN_PARSE, false))
		.add(new ErrorIndicator()));
	form.add(new TextField<Long>("couponType.purchasePrice") {
	    private static final long serialVersionUID = 1L;

	    public IConverter getConverter(Class<?> type) {
		return (new AmountConverter(basePage,
			"couponType.purchasePrice"));
	    };
	}.add(new ErrorIndicator()));
	form.add(new LocalizableLookupDropDownChoice<String>(
		"couponType.purchaseCurrency", String.class,
		Constants.RESOURCE_BUNDLE_CURRENCIES, this)
		.add(new ErrorIndicator()));
	form.add(new CheckBox("couponType.isPublic"));
	form.add(new TextField<Long>("couponType.maxViews").add(
		new MinimumValidator<Long>(0L)).add(new ErrorIndicator()));
	form.add(new TextField<Long>("couponType.maxUses").add(
		new MinimumValidator<Long>(0L)).add(new ErrorIndicator()));
	final LocalizableLookupDropDownChoice<String> codeTypeDropDown = new LocalizableLookupDropDownChoice<String>(
		"couponType.codeType", String.class,
		Constants.RESOURCE_BUNDLE_CODE_TYPE, basePage);
	codeTypeDropDown.setNullValid(true).setRequired(true)
		.add(new ErrorIndicator());
	form.add(codeTypeDropDown);
	Button saveButton = new Button("Save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    }
	};
	if (PortalUtils.exists(getCouponType())
		&& PortalUtils.exists(getCouponType().getId()))
	    saveButton.add(new PrivilegedBehavior(basePage,
		    Constants.PRIV_EDIT_CPN_TYPE));
	form.add(saveButton);
	form.add(new Button("Cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(CouponTypesPage.class);
	    }
	}.setDefaultFormProcessing(false));

	add(form);
    }

    protected void handleSubmit() {
	LOG.debug("# CouponTypePanel.handleSubmit()");
	// validate purchase price and currency
	if (PortalUtils.exists(couponType.getPurchasePrice())
		&& !PortalUtils.exists(couponType.getPurchaseCurrency()))
	    error(getLocalizer().getString(
		    "couponType.purchaseCurrency.required", this));
	else if (!PortalUtils.exists(couponType.getPurchasePrice())
		&& PortalUtils.exists(couponType.getPurchaseCurrency()))
	    error(getLocalizer().getString("couponType.price.required", this));
	else {
	    if (PortalUtils.exists(issueFromDate)) {
		XMLGregorianCalendar fromDate = null;
		fromDate = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			issueFromDate, basePage.getMobiliserWebSession()
				.getTimeZone());
		fromDate.setHour(0);
		fromDate.setMinute(0);
		fromDate.setSecond(0);
		fromDate.setMillisecond(0);
		couponType.setIssueFrom(fromDate);
	    }
	    if (PortalUtils.exists(issueToDate)) {
		XMLGregorianCalendar toDate = null;
		toDate = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			issueToDate, basePage.getMobiliserWebSession()
				.getTimeZone());
		toDate.setHour(23);
		toDate.setMinute(59);
		toDate.setSecond(59);
		toDate.setMillisecond(999);
		couponType.setIssueTo(toDate);
	    }
	    if (PortalUtils.exists(couponType.getId())) {
		// update coupon type
		try {
		    UpdateCouponTypeRequest req = basePage
			    .getNewMobiliserRequest(UpdateCouponTypeRequest.class);
		    req.setType(couponType);
		    UpdateCouponTypeResponse res = basePage.wsCouponsClient
			    .updateCouponType(req);
		    if (basePage.evaluateMobiliserResponse(res)) {
			info(getLocalizer().getString(
				"update.coupon.type.success", this));
		    }
		} catch (Exception e) {
		    LOG.error("An error occurred in updating coupon type", e);
		    error(getLocalizer().getString("update.coupon.type.error",
			    this));
		}
	    } else {
		// create coupon type
		try {
		    CreateCouponTypeRequest req = basePage
			    .getNewMobiliserRequest(CreateCouponTypeRequest.class);
		    req.setType(couponType);
		    CreateCouponTypeResponse res = basePage.wsCouponsClient
			    .createCouponType(req);
		    if (basePage.evaluateMobiliserResponse(res)) {
			info(getLocalizer().getString(
				"create.coupon.type.success", this));
			couponType.setId(res.getId());
			basePage.getMobiliserWebSession().setCouponType(
				couponType);
			setResponsePage(new EditCouponTypePage(couponType));
		    }
		} catch (Exception e) {
		    LOG.error("An error occurred in creating coupon type", e);
		    error(getLocalizer().getString("create.coupon.type.error",
			    this));
		}
	    }
	}
    }

    private List<KeyValue<Long, String>> getAllIssuersList() {
	LOG.debug("# CouponTypePanel.getAllIssuersList()");
	List<KeyValue<Long, String>> issuersList = new ArrayList<KeyValue<Long, String>>();
	try {
	    FindCustomerRequest request = basePage
		    .getNewMobiliserRequest(FindCustomerRequest.class);
	    request.setCustomerType(CUSTOMER_TYPE);
	    request.setMaxRecords(MAX_RECORDS);
	    FindCustomerResponse res = basePage.wsCustomerClient
		    .findCustomer(request);

	    if (basePage.evaluateMobiliserResponse(res)) {
		for (Customer issuer : res.getCustomers())
		    issuersList.add(new KeyValue<Long, String>(issuer.getId(),
			    issuer.getDisplayName()));
	    }
	} catch (Exception e) {
	    LOG.error("Unable to load issuer list", e);
	    error(getLocalizer().getString("issuer.list.load.error", this));
	}
	return issuersList;
    }

    public void setCouponType(CouponType couponType) {
	this.couponType = couponType;
    }

    public CouponType getCouponType() {
	return couponType;
    }

    public void setIssueFromDateString(Date issueFromDateString) {
	this.issueFromDate = issueFromDateString;
    }

    public Date getIssueFromDateString() {
	return issueFromDate;
    }

    public void setIssueToDateString(Date issueToDateString) {
	this.issueToDate = issueToDateString;
    }

    public Date getIssueToDateString() {
	return issueToDate;
    }

}
