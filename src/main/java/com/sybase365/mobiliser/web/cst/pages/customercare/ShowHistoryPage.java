package com.sybase365.mobiliser.web.cst.pages.customercare;

import java.util.Date;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerHistoryRequest;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.panels.HistoryPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class ShowHistoryPage extends CustomerCareMenuGroup {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ShowHistoryPage.class);

    private Long agentId;
    private Integer timeframe;
    private Date fromDate;
    private Date toDate;

    private DateTextField fromDateTxt;
    private DateTextField toDateTxt;

    public ShowHistoryPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public ShowHistoryPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	final String chooseDtTxt = this.getLocalizer().getString(
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
			+ "  $('#fromDate').datepicker( { \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n"

			+ "  $('#toDate').datepicker( { \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");
	    }
	}));

	constructSearchForm();
    }

    private void constructSearchForm() {
	final Form<?> form = new Form("historyListForm",
		new CompoundPropertyModel<ShowHistoryPage>(this));

	form.add(new FeedbackPanel("errorMessages"));

	final WebMarkupContainer fromDateLabel = new WebMarkupContainer(
		"fromDateLabel", new Model());
	fromDateLabel.setOutputMarkupPlaceholderTag(true);
	fromDateLabel.setOutputMarkupId(true);
	fromDateTxt = (DateTextField) new DateTextField("fromDate",
		new PropertyModel<Date>(this, "fromDate"),
		new PatternDateConverter(Constants.DATE_FORMAT_PATTERN_PARSE,
			false));
	fromDateTxt.add(new ErrorIndicator());
	fromDateTxt.setOutputMarkupPlaceholderTag(true);
	fromDateTxt.setMarkupId("fromDate");
	fromDateLabel.add(fromDateTxt);
	form.add(fromDateLabel);

	final WebMarkupContainer toDateLabel = new WebMarkupContainer(
		"toDateLabel", new Model());
	toDateLabel.setOutputMarkupPlaceholderTag(true);
	toDateLabel.setOutputMarkupId(true);
	toDateTxt = (DateTextField) new DateTextField("toDate",
		new PropertyModel<Date>(this, "toDate"),
		new PatternDateConverter(Constants.DATE_FORMAT_PATTERN_PARSE,
			false));
	toDateTxt.add(new ErrorIndicator());
	toDateTxt.setMarkupId("toDate");
	toDateTxt.setOutputMarkupPlaceholderTag(true);
	toDateLabel.add(toDateTxt);
	form.add(toDateLabel);

	final KeyValueDropDownChoice<Integer, String> timeframeDropDown = new KeyValueDropDownChoice<Integer, String>(
		"timeframe", getSelectableTimeFrame());
	/*
	 * timeframeDropDown .add(new
	 * AjaxFormComponentUpdatingBehavior("onchange") { private static final
	 * long serialVersionUID = 1L;
	 * 
	 * @Override protected void onUpdate(AjaxRequestTarget target) { Integer
	 * newSelection = timeframeDropDown .getModelObject(); if (newSelection
	 * != null) { fromDateTxt.setEnabled(false);
	 * toDateTxt.setEnabled(false); } else { fromDateTxt.setEnabled(true);
	 * toDateTxt.setEnabled(true); } target.addComponent(fromDateTxt);
	 * target.addComponent(toDateTxt); } });
	 */
	timeframeDropDown.setOutputMarkupId(true);
	timeframeDropDown.setOutputMarkupPlaceholderTag(true);
	timeframeDropDown.setEnabled(true);
	timeframeDropDown.setNullValid(true);

	form.add(timeframeDropDown);

	form.add(new TextField<Long>("agentId").add(
		Constants.amountSimpleAttributeModifier).add(
		new ErrorIndicator()));
	form.addOrReplace(new HistoryPanel("historyPanel",
		ShowHistoryPage.this, null).setVisible(false));

	form.add(new Button("Search") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		GetCustomerHistoryRequest historyReq = createHistoryRequest();
		form.addOrReplace(new HistoryPanel("historyPanel",
			ShowHistoryPage.this, historyReq).setVisible(true));
	    };
	});

	add(form);
    }

    protected GetCustomerHistoryRequest createHistoryRequest() {
	GetCustomerHistoryRequest historyReq = null;
	try {
	    historyReq = getNewMobiliserRequest(GetCustomerHistoryRequest.class);
	    if (getTimeframe() != null) {
		historyReq
			.setFromDate(PortalUtils.getTimeFrameFromDate(
				getTimeframe(), getMobiliserWebSession()
					.getTimeZone()));
		historyReq.setToDate(PortalUtils
			.getTimeFrameToDate(getMobiliserWebSession()
				.getTimeZone()));
		// fromDateTxt.setEnabled(false);
		// toDateTxt.setEnabled(false);
	    } else {
		XMLGregorianCalendar fromDateXml = null;
		XMLGregorianCalendar toDateXml = null;
		if (PortalUtils.exists(fromDate)) {
		    fromDateXml = PortalUtils
			    .getSaveXMLGregorianCalendarFromDate(fromDate,
				    getMobiliserWebSession().getTimeZone());
		}
		if (PortalUtils.exists(toDate)) {
		    toDateXml = PortalUtils
			    .getSaveXMLGregorianCalendarFromDate(toDate,
				    getMobiliserWebSession().getTimeZone());
		}
		// set the two dates in correct order...
		if (fromDateXml != null && toDateXml != null) {
		    if (fromDateXml.compare(toDateXml) == DatatypeConstants.GREATER) {
			XMLGregorianCalendar temp = fromDateXml;
			fromDateXml = toDateXml;
			toDateXml = temp;
		    }
		}

		if (fromDateXml != null) {
		    // cFrom's time is 00:00:00.000
		    fromDateXml.setHour(0);
		    fromDateXml.setMinute(0);
		    fromDateXml.setSecond(0);
		    fromDateXml.setMillisecond(0);
		    historyReq.setFromDate(fromDateXml);
		}

		if (toDateXml != null) {
		    // set cTo's time to 23:59:59.999
		    toDateXml.setHour(23);
		    toDateXml.setMinute(59);
		    toDateXml.setSecond(59);
		    toDateXml.setMillisecond(999);
		    historyReq.setToDate(toDateXml);
		} else {
		    historyReq.setToDate(PortalUtils
			    .getTimeFrameToDate(getMobiliserWebSession()
				    .getTimeZone()));
		}
	    }
	    if (getAgentId() != null) {
		historyReq.setCreatorId(getAgentId());
	    }
	    historyReq.setCustomerId(getMobiliserWebSession().getCustomer()
		    .getId());

	    LOG.debug("Date range for filter: {} to {}",
		    historyReq.getFromDate(), historyReq.getToDate());

	} catch (Exception e) {
	    LOG.error("Error in loading customer history.", e);
	    error(getLocalizer().getString("history.load.error", this));
	}
	return historyReq;
    }

    public Long getAgentId() {
	return agentId;
    }

    public void setAgentId(Long agentId) {
	this.agentId = agentId;
    }

    public Integer getTimeframe() {
	return timeframe;
    }

    public void setTimeframe(Integer timeframe) {
	this.timeframe = timeframe;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }
}
