package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.ActivateCouponBatchRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.ActivateCouponBatchResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetCouponBatchRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetCouponBatchResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.UpdateCouponBatchRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.UpdateCouponBatchResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponBatch;
import com.sybase365.mobiliser.util.tools.formatutils.FormatUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.CouponTypeBatchesPage;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.EditCouponTypeBatchPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CouponTypeBatchPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypeBatchPanel.class);
    private CouponBatch couponTypeBatch;
    private Date issueFromDate;
    private Date issueToDate;
    private MobiliserBasePage basePage;
    private String action;

    public CouponTypeBatchPanel(String id, MobiliserBasePage basePage,
	    CouponBatch couponTypeBatch) {
	super(id);
	if (PortalUtils.exists(couponTypeBatch)) {
	    if (PortalUtils.exists(couponTypeBatch.getIssueFrom()))
		issueFromDate = FormatUtils.getSaveDate(couponTypeBatch
			.getIssueFrom());
	    if (PortalUtils.exists(couponTypeBatch.getIssueTo()))
		issueToDate = FormatUtils.getSaveDate(couponTypeBatch
			.getIssueTo());
	}
	this.basePage = basePage;
	this.couponTypeBatch = couponTypeBatch;
	final String chooseDtTxt = this.basePage.getLocalizer().getString(
		"datepicker.chooseDate", this.basePage);
	add(new HeaderContributor(new IHeaderContributor() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void renderHead(IHeaderResponse response) {

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
	final Form<?> form = new Form("couponTypeBatchForm",
		new CompoundPropertyModel<CouponTypeBatchPanel>(this));
	form.add(new FeedbackPanel("errorMessages"));

	form.add(new Label("couponTypeBatch.id"));

	form.add(new Label("couponTypeBatch.creationDate", PortalUtils
		.getFormattedDate(couponTypeBatch.getCreationDate(),
			Locale.getDefault())));

	form.add(new Label("status", basePage.getDisplayValue(
		String.valueOf(couponTypeBatch.getStatus()),
		Constants.RESOURCE_BUNDLE_COUPON_STATUS)));

	form.add(new Label("couponTypeBatch.quantity"));

	form.add(new Label("leftCount", getLeftCount()));
	final KeyValueDropDownChoice<Boolean, String> actionDropDown = new KeyValueDropDownChoice<Boolean, String>(
		"couponTypeBatch.isActive", getOptions());
	actionDropDown.setNullValid(true).setRequired(true).setEnabled(true);
	form.add(actionDropDown);
	form.add(new DateTextField("issueFromField", new PropertyModel<Date>(
		this, "issueFromDate"), new PatternDateConverter(
		Constants.DATE_FORMAT_PATTERN_PARSE, false)).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new DateTextField("issueToField", new PropertyModel<Date>(
		this, "issueToDate"), new PatternDateConverter(
		Constants.DATE_FORMAT_PATTERN_PARSE, false)).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new Button("Save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    }
	});
	Button approveButton = new Button("activate") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleActivate();
	    }
	};
	approveButton.add(new PrivilegedBehavior(basePage,
		Constants.PRIV_ACTIVATE_CPN_BATCH));
	if (PortalUtils.exists(couponTypeBatch.getStatus())
		&& couponTypeBatch.getStatus() == Constants.CPN_ACTIVATION_PENDING)
	    approveButton.setVisible(true);
	else
	    approveButton.setVisible(false);
	form.add(approveButton);
	form.add(new Button("Cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(CouponTypeBatchesPage.class);
	    }
	}.setDefaultFormProcessing(false));

	add(form);
    }

    protected void handleActivate() {
	LOG.debug("# CouponTypeBatchPanel.handleActivate()");
	try {
	    ActivateCouponBatchRequest request = basePage
		    .getNewMobiliserRequest(ActivateCouponBatchRequest.class);
	    request.setBatchId(couponTypeBatch.getId());
	    ActivateCouponBatchResponse response = basePage.wsCouponsClient
		    .activateCouponBatch(request);
	    if (basePage.evaluateMobiliserResponse(response)) {
		info(getLocalizer().getString("batch.activate.success", this));
	    }
	    GetCouponBatchRequest batchReq = basePage
		    .getNewMobiliserRequest(GetCouponBatchRequest.class);
	    batchReq.setId(couponTypeBatch.getId());
	    GetCouponBatchResponse batchRes = basePage.wsCouponsClient
		    .getCouponBatch(batchReq);
	    if (basePage.evaluateMobiliserResponse(batchRes)) {
		setResponsePage(new EditCouponTypeBatchPage(batchRes.getBatch()));
	    }

	} catch (Exception e) {
	    LOG.error("An error occurred while activating the coupon batch ", e);
	    error(getLocalizer().getString("batch.activate.error", this));
	}

    }

    private List<KeyValue<Boolean, String>> getOptions() {
	List<KeyValue<Boolean, String>> options = new ArrayList<KeyValue<Boolean, String>>();
	options.add(new KeyValue<Boolean, String>(true, getLocalizer()
		.getString("action.option.enable", this)));
	options.add(new KeyValue<Boolean, String>(false, getLocalizer()
		.getString("action.option.disable", this)));
	return options;
    }

    private String getLeftCount() {
	String left = "";
	if (PortalUtils.exists(couponTypeBatch)
		&& PortalUtils.exists(couponTypeBatch.getQuantity())
		&& PortalUtils.exists(couponTypeBatch.getUsed())) {
	    left = String.valueOf(couponTypeBatch.getQuantity()
		    - couponTypeBatch.getUsed());
	}

	return left;
    }

    protected void handleSubmit() {
	LOG.debug("# CouponTypeBatchPanel.handleSubmit()");
	if (PortalUtils.exists(couponTypeBatch)
		&& PortalUtils.exists(couponTypeBatch.getId())) {

	    try {
		UpdateCouponBatchRequest request = basePage
			.getNewMobiliserRequest(UpdateCouponBatchRequest.class);
		if (PortalUtils.exists(issueFromDate)) {
		    XMLGregorianCalendar fromDate = null;
		    fromDate = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			    issueFromDate, basePage.getMobiliserWebSession()
				    .getTimeZone());
		    fromDate.setHour(0);
		    fromDate.setMinute(0);
		    fromDate.setSecond(0);
		    fromDate.setMillisecond(0);
		    couponTypeBatch.setIssueFrom(fromDate);
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
		    couponTypeBatch.setIssueTo(toDate);
		}
		request.setBatch(couponTypeBatch);
		UpdateCouponBatchResponse response = basePage.wsCouponsClient
			.updateCouponBatch(request);

		if (basePage.evaluateMobiliserResponse(response)) {
		    info(getLocalizer().getString(
			    "update.coupon.batch.success", this));
		}
	    } catch (Exception e) {
		LOG.error("An error occurred in updating batch", e);
		error(getLocalizer().getString("update.coupon.batch.error",
			this));
	    }
	} else {
	    return;
	}
    }

    public void setCouponType(CouponBatch couponTypeBatch) {
	this.couponTypeBatch = couponTypeBatch;
    }

    public CouponBatch getCouponType() {
	return couponTypeBatch;
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

    public String getAction() {
	return action;
    }

    public void setAction(String action) {
	this.action = action;
    }

}
