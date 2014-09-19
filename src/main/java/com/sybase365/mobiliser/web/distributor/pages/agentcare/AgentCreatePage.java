package com.sybase365.mobiliser.web.distributor.pages.agentcare;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreateBalanceAlertRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreateBalanceAlertResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.BalanceAlert;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.BalanceAlertDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.AmountConverter;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_AGENTS)
public class AgentCreatePage extends AgentCareMenuGroup {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AgentCreatePage.class);
    private CustomerBean customer;
    private List<BalanceAlert> balanceAlerts;
    private BalanceAlert balanceAlert;
    private String confirmPassword;
    private String actionMode;
    private RequiredTextField<Long> threshold;
    private RequiredTextField<String> templateName;
    private CheckBox onlyTxn;
    private TextField<String> eMail;
    private TextField<String> msisdn;
    private LocalizableLookupDropDownChoice<String> country;
    private LocalizableLookupDropDownChoice<String> language;

    // Data View
    private BalanceAlertDataProvider dataProvider;
    private List<BalanceAlert> balanceAlerList;
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private int rowIndex = 1;
    private FeedbackPanel errorMessagePanel;
    private FeedbackPanel globalErrorMessagePanel;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByTemplateName = "orderByTemplateName";
    private static final String WICKET_ID_removeLink = "removeLink";
    private static final String WICKET_ID_editLink = "editLink";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_threshold = "threshold";
    private static final String WICKET_ID_onlyTransition = "onlyTransition";
    private static final String WICKET_ID_templateName = "templateName";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public AgentCreatePage() {
	super();
	PageParameters params = super.getPageParameters();
	if (PortalUtils.exists(params)) {
	    this.actionMode = params.getString("action");
	}
    }

    public AgentCreatePage(final PageParameters parameter) {
	super(parameter);
	if (PortalUtils.exists(parameter)) {
	    this.actionMode = parameter.getString("action");
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	PageParameters params = super.getPageParameters();
	if (PortalUtils.exists(params)) {
	    this.actionMode = params.getString("action");
	}
	if (!PortalUtils.exists(actionMode)
		|| !"edit".equalsIgnoreCase(actionMode)) {
	    getMobiliserWebSession().setCustomer(null);
	}
	Form<?> agentCreateForm = new Form("AgentCreateForm",
		new CompoundPropertyModel<AgentCreatePage>(this));
	errorMessagePanel = new FeedbackPanel("errorMessages");
	globalErrorMessagePanel = new FeedbackPanel("globalerrorMessages");
	getMobiliserWebSession().setBalanceAlertList(
		new ArrayList<BalanceAlert>());
	agentCreateForm.add(new RequiredTextField<String>(
		"customer.address.firstName")
		.add(new PatternValidator(Constants.REGEX_FIRSTNAME))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentCreateForm.add(new RequiredTextField<String>(
		"customer.address.lastName")
		.add(new PatternValidator(Constants.REGEX_FIRSTNAME))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentCreateForm.add(new TextField<String>("customer.address.company")
		.add(new PatternValidator(Constants.REGEX_COMPANY))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentCreateForm.add(
		new TextField<String>("customer.address.position")
			.add(new PatternValidator(Constants.REGEX_POSITION))
			.add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());

	agentCreateForm.add(new TextField<String>("customer.address.email")
		.setRequired(true).add(EmailAddressValidator.getInstance())
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	agentCreateForm.add(new LocalizableLookupDropDownChoice<String>(
		"customer.timeZone", String.class, "timezones", this, false,
		true).add(new ErrorIndicator()));
	// role id
	if (customer == null) {
	    customer = new CustomerBean();
	    customer.setParentId(getWebSession().getLoggedInCustomer()
		    .getCustomerId());
	}

	agentCreateForm.add(new LocalizableLookupDropDownChoice<Integer>(
		"customer.customerTypeId", Integer.class,
		Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE, this, true, true,
		getAllowedChildRoles(getMobiliserWebSession()
			.getLoggedInCustomer().getCustomerId()))
		.setNullValid(true)
		.setRequired(true)
		.add(new ErrorIndicator())
		.add(new SimpleAttributeModifier("onchange",
			"handleView_agentCreate();")));

	agentCreateForm.add(new TextField<Long>(
		"customer.limitClass.dailyCreditLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(AgentCreatePage.this,
			"agentCreate.limitClass.dailyCreditLimitAmount");
	    }

	    ;
	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentCreateForm.add(new TextField<Long>(
		"customer.limitClass.weeklyCreditLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(AgentCreatePage.this,
			"agentCreate.limitClass.weeklyCreditLimitAmount");
	    }

	    ;
	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentCreateForm.add(new TextField<Long>(
		"customer.limitClass.monthlyCreditLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(AgentCreatePage.this,
			"agentCreate.limitClass.monthlyCreditLimitAmount");
	    }

	    ;
	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentCreateForm.add(new TextField<Long>(
		"customer.limitClass.absoluteCreditLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(AgentCreatePage.this,
			"agentCreate.limitClass.absoluteCreditLimitAmount");
	    }

	    ;
	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentCreateForm.add(new TextField<Long>(
		"customer.limitClass.dailyDebitLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(AgentCreatePage.this,
			"agentCreate.limitClass.dailyDebitLimitAmount");
	    }

	    ;
	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentCreateForm.add(new TextField<Long>(
		"customer.limitClass.weeklyDebitLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(AgentCreatePage.this,
			"agentCreate.limitClass.weeklyDebitLimitAmount");
	    }

	    ;
	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentCreateForm.add(new TextField<Long>(
		"customer.limitClass.monthlyDebitLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(AgentCreatePage.this,
			"agentCreate.limitClass.monthlyDebitLimitAmount");
	    }

	    ;
	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentCreateForm.add(new TextField<Long>(
		"customer.limitClass.absoluteDebitLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(AgentCreatePage.this,
			"agentCreate.limitClass.absoluteDebitLimitAmount");
	    }

	    ;
	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	// Balance Alert

	final WebMarkupContainer bAlertGrid = new WebMarkupContainer(
		"balanceAlertGrid");
	createMarkupContainer(bAlertGrid, agentCreateForm, "ADD");
	// createBalanceAlertDataGrid(agentCreateForm);
	createBalanceAlertDataView(agentCreateForm);

	// Login Data
	agentCreateForm.add(new RequiredTextField<String>("customer.userName")
		.add(new PatternValidator(Constants.REGEX_FIRSTNAME))
		.add(Constants.mediumStringValidator).add(new ErrorIndicator())
		.add(Constants.mediumSimpleAttributeModifier));
	PasswordTextField password = new PasswordTextField("customer.password");
	PasswordTextField confirmPassword = new PasswordTextField(
		"confirmPassword");
	agentCreateForm.add(password.setRequired(true)
		.add(Constants.largeStringValidator)
		.add(new SimpleAttributeModifier("autocomplete", "off"))
		.add(Constants.largeSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentCreateForm.add(confirmPassword.setRequired(true)
		.add(Constants.largeStringValidator)
		.add(new SimpleAttributeModifier("autocomplete", "off"))
		.add(Constants.largeSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentCreateForm.add(new EqualPasswordInputValidator(password,
		confirmPassword));
	agentCreateForm.add(new Button("createAgent") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (!existsCustomer() && !errorMessagePanel.anyErrorMessage()) {
		    errorMessagePanel = new FeedbackPanel("errorMessages");

		    errorMessagePanel.setVisible(true);
		    this.getForm().addOrReplace(errorMessagePanel);
		    createAgent();
		}
	    }

	    ;
	});
	agentCreateForm.add(errorMessagePanel);
	addOrReplace(globalErrorMessagePanel.setVisible(false));
	agentCreateForm.setVisible(true);
	if (getMobiliserWebSession().getCustomer() != null) {

	    if (getMobiliserWebSession().getMaxCreateLevel() != -1
		    && (getMobiliserWebSession().getMaxCreateLevel() == 0 || getMobiliserWebSession()
			    .getMaxCreateLevel() <= getMobiliserWebSession()
			    .getCustomer().getHierarchyLevel())
		    && (PortalUtils
			    .exists(getAllowedChildRoles(getMobiliserWebSession()
				    .getCustomer().getId())))) {
		agentCreateForm.setVisible(false);
		addOrReplace((FeedbackPanel) (new FeedbackPanel(
			"globalerrorMessages").setVisible(true)));
		super.error(getLocalizer().getString(
			"ERROR.MISSING_CREATE_PRIV", this));
	    }
	}
	add(agentCreateForm);
    }

    @SuppressWarnings("unchecked")
    private WebMarkupContainer createMarkupContainer(
	    final WebMarkupContainer bAlertGrid, Form<?> agentCreateForm,
	    String mode) {
	bAlertGrid.setOutputMarkupId(true);
	bAlertGrid.setOutputMarkupPlaceholderTag(true);
	threshold = new RequiredTextField<Long>("balanceAlert.threshold") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(AgentCreatePage.this,
			"agentCreate.balanceAlert.threshold");
	    }

	};

	templateName = new RequiredTextField<String>(
		"balanceAlert.templateName");
	templateName.add(new PatternValidator(Constants.REGEX_TEMPLATE));
	templateName.add(Constants.mediumStringValidator);
	templateName.add(Constants.mediumSimpleAttributeModifier);
	templateName.add(new ErrorIndicator());

	eMail = new TextField<String>("balanceAlert.emails");
	msisdn = new TextField<String>("balanceAlert.msisdns");
	onlyTxn = new CheckBox("balanceAlert.onlyTransition");

	country = (LocalizableLookupDropDownChoice<String>) new LocalizableLookupDropDownChoice<String>(
		"balanceAlert.country", String.class, "countries", this, false,
		true).setNullValid(true).add(new ErrorIndicator());
	language = (LocalizableLookupDropDownChoice<String>) new LocalizableLookupDropDownChoice<String>(
		"balanceAlert.language", String.class, "languages", this,
		false, true).setNullValid(true).add(new ErrorIndicator());
	bAlertGrid.addOrReplace(threshold.add(
		Constants.amountSimpleAttributeModifier).add(
		new ErrorIndicator()));
	bAlertGrid.addOrReplace(eMail
		.add(new PatternValidator(Constants.REGEX_EMAILS))
		.add(Constants.largeStringValidator)
		.add(Constants.largeSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	bAlertGrid.addOrReplace(msisdn
		.add(new PatternValidator(Constants.REGEX_PHONE_NUMBERS))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	bAlertGrid.addOrReplace(templateName
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	bAlertGrid.addOrReplace(onlyTxn);
	bAlertGrid.addOrReplace(language.add(new ErrorIndicator()));
	bAlertGrid.addOrReplace(country.add(new ErrorIndicator()));
	bAlertGrid.addOrReplace(new Button("cancelBalanceAlert") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		clearBalAlertFormInput();
		bAlertGrid.setVisible(false);
	    }

	}.setDefaultFormProcessing(false));
	toggleButtons(bAlertGrid, mode);

	agentCreateForm.addOrReplace(new AjaxLink("balanceAlertAddLink") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		balanceAlert = null;
		toggleButtons(bAlertGrid, "ADD");
		bAlertGrid.setVisible(true);
		target.addComponent(bAlertGrid);
	    }
	});
	agentCreateForm.addOrReplace(bAlertGrid);
	bAlertGrid.setVisible(false);
	return bAlertGrid;
    }

    private void createAgent() {
	LOG.debug("#inside createAgent()");
	if (valedatePasswordStrength(customer.getPassword(),
		Constants.CREDENTIAL_TYPE_PASSWORD)) {
	    try {
		saveManageAgent();
		if (isCreateStatus()) {
		    getMobiliserWebSession().info(
			    getLocalizer().getString(
				    "MESSAGE.CREATE_AGENT_SUCCESS", this));
		    setResponsePage(AgentUmgrHandlingPage.class);
		}

		if (PortalUtils.exists(customer)
			&& customer.isPendingApproval()) {
		    info(getLocalizer().getString("pendingApproval.msg", this));
		    customer = null;
		    return;
		} else {
		    customer.setDisplayName(createDisplayName(customer
			    .getAddress().getFirstName(), customer.getAddress()
			    .getLastName()));
		    getMobiliserWebSession().setCustomer(customer);
		}

	    } catch (Exception ex) {
		LOG.error("# An error occurred while creating agent", ex);
		error(getLocalizer().getString("ERROR.CREATE_AGENT_FAILURE",
			this));
	    }

	}
    }

    private void saveManageAgent() {

	LOG.debug("# try to save new merchant user as \"merchant\"");
	if (getMobiliserWebSession().getCustomer() != null) {
	    customer.setParentId(getMobiliserWebSession().getCustomer().getId());
	    customer.setHierarchyLevel(getMobiliserWebSession().getCustomer()
		    .getHierarchyLevel() + 1);

	}
	try {
	    if (!getMobiliserWebSession().hasPrivilege(
		    Constants.PRIV_ACTIVATE_DESCENDANTS)) {
		if (!getMobiliserWebSession().hasPrivilege(
			Constants.PRIV_CREATE_ACTIVE_DESCENDANTS)) {
		    customer.setBlackListReason(Constants.PENDING_REG_MERCHANT_BLACKLSTREASON);
		} else {
		    customer.setBlackListReason(Constants.DEFAULT_MERCHANT_BLACKLSTREASON);
		}
	    } else {
		customer.setBlackListReason(Constants.DEFAULT_MERCHANT_BLACKLSTREASON);
	    }
	    setCreateStatus(false);
	    customer.setRiskCategoryId(getConfiguration()
		    .getDefaultRiskCatForNewCustomer());
	    customer = createFullCustomer(customer, null);

	} catch (Exception e1) {
	    setCreateStatus(false);
	    error(getLocalizer().getString("ERROR.CREATE_AGENT_FAILURE", this));
	    LOG.error("# An error occurred while creating new customer", e1);
	}

	if (isCreateStatus()) {
	    // create an SVA
	    try {
		createSvaWalletWithPI(customer);
	    } catch (Exception e) {
		setCreateStatus(false);
		error(getLocalizer().getString("ERROR.CREATE_AGENT_FAILURE",
			this));

		LOG.error(
			"# An error occurred while creating new customer's SVA and Payment Instrument",
			e);
	    }

	    // inherit parent PI's if customer has privilege
	    try {
		List<String> privileges = getDefaultPrivileges(
			customer.getCustomerTypeId(),
			customer.getRiskCategoryId());
		if (PortalUtils.exists(privileges)
			&& privileges.contains(Constants.PRIV_AUTO_INHERIT_PIS)) {
		    List<WalletEntry> weList = getWalletEntryList(
			    customer.getParentId(), null, null);
		    if (PortalUtils.exists(weList)) {
			for (WalletEntry we : weList) {
			    LOG.debug("# add sva["
				    + we.getPaymentInstrumentId()
				    + "] to the wallet of customer["
				    + customer.getId() + "]");

			    createAgentWalletEntry(customer.getId(),
				    we.getPaymentInstrumentId(), we.getAlias());
			}
		    }
		}
	    } catch (Exception e) {
		setCreateStatus(false);
		error(getLocalizer().getString("ERROR.CREATE_AGENT_FAILURE",
			this));

		LOG.error(
			"# An error occurred while creating new customer's wallet Entry",
			e);
	    }

	}

	if (isCreateStatus()) {
	    // Save Balance Alert

	    try {

		// TODO
		// Check with Andreas whether it is correct to set parent's
		// PI's PI ID
		WalletEntry svaWallet = getSvaPI(customer.getId());
		Long paymentInstrumentId = null;
		if (PortalUtils.exists(svaWallet))
		    paymentInstrumentId = svaWallet.getPaymentInstrumentId();
		if (PortalUtils.exists(paymentInstrumentId)) {
		    List<BalanceAlert> balanceAlertList = getMobiliserWebSession()
			    .getBalanceAlertList();
		    for (BalanceAlert balAlert : balanceAlertList) {
			if (!PortalUtils.exists(balAlert.getCountry())) {
			    balAlert.setCountry(null);
			}
			if (!PortalUtils.exists(balAlert.getLanguage())) {
			    balAlert.setLanguage(null);
			}

			CreateBalanceAlertRequest balAlertReq = getNewMobiliserRequest(CreateBalanceAlertRequest.class);
			balAlertReq.setBalanceAlert(balAlert);
			balAlert.setPaymentInstrumentId(paymentInstrumentId);
			CreateBalanceAlertResponse balAlertResp = wsBalanceAlertClient
				.createBalanceAlert(balAlertReq);
			if (!evaluateMobiliserResponse(balAlertResp)) {
			    setCreateStatus(false);
			    return;
			}
			setCreateStatus(true);
			LOG.info("# Successfully created Balance Alert data");
		    }
		}
	    } catch (Exception e1) {
		setCreateStatus(false);
		error(getLocalizer().getString("ERROR.CREATE_AGENT_FAILURE",
			this));
		LOG.error(
			"# An error occurred while creating balance alert data",
			e1);
	    }

	    // Save Agent Setting : Limit

	    try {

		if (customer.getLimitClass() != null) {
		    customer = createAgentLimitSetting(customer);

		}
	    } catch (Exception e) {
		LOG.error(
			"# An error occurred whilte updating Limit setting data, with agent Id {}",
			customer.getId(), e);
		error(getLocalizer().getString("ERROR.CHANGE_AGENT_SETTINGS",
			this));
	    }

	    LOG.info("# new merchant user has successfully been stored as \"merchant\"");
	}
	if (!isCreateStatus()) {
	    try {
		if (PortalUtils.exists(customer.getId()))
		    deleteCustomer(customer.getId());
		customer.setId(null);
		if (PortalUtils.exists(customer.getAddress()))
		    customer.getAddress().setId(null);
		customer.setLimitId(null);
		if (PortalUtils.exists(customer.getLimitClass()))
		    customer.getLimitClass().setId(null);
	    } catch (Exception e) {
		LOG.error("# An error occurred whilte inactivating agent", e);
	    }
	}
    }

    private void createBalanceAlertDataView(final Form agentCreateForm) {
	final WebMarkupContainer balanceAlertListGrid = new WebMarkupContainer(
		"balanceAlertListGrid");
	agentCreateForm.addOrReplace(balanceAlertListGrid);
	dataProvider = new BalanceAlertDataProvider(WICKET_ID_pageable, this);
	balanceAlerList = new ArrayList<BalanceAlert>();
	final DataView<BalanceAlert> dataView = new DataView<BalanceAlert>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.getBalanceAlerts(getMobiliserWebSession());
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error("An error occurred while loading balance alerts",
			    dple);
		    error(getLocalizer().getString("balanceAlert.list.failure",
			    this));
		}

		refreshTotalItemCount();
		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}
		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<BalanceAlert> item) {
		final BalanceAlert entry = item.getModelObject();
		balanceAlerList.add(entry);
		item.add(new Label(
			WICKET_ID_threshold,
			String.valueOf(PortalUtils.exists(entry.getThreshold()) ? convertAmountToString(entry
				.getThreshold()) : "")));

		item.add(new Label(WICKET_ID_onlyTransition, entry
			.isOnlyTransition() ? "Yes" : "No"));
		item.add(new Label(WICKET_ID_templateName, entry
			.getTemplateName()));

		Link editLink = new Link<BalanceAlert>(WICKET_ID_editLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			balanceAlert = (BalanceAlert) item.getModelObject();
			WebMarkupContainer bAlertGrid = new WebMarkupContainer(
				"balanceAlertGrid");
			bAlertGrid = createMarkupContainer(bAlertGrid,
				agentCreateForm, "EDIT");
			bAlertGrid.setVisible(true);
			agentCreateForm.addOrReplace(bAlertGrid);
		    }
		};

		item.add(editLink);

		Link removeLink = new Link<BalanceAlert>(WICKET_ID_removeLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			BalanceAlert balAlart = (BalanceAlert) item
				.getModelObject();
			List<BalanceAlert> balAlertList = getMobiliserWebSession()
				.getBalanceAlertList();
			balAlertList.remove(balAlart);
			getMobiliserWebSession().setBalanceAlertList(
				balAlertList);
			WebMarkupContainer bAlertGrid = new WebMarkupContainer(
				"balanceAlertGrid");
			bAlertGrid = createMarkupContainer(bAlertGrid,
				agentCreateForm, "");
			bAlertGrid.setVisible(false);
			balanceAlert = null;
			agentCreateForm.addOrReplace(bAlertGrid);
			balanceAlertListGrid.setVisible(true);
		    }
		};
		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"balanceAlert.remove.confirm", this)
				+ "');"));
		item.add(removeLink);

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
	balanceAlertListGrid.addOrReplace(dataView);
	balanceAlertListGrid.addOrReplace(new OrderByBorder(
		WICKET_ID_orderByTemplateName, WICKET_ID_templateName,
		dataProvider) {
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

	balanceAlertListGrid.addOrReplace(new MultiLineLabel(
		WICKET_ID_noItemsMsg, getLocalizer().getString(
			"balance.alert.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString(
				"balance.alert.noItemsHelpMsg", this)) {
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
	balanceAlertListGrid.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	balanceAlertListGrid.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	balanceAlertListGrid.addOrReplace(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	balanceAlertListGrid.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));
	balanceAlertListGrid.setVisible(true);
    }

    private void toggleButtons(final WebMarkupContainer bAlertGrid, String mode) {
	boolean visible;
	if ("EDIT".equalsIgnoreCase(mode)) {
	    visible = false;
	} else {
	    visible = true;
	}

	bAlertGrid.addOrReplace(new Button("addBalanceAlert") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (gatherFormValues()) {
		    bAlertGrid.setVisible(false);
		    createBalanceAlertDataView(this.getForm());
		} else {
		    return;
		}
	    }
	}.setDefaultFormProcessing(false).setVisible(visible));

	bAlertGrid.addOrReplace(new Button("updateBalanceAlert") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (gatherFormValues()) {
		    bAlertGrid.setVisible(false);
		    createBalanceAlertDataView(this.getForm());
		} else {
		    return;
		}
	    }

	}.setDefaultFormProcessing(false).setVisible(!visible));

    }

    private boolean gatherFormValues() {
	threshold.validate();
	templateName.validate();
	eMail.validate();
	msisdn.validate();
	onlyTxn.validate();
	country.validate();
	language.validate();
	if (threshold.hasErrorMessage() || templateName.hasErrorMessage()
		|| eMail.hasErrorMessage() || msisdn.hasErrorMessage()
		|| onlyTxn.hasErrorMessage()) {
	    return false;
	}
	if (errorMessagePanel.anyErrorMessage()) {
	    return false;
	}
	List<BalanceAlert> balAlertList = getMobiliserWebSession()
		.getBalanceAlertList();
	balAlertList.remove(balanceAlert);
	balanceAlert = new BalanceAlert();
	balanceAlert.setThreshold(threshold.getConvertedInput());
	balanceAlert.setTemplateName(templateName.getConvertedInput());
	balanceAlert.setEmails(eMail.getConvertedInput());
	balanceAlert.setMsisdns(msisdn.getConvertedInput());
	balanceAlert
		.setOnlyTransition(onlyTxn.getConvertedInput() == null ? false
			: onlyTxn.getConvertedInput());
	balanceAlert.setCountry(country.getConvertedInput() == null ? ""
		: country.getConvertedInput());
	balanceAlert.setLanguage(language.getConvertedInput() == null ? ""
		: language.getConvertedInput());
	getMobiliserWebSession().setBalanceAlertList(balanceAlert);
	balanceAlert = null;
	clearBalAlertFormInput();
	return true;
    }

    private void clearBalAlertFormInput() {
	threshold.clearInput();
	templateName.clearInput();
	eMail.clearInput();
	msisdn.clearInput();
	onlyTxn.clearInput();
	country.clearInput();
	language.clearInput();
    }

    private boolean existsCustomer() {
	try {
	    // check if the MSISDN is already registered
	    if (!uniqueIdentificationCheck(customer.getUserName(),
		    Constants.IDENT_TYPE_USERNAME, customer.getId())) {
		return true;
	    }

	} catch (Exception e) {
	    LOG.error(
		    "# An error has occurred for GetCustomerByIdentificationRequest ",
		    e);
	    error(getLocalizer().getString("customerRegistration.error", this));
	}
	return false;
    }

    public String getConfirmPassword() {
	return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
	this.confirmPassword = confirmPassword;
    }

    public List<BalanceAlert> getBalanceAlerts() {
	return balanceAlerts;
    }

    public void setBalanceAlerts(List<BalanceAlert> balanceAlerts) {
	this.balanceAlerts = balanceAlerts;
    }

    public BalanceAlert getBalanceAlert() {
	return balanceAlert;
    }

    public void setBalanceAlert(BalanceAlert balanceAlert) {
	this.balanceAlert = balanceAlert;
    }

    public void setCustomer(CustomerBean customer) {
	this.customer = customer;
    }

    public CustomerBean getCustomer() {
	return customer;
    }
}
