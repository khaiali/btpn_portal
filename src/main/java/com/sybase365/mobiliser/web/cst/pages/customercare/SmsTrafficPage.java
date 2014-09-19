package com.sybase365.mobiliser.web.cst.pages.customercare;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.contract.v5_0.messaging.FindMessageLogsRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageLog;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageLogSearchCriteria;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.MessageLogDataProvider;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@SuppressWarnings("all")
public class SmsTrafficPage extends CustomerCareMenuGroup {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SmsTrafficPage.class);

    private Date fromDate;
    private Date toDate;
    private String fromMonth;
    private String templateName;
    private String filterType = Constants.TXN_FILTERTYPE_MONTH;
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;
    private int rowIndex = 1;
    private MessageLogDataProvider dataProvider;
    private List<MessageLog> messageLogList;
    private XMLGregorianCalendar fromDateXml;
    private XMLGregorianCalendar toDateXml;

    private boolean forceReload = true;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderById = "orderById";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";
    private static final String WICKET_ID_messageLogId = "messageLogId";
    private static final String WICKET_ID_templateName = "templateName";
    private static final String WICKET_ID_messageText = "messageText";
    private static final String WICKET_ID_sentDate = "sentDate";
    private static final String WICKET_ID_detailsAction = "detailsAction";

    public SmsTrafficPage() {
	super();
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

	add(new FeedbackPanel("errorMessages"));
	Form<?> form = new Form("smsTrafficForm",
		new CompoundPropertyModel<SmsTrafficPage>(this));

	final WebMarkupContainer fromDateDiv = new WebMarkupContainer(
		"fromDateDiv");
	fromDateDiv.add(new DateTextField("fromDate", new PropertyModel<Date>(
		this, "fromDate"), new PatternDateConverter(
		Constants.DATE_FORMAT_PATTERN_PARSE, false)));
	fromDateDiv.setOutputMarkupId(true);
	fromDateDiv.setOutputMarkupPlaceholderTag(true);
	fromDateDiv.setVisible(false);
	form.add(fromDateDiv);

	final WebMarkupContainer toDateDiv = new WebMarkupContainer("toDateDiv");
	toDateDiv.add(new DateTextField("toDate", new PropertyModel<Date>(this,
		"toDate"), new PatternDateConverter(
		Constants.DATE_FORMAT_PATTERN_PARSE, false)));
	toDateDiv.setOutputMarkupId(true);
	toDateDiv.setOutputMarkupPlaceholderTag(true);
	toDateDiv.setVisible(false);
	form.add(toDateDiv);

	final WebMarkupContainer monthDiv = new WebMarkupContainer("monthDiv");
	monthDiv.add(new KeyValueDropDownChoice<String, String>("fromMonth",
		getSelectableMonth()).setNullValid(true));
	monthDiv.setOutputMarkupId(true);
	monthDiv.setOutputMarkupPlaceholderTag(true);
	form.add(monthDiv);

	RadioGroup radioGroup = new RadioGroup("filterType");
	Radio monthRadio = new Radio("Month", new Model(
		Constants.TXN_FILTERTYPE_MONTH));
	monthRadio.add(new AjaxEventBehavior("onclick") {
	    private static final long serialVersionUID = 2L;

	    @Override
	    protected void onEvent(AjaxRequestTarget target) {
		monthDiv.setVisible(true);
		fromDateDiv.setVisible(false);
		toDateDiv.setVisible(false);
		target.addComponent(monthDiv);
		target.addComponent(fromDateDiv);
		target.addComponent(toDateDiv);
	    }
	});
	radioGroup.add(monthRadio);

	Radio dateRadio = new Radio("Date", new Model(
		Constants.TXN_FILTERTYPE_TIMEFRAME));
	dateRadio.add(new AjaxEventBehavior("onclick") {
	    private static final long serialVersionUID = 2L;

	    @Override
	    protected void onEvent(AjaxRequestTarget target) {
		monthDiv.setVisible(false);
		fromDateDiv.setVisible(true);
		toDateDiv.setVisible(true);
		target.addComponent(monthDiv);
		target.addComponent(fromDateDiv);
		target.addComponent(toDateDiv);
		target.appendJavascript("$j('#fromDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n"
			+ "$j('#toDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); ");
	    }
	});
	radioGroup.add(dateRadio);

	form.add(radioGroup);

	form.add(new TextField<String>("templateName").add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier));

	form.add(new Button("search") {
	    @Override
	    public void onSubmit() {
		searchMessageLog();
	    }
	});
	add(form);
	createMessageLogDataView(false);

    }

    private void searchMessageLog() {
	validateInputs();
	createMessageLogDataView(true);
    }

    private void createMessageLogDataView(boolean isVisible) {
	WebMarkupContainer messageLogDataViewContainer = new WebMarkupContainer(
		"messageLogDataViewContainer");
	messageLogDataViewContainer.setVisible(isVisible);
	dataProvider = new MessageLogDataProvider(WICKET_ID_pageable, this);
	messageLogList = new ArrayList<MessageLog>();

	final DataView<MessageLog> dataView = new DataView<MessageLog>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.findMessageLogs(createSerchRequest());
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error("An error occurred while loading message logs",
			    dple);
		    error(getLocalizer().getString("messageLog.list.failure",
			    this));
		} catch (Exception e) {
		    LOG.error("An error occurred while loading balance alerts",
			    e);
		    error(getLocalizer().getString("messageLog.list.failure",
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
		totalItemString = new Integer(dataProvider.size()).toString();
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
	    protected void populateItem(Item<MessageLog> item) {
		final MessageLog entry = item.getModelObject();
		messageLogList.add(entry);
		item.add(new Label(WICKET_ID_messageLogId, String.valueOf(entry
			.getId())));
		item.add(new Label(WICKET_ID_messageText, entry
			.getMessageText()));
		item.add(new Label(WICKET_ID_templateName, entry
			.getTemplateName()));
		item.add(new Label(WICKET_ID_sentDate, PortalUtils
			.getFormattedDateTime(entry.getSentDate(),
				getMobiliserWebSession().getLocale(),
				getMobiliserWebSession().getTimeZone())));

		Link<MessageLog> detailLink = new Link<MessageLog>(
			WICKET_ID_detailsAction, item.getModel()) {
		    @Override
		    public void onClick() {
			MessageLog entry = (MessageLog) getModelObject();
			setResponsePage(new MessageLogDetailPage(entry,
				this.getWebPage()));
		    }
		};

		item.add(detailLink);

	    }

	};

	dataView.setItemsPerPage(10);
	messageLogDataViewContainer.addOrReplace(dataView);
	messageLogDataViewContainer.addOrReplace(new OrderByBorder(
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

	messageLogDataViewContainer.addOrReplace(new MultiLineLabel(
		WICKET_ID_noItemsMsg, getLocalizer().getString(
			"messageLog.noItemsMsg", this)) {
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
	messageLogDataViewContainer.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	messageLogDataViewContainer.addOrReplace(new Label(
		WICKET_ID_totalItems, new PropertyModel<String>(this,
			"totalItemString")));

	messageLogDataViewContainer.addOrReplace(new Label(
		WICKET_ID_startIndex, new PropertyModel(this, "startIndex")));

	messageLogDataViewContainer.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));
	messageLogDataViewContainer.addOrReplace(dataView);
	addOrReplace(messageLogDataViewContainer);

    }

    private void validateInputs() {

	LOG.debug("#validateInputs");

	TimeZone timeZone = TimeZone.getDefault();
	if (PortalUtils.exists(getMobiliserWebSession().getCustomer()
		.getTimeZone())) {
	    timeZone = TimeZone.getTimeZone(getMobiliserWebSession()
		    .getCustomer().getTimeZone());
	}
	fromDateXml = null;
	toDateXml = null;

	if (filterType.equals(Constants.TXN_FILTERTYPE_TIMEFRAME)
		&& PortalUtils.exists(fromDate) && PortalUtils.exists(toDate)) {
	    if (PortalUtils.exists(fromDate)) {
		fromDateXml = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			fromDate, getMobiliserWebSession().getTimeZone());
	    }
	    if (PortalUtils.exists(toDate)) {
		toDateXml = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			toDate, getMobiliserWebSession().getTimeZone());
	    }
	    // set the two dates in correct order...
	    if (fromDateXml != null && toDateXml != null) {
		if (fromDateXml.compare(toDateXml) == DatatypeConstants.GREATER) {
		    XMLGregorianCalendar temp = fromDateXml;
		    fromDateXml = toDateXml;
		    toDateXml = temp;
		}
		fromDateXml.setHour(0);
		fromDateXml.setMinute(0);
		fromDateXml.setSecond(0);
		fromDateXml.setMillisecond(0);
		toDateXml.setHour(23);
		toDateXml.setMinute(59);
		toDateXml.setSecond(59);
		toDateXml.setMillisecond(999);
	    }

	    LOG.debug("From [{}] to [{}]", (fromDateXml == null ? "NOT SET"
		    : fromDateXml.toString()), (toDateXml == null ? "NOT SET"
		    : toDateXml.toString()));
	} else if (filterType.equals(Constants.TXN_FILTERTYPE_MONTH)
		&& PortalUtils.exists(fromMonth)) {

	    LOG.debug("Filter by month and year..");
	    String _month = fromMonth.substring(0, fromMonth.indexOf('-'));
	    String _year = fromMonth.substring(fromMonth.indexOf('-') + 1);
	    fromDateXml = PortalUtils.getXmlFromDateOfMonth(
		    getMobiliserWebSession().getTimeZone(), _month, _year);
	    toDateXml = PortalUtils.getXmlToDateOfMonth(
		    getMobiliserWebSession().getTimeZone(), _month, _year);
	    LOG.debug("From [{}] to [{}]", fromDateXml.toString(),
		    toDateXml.toString());

	}
    }

    private FindMessageLogsRequest createSerchRequest() throws Exception {
	MessageLogSearchCriteria searchCriteria = new MessageLogSearchCriteria();
	searchCriteria.setEntityReference(getMobiliserWebSession()
		.getCustomer().getId());
	searchCriteria.setFromDate(fromDateXml);
	searchCriteria.setToDate(toDateXml);
	searchCriteria.setTemplateName(templateName);

	FindMessageLogsRequest request = getNewMobiliserRequest(FindMessageLogsRequest.class);
	request.setCriteria(searchCriteria);
	return request;

    }
}
