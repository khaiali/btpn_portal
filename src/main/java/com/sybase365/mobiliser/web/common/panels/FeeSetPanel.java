package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import com.sybase365.mobiliser.money.contract.v5_0.system.CreateFeeSetRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateFeeSetResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateIndividualFeeSetRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateIndividualFeeSetResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.SetScaleStepsByFeeTypeAndFeeSetRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.SetScaleStepsByFeeTypeAndFeeSetResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.CurrencyScaleSteps;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.FeeSet;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.FeeType;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.FeeTypeCurrencyScaleSteps;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.ScaleStep;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.beans.ScaleStepConfBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.FeeSetDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.ScaleStepsDataProvider;
import com.sybase365.mobiliser.web.cst.pages.customercare.IndividualFeeSetConfig;
import com.sybase365.mobiliser.web.cst.pages.customercare.StandingDataPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.FeeSetConfigurationPage;
import com.sybase365.mobiliser.web.util.AmountConverter;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class FeeSetPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(FeeSetPanel.class);

    MobiliserBasePage basePage;
    CustomerBean customerBean;
    Long feeSetId;

    boolean isLoadIndividualFeeSets;
    boolean isCreateNewIndiConf;
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private String sStepTotalItemString = null;
    private int sStepStartIndex = 0;
    private int sStepEndIndex = 0;

    // Data Model for table list
    private FeeSetDataProvider dataProvider;
    WebMarkupContainer feeSetTableContainer;
    WebMarkupContainer addFeeSetContainer;

    private ScaleStepsDataProvider sStepDataProvider;
    WebMarkupContainer scaleStepTableContainer;
    WebMarkupContainer addEditScaleStepContainer;

    String currentFeeType = "";
    String currentCurrency = "";
    private List<ScaleStepConfBean> scaleStepsList;
    private ScaleStepConfBean scaleStepBean;

    private List<FeeSet> feeSets;
    private LocalizableLookupDropDownChoice<String> currencyList;
    private KeyValueDropDownChoice<Integer, String> feeTypesLList;
    private FeeSet feeSet;

    private boolean forceReloadFeeSet = true;
    private boolean forceReloadScaleStep = true;

    private int rowIndex = 1;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByFeeSet = "orderByFeeSet";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_name = "name";
    private static final String WICKET_ID_editAction = "editAction";
    private static final String WICKET_ID_removeAction = "removeAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    // For scale step configuration

    private static final String WICKET_ID_sStepNavigator = "sStepNavigator";
    private static final String WICKET_ID_sStepTotalItems = "sStepTotalItems";
    private static final String WICKET_ID_sStepStartIndex = "sStepStartIndex";
    private static final String WICKET_ID_sStepEndIndex = "sStepEndIndex";
    private static final String WICKET_ID_sStepOrderByfeeType = "sStepOrderByfeeType";
    private static final String WICKET_ID_sStepPageable = "sStepPageable";
    private static final String WICKET_ID_sStepFeeType = "sStepFeeType";

    private static final String WICKET_ID_sStepCurrency = "sStepCurrency";
    private static final String WICKET_ID_sStepThresholdAmt = "sStepThresholdAmt";
    private static final String WICKET_ID_sStepMinAmt = "sStepMinAmt";
    private static final String WICKET_ID_sStepMaxAmt = "sStepMaxAmt";
    private static final String WICKET_ID_sStepOnTopAmt = "sStepOnTopAmt";
    private static final String WICKET_ID_sStepPercentage = "sStepPercentage";
    private static final String WICKET_ID_sStepAddAction = "sStepAddAction";
    private static final String WICKET_ID_sStepEditAction = "sStepEditAction";
    private static final String WICKET_ID_sStepRemoveAction = "sStepRemoveAction";
    private static final String WICKET_ID_sStepNoItemsMsg = "sStepNoItemsMsg";

    public FeeSetPanel(String id, MobiliserBasePage basePage,
	    CustomerBean customer) {
	super(id);
	this.basePage = basePage;

	if (PortalUtils.exists(customer)) {

	    this.customerBean = customer;
	    isLoadIndividualFeeSets = true;
	    if (getCustomerBean().getFeeSetId().longValue() == 0
		    || (getCustomerBean().getFeeSetId().longValue() != 0
			    && PortalUtils.exists(getCustomerBean()
				    .getIsIndividualFeeSet()) && !getCustomerBean()
			    .getIsIndividualFeeSet().booleanValue())) {
		isCreateNewIndiConf = Boolean.TRUE;
	    }
	    this.feeSetId = customer.getFeeSetId();
	}

	this.constructPanel();
    }

    Form<?> feeSetConfigForm;

    private void constructPanel() {

	feeSetConfigForm = new Form("feeSetConfigurationForm",
		new CompoundPropertyModel<FeeSetConfigurationPage>(this));

	feeSetTableContainer = new WebMarkupContainer("feeSetTableContainer");
	createFeeSetsDataView(feeSetTableContainer);
	addFeeSetContainer = new WebMarkupContainer("addFeeSetContainer");
	createAddFeeSetContainer(feeSetTableContainer);
	feeSetConfigForm.add(feeSetTableContainer);

	scaleStepTableContainer = new WebMarkupContainer(
		"scaleStepTableContainer");
	createScaleStepDataView(scaleStepTableContainer);
	addEditScaleStepContainer = new WebMarkupContainer(
		"addEditScaleStepContainer");
	createAddEditScaleStepContainer(scaleStepTableContainer);

	if (isLoadIndividualFeeSets) {
	    scaleStepTableContainer.setVisible(true);
	    feeSetTableContainer.setVisible(false);
	    if (isCreateNewIndiConf) {
		addEditScaleStepContainer.setVisible(true);
	    } else {
		addEditScaleStepContainer.setVisible(false);
	    }
	} else {
	    addEditScaleStepContainer.setVisible(false);
	    scaleStepTableContainer.setVisible(false);
	}
	feeSetConfigForm.add(scaleStepTableContainer);

	feeSetConfigForm.add(new FeedbackPanel("errorMessages"));
	add(feeSetConfigForm);
    }

    private void createFeeSetsDataView(WebMarkupContainer parent) {

	dataProvider = new FeeSetDataProvider(WICKET_ID_name, this.basePage);

	feeSets = new ArrayList<FeeSet>();

	parent.addOrReplace(new Button("addFeeSet") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		scaleStepTableContainer.setVisible(false);
		setFeeSet(new FeeSet());
		addFeeSetContainer.setVisible(true);
		feeSetTableContainer.addOrReplace(addFeeSetContainer);

	    };
	}.setDefaultFormProcessing(false));

	final DataView<FeeSet> dataView = new DataView<FeeSet>(
		WICKET_ID_pageable, dataProvider) {

	    /**
		     *
		     */
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {

		    dataProvider.loadFeeSetsList(forceReloadFeeSet, feeSetId,
			    new Boolean(isLoadIndividualFeeSets));
		    forceReloadFeeSet = false;
		    refreshTotalItemCount();
		    if (dataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }

		} catch (DataProviderLoadException dple) {
		    LOG.error("# An error occurred while loading fee sets",
			    dple);
		    error(getLocalizer().getString("feeSets.load.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<FeeSet> item) {

		final FeeSet entry = (FeeSet) item.getModelObject();
		feeSets.add(entry);

		item.add(new Label(WICKET_ID_name, entry.getName()));

		// Edit Action
		Link<FeeSet> editLink = new Link<FeeSet>(WICKET_ID_editAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			forceReloadScaleStep = true;
			setFeeSet(entry);
			setFeeSetId((entry.getId()));
			scaleStepTableContainer.setVisible(true);
			currentFeeType = "";
			currentCurrency = "";
			createScaleStepDataView(scaleStepTableContainer);
		    }
		};
		item.add(editLink);

		Link removeLink = new Link<FeeSet>(WICKET_ID_removeAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			removeFeeSet((FeeSet) item.getModelObject());
			scaleStepTableContainer.setVisible(false);
		    }
		};

		removeLink
			.add(new SimpleAttributeModifier("onclick",
				"return confirm('"
					+ getLocalizer().getString(
						"feeSet.remove.confirm", this)
					+ "');"));

		item.add(removeLink);

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
	parent.addOrReplace(dataView);

	parent.addOrReplace(new OrderByBorder(WICKET_ID_orderByFeeSet,
		WICKET_ID_name, dataProvider) {
	    /**
		     *
		     */
	    private static final long serialVersionUID = 1L;

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

	parent.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("feeSetsList.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString("feeSetsList.addFeeSetHelp",
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
	parent.addOrReplace(new CustomPagingNavigator(WICKET_ID_navigator,
		dataView));

	parent.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	parent.addOrReplace(new Label(WICKET_ID_startIndex, new PropertyModel(
		this, "startIndex")));

	parent.addOrReplace(new Label(WICKET_ID_endIndex, new PropertyModel(
		this, "endIndex")));

    }

    public void createScaleStepDataView(final WebMarkupContainer parent) {
	sStepDataProvider = new ScaleStepsDataProvider(WICKET_ID_sStepFeeType,
		this.basePage);

	scaleStepsList = new ArrayList<ScaleStepConfBean>();

	parent.addOrReplace(new Button("addFeeType") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		feeSetConfigForm.clearInput();
		setScaleStepBean(new ScaleStepConfBean());
		addEditScaleStepContainer.setVisible(true);

		feeTypesLList.setEnabled(true);
		feeTypesLList.add(new SimpleAttributeModifier("style",
			"background-color: #FFFFFF;"));
		currencyList.setEnabled(true);
		currencyList.add(new SimpleAttributeModifier("style",
			"background-color: #FFFFFF;"));
	    };
	}.setDefaultFormProcessing(false));

	final DataView<ScaleStepConfBean> dataView = new DataView<ScaleStepConfBean>(
		WICKET_ID_sStepPageable, sStepDataProvider) {

	    /**
		     *
		     */
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    currentFeeType = "";
		    sStepDataProvider.loadScaleStepsList(getFeeSetId(),
			    forceReloadScaleStep);
		    forceReloadScaleStep = false;
		    refreshTotalItemCount();
		    if (sStepDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }

		} catch (DataProviderLoadException dple) {
		    LOG.error("# An error occurred while loading scale steps",
			    dple);
		    error(getLocalizer().getString("scaleSteps.load.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<ScaleStepConfBean> item) {

		final ScaleStepConfBean entry = item.getModelObject();

		scaleStepsList.add(entry);

		item.add(new Label(WICKET_ID_sStepFeeType, currentFeeType
			.equals(entry.getFeeTypeName()) ? "" : entry
			.getFeeTypeName()));

		item.add(new Label(WICKET_ID_sStepCurrency, (entry
			.getCurrency() == null || currentCurrency.equals(entry
			.getCurrency())) ? "" : entry.getCurrency()));

		item.add(new Label(WICKET_ID_sStepThresholdAmt, entry
			.getThresholdAmount() == null ? "" : basePage
			.convertAmountToString(entry.getThresholdAmount()
				.longValue())));

		item.add(new Label(WICKET_ID_sStepMinAmt,
			entry.getMinAmount() == null ? "" : basePage
				.convertAmountToString(entry.getMinAmount()
					.longValue())));

		item.add(new Label(WICKET_ID_sStepMaxAmt,
			entry.getMaxAmount() == null ? "" : basePage
				.convertAmountToString((entry.getMaxAmount()
					.longValue()))));

		item.add(new Label(WICKET_ID_sStepOnTopAmt,
			entry.getOnTop() == null ? "" : basePage
				.convertAmountToString((entry.getOnTop()
					.longValue()))));

		item.add(new Label(WICKET_ID_sStepPercentage, entry
			.getPercentage() == null ? "" : String.valueOf(entry
			.getPercentage())));

		// Add Action
		Link<ScaleStepConfBean> addLink = new Link<ScaleStepConfBean>(
			WICKET_ID_sStepAddAction, item.getModel()) {
		    @Override
		    public void onClick() {
			feeSetConfigForm.clearInput();
			ScaleStepConfBean entry = (ScaleStepConfBean) getModelObject();
			if (entry.getCssStyle()
				.equals(Constants.CSS_STYLE_EVEN)
				|| entry.getCssStyle().equals(
					Constants.CSS_STYLE_ODD))
			    editScaleStep(entry, Boolean.TRUE);
			else
			    editScaleStep(entry, Boolean.FALSE);
		    }
		};

		item.add(addLink);

		// Edit Action
		Link<ScaleStepConfBean> editLink = new Link<ScaleStepConfBean>(
			WICKET_ID_sStepEditAction, item.getModel()) {
		    @Override
		    public void onClick() {
			feeSetConfigForm.clearInput();
			ScaleStepConfBean entry = new ScaleStepConfBean();
			entry = getModelObject();
			editScaleStep(entry, Boolean.TRUE);
		    }
		};

		item.add(editLink);

		Link removeLink = new Link<ScaleStepConfBean>(
			WICKET_ID_sStepRemoveAction, item.getModel()) {
		    @Override
		    public void onClick() {
			ScaleStepConfBean entry = (ScaleStepConfBean) getModelObject();
			removeScaleStep(entry);
		    }
		};

		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"scaleStep.remove.confirm", this)
				+ "');"));

		item.add(removeLink);

		if (entry.getCssStyle().equals(Constants.CSS_STYLE_FEE_TYPE)
			|| entry.getCssStyle().equals(Constants.CSS_STYLE_ODD)) {
		    addLink.setVisible(true);
		    editLink.setVisible(false);
		    removeLink.setVisible(false);
		} else {
		    addLink.setVisible(false);
		    editLink.setVisible(true);
		    removeLink.setVisible(true);
		}

		// set items in even/odd rows to different css style classes
		item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
			true, new AbstractReadOnlyModel<String>() {
			    @Override
			    public String getObject() {
				return (entry.getCssStyle());
			    }
			}));

		currentFeeType = entry.getFeeTypeName();
		if (entry.getCurrency() != null)
		    currentCurrency = entry.getCurrency();
		else
		    currentCurrency = "";
	    }

	    private void refreshTotalItemCount() {
		sStepTotalItemString = new Integer(sStepDataProvider.size())
			.toString();
		int total = getItemCount();
		if (total > 0) {
		    sStepStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    sStepEndIndex = sStepStartIndex + getItemsPerPage() - 1;
		    if (sStepEndIndex > total)
			sStepEndIndex = total;
		} else {
		    sStepStartIndex = 0;
		    sStepEndIndex = 0;
		}
	    }

	};

	dataView.setItemsPerPage(10);
	parent.addOrReplace(dataView);

	parent.addOrReplace(new MultiLineLabel(WICKET_ID_sStepNoItemsMsg,
		getLocalizer().getString("scaleStepsList.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString(
				"scaleStepsList.addScaleStepHelp", this)) {
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
	parent.addOrReplace(new CustomPagingNavigator(WICKET_ID_sStepNavigator,
		dataView));

	parent.addOrReplace(new Label(WICKET_ID_sStepTotalItems,
		new PropertyModel<String>(this, "sStepTotalItemString")));

	parent.addOrReplace(new Label(WICKET_ID_sStepStartIndex,
		new PropertyModel(this, "sStepStartIndex")));

	parent.addOrReplace(new Label(WICKET_ID_sStepEndIndex,
		new PropertyModel(this, "sStepEndIndex")));

    }

    private void createAddFeeSetContainer(WebMarkupContainer parent) {

	addFeeSetContainer.setOutputMarkupId(true);
	addFeeSetContainer.setOutputMarkupPlaceholderTag(true);

	addFeeSetContainer.add(new RequiredTextField<String>("feeSet.name")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	addFeeSetContainer.add(new Button("save") {
	    @Override
	    public void onSubmit() {
		addFeeSetContainer.setVisible(false);
		createFeeSet();

	    }
	});
	addFeeSetContainer.add(new AjaxLink("cancel") {

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		addFeeSetContainer.setVisible(false);
		target.addComponent(addFeeSetContainer);

	    }
	});

	add(addFeeSetContainer.setVisible(false));

	parent.addOrReplace(addFeeSetContainer);

    }

    private void createAddEditScaleStepContainer(WebMarkupContainer parent) {

	addEditScaleStepContainer.setOutputMarkupId(true);
	addEditScaleStepContainer.setOutputMarkupPlaceholderTag(true);

	currencyList = (LocalizableLookupDropDownChoice<String>) new LocalizableLookupDropDownChoice<String>(
		"scaleStepBean.currency", String.class, "currencies", this,
		true, true).setNullValid(true).add(new ErrorIndicator());

	currencyList.setRequired(true);
	addEditScaleStepContainer.add(currencyList);

	feeTypesLList = (KeyValueDropDownChoice<Integer, String>) new KeyValueDropDownChoice<Integer, String>(
		"scaleStepBean.feeTypeId", basePage.getFeeTypesList())
		.setNullValid(true).add(new ErrorIndicator());
	feeTypesLList.setRequired(true);
	addEditScaleStepContainer.add(feeTypesLList);

	addEditScaleStepContainer.add(new RequiredTextField<Long>(
		"scaleStepBean.thresholdAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage, "scaleSteps.thresholdAmt", Constants.REGEX_AMOUNT_16_2_NEG, false);
	    }
	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	addEditScaleStepContainer.add(new RequiredTextField<Long>(
		"scaleStepBean.minAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage, "scaleSteps.min", Constants.REGEX_AMOUNT_16_2_NEG, false);
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	addEditScaleStepContainer.add(new TextField<Long>(
		"scaleStepBean.maxAmount") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage, "scaleSteps.max", Constants.REGEX_AMOUNT_16_2_NEG, false);
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	addEditScaleStepContainer.add(new RequiredTextField<Long>(
		"scaleStepBean.onTop") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage, "scaleSteps.onTop", Constants.REGEX_AMOUNT_16_2_NEG, false);
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	addEditScaleStepContainer.add(new RequiredTextField<Float>(
		"scaleStepBean.percentage").add(new ErrorIndicator()));

	addEditScaleStepContainer.add(new Button("save") {
	    @Override
	    public void onSubmit() {
		if (basePage.getMobiliserWebSession().getFeedbackMessages()
			.size() > 0) {
		    return;
		}
		saveScaleStep();
		// addEditScaleStepContainer.setVisible(false);
	    }
	});
	addEditScaleStepContainer.add(new AjaxLink("cancel") {

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		addEditScaleStepContainer.setVisible(false);
		target.addComponent(addEditScaleStepContainer);

	    }
	});

	addEditScaleStepContainer.setVisible(false);
	parent.addOrReplace(addEditScaleStepContainer);

    }

    private void editScaleStep(ScaleStepConfBean scaleStep,
	    boolean disableCurrency) {
	setScaleStepBean(scaleStep);
	addEditScaleStepContainer.setVisible(true);

	if (disableCurrency) {
	    currencyList.setEnabled(Boolean.FALSE);
	    currencyList.add(new SimpleAttributeModifier("style",
		    "background-color: #E6E6E6;"));
	    feeTypesLList.setEnabled(Boolean.FALSE);
	    feeTypesLList.add(new SimpleAttributeModifier("style",
		    "background-color: #E6E6E6;"));

	} else {
	    feeTypesLList.setEnabled(false);
	    feeTypesLList.add(new SimpleAttributeModifier("style",
		    "background-color: #E6E6E6;"));
	    currencyList.setEnabled(true);
	    currencyList.add(new SimpleAttributeModifier("style",
		    "background-color: #FFFFFF;"));

	}

    }

    private void removeScaleStep(ScaleStepConfBean scaleStep) {

	try {
	    saveScaleStepConf(scaleStep, getFeeSetId(), Boolean.TRUE);
	    forceReloadScaleStep = true;
	    createScaleStepDataView(scaleStepTableContainer);
	} catch (Exception e) {
	    LOG.error("# An error occurred while deleting the scale step", e);
	    error(getLocalizer().getString("scaleSteps.remove.error", this));
	}

    }

    private void createFeeSet() {
	try {
	    createFeeSet(getFeeSet());
	    forceReloadFeeSet = true;
	    if (!isCreateNewIndiConf)
		setFeeSetId(null);
	    createFeeSetsDataView(feeSetTableContainer);
	} catch (Exception e) {
	    error(getLocalizer().getString("create.feeset.error", this));
	    LOG.error("# An error occurred while creating a new fee set");
	}
    }

    private void removeFeeSet(FeeSet feeSet) {
	try {
	    basePage.removeFeeSet(feeSet);
	    if (!feeSet.isIndividual()) {
		forceReloadFeeSet = true;
		setFeeSetId(null);
		createFeeSetsDataView(feeSetTableContainer);
	    } else {
		getCustomerBean().setFeeSetId(new Long(0));
		getCustomerBean().setOriginalFeeSetId((new Long(0)));
		getCustomerBean().setIsIndividualFeeSet(Boolean.FALSE);
		basePage.getMobiliserWebSession()
			.setCustomer(getCustomerBean());
		setResponsePage(new StandingDataPage(getCustomerBean()));
	    }
	} catch (Exception e) {
	    error(getLocalizer().getString("feeSets.remove.error", this));
	    LOG.error("# An error occurred while deleting the fee set");
	}
    }

    private void saveScaleStep() {
	boolean isNewIndiConfig = isCreateNewIndiConf;

	if (isNewIndiConfig) {
	    saveIndividualFeeSet(getCustomerBean().getId());
	}
	try {
	    addEditScaleStepContainer.setVisible(false);

	    saveScaleStepConf(getScaleStepBean(), getFeeSetId(), Boolean.FALSE);
	    forceReloadScaleStep = true;
	    createScaleStepDataView(scaleStepTableContainer);

	    if (isNewIndiConfig) {
		setResponsePage(new IndividualFeeSetConfig(getCustomerBean()));
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred while saving scale step");
	    error(getLocalizer().getString("scaleSteps.add.error", this));
	}

    }

    private void saveIndividualFeeSet(Long customerId) {
	FeeSet feeSet = null;
	try {
	    feeSet = createIndividualFeeSet(customerId);
	    getCustomerBean().setFeeSetId(feeSet.getId());
	    getCustomerBean().setOriginalFeeSetId(feeSet.getId());
	    getCustomerBean().setIsIndividualFeeSet(Boolean.TRUE);
	    basePage.getMobiliserWebSession().setCustomer(getCustomerBean());
	    setFeeSet(feeSet);
	    setFeeSetId(feeSet.getId());

	} catch (Exception e) {
	    LOG.error("# Error occurred while creating individual fee set");
	    error(getLocalizer().getString("create.individual.feeset.error",
		    this));
	}
    }

    private FeeSet createIndividualFeeSet(Long customerId) throws Exception {
	LOG.debug("# MobiliserBasePage.createIndividualFeeSet()");
	CreateIndividualFeeSetResponse response;
	CreateIndividualFeeSetRequest request = basePage
		.getNewMobiliserRequest(CreateIndividualFeeSetRequest.class);
	request.setCustomerId(customerId);
	response = basePage.wsFeeConfClient.createIndividualFeeSet(request);

	if (!basePage.evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error occurred while creating individual fee set");
	    return null;
	}

	FeeSet feeSet = new FeeSet();
	feeSet.setIndividual(Boolean.TRUE);
	feeSet.setId(response.getFeeSetId());
	return feeSet;
    }

    private void saveScaleStepConf(ScaleStepConfBean scaleStepBean,
	    Long feeSetId, boolean isDelete) throws Exception {
	SetScaleStepsByFeeTypeAndFeeSetRequest request = basePage
		.getNewMobiliserRequest(SetScaleStepsByFeeTypeAndFeeSetRequest.class);
	SetScaleStepsByFeeTypeAndFeeSetResponse response;
	request.setFeeSetId(feeSetId);

	CurrencyScaleSteps currencyScaleStep = null;
	ScaleStep scaleStep = null;
	if (scaleStepBean.getFeeTypeId() == null) {

	    List<FeeType> feeTypes = basePage.getFeeTypes();
	    for (FeeType feeType : feeTypes) {
		if (feeType.getName().equals(scaleStepBean.getFeeTypeName())) {
		    request.setFeeTypeId(feeType.getId());
		}
	    }
	    currencyScaleStep = new CurrencyScaleSteps();
	    scaleStep = new ScaleStep();
	    currencyScaleStep.setCurrency(scaleStepBean.getCurrency());
	    scaleStep.setMaximumAmount(scaleStepBean.getMaxAmount());
	    scaleStep
		    .setMinimumAmount(scaleStepBean.getMinAmount().longValue());
	    scaleStep.setOnTopAmount(scaleStepBean.getOnTop().longValue());
	    scaleStep.setPercentage(scaleStepBean.getPercentage());
	    scaleStep.setThresholdAmount(scaleStepBean.getThresholdAmount()
		    .longValue());
	    currencyScaleStep.getScaleSteps().add(scaleStep);

	} else {
	    request.setFeeTypeId(scaleStepBean.getFeeTypeId());

	    List<FeeTypeCurrencyScaleSteps> feeTypeCurScaleSteps = basePage
		    .getScaleSteps(feeSetId, scaleStepBean.getFeeTypeId());
	    if (PortalUtils.exists(feeTypeCurScaleSteps)) {
		currencyScaleStep = getCurrencyScaleStep(feeTypeCurScaleSteps,
			scaleStepBean.getCurrency());
	    } else {
		currencyScaleStep = new CurrencyScaleSteps();
	    }

	    // Adding new currency
	    if (!PortalUtils.exists(currencyScaleStep.getScaleSteps())) {
		currencyScaleStep.setCurrency(scaleStepBean.getCurrency());
		scaleStep = new ScaleStep();
		scaleStep.setMaximumAmount(scaleStepBean.getMaxAmount());
		scaleStep.setMinimumAmount(scaleStepBean.getMinAmount()
			.longValue());
		scaleStep.setOnTopAmount(scaleStepBean.getOnTop().longValue());
		scaleStep.setPercentage(scaleStepBean.getPercentage());
		scaleStep.setThresholdAmount(scaleStepBean.getThresholdAmount()
			.longValue());
		currencyScaleStep.getScaleSteps().add(scaleStep);
	    } else {

		// add new scale step to existing currency
		if (!PortalUtils.exists(scaleStepBean.getScalePeriodId())) {
		    scaleStep = new ScaleStep();
		    scaleStep.setMaximumAmount(scaleStepBean.getMaxAmount());
		    scaleStep.setMinimumAmount(scaleStepBean.getMinAmount()
			    .longValue());
		    scaleStep.setOnTopAmount(scaleStepBean.getOnTop()
			    .longValue());
		    scaleStep.setPercentage(scaleStepBean.getPercentage());
		    scaleStep.setThresholdAmount(scaleStepBean
			    .getThresholdAmount().longValue());
		    currencyScaleStep.getScaleSteps().add(scaleStep);

		} else {// editing the existing scale step
		    for (ScaleStep sStep : currencyScaleStep.getScaleSteps()) {

			if (sStep.getScalePeriodId().equals(
				scaleStepBean.getScalePeriodId())) {

			    scaleStep = sStep;

			    if (isDelete) {
				currencyScaleStep.getScaleSteps().remove(
					scaleStep);
				break;
			    }

			    scaleStep.setMaximumAmount(scaleStepBean
				    .getMaxAmount());
			    scaleStep.setMinimumAmount(scaleStepBean
				    .getMinAmount().longValue());
			    scaleStep.setOnTopAmount(scaleStepBean.getOnTop()
				    .longValue());
			    scaleStep.setPercentage(scaleStepBean
				    .getPercentage());
			    scaleStep.setThresholdAmount(scaleStepBean
				    .getThresholdAmount().longValue());
			    break;
			}
		    }

		}
	    }

	}
	// The following for loop is mandatory
	for (ScaleStep temp : currencyScaleStep.getScaleSteps()) {
	    temp.setScalePeriodId(null);
	}

	request.setCurrencyScaleStep(currencyScaleStep);
	response = basePage.wsFeeConfClient
		.setScaleStepsByFeeTypeAndFeeSet(request);
	if (!basePage.evaluateMobiliserResponse(response)) {
	    LOG.warn("#An error occurred while saving scale step");
	    return;
	}

    }

    private CurrencyScaleSteps getCurrencyScaleStep(
	    List<FeeTypeCurrencyScaleSteps> feeTypeCurScaleSteps,
	    String currency) {

	// There will be always only one FeeTypeCurrencyScaleSteps in the list

	List<CurrencyScaleSteps> curScaleSteps = feeTypeCurScaleSteps.get(0)
		.getCurrenciesScaleSteps();
	for (CurrencyScaleSteps currScaleStep : curScaleSteps) {
	    if (currScaleStep.getCurrency().equals(currency)) {
		return currScaleStep;
	    }
	}

	return new CurrencyScaleSteps();

    }

    private Long createFeeSet(FeeSet feeset) throws Exception {
	LOG.debug("# MobiliserBasePage.createFeeSet()");
	CreateFeeSetResponse response;
	CreateFeeSetRequest request = basePage
		.getNewMobiliserRequest(CreateFeeSetRequest.class);
	request.setFeeSet(feeset);
	response = basePage.wsFeeConfClient.createFeeSet(request);
	if (!basePage.evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error occurred while creating a new fee set");
	    return new Long(-1);
	}
	return response.getFeeSetId();
    }

    public ScaleStepConfBean getScaleStepBean() {
	return scaleStepBean;
    }

    public void setScaleStepBean(ScaleStepConfBean scaleStepBean) {
	this.scaleStepBean = scaleStepBean;
    }

    public FeeSet getFeeSet() {
	return feeSet;
    }

    public void setFeeSet(FeeSet feeSet) {
	this.feeSet = feeSet;
    }

    public Long getFeeSetId() {
	return feeSetId;
    }

    public void setFeeSetId(Long feeSetId) {
	this.feeSetId = feeSetId;
    }

    public CustomerBean getCustomerBean() {
	return customerBean;
    }

    public void setCustomerBean(CustomerBean customerBean) {
	this.customerBean = customerBean;
    }

}
