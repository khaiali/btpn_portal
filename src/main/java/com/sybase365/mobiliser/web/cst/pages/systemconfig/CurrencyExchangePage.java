package com.sybase365.mobiliser.web.cst.pages.systemconfig;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator.LengthBetweenValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteExchangeRateRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteExchangeRateResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.SetExchangeRateRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.SetExchangeRateResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.ExchangeRate;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.ExchangeRateDataProvider;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CurrencyExchangePage extends BaseSystemConfigurationPage {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CurrencyExchangePage.class);

    private ExchangeRateDataProvider dataProvider;
    private List<ExchangeRate> exchangeRateList;
    private ExchangeRate exchangeRate;
    private String strToAmount;
    private String strFromAmount;
    private String strRate;
    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private int rowIndex = 1;
    private static final String WICKET_ID_removeAction = "removeLink";
    private static final String WICKET_ID_editAction = "editLink";
    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByfromCurrency = "orderByfromCurrency";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_fromCurrency = "fromCurrency";
    private static final String WICKET_ID_toCurrency = "toCurrency";
    private static final String WICKET_ID_ratio = "ratio";
    private static final String WICKET_ID_rate = "rate";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";
    private static final String WICKET_ID_rateidLink = "rateidLink";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new FeedbackPanel("errorMessages"));
	final WebMarkupContainer addExchangerateContainer = new WebMarkupContainer(
		"addExchangerateContainer");
	final WebMarkupContainer updateExchangerateContainer = new WebMarkupContainer(
		"updateExchangerateContainer");
	createExchangeRateDataView(updateExchangerateContainer,
		addExchangerateContainer);
	createAddExchangeRateForm(addExchangerateContainer,
		updateExchangerateContainer);
	createUpdateExchangeRateForm(updateExchangerateContainer,
		addExchangerateContainer);
    }

    private void createAddExchangeRateForm(
	    final WebMarkupContainer addExchangerateContainer,
	    final WebMarkupContainer updateExchangerateContainer) {

	addExchangerateContainer.setOutputMarkupId(true);
	addExchangerateContainer.setOutputMarkupPlaceholderTag(true);
	final Form<?> addExchangeRateForm = new Form("addExchangeRateForm",
		new CompoundPropertyModel<CurrencyExchangePage>(this));
	final FormComponent fromCurrency = new LocalizableLookupDropDownChoice<String>(
		"exchangeRate.fromCurrency", String.class, "currencies", this,
		true, true);

	fromCurrency.add(LengthBetweenValidator.exactLength(3))
		.setRequired(true).add(new ErrorIndicator());
	addExchangeRateForm.add(fromCurrency);
	final FormComponent toCurrency = new LocalizableLookupDropDownChoice<String>(
		"exchangeRate.toCurrency", String.class, "currencies", this,
		true, true);

	toCurrency.add(LengthBetweenValidator.exactLength(3)).setRequired(true)
		.add(new ErrorIndicator());
	addExchangeRateForm.add(toCurrency);
	final TextField fromAmount = new TextField<String>("strFromAmount");
	fromAmount.add(new AmountValidator(this, Constants.REGEX_AMOUNT_18_0))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator());
	addExchangeRateForm.add(fromAmount);
	final FormComponent toAmount = new TextField<String>("strToAmount");
	toAmount.add(new AmountValidator(this, Constants.REGEX_AMOUNT_18_0))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator());
	addExchangeRateForm.add(toAmount);
	final FormComponent rate = new RequiredTextField<String>("strRate");
	rate.add(new AmountValidator(this, Constants.REGEX_AMOUNT_12_6)).add(
		new ErrorIndicator());
	addExchangeRateForm.add(rate);
	final FormComponent[] components = { fromAmount, toAmount, rate,
		fromCurrency, toCurrency };
	addExchangeRateForm.add(new ExchangeRateAmountValidator(components));
	addExchangeRateForm.add(new Button("save") {
	    @Override
	    public void onSubmit() {
		ExchangeRate ec = getUniqueExchangeRate();
		if (PortalUtils.exists(ec)) {
		    error(getLocalizer().getString(
			    "exchangerate.already.exist", this,
			    new Model<ExchangeRate>(ec)));
		    return;
		}
		saveExchnageRate(addExchangerateContainer,
			updateExchangerateContainer);
		addExchangeRateForm.clearInput();
	    }
	});
	addExchangeRateForm.add(new AjaxLink("cancel") {

	    @Override
	    public void onClick(final AjaxRequestTarget target) {
		addExchangerateContainer.setVisible(false);
		updateExchangerateContainer.setVisible(false);
		addExchangeRateForm.clearInput();
		target.addComponent(addExchangerateContainer);

	    }
	});
	addExchangerateContainer.add(addExchangeRateForm);
	add(addExchangerateContainer.setVisible(false));
	add(new AjaxLink("exchangeRateAddLink") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(final AjaxRequestTarget target) {
		addExchangeRateForm.clearInput();
		setExchangeRate(null);
		addExchangerateContainer.setVisible(true);
		updateExchangerateContainer.setVisible(false);
		target.addComponent(addExchangerateContainer);
		target.addComponent(updateExchangerateContainer);
	    }
	}.add(new PrivilegedBehavior(this, Constants.PRIV_FOREX_WRITE)));

    }

    private void createUpdateExchangeRateForm(
	    final WebMarkupContainer updateExchangerateContainer,
	    final WebMarkupContainer addExchangerateContainer) {

	updateExchangerateContainer.setOutputMarkupId(true);
	updateExchangerateContainer.setOutputMarkupPlaceholderTag(true);
	final Form<?> updateExchangeRateForm = new Form(
		"updateExchangeRateForm",
		new CompoundPropertyModel<CurrencyExchangePage>(this));
	FormComponent<String> fromAmount = (FormComponent<String>) new TextField<String>(
		"strFromAmount")
		.add(new AmountValidator(this, Constants.REGEX_AMOUNT_18_0))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator());
	updateExchangeRateForm.addOrReplace(fromAmount);
	FormComponent<String> toAmount = (FormComponent<String>) new TextField<String>(
		"strToAmount")
		.add(new AmountValidator(this, Constants.REGEX_AMOUNT_18_0))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator());
	updateExchangeRateForm.addOrReplace(toAmount);
	FormComponent<String> rate = new RequiredTextField<String>("strRate");
	rate.add(new AmountValidator(this, Constants.REGEX_AMOUNT_12_6)).add(
		new ErrorIndicator());
	updateExchangeRateForm.addOrReplace(rate);
	FormComponent[] components = { fromAmount, toAmount, rate };
	updateExchangeRateForm.add(new ExchangeRateAmountValidator(components));
	updateExchangeRateForm.addOrReplace(new Button("update") {
	    @Override
	    public void onSubmit() {
		ExchangeRate ec = getUniqueExchangeRate();
		if (!PortalUtils.exists(ec)) {
		    error(getLocalizer().getString(
			    "exchangeRate.update.noExist.error", this));
		    return;
		}
		saveExchnageRate(addExchangerateContainer,
			updateExchangerateContainer);
	    }
	});
	updateExchangeRateForm.addOrReplace(new AjaxLink("cancel") {

	    @Override
	    public void onClick(final AjaxRequestTarget target) {
		addExchangerateContainer.setVisible(false);
		updateExchangerateContainer.setVisible(false);
		updateExchangeRateForm.clearInput();
		target.addComponent(updateExchangerateContainer);

	    }
	});
	updateExchangerateContainer.addOrReplace(updateExchangeRateForm);
	updateExchangerateContainer.setVisible(false);
	addOrReplace(updateExchangerateContainer);

    }

    private void createExchangeRateDataView(
	    final WebMarkupContainer updateExchangerateContainer,
	    final WebMarkupContainer addExchangerateContainer) {
	dataProvider = new ExchangeRateDataProvider(WICKET_ID_fromCurrency,
		this);
	exchangeRateList = new ArrayList<ExchangeRate>();

	final DataView<ExchangeRate> dataView = new DataView<ExchangeRate>(
		WICKET_ID_pageable, dataProvider) {
	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadExchangeRates(forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading exchange rates",
			    dple);
		    error(getLocalizer().getString("exchangerate.load.error",
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

	    @Override
	    protected void populateItem(final Item<ExchangeRate> item) {
		final ExchangeRate entry = item.getModelObject();
		exchangeRateList.add(entry);
		item.add(new Label(WICKET_ID_fromCurrency, entry
			.getFromCurrency()));
		item.add(new Label(WICKET_ID_toCurrency, entry.getToCurrency()));
		item.add(new Label(WICKET_ID_ratio, entry.getFromAmount() + ":"
			+ entry.getToAmount()));
		NumberFormat format = NumberFormat
			.getInstance(getMobiliserWebSession().getLocale());
		item.add(new Label(WICKET_ID_rate, format.format(entry
			.getRate())));
		Link<ExchangeRate> editLink = new Link<ExchangeRate>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			ExchangeRate entry = (ExchangeRate) getModelObject();
			setExchangeRate(entry);
			setStrToAmount(String.valueOf(entry.getToAmount()));
			setStrFromAmount(String.valueOf(entry.getFromAmount()));
			NumberFormat format = NumberFormat
				.getInstance(getMobiliserWebSession()
					.getLocale());
			setStrRate(format.format(entry.getRate()));
			updateExchangerateContainer.setVisible(true);
			addExchangerateContainer.setVisible(false);
		    }
		};
		PrivilegedBehavior disableEdit = new PrivilegedBehavior(
			this.getWebPage(), Constants.PRIV_FOREX_WRITE);
		disableEdit.setMissingPrivilegeHidesComponent(false);
		editLink.add(disableEdit);
		item.add(editLink);

		Link<ExchangeRate> removeLink = new Link<ExchangeRate>(
			WICKET_ID_removeAction, item.getModel()) {
		    @Override
		    public void onClick() {
			ExchangeRate entry = (ExchangeRate) getModelObject();
			deleteExchangeRate(entry, addExchangerateContainer,
				updateExchangerateContainer);
		    }
		};
		PrivilegedBehavior disableRemove = new PrivilegedBehavior(
			this.getWebPage(), Constants.PRIV_FOREX_WRITE);
		disableRemove.setMissingPrivilegeHidesComponent(false);
		removeLink.add(disableRemove);
		if (removeLink.isEnabled()) {
		    removeLink
			    .add(new SimpleAttributeModifier(
				    "onclick",
				    "return confirm('"
					    + getLocalizer()
						    .getString(
							    "exchangerate.remove.confirm",
							    this) + "');"));
		}
		item.add(removeLink);

	    }

	};
	dataView.setItemsPerPage(10);
	addOrReplace(dataView);

	addOrReplace(new OrderByBorder(WICKET_ID_orderByfromCurrency,
		WICKET_ID_fromCurrency, dataProvider) {
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

	addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer()
		.getString("exchangerate.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString("exchangerate.addExchangeRateHelp",
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
	addOrReplace(new CustomPagingNavigator(WICKET_ID_navigator, dataView));

	addOrReplace(new Label(WICKET_ID_totalItems, new PropertyModel<String>(
		this, "totalItemString")));

	addOrReplace(new Label(WICKET_ID_startIndex, new PropertyModel(this,
		"startIndex")));

	addOrReplace(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));

    }

    public ExchangeRate getExchangeRate() {
	return exchangeRate;
    }

    public void setExchangeRate(final ExchangeRate exchangeRate) {
	this.exchangeRate = exchangeRate;
    }

    private void saveExchnageRate(
	    final WebMarkupContainer addExchangerateContainer,
	    final WebMarkupContainer updateExchangerateContainer) {

	NumberFormat format = NumberFormat.getInstance(getMobiliserWebSession()
		.getLocale());

	try {
	    if (PortalUtils.exists(getStrFromAmount())) {
		getExchangeRate().setFromAmount(
			format.parse(getStrFromAmount()).longValue());
	    } else {
		getExchangeRate().setFromAmount(1);
	    }
	    if (PortalUtils.exists(getStrToAmount())) {
		getExchangeRate().setToAmount(
			format.parse(getStrToAmount()).longValue());
	    } else {
		getExchangeRate().setToAmount(1);
	    }

	    getExchangeRate().setRate(
		    BigDecimal
			    .valueOf(format.parse(getStrRate()).doubleValue()));
	    SetExchangeRateRequest request;
	    request = getNewMobiliserRequest(SetExchangeRateRequest.class);
	    request.setExchangeRate(getExchangeRate());
	    SetExchangeRateResponse response = wsSystemConfClient
		    .setExchangeRate(request);
	    if (!evaluateMobiliserResponse(response))
		return;
	    this.forceReload = true;
	    createExchangeRateDataView(updateExchangerateContainer,
		    addExchangerateContainer);
	    addExchangerateContainer.setVisible(false);
	    updateExchangerateContainer.setVisible(false);
	    setExchangeRate(null);
	    setStrFromAmount(null);
	    setStrToAmount(null);
	    LOG.info("# Exchange rate saved successfully.");
	} catch (Exception e) {
	    LOG.error("# An error occurred while saving Exchange rate", e);
	    error(getLocalizer().getString("save.exchangerate.error", this));
	}
    }

    private void deleteExchangeRate(final ExchangeRate exchangeRate,
	    final WebMarkupContainer addExchangerateContainer,
	    final WebMarkupContainer updateExchangerateContainer) {
	DeleteExchangeRateRequest request;
	try {
	    request = getNewMobiliserRequest(DeleteExchangeRateRequest.class);
	    request.setFromCurrency(exchangeRate.getFromCurrency());
	    request.setToCurrency(exchangeRate.getToCurrency());
	    DeleteExchangeRateResponse response = wsSystemConfClient
		    .deleteExchangeRate(request);
	    if (!evaluateMobiliserResponse(response))
		return;
	    this.forceReload = true;
	    createExchangeRateDataView(updateExchangerateContainer,
		    addExchangerateContainer);
	    addExchangerateContainer.setVisible(false);
	    updateExchangerateContainer.setVisible(false);
	    LOG.info("# Exchange rate deleted successfully");
	} catch (Exception e) {
	    LOG.error("# An error occurred while deleting Exchange rate", e);
	    error(getLocalizer().getString("delete.exchangerate.error", this));
	}
    }

    private ExchangeRate getUniqueExchangeRate() {
	ExchangeRate exchangeRate = null;
	for (ExchangeRate exgRate : exchangeRateList) {
	    if (getExchangeRate().getFromCurrency().equals(
		    exgRate.getFromCurrency())
		    && getExchangeRate().getToCurrency().equals(
			    exgRate.getToCurrency())) {
		exchangeRate = exgRate;
	    }
	}

	return exchangeRate;

    }

    private class ExchangeRateAmountValidator extends AbstractFormValidator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1455821767135971763L;
	FormComponent[] components;

	public ExchangeRateAmountValidator(FormComponent[] components) {
	    this.components = components;
	}

	@Override
	public FormComponent<?>[] getDependentFormComponents() {
	    return this.components != null ? this.components.clone() : null;
	}

	@Override
	public void validate(Form<?> form) {
	    FormComponent[] components = getDependentFormComponents();
	    String strFromAmount = String.valueOf(components[0].getInput());
	    String strToAmount = String.valueOf(components[1].getInput());
	    String strRate = String.valueOf(components[2].getInput());
	    String fromCurrency = null;
	    String toCurrency = null;
	    if (components.length > 3) {
		fromCurrency = String.valueOf(components[3].getInput());
		toCurrency = String.valueOf(components[4].getInput());
	    }
	    if ((fromCurrency != null && toCurrency != null)
		    && fromCurrency.equals(toCurrency))
		form.error(getLocalizer().getString("forex.error.fromSameAsTo",
			this.getDependentFormComponents()[0].getPage()));
	    // check if percentage from amount and/or percentage to amount equal
	    // "0"
	    if (PortalUtils.exists(strFromAmount) && "0".equals(strFromAmount))
		form.error(getLocalizer().getString("forex.fromAmount.zero",
			this.getDependentFormComponents()[0].getPage()));
	    if (PortalUtils.exists(strToAmount) && "0".equals(strToAmount))
		form.error(getLocalizer().getString("forex.toAmount.zero",
			this.getDependentFormComponents()[0].getPage()));

	    if (form.hasError())
		return;
	    // if (!PortalUtils.exists(strFromAmount)) {
	    // components[0].setModelObject((new Long(1)));
	    // }
	    // if (!PortalUtils.exists(strToAmount))
	    // components[1].setModelObject(new Long(1));

	}
    }

    public String getStrToAmount() {
	return strToAmount;
    }

    public void setStrToAmount(String strToAmount) {
	this.strToAmount = strToAmount;
    }

    public String getStrFromAmount() {
	return strFromAmount;
    }

    public void setStrFromAmount(String strFromAmount) {
	this.strFromAmount = strFromAmount;
    }

    public void setStrRate(String strRate) {
	this.strRate = strRate;
    }

    public String getStrRate() {
	return strRate;
    }

}
