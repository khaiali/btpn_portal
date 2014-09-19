package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.customer.FindCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindHierarchicalCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindPendingCustomersRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.AddressFindBean;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.IdentificationFindBean;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.CustomerBeanDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.model.ICustomerFinder;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class FindCustomerPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(FindCustomerPanel.class);

    private CustomerBeanDataProvider dataProvider;
    private List<CustomerBean> agentsList;
    ICustomerFinder customerFinder;
    MobiliserBasePage basePage;
    boolean isHierarchical;
    private String customerSearchType = null;
    private Integer customerTypeId;

    private String filterType = Constants.TXN_FILTERTYPE_MONTH;
    private String fromMonth;
    private Date fromDate;
    private Date toDate;
    private XMLGregorianCalendar fromDateXml;
    private XMLGregorianCalendar toDateXml;

    private boolean forceReload = true;
    private boolean sortAsc = true;
    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderById = "orderById";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_LidLink = "LidLink";
    private static final String WICKET_ID_LfirstName = "LfirstName";
    private static final String WICKET_ID_LlastName = "LlastName";
    private static final String WICKET_ID_LuserName = "LuserName";
    private static final String WICKET_ID_Lemail = "Lemail";
    private static final String WICKET_ID_LdisplayName = "LdisplayName";
    private static final String WICKET_ID_Lmsisdn = "Lmsisdn";
    private static final String WICKET_ID_Lid = "Lid";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    private String msisdn;
    private String lastName;
    private String firstName;
    private String displayName;
    private String street;
    private String city;
    private String zip;
    private String email;
    private Long customerId;
    private String userName;

    public FindCustomerPanel(String id, MobiliserBasePage basePage,
	    ICustomerFinder customerFinder, boolean isHierarchical, String type) {
	super(id);
	this.customerFinder = customerFinder;
	this.basePage = basePage;
	this.isHierarchical = isHierarchical;
	this.customerSearchType = type;
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
			+ "  $('#fromDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'buttonOnlyImage': true} ); \n"

			+ "  $('#toDate').datepicker( { \n"
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

    @SuppressWarnings("unchecked")
    private void constructPanel() {

	@SuppressWarnings("rawtypes")
	final Form<?> form = new Form("findAgentForm",
		new CompoundPropertyModel<FindCustomerPanel>(this));
	form.add(new FeedbackPanel("errorMessages"));

	// /////////////////////

	RadioGroup rg = new RadioGroup("radioGroup", new PropertyModel(this,
		"filterType"));

	Radio filteTypeMonth = new Radio("Month", new Model(
		Constants.TXN_FILTERTYPE_MONTH));
	filteTypeMonth.setOutputMarkupId(true).setMarkupId("Month");
	rg.add(filteTypeMonth);

	Radio filteTypeDate = new Radio("Date", new Model(
		Constants.TXN_FILTERTYPE_TIMEFRAME));
	filteTypeDate.setOutputMarkupId(true).setMarkupId("Date");
	rg.add(filteTypeDate);

	final WebMarkupContainer monthLbl = new WebMarkupContainer(
		"monthLabel", new Model());
	monthLbl.setOutputMarkupPlaceholderTag(true);
	monthLbl.setOutputMarkupId(true);

	final KeyValueDropDownChoice<String, String> monthDropDown = new KeyValueDropDownChoice<String, String>(
		"fromMonth", basePage.getSelectableMonth());
	monthDropDown.setOutputMarkupPlaceholderTag(true);
	monthDropDown.setEnabled(true);

	monthLbl.add(monthDropDown);

	final WebMarkupContainer fromDateLabel = new WebMarkupContainer(
		"fromDateLabel", new Model());
	fromDateLabel.setOutputMarkupPlaceholderTag(true);
	fromDateLabel.setOutputMarkupId(true);
	fromDateLabel.setVisible(false);
	final DateTextField fromDateTxt = (DateTextField) new DateTextField(
		"fromDate", new PropertyModel<Date>(this, "fromDate"),
		new PatternDateConverter(Constants.DATE_FORMAT_PATTERN_PARSE,
			false));

	fromDateTxt.add(new ErrorIndicator());
	fromDateTxt.setOutputMarkupPlaceholderTag(true);
	fromDateTxt.setMarkupId("fromDate");
	fromDateLabel.add(fromDateTxt);

	final WebMarkupContainer toDateLabel = new WebMarkupContainer(
		"toDateLabel", new Model());
	toDateLabel.setOutputMarkupPlaceholderTag(true);
	toDateLabel.setOutputMarkupId(true);
	toDateLabel.setVisible(false);
	final DateTextField toDateTxt = (DateTextField) new DateTextField(
		"toDate", new PropertyModel<Date>(this, "toDate"),
		new PatternDateConverter(Constants.DATE_FORMAT_PATTERN_PARSE,
			false));
	toDateTxt.add(new ErrorIndicator());
	toDateTxt.setMarkupId("toDate");
	toDateTxt.setOutputMarkupPlaceholderTag(true);
	toDateLabel.add(toDateTxt);

	filteTypeMonth.add(new AjaxEventBehavior("onclick") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onEvent(AjaxRequestTarget target) {
		monthLbl.setVisible(true);
		target.addComponent(monthLbl);
		fromDateLabel.setVisible(false);
		target.addComponent(fromDateLabel);
		toDateLabel.setVisible(false);
		target.addComponent(toDateLabel);
	    }
	});

	filteTypeDate.add(new AjaxEventBehavior("onclick") {
	    private static final long serialVersionUID = 2L;

	    @Override
	    protected void onEvent(AjaxRequestTarget target) {
		monthLbl.setVisible(false);
		target.addComponent(monthLbl);
		fromDateLabel.setVisible(true);
		target.addComponent(fromDateLabel);
		toDateLabel.setVisible(true);
		target.addComponent(toDateLabel);
		target.appendJavascript("$j('#fromDate').datepicker( { \n"
			+ "	'changeMonth' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n"
			+ "$j('#toDate').datepicker( { \n"
			+ "	'changeMonth' : true,  \n"
			+ "	'showOn': 'both', \n" + "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n");
	    }
	});

	form.add(monthLbl);
	form.add(fromDateLabel);
	form.add(toDateLabel);
	form.add(rg);

	// ////////////////////

	WebMarkupContainer msisdnDiv = new WebMarkupContainer("msisdnDiv");
	msisdnDiv.add(new TextField<String>("msisdn")
		.add(new PatternValidator(Constants.REGEX_MSISDN))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(msisdnDiv);

	WebMarkupContainer customerIdDiv = new WebMarkupContainer(
		"customerIdDiv");
	customerIdDiv.add(new TextField<Long>("customerId").add(
		Constants.amountSimpleAttributeModifier).add(
		new ErrorIndicator()));
	form.add(customerIdDiv);

	WebMarkupContainer userNameDiv = new WebMarkupContainer("userNameDiv");
	userNameDiv.add(new TextField<String>("userName")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(userNameDiv);

	WebMarkupContainer displayNameDiv = new WebMarkupContainer(
		"displayNameDiv");
	displayNameDiv.add(new TextField<String>("displayName")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(displayNameDiv);

	WebMarkupContainer streetDiv = new WebMarkupContainer("streetDiv");
	streetDiv.add(new TextField<String>("street")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(streetDiv);

	WebMarkupContainer cityDiv = new WebMarkupContainer("cityDiv");
	cityDiv.add(new TextField<String>("city")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(cityDiv);

	WebMarkupContainer zipDiv = new WebMarkupContainer("zipDiv");
	zipDiv.add(new TextField<String>("zip")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(zipDiv);

	form.add(new TextField<String>("firstName")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("lastName")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	WebMarkupContainer emailDiv = new WebMarkupContainer("emailDivDiv");
	emailDiv.add(new TextField<String>("email")
		.add(new PatternValidator(Constants.REGEX_EMAIL))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(emailDiv);

	WebMarkupContainer columnBuffer = new WebMarkupContainer("columnBuffer");
	form.add(columnBuffer);

	WebMarkupContainer customerTypeDiv = new WebMarkupContainer(
		"customerTypeDiv");
	// customerTypeDiv.setVisible(true);

	customerTypeDiv.add(new LocalizableLookupDropDownChoice<Integer>(
		"customerTypeId", Integer.class,
		Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE, this, Boolean.FALSE,
		true).setNullValid(false).add(new ErrorIndicator()));

	form.add(customerTypeDiv);

	// toggling the search fields based on the customer type
	if (Constants.SEARCH_TYPE_AGENT.equals(customerSearchType)) {
	    msisdnDiv.setVisible(false);
	    displayNameDiv.setVisible(false);
	    streetDiv.setVisible(false);
	    cityDiv.setVisible(false);
	    zipDiv.setVisible(false);
	    customerTypeDiv.setVisible(false);
	    monthLbl.setVisible(false);
	    rg.setVisible(false);
	    columnBuffer.setVisible(false);
	} else if (Constants.SEARCH_TYPE_CUSTOMER.equals(customerSearchType)) {
	    customerIdDiv.setVisible(false);
	    userNameDiv.setVisible(false);
	    customerTypeDiv.setVisible(false);
	    rg.setVisible(false);
	    monthLbl.setVisible(false);
	    columnBuffer.setVisible(false);
	} else {
	    displayNameDiv.setVisible(false);
	    streetDiv.setVisible(false);
	    cityDiv.setVisible(false);
	    zipDiv.setVisible(false);
	    customerIdDiv.setVisible(false);
	    emailDiv.setVisible(false);
	}

	final WebMarkupContainer dataViewContainer = new WebMarkupContainer(
		"dataViewContainer");
	form.add(dataViewContainer);
	form.add(new Button("findAgent") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {

		if (PortalUtils.exists(getUserName())
			&& PortalUtils.exists(getCustomerId())) {
		    error(getLocalizer().getString(
			    "agent.find.single.search.identity", this));
		    createFindAgentDataView(dataViewContainer, false);
		} else {

		    if (!PortalUtils.exists(customerSearchType)) {
			// for pending approvals
			validateInputs();
		    }
		    createFindAgentDataView(dataViewContainer, true);
		}
	    };
	});

	add(form);
	createFindAgentDataView(dataViewContainer, false);

    }

    private void validateInputs() {

	LOG.debug("#validateInputs");

	fromDateXml = null;
	toDateXml = null;

	if (filterType.equals(Constants.TXN_FILTERTYPE_TIMEFRAME)
		&& PortalUtils.exists(fromDate) && PortalUtils.exists(toDate)) {
	    if (PortalUtils.exists(fromDate)) {
		fromDateXml = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			fromDate, this.basePage.getMobiliserWebSession()
				.getTimeZone());
	    }
	    if (PortalUtils.exists(toDate)) {
		toDateXml = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			toDate, this.basePage.getMobiliserWebSession()
				.getTimeZone());
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
	    fromDateXml = PortalUtils.getXmlFromDateOfMonth(this.basePage
		    .getMobiliserWebSession().getTimeZone(), _month, _year);
	    toDateXml = PortalUtils.getXmlToDateOfMonth(this.basePage
		    .getMobiliserWebSession().getTimeZone(), _month, _year);
	    LOG.debug("From [{}] to [{}]", fromDateXml.toString(),
		    toDateXml.toString());

	}
    }

    @SuppressWarnings("rawtypes")
    protected void createFindAgentDataView(
	    final WebMarkupContainer dataViewContainer, boolean isVisible) {
	// create the header row
	if (Constants.SEARCH_TYPE_AGENT.equals(customerSearchType)) {
	    dataViewContainer.addOrReplace(new Label("HuserName",
		    getLocalizer().getString("findAgent.table.username", this))
		    .setVisible(true));
	    dataViewContainer
		    .addOrReplace(new Label("HfirstName", getLocalizer()
			    .getString("findAgent.table.firstname", this))
			    .setVisible(true));
	    dataViewContainer.addOrReplace(new Label("HlastName",
		    getLocalizer().getString("findAgent.table.lastname", this))
		    .setVisible(true));
	    dataViewContainer
		    .addOrReplace(new Label("Hemail", getLocalizer().getString(
			    "findAgent.table.email", this)).setVisible(true));
	    dataViewContainer.addOrReplace(new Label("HdisplayName",
		    getLocalizer().getString("findAgent.table.displayName",
			    this)).setVisible(false));
	    dataViewContainer.addOrReplace(new Label("Hmsisdn", getLocalizer()
		    .getString("findAgent.table.msisdn", this))
		    .setVisible(false));
	} else if (Constants.SEARCH_TYPE_CUSTOMER.equals(customerSearchType)) {
	    dataViewContainer.addOrReplace(new Label("HuserName",
		    getLocalizer().getString("findAgent.table.username", this))
		    .setVisible(false));
	    dataViewContainer
		    .addOrReplace(new Label("HfirstName", getLocalizer()
			    .getString("findAgent.table.firstname", this))
			    .setVisible(false));
	    dataViewContainer.addOrReplace(new Label("HlastName",
		    getLocalizer().getString("findAgent.table.lastname", this))
		    .setVisible(false));
	    dataViewContainer.addOrReplace(new Label("Hemail", getLocalizer()
		    .getString("findAgent.table.email", this))
		    .setVisible(false));
	    dataViewContainer.addOrReplace(new Label("HdisplayName",
		    getLocalizer().getString("findAgent.table.displayName",
			    this)).setVisible(true));
	    dataViewContainer.addOrReplace(new Label("Hmsisdn", getLocalizer()
		    .getString("findAgent.table.msisdn", this))
		    .setVisible(true));

	} else {
	    // For pending approvals
	    dataViewContainer.addOrReplace(new Label("HuserName",
		    getLocalizer().getString("findAgent.table.username", this))
		    .setVisible(true));
	    dataViewContainer
		    .addOrReplace(new Label("HfirstName", getLocalizer()
			    .getString("findAgent.table.firstname", this))
			    .setVisible(true));
	    dataViewContainer.addOrReplace(new Label("HlastName",
		    getLocalizer().getString("findAgent.table.lastname", this))
		    .setVisible(true));
	    dataViewContainer
		    .addOrReplace(new Label("Hemail", getLocalizer().getString(
			    "findAgent.table.email", this)).setVisible(true));
	    dataViewContainer.addOrReplace(new Label("Hmsisdn", getLocalizer()
		    .getString("findAgent.table.msisdn", this))
		    .setVisible(true));
	    dataViewContainer.addOrReplace(new Label("HdisplayName",
		    getLocalizer().getString("findAgent.table.displayName",
			    this)).setVisible(false));

	}
	dataProvider = new CustomerBeanDataProvider(WICKET_ID_Lid, basePage,
		sortAsc);
	agentsList = new ArrayList<CustomerBean>();
	final DataView<CustomerBean> dataView = new DataView<CustomerBean>(
		WICKET_ID_pageable, dataProvider) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {
		IdentificationFindBean id = null;
		if (PortalUtils.exists(getMsisdn())) {
		    PhoneNumber pn = new PhoneNumber(getMsisdn(), basePage
			    .getConfiguration().getCountryCode());
		    setMsisdn(pn.getInternationalFormat());
		    id = new IdentificationFindBean();
		    id.setType(Constants.IDENT_TYPE_MSISDN);
		    id.setIdentification(getMsisdn());
		    // TODO: Add drop down selector for the org unit of the
		    // identification one option would be find for any org unit
		    id.setAnyOrgUnit(Boolean.TRUE);
		}
		if (PortalUtils.exists(getCustomerId())) {
		    id = new IdentificationFindBean();
		    id.setType(Constants.IDENT_TYPE_CUST_ID);
		    id.setIdentification(Long.toString(getCustomerId()));
		    // TODO: Add drop down selector for the org unit of the
		    // identification one option would be find for any org unit
		    id.setAnyOrgUnit(Boolean.TRUE);
		} else if (PortalUtils.exists(getUserName())) {
		    id = new IdentificationFindBean();
		    id.setType(Constants.IDENT_TYPE_USERNAME);
		    id.setIdentification(getUserName().replaceAll("\\*", "%"));
		}
		AddressFindBean address = null;
		if (PortalUtils.exists(getFirstName())
			|| PortalUtils.exists(getLastName())
			|| PortalUtils.exists(getEmail())
			|| PortalUtils.exists(getCity())
			|| PortalUtils.exists(getStreet())
			|| PortalUtils.exists(getZip())) {
		    address = new AddressFindBean();
		    if (PortalUtils.exists(getFirstName()))
			address.setFirstName(getFirstName().replaceAll("\\*",
				"%"));
		    if (PortalUtils.exists(getLastName()))
			address.setLastName(getLastName()
				.replaceAll("\\*", "%"));
		    if (PortalUtils.exists(getEmail()))
			address.setEmail(getEmail().replaceAll("\\*", "%"));
		    if (PortalUtils.exists(getCity()))
			address.setCity(getCity().replaceAll("\\*", "%"));
		    if (PortalUtils.exists(getStreet()))
			address.setStreet(getStreet().replaceAll("\\*", "%"));
		    if (PortalUtils.exists(getZip()))
			address.setZip(getZip().replaceAll("\\*", "%"));
		}

		try {
		    if (isHierarchical) {
			final FindHierarchicalCustomerRequest request = customerFinder
				.createFindHierarchicalAgentRequest(id, address);
			// if agent is logged in then use parent id to get the
			// children list
			// get the agent type id list acting on behalf of agents
			// from prefs
			String agentTypeIdsStr = basePage.getConfiguration()
				.getMerchantAgentTypeIds();
			String[] agentTypeIdsStrArr = agentTypeIdsStr
				.split(",");
			for (String agentTypeId : agentTypeIdsStrArr) {
			    if (PortalUtils.exists(agentTypeId)
				    && Long.parseLong(agentTypeId.trim()) == basePage
					    .getMobiliserWebSession()
					    .getLoggedInCustomer()
					    .getCustomerTypeId()) {
				request.setAgentId(basePage
					.getMobiliserWebSession()
					.getLoggedInCustomer().getParentId());
				continue;
			    }
			}
			dataProvider.findCustomer(request, forceReload);
		    } else {

			if (PortalUtils.exists(customerSearchType)) {
			    final FindCustomerRequest request = customerFinder
				    .createFindAgentRequest(id, address);
			    dataProvider.findCustomer(request, forceReload,
				    customerSearchType);

			} else {
			    final FindPendingCustomersRequest request = customerFinder
				    .createFindPendingCustomerRequest(
					    getCustomerTypeId(), getUserName(),
					    fromDateXml, toDateXml, id, address);
			    dataProvider.findPendingCustomers(request,
				    forceReload, customerSearchType);
			}

		    }
		    forceReload = false;
		    refreshTotalItemCount();
		    if (dataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error("An error occured while loading agent list", dple);
		    error(getLocalizer().getString("agent.find.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @SuppressWarnings("rawtypes")
	    @Override
	    protected void populateItem(final Item<CustomerBean> item) {
		final CustomerBean entry = item.getModelObject();

		agentsList.add(entry);
		Link idLink = new Link<CustomerBean>(WICKET_ID_LidLink,
			item.getModel()) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {
			CustomerBean cModel = (CustomerBean) item
				.getModelObject();
			customerFinder.loadAgentDetails(cModel);
		    }
		};
		item.add(idLink);
		idLink.add(new Label(WICKET_ID_Lid, String.valueOf(entry
			.getId())));
		if (Constants.SEARCH_TYPE_AGENT.equals(customerSearchType)) {
		    item.add(new Label(WICKET_ID_LuserName, entry.getUserName())
			    .setVisible(true));
		    item.add(new Label(WICKET_ID_LfirstName,
			    entry.getAddress() != null ? entry.getAddress()
				    .getFirstName() : "").setVisible(true));
		    item.add(new Label(WICKET_ID_LlastName,
			    entry.getAddress() != null ? entry.getAddress()
				    .getLastName() : "").setVisible(true));
		    item.add(new Label(WICKET_ID_Lemail,
			    entry.getAddress() != null ? entry.getAddress()
				    .getEmail() : "").setVisible(true));
		    item.add(new Label(WICKET_ID_LdisplayName, entry
			    .getDisplayName()).setVisible(false));
		    item.add(new Label(WICKET_ID_Lmsisdn, entry.getMsisdn())
			    .setVisible(false));
		} else if (Constants.SEARCH_TYPE_CUSTOMER
			.equals(customerSearchType)) {
		    item.add(new Label(WICKET_ID_LuserName, entry.getUserName())
			    .setVisible(false));
		    item.add(new Label(WICKET_ID_LfirstName,
			    entry.getAddress() != null ? entry.getAddress()
				    .getFirstName() : "").setVisible(false));
		    item.add(new Label(WICKET_ID_LlastName,
			    entry.getAddress() != null ? entry.getAddress()
				    .getLastName() : "").setVisible(false));
		    item.add(new Label(WICKET_ID_Lemail,
			    entry.getAddress() != null ? entry.getAddress()
				    .getEmail() : "").setVisible(false));
		    item.add(new Label(WICKET_ID_LdisplayName, entry
			    .getDisplayName()).setVisible(true));
		    item.add(new Label(WICKET_ID_Lmsisdn, entry.getMsisdn())
			    .setVisible(true));
		} else {
		    item.add(new Label(WICKET_ID_LuserName, entry.getUserName())
			    .setVisible(true));
		    item.add(new Label(WICKET_ID_LfirstName,
			    entry.getAddress() != null ? entry.getAddress()
				    .getFirstName() : "").setVisible(true));
		    item.add(new Label(WICKET_ID_LlastName,
			    entry.getAddress() != null ? entry.getAddress()
				    .getLastName() : "").setVisible(true));
		    item.add(new Label(WICKET_ID_Lemail,
			    entry.getAddress() != null ? entry.getAddress()
				    .getEmail() : "").setVisible(true));
		    item.add(new Label(WICKET_ID_LdisplayName, entry
			    .getDisplayName()).setVisible(false));
		    item.add(new Label(WICKET_ID_Lmsisdn, entry.getMsisdn())
			    .setVisible(true));
		}

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

	};
	dataView.setItemsPerPage(10);
	dataViewContainer.addOrReplace(dataView);

	dataViewContainer.addOrReplace(new OrderByBorder(WICKET_ID_orderById,
		WICKET_ID_LfirstName, dataProvider) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSortChanged() {
		sortAsc = !sortAsc;

		// For some reasons the dataView can be null when the
		// page is
		// loading
		// and the sort is clicked (clicking the name header),
		// so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}

		createFindAgentDataView(dataViewContainer, true);

	    }
	});

	String noItemMsg = PortalUtils.exists(customerSearchType) ? getLocalizer()
		.getString("agentList.noItemsMsg", this) : getLocalizer()
		.getString("pending.customers.noItemsMsg", this);

	dataViewContainer.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		noItemMsg) {
	    private static final long serialVersionUID = 1L;

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
	dataViewContainer.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));
	dataViewContainer.setVisible(isVisible);

    }

    public Integer getCustomerTypeId() {
	return customerTypeId;
    }

    public void setCustomerTypeId(Integer customerTypeId) {
	this.customerTypeId = customerTypeId;
    }

    public String getMsisdn() {
	return msisdn;
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getDisplayName() {
	return displayName;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    public String getStreet() {
	return street;
    }

    public void setStreet(String street) {
	this.street = street;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getZip() {
	return zip;
    }

    public void setZip(String zip) {
	this.zip = zip;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public Long getCustomerId() {
	return customerId;
    }

    public void setCustomerId(Long customerId) {
	this.customerId = customerId;
    }

    public String getUserName() {
	return userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
    }

}
