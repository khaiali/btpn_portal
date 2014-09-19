package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.Date;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.MinimumValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponBatchFromCouponRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponBatchFromCouponResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class GenerateCouponBatchPage extends CouponTypeMenuGroup {
    private static final Logger LOG = LoggerFactory
	    .getLogger(GenerateCouponBatchPage.class);

    private Date issueStartDate;
    private Date issueEndDate;
    private Long quantity;
    private String serialNumber;
    private String codeHash;
    private Date validUntil;
    private Date today;
    private XMLGregorianCalendar issueStartDateXml;
    private XMLGregorianCalendar issueEndDateXml;
    private XMLGregorianCalendar validUntilXml;
    private XMLGregorianCalendar todayXml;

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	final String chooseDtTxt = getLocalizer().getString(
		"datepicker.chooseDate", this);
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
			+ "  $('#issueStartDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER
			+ "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonText' : '"
			+ chooseDtTxt
			+ "', \n"
			+ "	'buttonOnlyImage': true} ); \n"

			+ "  $('#issueEndDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n"
			+ "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER
			+ "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonText' : '"
			+ chooseDtTxt
			+ "', \n"
			+ "	'buttonOnlyImage': true} ); \n"

			+ "  $('#validUntil').datepicker( { \n"
			+ "	'changeMonth' : true, \n"
			+ "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER
			+ "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonText' : '"
			+ chooseDtTxt
			+ "', \n"
			+ "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");

	    }
	}));

	Form<?> form = new Form("couponForm",
		new CompoundPropertyModel<GenerateCouponBatchPage>(this));
	form.add(new FeedbackPanel("errorMessages"));

	form.add(new RequiredTextField<Long>("quantity").add(
		new MinimumValidator<Long>(1L)).add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("serialNumber")
		.add(new ErrorIndicator()));
	form.add(new RequiredTextField<String>("codeHash")
		.add(new ErrorIndicator()));
	form.add(new DateTextField("validUntilField", new PropertyModel<Date>(
		this, "validUntil"), new PatternDateConverter(
		Constants.DATE_FORMAT_PATTERN_PARSE, false)).setRequired(true)
		.add(new ErrorIndicator()));
	form.add(new DateTextField("issueStartDateField",
		new PropertyModel<Date>(this, "issueStartDate"),
		new PatternDateConverter(Constants.DATE_FORMAT_PATTERN_PARSE,
			false)).add(new ErrorIndicator()));
	form.add(new DateTextField("issueEndDateField",
		new PropertyModel<Date>(this, "issueEndDate"),
		new PatternDateConverter(Constants.DATE_FORMAT_PATTERN_PARSE,
			false)).add(new ErrorIndicator()));
	form.add(new Button("save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    }
	});
	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(new EditCouponTypePage(getMobiliserWebSession()
			.getCouponType()));
	    }
	}.setDefaultFormProcessing(false));

	add(form);

    }

    protected void handleSubmit() {
	try {
	    today = new Date();
	    todayXml = PortalUtils.getSaveXMLGregorianCalendarFromDate(today,
		    this.getMobiliserWebSession().getTimeZone());

	    if (PortalUtils.exists(issueStartDate)) {
		issueStartDateXml = PortalUtils
			.getSaveXMLGregorianCalendarFromDate(issueStartDate,
				this.getMobiliserWebSession().getTimeZone());
		issueStartDateXml.setHour(0);
		issueStartDateXml.setMinute(0);
		issueStartDateXml.setSecond(0);
		issueStartDateXml.setMillisecond(0);
	    }

	    if (PortalUtils.exists(issueEndDate)) {
		issueEndDateXml = PortalUtils
			.getSaveXMLGregorianCalendarFromDate(issueEndDate, this
				.getMobiliserWebSession().getTimeZone());
		issueEndDateXml.setHour(23);
		issueEndDateXml.setMinute(59);
		issueEndDateXml.setSecond(59);
		issueEndDateXml.setMillisecond(999);
	    }

	    if (PortalUtils.exists(validUntil)) {
		validUntilXml = PortalUtils
			.getSaveXMLGregorianCalendarFromDate(validUntil, this
				.getMobiliserWebSession().getTimeZone());
		validUntilXml.setHour(23);
		validUntilXml.setMinute(59);
		validUntilXml.setSecond(59);
		validUntilXml.setMillisecond(999);
	    }
	    if (PortalUtils.exists(issueStartDateXml)) {
		if (todayXml.compare(issueStartDateXml) == DatatypeConstants.GREATER) {
		    error(getLocalizer().getString(
			    "generate.issueStartDate.invalid", this));
		    return;
		}
	    }
	    if (PortalUtils.exists(issueEndDateXml)) {
		if (validUntilXml.compare(issueEndDateXml) == DatatypeConstants.LESSER) {
		    error(getLocalizer().getString(
			    "generate.error.validUntilDate", this));
		    return;
		}
	    }
	    if (PortalUtils.exists(issueStartDateXml)
		    && PortalUtils.exists(issueEndDateXml)) {
		if (issueEndDateXml.compare(issueStartDateXml) == DatatypeConstants.LESSER) {
		    error(getLocalizer().getString(
			    "generate.error.issueStartDate", this));
		    return;
		}
	    }

	    if (validUntil.compareTo(today) < 1) {
		error(getLocalizer().getString("generate.error.validUntilDate",
			this));
		return;
	    }

	    CreateCouponBatchFromCouponRequest req = getNewMobiliserRequest(CreateCouponBatchFromCouponRequest.class);
	    req.setQuantity(quantity);
	    req.setCouponType(getMobiliserWebSession().getCouponType().getId());
	    req.setIssueFrom(issueStartDateXml);
	    req.setIssueTo(issueEndDateXml);
	    req.setSerialNumber(serialNumber);
	    req.setCode(codeHash);
	    req.setValidUntil(validUntilXml);
	    CreateCouponBatchFromCouponResponse response = wsCouponsClient
		    .createCouponBatchFromCoupon(req);
	    if (evaluateMobiliserResponse(response)) {
		info(getLocalizer().getString("generate.coupon.success", this));
		setResponsePage(CouponTypeBatchesPage.class);
	    }
	} catch (Exception e) {
	    LOG.error("An error occurred in generating coupon batch", e);
	    error(getLocalizer().getString("generate.coupon.error", this));
	}
    }

    public void setIssueStartDate(Date issueStartDate) {
	this.issueStartDate = issueStartDate;
    }

    public Date getIssueStartDate() {
	return issueStartDate;
    }

    public void setIssueEndDate(Date issueEndDate) {
	this.issueEndDate = issueEndDate;
    }

    public Date getIssueEndDate() {
	return issueEndDate;
    }

    public Date getValidUntil() {
	return validUntil;
    }

    public void setValidUntil(Date validUntil) {
	this.validUntil = validUntil;
    }

}
