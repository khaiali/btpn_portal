package com.sybase365.mobiliser.web.cst.pages.systemconfig;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.system.CreateRestrictionRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateRestrictionResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateRestrictionRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateRestrictionResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Restriction;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.RestrictionRule;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.RiskCategory;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.RestrictionSetBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownMultiChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.RestrictionRulesDataProvider;
import com.sybase365.mobiliser.web.util.AmountConverter;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class RestrictionPage extends BaseSystemConfigurationPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(RestrictionPage.class);

    private KeyValueDropDownChoice<Boolean, String> payeeFeeOptions;
    private KeyValueDropDownChoice<Integer, String> restGrpList;
    private List<String> selectedOrgUnitsListFor;
    private List<Integer> selectedRiskCatListFor;
    private List<String> selectedOrgUnitsListOn;
    private List<RiskCategory> selectedRiskCatListOn;
    private List<Integer> selectedUseCaseListOn;

    private MobiliserBasePage basePage;
    private Restriction restriction;
    private List<RestrictionRule> restRulesList;
    private RestrictionRule rule;
    private String timeFrame;
    private static DatatypeFactory df;
    private boolean isEditMode;

    private RestrictionRulesDataProvider dataProvider;
    private WebMarkupContainer addRestRuleContainer;
    private LocalizableLookupDropDownChoice<String> currencyList;
    private TextField<Long> txtMinTxnAmt;
    private TextField<Long> TxtMaxTxnAmt;
    private TextField<String> txtTimeFrame;
    private TextField<Long> txtMaxTxnSum;
    private TextField<Long> txtMaxTxn;

    private LocalizableLookupDropDownMultiChoice<String> orgUnitsListFor;
    private LocalizableLookupDropDownMultiChoice<Integer> riskCatListFor;

    private LocalizableLookupDropDownMultiChoice<String> orgUnitsListOn;
    private LocalizableLookupDropDownMultiChoice<Integer> riskCatListOn;
    private LocalizableLookupDropDownMultiChoice<Integer> useCasesListOn;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private int rowIndex = 1;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByCurrency = "orderByCurrency";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_selected = "selected";
    private static final String WICKET_ID_minTxnAmount = "restriction.rules.minTransactionAmount";
    private static final String WICKET_ID_maxTxnAmount = "restriction.rules.maxTransactionAmount";
    private static final String WICKET_ID_timeframe = "restriction.rules.timeframe";
    private static final String WICKET_ID_maxTxns = "restriction.rules.maxTransactions";
    private static final String WICKET_ID_maxTxnAmtSum = "restriction.rules.maxTransactionAmountSum";
    private static final String WICKET_ID_currency = "restriction.rules.currency";
    private static final String WICKET_ID_editAction = "editAction";
    private static final String WICKET_ID_addAction = "addAction";
    private static final String WICKET_ID_removeAction = "removeAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    static {
	try {
	    df = DatatypeFactory.newInstance();
	} catch (Exception e) {
	    if (LOG.isErrorEnabled())
		LOG.error("# Datatype factory instantiation failed!", e);
	}
    }

    public RestrictionPage(int restGrpId, Restriction restriction) {
	super();
	basePage = this;
	if (PortalUtils.exists(restriction)) {
	    this.restriction = restriction;
	    isEditMode = Boolean.TRUE;
	    preInit();
	} else
	    this.restriction = new Restriction();

	getRestriction().setRestrictionGroupId(restGrpId);
	initPageComponents();
    }

    @SuppressWarnings({ "unchecked", "serial" })
    private void initPageComponents() {

	Form<?> form = new Form("restrictionForm",
		new CompoundPropertyModel<RestrictionSetsPage>(this));

	form.add(new RequiredTextField<String>("restriction.name")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	restGrpList = (KeyValueDropDownChoice<Integer, String>) new KeyValueDropDownChoice<Integer, String>(
		"restriction.restrictionGroupId", getRestrictionGrpList())
		.setNullValid(true);

	if (!PortalUtils.exists(getRestriction().getRestrictionID())) {
	    restGrpList.setEnabled(Boolean.FALSE);
	    restGrpList.add(new SimpleAttributeModifier("style",
		    "background-color: #E6E6E6;"));
	}

	form.add(restGrpList);

	RadioGroup rg = new RadioGroup("radioGroup", new PropertyModel(this,
		"restriction.isPayer"));

	Radio filteTypeMonth = new Radio("Payer", new Model(true));
	rg.add(filteTypeMonth);

	Radio filteTypeDate = new Radio("Payee", new Model(false));
	rg.add(filteTypeDate);

	form.add(rg);

	form.add(new RequiredTextField<Long>("restriction.priority").add(
		Constants.idLongAttributeModifier).add(new ErrorIndicator()));

	// applied for

	orgUnitsListFor = (LocalizableLookupDropDownMultiChoice<String>) new LocalizableLookupDropDownMultiChoice<String>(
		"restriction.appliedFor.orgUnits.orgUnit", String.class,
		"orgunits", this, false, true).add(new ErrorIndicator());

	orgUnitsListFor.add(new AjaxFormComponentUpdatingBehavior("onchange") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		if (orgUnitsListFor.getModelObject().contains(
			Constants.DROP_DOWN_ALL_OPTION)) {
		    orgUnitsListFor.getModelObject().clear();
		    orgUnitsListFor.getModelObject().add(
			    Constants.DROP_DOWN_ALL_OPTION);
		    target.addComponent(orgUnitsListFor);
		}

	    }
	});
	orgUnitsListFor.setMaxRows(5);
	form.add(orgUnitsListFor);

	riskCatListFor = (LocalizableLookupDropDownMultiChoice<Integer>) new LocalizableLookupDropDownMultiChoice<Integer>(
		"restriction.appliedFor.riskCategories.riskCategory",
		Integer.class, "riskcategories", this, false, true)
		.add(new ErrorIndicator());

	riskCatListFor.add(new AjaxFormComponentUpdatingBehavior("onchange") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		if (riskCatListFor.getModelObject().contains(
			Constants.DROP_DOWN_ALL_OPTION_INT)) {
		    riskCatListFor.getModelObject().clear();
		    riskCatListFor.getModelObject().add(
			    Constants.DROP_DOWN_ALL_OPTION_INT);
		    target.addComponent(riskCatListFor);
		}

	    }
	});
	riskCatListFor.setMaxRows(5);
	form.add(riskCatListFor);

	// applied on
	List<KeyValue<String, String>> orgUnitsappliedOnList = getOrgUnits();
	orgUnitsListOn = (LocalizableLookupDropDownMultiChoice<String>) new LocalizableLookupDropDownMultiChoice<String>(
		"restriction.appliedOn.orgUnits.orgUnit", String.class,
		"orgunits", this, false, true).add(new ErrorIndicator());

	orgUnitsListOn.add(new AjaxFormComponentUpdatingBehavior("onchange") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		if (orgUnitsListOn.getModelObject().contains(
			Constants.DROP_DOWN_ALL_OPTION)) {
		    orgUnitsListOn.getModelObject().clear();
		    orgUnitsListOn.getModelObject().add(
			    Constants.DROP_DOWN_ALL_OPTION);
		    target.addComponent(orgUnitsListOn);
		}

	    }
	});
	orgUnitsListOn.setMaxRows(5);
	form.add(orgUnitsListOn);

	List<KeyValue<Integer, String>> riskCategoryAppliedOnList = getRiskCategories();

	riskCatListOn = (LocalizableLookupDropDownMultiChoice<Integer>) new LocalizableLookupDropDownMultiChoice<Integer>(
		"restriction.appliedOn.riskCategories.riskCategory",
		Integer.class, "riskcategories", this, false, true)
		.add(new ErrorIndicator());

	riskCatListOn.add(new AjaxFormComponentUpdatingBehavior("onchange") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		if (riskCatListOn.getModelObject().contains(
			Constants.DROP_DOWN_ALL_OPTION_INT)) {
		    riskCatListOn.getModelObject().clear();
		    riskCatListOn.getModelObject().add(
			    Constants.DROP_DOWN_ALL_OPTION_INT);
		    target.addComponent(riskCatListOn);
		}

	    }
	});
	riskCatListOn.setMaxRows(5);
	form.add(riskCatListOn);

	useCasesListOn = (LocalizableLookupDropDownMultiChoice<Integer>) new LocalizableLookupDropDownMultiChoice<Integer>(
		"restriction.appliedOn.useCases.useCase", Integer.class,
		"usecases", this, Boolean.FALSE, true)
		.add(new ErrorIndicator());

	useCasesListOn.add(new AjaxFormComponentUpdatingBehavior("onchange") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		if (useCasesListOn.getModelObject().contains(
			Constants.DROP_DOWN_ALL_OPTION_INT)) {
		    useCasesListOn.getModelObject().clear();
		    useCasesListOn.getModelObject().add(
			    Constants.DROP_DOWN_ALL_OPTION_INT);
		    target.addComponent(useCasesListOn);
		}

	    }
	});
	useCasesListOn.setMaxRows(5);
	form.add(useCasesListOn);

	createRestrictionRulesDataView(form);

	createAddRestRuleContainer(form);

	form.add(new Button("save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (getMobiliserWebSession().getFeedbackMessages().size() > 0) {
		    return;
		}

		saveRestriction();
	    }
	});
	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(new RestrictionSetsPage());
	    };
	}.setDefaultFormProcessing(false));

	add(form);

	form.add(new FeedbackPanel("errorMessages"));

    }

    public void createRestrictionRulesDataView(final Form form) {

	dataProvider = new RestrictionRulesDataProvider(WICKET_ID_currency,
		this);

	restRulesList = new ArrayList<RestrictionRule>();

	form.addOrReplace(new Button("addRestrictionRule") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		rule = new RestrictionRule();
		currencyList.clearInput();
		txtMaxTxn.clearInput();
		txtMaxTxnSum.clearInput();
		txtMinTxnAmt.clearInput();
		txtTimeFrame.clearInput();
		TxtMaxTxnAmt.clearInput();
		addRestRuleContainer.setVisible(true);
	    };
	}.setDefaultFormProcessing(false));

	final DataView<RestrictionRule> dataView = new DataView<RestrictionRule>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    // dataProvider.loadRestrictionRulesList(forceReload);
		    dataProvider.loadRestrictionRulesList(getRestriction());
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
			    "# An error occurred while loading restriction groups",
			    dple);
		    error(getLocalizer().getString(
			    "restrictionRules.load.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<RestrictionRule> item) {

		final RestrictionRule entry = item.getModelObject();

		restRulesList.add(entry);

		item.add(new Label(WICKET_ID_currency, entry.getCurrency()));

		item.add(new Label(
			WICKET_ID_minTxnAmount,
			PortalUtils.exists(entry.getMinTransactionAmount()) ? convertAmountToString(entry
				.getMinTransactionAmount().longValue()) : ""));

		item.add(new Label(
			WICKET_ID_maxTxnAmount,
			PortalUtils.exists(entry.getMaxTransactionAmount()) ? convertAmountToString(entry
				.getMaxTransactionAmount().longValue()) : ""));

		item.add(new Label(WICKET_ID_timeframe, PortalUtils
			.exists(entry.getTimeframe()) ? entry.getTimeframe()
			.toString() : ""));

		item.add(new Label(WICKET_ID_maxTxns, PortalUtils.exists(entry
			.getMaxTransactions()) ? entry.getMaxTransactions()
			.toString() : ""));

		item.add(new Label(
			WICKET_ID_maxTxnAmtSum,
			PortalUtils.exists(entry.getMaxTransactionAmountSum()) ? convertAmountToString(entry
				.getMaxTransactionAmountSum().longValue()) : ""));

		// Remove Action
		Link removeRestLink = new Link<RestrictionRule>(
			WICKET_ID_removeAction, item.getModel()) {
		    @Override
		    public void onClick() {
			forceReload = true;
			removeRule(entry);
			createRestrictionRulesDataView(form);
		    }
		};

		removeRestLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"restrictionRule.remove.confirm", this)
				+ "');"));

		item.add(removeRestLink);

		// Edit Action
		Link<RestrictionRule> editLink = new Link<RestrictionRule>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			rule = item.getModelObject();
			timeFrame = PortalUtils.exists(rule.getTimeframe()) ? rule
				.getTimeframe().toString() : "";
			addRestRuleContainer.setVisible(true);
		    }
		};
		item.add(editLink);

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
	form.addOrReplace(dataView);

	form.addOrReplace(new OrderByBorder(WICKET_ID_orderByCurrency,
		WICKET_ID_currency, dataProvider) {
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

	form.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("restrictionRulesList.noItemsMsg",
			this)
			+ "\n"
			+ getLocalizer().getString(
				"restrictionRulesList.addRestrictionRuleHelp",
				this)) {
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
	form.addOrReplace(new CustomPagingNavigator(WICKET_ID_navigator,
		dataView));

	form.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	form.addOrReplace(new Label(WICKET_ID_startIndex, new PropertyModel(
		this, "startIndex")));

	form.addOrReplace(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));

    }

    public Restriction getRestriction() {
	return restriction;
    }

    public void setRestriction(Restriction restriction) {
	this.restriction = restriction;
    }

    private void createAddRestRuleContainer(Form form) {

	addRestRuleContainer = new WebMarkupContainer("addRestRuleContainer");

	addRestRuleContainer.setOutputMarkupId(true);
	addRestRuleContainer.setOutputMarkupPlaceholderTag(true);

	currencyList = (LocalizableLookupDropDownChoice<String>) new LocalizableLookupDropDownChoice<String>(
		"rule.currency", String.class, "currencies", this, true, true)
		.setNullValid(true).setRequired(true).add(new ErrorIndicator());

	addRestRuleContainer.add(currencyList);

	txtMinTxnAmt = new TextField<Long>("rule.minTransactionAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"restrictionRule.minTxnAmt");
	    }

	};
	addRestRuleContainer.add(txtMinTxnAmt.add(
		Constants.amountSimpleAttributeModifier).add(
		new ErrorIndicator()));

	txtTimeFrame = new TextField<String>("timeFrame");
	addRestRuleContainer.add(txtTimeFrame.add(new ErrorIndicator()));

	TxtMaxTxnAmt = new TextField<Long>("rule.maxTransactionAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"restrictionRule.maxTxnAmt");
	    }

	};
	addRestRuleContainer.add(TxtMaxTxnAmt.add(
		Constants.amountSimpleAttributeModifier).add(
		new ErrorIndicator()));

	txtMaxTxn = new TextField<Long>("rule.maxTransactions");
	addRestRuleContainer.add(txtMaxTxn.add(new ErrorIndicator()));

	txtMaxTxnSum = new TextField<Long>("rule.maxTransactionAmountSum") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"restrictionRule.maxTxnSum");
	    }

	};
	addRestRuleContainer.add(txtMaxTxnSum.add(
		Constants.amountSimpleAttributeModifier).add(
		new ErrorIndicator()));

	form.add(addRestRuleContainer.setVisible(false));

	form.addOrReplace(addRestRuleContainer);

    }

    private void saveRestriction() {

	List<String> orgUnitAppFor = getRestriction().getAppliedFor()
		.getOrgUnits().getOrgUnit();
	if (orgUnitAppFor.size() == 1
		&& orgUnitAppFor.get(0).equals(Constants.DROP_DOWN_ALL_OPTION)) {
	    orgUnitAppFor.clear();
	    for (KeyValue orgUnitFor : getOrgUnits()) {
		orgUnitAppFor.add(orgUnitFor.getKey().toString());
	    }
	}

	List<Integer> riskCategoryAppFor = getRestriction().getAppliedFor()
		.getRiskCategories().getRiskCategory();

	if (riskCategoryAppFor.size() == 1
		&& riskCategoryAppFor.get(0).equals(
			Constants.DROP_DOWN_ALL_OPTION_INT)) {
	    riskCategoryAppFor.clear();
	    for (KeyValue<Integer, String> riskCatFor : getRiskCategories()) {
		riskCategoryAppFor.add(riskCatFor.getKey());
	    }
	}

	List<String> orgUnitAppOn = getRestriction().getAppliedOn()
		.getOrgUnits().getOrgUnit();
	if (orgUnitAppOn.size() == 1
		&& orgUnitAppOn.get(0).equals(Constants.DROP_DOWN_ALL_OPTION)) {
	    orgUnitAppOn.clear();
	    for (KeyValue orgUnitOn : getOrgUnits()) {
		orgUnitAppOn.add(orgUnitOn.getKey().toString());
	    }
	}

	List<Integer> riskCategoryAppOn = getRestriction().getAppliedOn()
		.getRiskCategories().getRiskCategory();

	if (riskCategoryAppOn.size() == 1
		&& riskCategoryAppOn.get(0).equals(
			Constants.DROP_DOWN_ALL_OPTION_INT)) {
	    riskCategoryAppOn.clear();
	    for (KeyValue<Integer, String> riskCatOn : getRiskCategories()) {
		riskCategoryAppOn.add(riskCatOn.getKey());
	    }
	}

	List<Integer> usecaseAppOn = getRestriction().getAppliedOn()
		.getUseCases().getUseCase();

	if (usecaseAppOn.size() == 1
		&& usecaseAppOn.get(0).equals(
			Constants.DROP_DOWN_ALL_OPTION_INT)) {
	    usecaseAppOn.clear();
	    for (KeyValue<String, String> usecaseOn : fetchLookupEntries(
		    "usecases", "usecase.lookUp.error")) {
		usecaseAppOn.add(Integer.valueOf(usecaseOn.getKey()));
	    }
	}

	LOG.debug("# MobiliserBasePage.saveRestriction()");
	if (!isEditMode) {
	    createNewRestriction();
	} else {
	    updateRestriction();
	}
    }

    private void updateRestriction() {
	LOG.debug("# MobiliserBasePage.updateRestriction()");
	UpdateRestrictionResponse response;
	try {
	    UpdateRestrictionRequest request = getNewMobiliserRequest(UpdateRestrictionRequest.class);
	    if (PortalUtils.exists(getRule())
		    && PortalUtils.exists(getRule().getCurrency())) {
		RestrictionRule rule = getRule();
		Duration d = null;
		if (PortalUtils.exists(this.timeFrame)) {
		    try {
			d = df.newDuration(this.timeFrame.toUpperCase());
			LOG.debug("# Entered timeframe is valid: " + d);
		    } catch (NumberFormatException ne) {
			LOG.error("# Enter timeframe is not valid");
			error(new StringBuffer()
				.append("'")
				.append(this.timeFrame)
				.append("'")
				.append(getLocalizer().getString(
					"duration.format.error", this))
				.toString());
			return;
		    }

		}

		rule.setTimeframe(d);

		if (PortalUtils.exists(rule.getEntityId())) {
		    getRestriction().getRules().remove(rule);
		}
		getRestriction().getRules().add(rule);
	    }

	    request.setRestriction(getRestriction());
	    response = wsTxnRestrictionClient.updateRestriction(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while updating restriction");
		return;
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while updating restriction", e);
	    error(getLocalizer().getString("update.restriction.error", this));
	    return;

	}
	setResponsePage(new RestrictionSetsPage());
    }

    private void createNewRestriction() {
	LOG.debug("# MobiliserBasePage.createNewRestriction()");
	CreateRestrictionResponse response = null;
	try {
	    CreateRestrictionRequest request = getNewMobiliserRequest(CreateRestrictionRequest.class);
	    if (PortalUtils.exists(getRule())
		    && PortalUtils.exists(getRule().getCurrency())) {
		RestrictionRule rule = getRule();

		if (PortalUtils.exists(this.timeFrame)) {
		    Duration d = null;
		    try {
			d = df.newDuration(this.timeFrame.toUpperCase());
			LOG.debug("# Entered timeframe is valid: " + d);
			rule.setTimeframe(d);
		    } catch (Exception ne) {
			LOG.error("# Enter timeframe is not valid");
			error(new StringBuffer()
				.append("'")
				.append(this.timeFrame)
				.append("'")
				.append(getLocalizer().getString(
					"duration.format.error", this))
				.toString());
			return;
		    }

		}

		getRestriction().getRules().add(rule);
	    }
	    request.setRestriction(getRestriction());
	    response = wsTxnRestrictionClient.createRestriction(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while saving new restriction");
		return;
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while saving new restriction", e);
	    error(getLocalizer().getString("create.restriction.error", this));
	    return;
	}
	setResponsePage(new RestrictionSetsPage());
    }

    public List<KeyValue<Integer, String>> getRestrictionGrpList() {
	LOG.debug("# MobiliserBasePage.getRestrictionGrpList()");
	List<RestrictionSetBean> restGrps = null;
	List<KeyValue<Integer, String>> RESTRICTION_GROUPS = new ArrayList<KeyValue<Integer, String>>();
	try {
	    restGrps = getRestrictionSetBeanList();
	    for (RestrictionSetBean restGrp : restGrps) {
		// if restriction entry not exist, then it is group entry
		if (!PortalUtils.exists(restGrp.getRestriction())) {
		    RESTRICTION_GROUPS.add(new KeyValue<Integer, String>(
			    restGrp.getRestrictionSet().getGroupId(), restGrp
				    .getRestrictionSet().getName()));
		}

	    }
	} catch (DataProviderLoadException e) {
	    LOG.error("# An error occurred while fetching restriction groups");
	}

	return RESTRICTION_GROUPS;
    }

    private void removeRule(RestrictionRule rule) {
	getRestriction().getRules().remove(rule);
    }

    public RestrictionRule getRule() {
	return rule;
    }

    public void setRule(RestrictionRule rule) {
	this.rule = rule;
    }

    public List<String> getSelectedOrgUnitsListFor() {
	return selectedOrgUnitsListFor;
    }

    public void setSelectedOrgUnitsListFor(List<String> selectedOrgUnitsListFor) {
	this.selectedOrgUnitsListFor = selectedOrgUnitsListFor;
    }

    public List<Integer> getSelectedRiskCatListFor() {
	return selectedRiskCatListFor;
    }

    public void setSelectedRiskCatListFor(List<Integer> selectedRiskCatListFor) {
	this.selectedRiskCatListFor = selectedRiskCatListFor;
    }

    public List<String> getSelectedOrgUnitsListOn() {
	return selectedOrgUnitsListOn;
    }

    public void setSelectedOrgUnitsListOn(List<String> selectedOrgUnitsListOn) {
	this.selectedOrgUnitsListOn = selectedOrgUnitsListOn;
    }

    public List<RiskCategory> getSelectedRiskCatListOn() {
	return selectedRiskCatListOn;
    }

    public void setSelectedRiskCatListOn(
	    List<RiskCategory> selectedRiskCatListOn) {
	this.selectedRiskCatListOn = selectedRiskCatListOn;
    }

    public List<Integer> getSelectedUseCaseListOn() {
	return selectedUseCaseListOn;
    }

    public void setSelectedUseCaseListOn(List<Integer> selectedUseCaseListOn) {
	this.selectedUseCaseListOn = selectedUseCaseListOn;
    }

    private void preInit() {
	List<String> orgUnits = getRestriction().getAppliedFor().getOrgUnits()
		.getOrgUnit();

	if (orgUnits.size() == getOrgUnits().size()) {
	    orgUnits.clear();
	    orgUnits.add(Constants.DROP_DOWN_ALL_OPTION);
	}

	List<Integer> riskCategory = getRestriction().getAppliedFor()
		.getRiskCategories().getRiskCategory();
	if (riskCategory.size() == getRiskCategories().size()) {
	    riskCategory.clear();
	    riskCategory.add(Constants.DROP_DOWN_ALL_OPTION_INT);
	}

	List<String> orgUnitsAppOn = getRestriction().getAppliedOn()
		.getOrgUnits().getOrgUnit();
	if (orgUnitsAppOn.size() == getOrgUnits().size()) {
	    orgUnitsAppOn.clear();
	    orgUnitsAppOn.add(Constants.DROP_DOWN_ALL_OPTION);
	}

	List<Integer> riskCategoryAppOn = getRestriction().getAppliedOn()
		.getRiskCategories().getRiskCategory();
	if (riskCategoryAppOn.size() == getRiskCategories().size()) {
	    riskCategoryAppOn.clear();
	    riskCategoryAppOn.add(Constants.DROP_DOWN_ALL_OPTION_INT);
	}

	List<Integer> usecases = getRestriction().getAppliedOn().getUseCases()
		.getUseCase();
	if (usecases.size() == fetchLookupEntries("usecases",
		"usecase.lookUp.error").size()) {
	    usecases.clear();
	    usecases.add(Constants.DROP_DOWN_ALL_OPTION_INT);
	}
    }

}
