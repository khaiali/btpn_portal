package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
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
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetTransactionDetailsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetTransactionDetailsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.DetailedTransaction;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.SimpleTransaction;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.beans.SearchTransactionCriteria;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.SimpleTransactionDataProvider;
import com.sybase365.mobiliser.web.common.model.ITransactionDetailViewer;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class TransactionHistoryPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TransactionHistoryPanel.class);

    /* Reference to logged in customer for locale settings */
    private CustomerBean customerBean;
    private MobiliserBasePage mobBasePage;
    private ITransactionDetailViewer txnDetailsViewer;
    private boolean isTxnDetailsPagePopup;

    private LocalizableLookupDropDownChoice<Integer> useCaseList;
    private Integer useCaseId;
    private String filterType = Constants.TXN_FILTERTYPE_MONTH;
    private String fromMonth;
    private Date fromDate;
    private Date toDate;
    private XMLGregorianCalendar fromDateXml;
    private XMLGregorianCalendar toDateXml;

    private Integer status;
    private Boolean showFaulty;
    private Boolean showInitial;
    private Boolean consumerIsPayer;
    private String txnId;
    private String orderId;
    boolean requiredStatus;
    SearchTransactionCriteria criteria;
    boolean isForApproval;

    // Data Model for table list
    private SimpleTransactionDataProvider dataProvider;

    List<SimpleTransaction> selectedFriends = new ArrayList<SimpleTransaction>();
    private List<SimpleTransaction> transactions = new ArrayList<SimpleTransaction>();

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private static List<KeyValue<Integer, String>> txnStatus;
    private static List<KeyValue<Boolean, String>> txnOptions;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_creationDate = "creationDate";
    private static final String WICKET_ID_useCase = "useCase";
    private static final String WICKET_ID_payer = "payer";
    private static final String WICKET_ID_txnId = "txnId";
    private static final String WICKET_ID_payee = "payee";
    private static final String WICKET_ID_participant = "participant";
    private static final String WICKET_ID_text = "text";
    private static final String WICKET_ID_h_errorCode = "h_errorCode";
    private static final String WICKET_ID_h_ParticipantName = "h_ParticipantName";
    private static final String WICKET_ID_h_text = "h_text";
    private static final String WICKET_ID_errorCode = "errorCode";
    private static final String WICKET_ID_txnStatus = "txnStatus";
    private static final String WICKET_ID_h_txnStatus = "h_txnStatus";
    private static final String WICKET_ID_h_txnCreationDate = "h_txnCreationDate";
    private static final String WICKET_ID_h_txnId = "h_txnId";

    private static final String WICKET_ID_h_payerId = "h_payerId";
    private static final String WICKET_ID_h_payeeId = "h_payeeId";
    private static final String WICKET_ID_amount = "amount";
    private static final String WICKET_ID_detailsAction = "detailsAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    private PrivilegedBehavior customer_merchant_txnPriv;
    private PrivilegedBehavior consumer_txnPriv;

    /*
     * @param id panel wicket id
     * 
     * @param joinedCustomerId agent id, needs to be set only to load
     * transaction between agent and customer
     * 
     * @param loggedInCustomer customer for which transaction needs to be
     * searched
     * 
     * @param mobBasePage page where panel is being added
     * 
     * @param txnDetailsViewer
     * 
     * @param isTxnDetailsPagePopup
     */
    public TransactionHistoryPanel(String id,
	    SearchTransactionCriteria criteria, CustomerBean customerBean,
	    MobiliserBasePage mobBasePage,
	    ITransactionDetailViewer txnDetailsViewer,
	    boolean isTxnDetailsPagePopup, Boolean requiredStatus) {

	super(id);

	this.mobBasePage = mobBasePage;

	if (!PortalUtils.exists(customerBean)) {
	    isForApproval = true;
	    status = Integer.valueOf(Constants.TXN_STATUS_PENDING_APPROVAL);
	}

	final String chooseDtTxt = this.mobBasePage.getLocalizer().getString(
		"datepicker.chooseDate", this.mobBasePage);

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

	this.customerBean = customerBean;
	this.txnDetailsViewer = txnDetailsViewer;
	this.isTxnDetailsPagePopup = isTxnDetailsPagePopup;
	this.requiredStatus = true;
	this.criteria = criteria;
	if (PortalUtils.exists(requiredStatus)) {
	    this.requiredStatus = requiredStatus;
	}

	customer_merchant_txnPriv = new PrivilegedBehavior(mobBasePage,
		Constants.PRIV_MERCHANT_TXN_HISTORY, Constants.PRIV_CST_LOGIN);
	customer_merchant_txnPriv.setMatchAllPrivileges(false);
	consumer_txnPriv = new PrivilegedBehavior(mobBasePage,
		Constants.PRIV_CONSUMER_TXN_HISTORY);
	LOG.info("Created new TransactionHistoryPanel");
	constructPanel();
    }

    @SuppressWarnings("unchecked")
    private void constructPanel() {

	Form<?> form = new Form("viewtxnform",
		new CompoundPropertyModel<TransactionHistoryPanel>(this));

	form.add(new FeedbackPanel("errorMessages"));

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
		"fromMonth", mobBasePage.getSelectableMonth());
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
	fromDateTxt.setRequired(true);
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

	form.add(new Button("Search") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit(this.getForm());

	    }
	});

	createTransactionsListDataView(form, false);

	WebMarkupContainer cstTxnFilter = new WebMarkupContainer("cstTxnFilter");

	cstTxnFilter.add(new KeyValueDropDownChoice<Integer, String>("status",
		getTxnStatus()).setNullValid(true).add(new ErrorIndicator()));
	cstTxnFilter.add(new KeyValueDropDownChoice<Boolean, String>(
		"showInitial", getTxnOptions()).setNullValid(true).add(
		new ErrorIndicator()));
	cstTxnFilter.add(new KeyValueDropDownChoice<Boolean, String>(
		"showFaulty", getTxnOptions()).setNullValid(true).add(
		new ErrorIndicator()));
	cstTxnFilter.add(new KeyValueDropDownChoice<Boolean, String>(
		"consumerIsPayer", getTxnOptions()).setNullValid(true).add(
		new ErrorIndicator()));
	cstTxnFilter.add(new TextField<String>("txnId")
		.add(Constants.amountSimpleAttributeModifier));
	cstTxnFilter.add(new TextField<String>("orderId").add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier));
	cstTxnFilter.add(new PrivilegedBehavior(mobBasePage,
		Constants.PRIV_CST_LOGIN));

	useCaseList = (LocalizableLookupDropDownChoice<Integer>) new LocalizableLookupDropDownChoice<Integer>(
		"useCaseId", Integer.class, "usecases", this, false, true)
		.setNullValid(true).add(new ErrorIndicator());
	useCaseList.setRequired(false);

	WebMarkupContainer useCaseContainer = new WebMarkupContainer(
		"useCaseFilter");
	useCaseContainer.add(useCaseList);
	useCaseContainer.setVisible(Boolean.FALSE);

	form.add(useCaseContainer);

	form.add(cstTxnFilter);

	if (!requiredStatus) {
	    monthDropDown.setRequired(false);
	    monthLbl.add(new Label("monthRequiredID", ""));
	    fromDateLabel.add(new Label("fromDateRequiredID", ""));
	    toDateLabel.add(new Label("toDateRequiredID", ""));
	    rg.add(new Label("searchOptionRequiredID", ""));
	} else {
	    monthDropDown.setRequired(true);
	    monthLbl.add(new Label("monthRequiredID", "*"));
	    fromDateLabel.add(new Label("fromDateRequiredID", "*"));
	    toDateLabel.add(new Label("toDateRequiredID", "*"));
	    rg.add(new Label("searchOptionRequiredID", "*"));
	}
	form.add(monthLbl);
	form.add(fromDateLabel);
	form.add(toDateLabel);
	form.add(rg);

	add(form);

	LOG.debug("PatternDateConverter format: "
		+ Constants.DATE_FORMAT_PATTERN_PARSE + " DatePicker format: "
		+ Constants.DATE_FORMAT_PATTERN_PICKER);
    }

    private void handleSubmit(Form<?> form) {

	transactions = null;

	forceReload = true;

	validateInputs();

	createTransactionsListDataView(form, true);
    }

    /**
     *
     */
    private void showTransactions() {

	if (!PortalUtils.exists(transactions)) {
	    LOG.debug("# no transaction found");
	    transactions = null;
	} else {
	    if (transactions.size() == Integer.valueOf(mobBasePage
		    .getConfiguration().getTxnMaxNumberToFetch())) {
		LOG.debug("The maximum number of transactions were fetched.");
	    }
	}
    }

    private void validateInputs() {

	LOG.debug("#validateInputs");

	fromDateXml = null;
	toDateXml = null;

	if (filterType.equals(Constants.TXN_FILTERTYPE_TIMEFRAME)
		&& PortalUtils.exists(fromDate) && PortalUtils.exists(toDate)) {
	    if (PortalUtils.exists(fromDate)) {
		fromDateXml = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			fromDate, mobBasePage.getMobiliserWebSession()
				.getTimeZone());
	    }
	    if (PortalUtils.exists(toDate)) {
		toDateXml = PortalUtils.getSaveXMLGregorianCalendarFromDate(
			toDate, mobBasePage.getMobiliserWebSession()
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
	    fromDateXml = PortalUtils.getXmlFromDateOfMonth(mobBasePage
		    .getMobiliserWebSession().getTimeZone(), _month, _year);
	    toDateXml = PortalUtils.getXmlToDateOfMonth(mobBasePage
		    .getMobiliserWebSession().getTimeZone(), _month, _year);
	    LOG.debug("From [{}] to [{}]", fromDateXml.toString(),
		    toDateXml.toString());

	}
    }

    protected void showTransactionDetails(SimpleTransaction txnBean,
	    boolean redirect) {
	if (txnDetailsViewer != null) {
	    if (redirect) {
		getRequestCycle().setRedirect(true);
		getRequestCycle().setResponsePage(
			txnDetailsViewer.getTransactionDetailViewer(txnBean,
				customerBean.getId(), mobBasePage));
	    } else
		setResponsePage(txnDetailsViewer.getTransactionDetailViewer(
			txnBean,
			PortalUtils.exists(customerBean) ? customerBean.getId()
				: null, mobBasePage));
	}
    }

    private void createTransactionsListDataView(Form form,
	    final boolean isVisible) {
	WebMarkupContainer dataViewContainer = new WebMarkupContainer(
		"dataViewContainer");

	Label dateHeader = (Label) new Label(WICKET_ID_h_txnCreationDate,
		getLocalizer().getString("transactionHistory.table.date", this));
	dataViewContainer.addOrReplace(dateHeader);

	Label txnIdHeader = (Label) new Label(WICKET_ID_h_txnId, getLocalizer()
		.getString("transactionHistory.table.txnId", this));
	dataViewContainer.addOrReplace(txnIdHeader.setVisible(false));

	Label statusHeader = (Label) new Label(WICKET_ID_h_txnStatus,
		getLocalizer().getString("transactionHistory.cstTxn.status",
			this)).add(customer_merchant_txnPriv);
	dataViewContainer.addOrReplace(statusHeader);

	Label participantHeader = (Label) new Label(
		WICKET_ID_h_ParticipantName, getLocalizer().getString(
			"transactionHistory.cstTxn.participant", this));

	dataViewContainer.addOrReplace(participantHeader);

	Label errorHeader = (Label) new Label(WICKET_ID_h_errorCode,
		getLocalizer().getString("transactionHistory.table.errorCode",
			this)).add(customer_merchant_txnPriv);
	dataViewContainer.addOrReplace(errorHeader);

	Label descLabel = (Label) new Label(WICKET_ID_h_text, getLocalizer()
		.getString("transactionHistory.table.text", this))
		.add(consumer_txnPriv);

	dataViewContainer.addOrReplace(descLabel);

	Label payeeHeader = (Label) new Label(WICKET_ID_h_payeeId,
		getLocalizer().getString("transactionHistory.table.payeeId",
			this));
	dataViewContainer.addOrReplace(payeeHeader.setVisible(false));

	Label payerHeader = (Label) new Label(WICKET_ID_h_payerId,
		getLocalizer().getString("transactionHistory.table.payerId",
			this));
	dataViewContainer.addOrReplace(payerHeader.setVisible(false));

	if (isForApproval) {
	    statusHeader.setVisible(false);
	    participantHeader.setVisible(false);
	    errorHeader.setVisible(false);
	    descLabel.setVisible(false);
	    payeeHeader.setVisible(true);
	    payerHeader.setVisible(true);
	    dateHeader.setVisible(false);
	    txnIdHeader.setVisible(true);
	}

	dataProvider = new SimpleTransactionDataProvider(
		WICKET_ID_creationDate, mobBasePage);
	transactions = new ArrayList<SimpleTransaction>();

	dataViewContainer.addOrReplace(new Label("txnAction", getLocalizer()
		.getString("transactionHistory.table.actions", this)));

	final DataView<SimpleTransaction> dataView = new DataView<SimpleTransaction>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		if (showFaulty != null)
		    criteria.setShowFaulty(showFaulty);
		if (showInitial != null)
		    criteria.setShowInitial(showInitial);
		criteria.setFromDateXml(fromDateXml);
		criteria.setToDateXml(toDateXml);
		criteria.setTxnStatus(status);
		criteria.setOrderID(orderId);
		criteria.setTxnID(txnId);
		criteria.setConsumerIsPayer(consumerIsPayer);
		criteria.setUseCaseId(getUseCaseId());

		try {
		    dataProvider.loadCustomerTransactions(criteria, Integer
			    .valueOf(mobBasePage.getConfiguration()
				    .getTxnMaxNumberToFetch()), forceReload);

		    forceReload = false;

		    refreshTotalItemCount();
		} catch (DataProviderLoadException dple) {
		    error(getLocalizer().getString("transactions.load.error",
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

	    @Override
	    protected void populateItem(final Item<SimpleTransaction> item) {

		final SimpleTransaction entry = item.getModelObject();

		if (isForApproval) {
		    getPendingTxnDetails(entry);
		    customerBean = Converter
			    .getInstance()
			    .getCustomerBeanFromCustomer(
				    mobBasePage.getCustomerByIdentification(
					    Constants.IDENT_TYPE_CUST_ID,
					    String.valueOf(entry.getPayerId())));
		}

		item.add(new Label(WICKET_ID_creationDate,
			PortalUtils.getFormattedDateTime(entry
				.getCreationDate(), mobBasePage
				.getMobiliserWebSession().getLocale(),
				mobBasePage.getMobiliserWebSession()
					.getTimeZone()))
			.setVisible(!isForApproval));

		item.add(new Label(WICKET_ID_txnId, String.valueOf(entry
			.getId())).setVisible(isForApproval));

		item.add(new Label(WICKET_ID_useCase, mobBasePage
			.getDisplayValue(String.valueOf(entry.getUseCase()),
				Constants.RESOURCE_BUNDLE_USE_CASES)));

		item.add(new Label(WICKET_ID_txnStatus, getLocalizer()
			.getString("cstTxn.status." + entry.getStatus(), this))
			.add(customer_merchant_txnPriv).setVisible(
				!isForApproval));

		item.add(new Label(WICKET_ID_errorCode, Integer.toString(entry
			.getErrorCode())).add(customer_merchant_txnPriv)
			.setVisible(!isForApproval));

		if (customerBean.getId() != entry.getPayerId()) {
		    item.add(new Label(WICKET_ID_participant, entry
			    .getPayerDisplayName()).setVisible(!isForApproval));
		} else {
		    item.add(new Label(WICKET_ID_participant, entry
			    .getPayeeDisplayName()).setVisible(!isForApproval));
		}

		item.add(new Label(WICKET_ID_payer, String.valueOf(entry
			.getPayerId())).setVisible(isForApproval));

		item.add(new Label(WICKET_ID_payee, String.valueOf(entry
			.getPayeeId())).setVisible(isForApproval));

		item.add(new Label(WICKET_ID_text, entry.getText()).add(
			consumer_txnPriv).setVisible(!isForApproval));

		item.add(new Label(WICKET_ID_amount, mobBasePage
			.getTransactionAmount(entry, customerBean.getId())));

		// Details Action
		Link detailsLink = (Link) new Link<SimpleTransaction>(
			WICKET_ID_detailsAction, item.getModel()) {
		    @Override
		    public void onClick() {
			SimpleTransaction entry = (SimpleTransaction) getModelObject();
			showTransactionDetails(entry, false);
		    }
		};

		if (isTxnDetailsPagePopup) {
		    PopupSettings popupSettings = new PopupSettings();
		    popupSettings.setWidth(375).setHeight(350)
			    .setWindowName("TxnDetailsPopup");
		    detailsLink.setPopupSettings(popupSettings);
		}

		item.add(detailsLink);

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
		totalItemString = new Integer(dataProvider.size()).toString();
		int total = dataProvider.size();
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

	dataViewContainer.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("transactionHistory.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString(
				"transactionHistory.search.help", this)) {
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
	form.addOrReplace(dataViewContainer);

    }

    private List<KeyValue<Integer, String>> getTxnStatus() {
	if (txnStatus == null) {
	    txnStatus = new ArrayList<KeyValue<Integer, String>>();
	    txnStatus.add(new KeyValue<Integer, String>(
		    Constants.TXN_STATUS_AUTHORISED, getLocalizer().getString(
			    "cstTxn.status." + Constants.TXN_STATUS_AUTHORISED,
			    this)));
	    txnStatus.add(new KeyValue<Integer, String>(
		    Constants.TXN_STATUS_CAPTURED, getLocalizer().getString(
			    "cstTxn.status." + Constants.TXN_STATUS_CAPTURED,
			    this)));
	    txnStatus.add(new KeyValue<Integer, String>(
		    Constants.TXN_STATUS_AUTHCANCEL, getLocalizer().getString(
			    "cstTxn.status." + Constants.TXN_STATUS_AUTHCANCEL,
			    this)));
	    txnStatus
		    .add(new KeyValue<Integer, String>(
			    Constants.TXN_STATUS_CAPTURECANCEL,
			    getLocalizer()
				    .getString(
					    "cstTxn.status."
						    + Constants.TXN_STATUS_CAPTURECANCEL,
					    this)));

	    txnStatus.add(new KeyValue<Integer, String>(
		    Constants.TXN_STATUS_PENDING_APPROVAL,
		    getLocalizer().getString(
			    "cstTxn.status."
				    + Constants.TXN_STATUS_PENDING_APPROVAL,
			    this)));

	}
	return txnStatus;

    }

    private List<KeyValue<Boolean, String>> getTxnOptions() {
	if (txnOptions == null) {
	    txnOptions = new ArrayList<KeyValue<Boolean, String>>();
	    txnOptions
		    .add(new KeyValue<Boolean, String>(
			    Constants.TXN_OPTION_YES, getLocalizer()
				    .getString(
					    "cstTxn.option."
						    + Constants.TXN_OPTION_YES,
					    this)));
	    txnOptions.add(new KeyValue<Boolean, String>(
		    Constants.TXN_OPTION_NO, getLocalizer().getString(
			    "cstTxn.option." + Constants.TXN_OPTION_NO, this)));

	}
	return txnOptions;
    }

    private void getPendingTxnDetails(SimpleTransaction txn) {
	LOG.debug("# SimpleTransactionDataProvider.getPendingTxnDetails(...)");
	GetTransactionDetailsResponse response;
	DetailedTransaction txnDetail;
	try {
	    GetTransactionDetailsRequest request = mobBasePage
		    .getNewMobiliserRequest(GetTransactionDetailsRequest.class);
	    request.setTxnId(txn.getId());
	    response = mobBasePage.wsTransactionsClient
		    .getTransactionDetails(request);

	    if (!mobBasePage.evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while loading details of pending transaction");
		return;

	    }
	    txnDetail = response.getTransaction();
	    txn.setAmount(txnDetail.getAmount());
	    txn.setAuthCode(txnDetail.getAuthCode());
	    txn.setCallerId(txnDetail.getCallerId());
	    txn.setCreationDate(txnDetail.getCreationDate());
	    txn.setErrorCode(txnDetail.getErrorCode());
	    txn.setId(txnDetail.getId());
	    txn.setPayeeAmount(txnDetail.getPayeeAmount());
	    txn.setPayerAmount(txnDetail.getPayerAmount());
	    txn.setPayeeDisplayName(txnDetail.getPayeeDisplayName());
	    txn.setPayerDisplayName(txnDetail.getPayerDisplayName());
	    txn.setPayeeId(txnDetail.getPayeeId());
	    txn.setPayerId(txnDetail.getPayerId());
	    txn.setPayeePiType(txnDetail.getPayeePiType());
	    txn.setPayerPiType(txnDetail.getPayerPiType());
	    txn.setText(txnDetail.getText());
	    txn.setUseCase(txnDetail.getUseCase());
	    txn.setPayeePiId(txnDetail.getPayeePiId());
	    txn.setPayerPiId(txnDetail.getPayerPiId());
	    txn.setStatus(txnDetail.getStatus());

	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while loading details of pending transaction",
		    e);
	}

    }

    public void setShowInitial(Boolean showInitial) {
	this.showInitial = showInitial;
    }

    public Boolean getShowInitial() {
	return showInitial;
    }

    public Integer getUseCaseId() {
	return useCaseId;
    }

    public void setUseCaseId(Integer useCaseId) {
	this.useCaseId = useCaseId;
    }

}
