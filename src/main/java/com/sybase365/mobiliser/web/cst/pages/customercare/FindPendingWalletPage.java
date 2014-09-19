package com.sybase365.mobiliser.web.cst.pages.customercare;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PendingWalletEntry;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.WalletEntryDataProvider;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_CST_LOGIN)
public class FindPendingWalletPage extends BaseCustomerCarePage {

    private static final long serialVersionUID = 1L;
    private WalletEntryDataProvider dataProvider;

    Form<?> form;
    private LocalizableLookupDropDownChoice<Integer> PITypesList;
    private Integer piType;
    private String filterType = Constants.TXN_FILTERTYPE_MONTH;
    private String fromMonth;
    private Date fromDate;
    private Date toDate;
    private XMLGregorianCalendar fromDateXml;
    private XMLGregorianCalendar toDateXml;

    private static final Logger LOG = LoggerFactory
	    .getLogger(FindPendingWalletPage.class);

    private List<WalletEntry> walletEntries;

    private int rowIndex = 1;
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
    private static final String WICKET_ID_piType = "piType";
    private static final String WICKET_ID_id = "id";
    private static final String WICKET_ID_LidLink = "LidLink";
    private static final String WICKET_ID_customerId = "customerId";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public FindPendingWalletPage() {
	super();
	LOG.info("Created new ApproveWalletsPage");
    }

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
	final Form<?> form = new Form("findWalletsForm",
		new CompoundPropertyModel<FindPendingWalletPage>(this));

	// form = new Form("findWalletsForm");
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
		"fromMonth", getSelectableMonth());
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

	PITypesList = (LocalizableLookupDropDownChoice<Integer>) new LocalizableLookupDropDownChoice<Integer>(
		"piType", Integer.class, "pitypes", this, false, true)
		.setNullValid(true).add(new ErrorIndicator());

	form.add(PITypesList);

	final WebMarkupContainer dataViewContainer = new WebMarkupContainer(
		"dataViewContainer");
	form.add(dataViewContainer);
	form.add(new Button("findWallets") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		validateInputs();
		createSearchResultsView(dataViewContainer, true);
	    };
	});
	createSearchResultsView(dataViewContainer, false);
	add(form);

    }

    @SuppressWarnings("rawtypes")
    protected void createSearchResultsView(
	    final WebMarkupContainer dataViewContainer, boolean isVisible) {
	// create the header row

	walletEntries = new ArrayList<WalletEntry>();

	dataProvider = new WalletEntryDataProvider(WICKET_ID_id, this,
		Boolean.TRUE);

	final DataView<WalletEntry> dataView = new DataView<WalletEntry>(
		WICKET_ID_pageable, dataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadPendingWalletEntries(getPiType(),
			    getFromDateXml(), getToDateXml(), forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    if (dataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }

		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading pending wallets",
			    dple);
		    error(getLocalizer().getString("pendingWallets.load.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<WalletEntry> item) {

		final WalletEntry entry = item.getModelObject();

		walletEntries.add(entry);

		Link idLink = new Link<WalletEntry>(WICKET_ID_LidLink,
			item.getModel()) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {
			approveWallet(entry);
		    }
		};
		item.add(idLink);

		idLink.add(new Label(WICKET_ID_id, ((PendingWalletEntry) entry)
			.getTaskId()));

		item.add(new Label(WICKET_ID_piType, getPiTypeName(entry)));

		// item.add(new Label(WICKET_ID_piId, String.valueOf(entry
		// .getPaymentInstrumentId())));

		item.add(new Label(WICKET_ID_customerId, String.valueOf(entry
			.getCustomerId())));

		// set items in even/odd rows to different css style classes
		item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
			true, new AbstractReadOnlyModel<String>() {
			    @Override
			    public String getObject() {
				return (item.getIndex() % 2 == 1) ? Constants.CSS_STYLE_ODD
					: Constants.CSS_STYLE_EVEN;
			    }
			}));

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

	};

	dataView.setItemsPerPage(10);
	dataViewContainer.addOrReplace(dataView);

	dataViewContainer.addOrReplace(new OrderByBorder(WICKET_ID_orderById,
		WICKET_ID_id, dataProvider) {
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

	dataViewContainer.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("findWallets.noItemsMsg", this)) {
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

    public String getFromMonth() {
	return fromMonth;
    }

    public void setFromMonth(String fromMonth) {
	this.fromMonth = fromMonth;
    }

    public XMLGregorianCalendar getFromDateXml() {
	return fromDateXml;
    }

    public void setFromDateXml(XMLGregorianCalendar fromDateXml) {
	this.fromDateXml = fromDateXml;
    }

    public XMLGregorianCalendar getToDateXml() {
	return toDateXml;
    }

    public void setToDateXml(XMLGregorianCalendar toDateXml) {
	this.toDateXml = toDateXml;
    }

    public Integer getPiType() {
	return piType;
    }

    public void setPiType(Integer piType) {
	this.piType = piType;
    }

    @Override
    protected Class getActiveMenu() {
	return this.getClass();
    }

    private void validateInputs() {

	LOG.debug("#validateInputs");

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

    private void approveWallet(WalletEntry wallet) {

	setResponsePage(getResponsePage(wallet));

    }

    private String getPiTypeName(WalletEntry wallet) {

	if (wallet.getSva() != null) {
	    return getDisplayValue(String.valueOf(wallet.getSva().getType()),
		    Constants.RESOURCE_BUNDLE_PI_TYPES);
	} else if (wallet.getBankAccount() != null) {
	    return getDisplayValue(
		    String.valueOf(wallet.getBankAccount().getType()),
		    Constants.RESOURCE_BUNDLE_PI_TYPES);
	} else if (wallet.getCreditCard() != null) {
	    return getDisplayValue(
		    String.valueOf(wallet.getCreditCard().getType()),
		    Constants.RESOURCE_BUNDLE_PI_TYPES);
	} else {
	    return getDisplayValue(
		    String.valueOf(wallet.getExternalAccount().getType()),
		    Constants.RESOURCE_BUNDLE_PI_TYPES);
	}

    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	return new LinkedList<IMenuEntry>();
    }

}
