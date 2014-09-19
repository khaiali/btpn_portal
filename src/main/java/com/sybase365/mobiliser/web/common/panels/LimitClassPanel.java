package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitClass;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSetClass;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.LimitClassDataProvider;
import com.sybase365.mobiliser.web.cst.pages.customercare.StandingDataPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.LimitClassPage;
import com.sybase365.mobiliser.web.util.AmountConverter;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class LimitClassPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(LimitClassPanel.class);

    private LimitClassDataProvider dataProvider;
    private List<LimitClass> limitClassList;
    private boolean editClicked;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private int rowIndex = 1;

    private static final String WICKET_ID_removeLink = "removeLink";
    private static final String WICKET_ID_editLink = "editLink";
    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_LName = "LName";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";
    private static final String WICKET_ID_LUseCase = "LUseCase";
    private static final String WICKET_ID_LLimitClass = "LLimitClass";

    private LimitClass limitClass;
    private Form<?> limitClassForm;
    private WebMarkupContainer limitClassFormDiv;
    private KeyValueDropDownChoice<String, String> currencyList;
    private WebMarkupContainer dataViewContainer;

    private String useCase;
    private FeedbackPanel errorMessagePanel;

    private MobiliserBasePage basePage;
    private CustomerBean customer;
    private Boolean individual;

    public LimitClassPanel(String id, MobiliserBasePage basePage,
	    CustomerBean customer) {
	super(id);
	this.basePage = basePage;
	this.customer = customer;
	if (PortalUtils.exists(customer)) {
	    this.individual = true;
	} else {
	    this.individual = false;
	}

	this.constructPanel();
    }

    private void constructPanel() {

	limitClassForm = new Form("limitClassForm",
		new CompoundPropertyModel<LimitClassPage>(this));
	limitClassFormDiv = new WebMarkupContainer("limitClassFormDiv");
	errorMessagePanel = new FeedbackPanel("errorMessages");

	limitClassForm.setOutputMarkupId(true);
	limitClassForm.setOutputMarkupPlaceholderTag(true);
	limitClassFormDiv.setOutputMarkupId(true);
	limitClassFormDiv.setOutputMarkupPlaceholderTag(true);

	AjaxLink addLimitClassLink = new AjaxLink("addNewLimitClassLink") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		limitClassFormDiv = createLimitClassForm(true, true);

		limitClass = null;
		target.addComponent(limitClassFormDiv);
	    }
	};
	// createLimitClassForm(false, false);
	add(addLimitClassLink.setVisible(!individual));
	// Data view

	dataViewContainer = new WebMarkupContainer(
		"limitClassDataViewContainer");
	try {
	    if (!individual) {
		createLimitClassDataView(dataViewContainer);
	    } else {

		Long entity = customer.getId();
		Integer type = Constants.LIMIT_CUSTOMER_TYPE;
		List<LimitSetClass> individualLimitSetClassList = basePage
			.getLimitSetClassList(entity, type);
		if (PortalUtils.exists(individualLimitSetClassList)
			&& individualLimitSetClassList.size() == 1) {
		    limitClass = individualLimitSetClassList.get(0)
			    .getLimitClass();

		}
		if (PortalUtils.exists(limitClass)) {
		    createLimitClassForm(true, false);
		} else {
		    limitClass = new LimitClass();
		    createLimitClassForm(true, true);
		    limitClass.setName(entity.toString());
		}

	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while creating limit class", e);
	    error(getLocalizer().getString("ERROR.LIMITCLASS_SAVE", this));
	}

	add(dataViewContainer.setVisible(!individual));

    }

    private void createLimitClassDataView(WebMarkupContainer dataViewContainer)
	    throws Exception {

	dataProvider = new LimitClassDataProvider(WICKET_ID_pageable, basePage);
	limitClassList = new ArrayList<LimitClass>();
	final DataView<LimitClass> dataView = new DataView<LimitClass>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    Long entity = null;
		    Integer type = null;
		    if (individual) {
			entity = customer.getId();
			type = Constants.LIMIT_CUSTOMER_TYPE;
		    }
		    dataProvider.findLimitClass(forceReload, individual,
			    entity, type);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error("# An error occurred while finding limit sets",
			    dple);
		    error(getLocalizer().getString("ERROR.LIMITSET_GET", this));
		}

		refreshTotalItemCount();
		if (dataProvider.size() > 0) {
		    setVisible(true);
		    if (!editClicked) {
			createLimitClassForm(false, false);
		    }
		} else {
		    setVisible(false);
		    createLimitClassForm(true, true);
		}
		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<LimitClass> item) {
		final LimitClass entry = item.getModelObject();

		limitClassList.add(entry);

		item.add(new Label(WICKET_ID_LName, entry.getName()));
		Link editLink = new Link<LimitClass>(WICKET_ID_editLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			limitClass = (LimitClass) item.getModelObject();
			editClicked = true;
			try {
			    createLimitClassForm(true, false);
			} catch (Exception e) {
			    LOG.error(
				    "# An error occurred while updating limit class",
				    e);
			    info(getLocalizer().getString(
				    "ERROR.LIMICLASS_UPDATE", this));
			}
		    }
		};

		item.add(editLink);

		Link removeLink = new Link<LimitClass>(WICKET_ID_removeLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			limitClass = (LimitClass) item.getModelObject();
			try {
			    if (basePage.removeLimitClass(limitClass.getId(),
				    basePage.getMobiliserWebSession()
					    .getLoggedInCustomer()
					    .getCustomerId(),
				    Constants.LIMIT_CUSTOMER_TYPE)) {
				info(getLocalizer().getString(
					"MESSAGE.LIMICLASS_REMOVED", this));
				limitClass = null;
			    }
			} catch (Exception e) {
			    LOG.error(
				    "# An error occurred while removing limit class",
				    e);
			    error(getLocalizer().getString(
				    "ERROR.LIMICLASS_REMOVE", this));
			}
		    }
		};
		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"limitClass.remove.confirm", this)
				+ "');"));
		item.add(removeLink.setVisible(!individual));

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
	dataView.setVisible(true);
	dataView.setItemsPerPage(10);
	dataViewContainer.addOrReplace(dataView);

	dataViewContainer.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("limitClass.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString("limitClass.noItemsHelpMsg",
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
	dataViewContainer.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));

    }

    private WebMarkupContainer createLimitClassForm(boolean visible,
	    boolean enabled) {
	limitClassForm.addOrReplace(new RequiredTextField<String>(
		"limitClass.name").add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.setEnabled(enabled).add(new ErrorIndicator()));

	limitClassForm
		.addOrReplace(new LocalizableLookupDropDownChoice<String>(
			"limitClass.currency", String.class, "currencies",
			this, true, true).setNullValid(false).setRequired(true)
			.add(new ErrorIndicator()));

	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.dailyDebitLimitCount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.dailyDebitLimitCount",
			Constants.REGEX_AMOUNT_18_0, true);
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.weeklyDebitLimitCount") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.weeklyDebitLimitCount",
			Constants.REGEX_AMOUNT_18_0, true);
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.monthlyDebitLimitCount") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.monthlyDebitLimitCount",
			Constants.REGEX_AMOUNT_18_0, true);
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.absoluteDebitLimitCount") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.absoluteDebitLimitCount",
			Constants.REGEX_AMOUNT_18_0, true);
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.dailyCreditLimitCount") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.dailyCreditLimitCount",
			Constants.REGEX_AMOUNT_18_0, true);
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.weeklyCreditLimitCount") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.weeklyCreditLimitCount",
			Constants.REGEX_AMOUNT_18_0, true);
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.monthlyCreditLimitCount") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.monthlyCreditLimitCount",
			Constants.REGEX_AMOUNT_18_0, true);
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.absoluteCreditLimitCount") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.absoluteCreditLimitCount",
			Constants.REGEX_AMOUNT_18_0, true);
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.dailyDebitLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.dailyDebitLimitAmount");
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.weeklyDebitLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.weeklyDebitLimitAmount");
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.monthlyDebitLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.monthlyDebitLimitAmount");
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.absoluteDebitLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.absoluteDebitLimitAmount");
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.dailyCreditLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.dailyCreditLimitAmount");
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.weeklyCreditLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.weeklyCreditLimitAmount");
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.monthlyCreditLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.monthlyCreditLimitAmount");
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.absoluteCreditLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.absoluteCreditLimitAmount");
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.singleDebitMinimum") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.singleDebitMinimum");
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.singleCreditMinimum") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.singleCreditMinimum");
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.singleCreditLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.singleCreditLimitAmount");
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	limitClassForm.addOrReplace(new TextField<Long>(
		"limitClass.singleDebitLimitAmount") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage,
			"cst.limitClass.singleDebitLimitAmount");
	    }

	}.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	limitClassFormDiv.setVisible(visible);
	limitClassForm.addOrReplace(new Button("saveLimitClass") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (validateLimitClass())
		    addLimitClass();
	    };
	});
	limitClassForm.addOrReplace(new Button("cancelLimitClass") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (individual) {
		    setResponsePage(new StandingDataPage(customer));
		} else {
		    limitClassFormDiv = createLimitClassForm(false, false);
		}

	    };
	}.setDefaultFormProcessing(false));
	limitClassFormDiv.addOrReplace(limitClassForm);
	addOrReplace(limitClassFormDiv);
	addOrReplace(errorMessagePanel);
	return limitClassFormDiv;

    }

    private boolean validateLimitClass() {
	boolean validate = true;
	// INT_WEEKLY_DEBIT_LIMIT>=INT_DAILY_DEBIT_LIMIT)
	// and (INT_MONTHLY_DEBIT_LIMIT is null or INT_MONTHLY_DEBIT_LIMIT is
	// not null and INT_MONTHLY_DEBIT_LIMIT>=INT_DAILY_DEBIT_LIMIT)
	// and (INT_ABSOLUTE_DEBIT_LIMIT is null or INT_ABSOLUTE_DEBIT_LIMIT is
	// not null and INT_ABSOLUTE_DEBIT_LIMIT>=INT_DAILY_DEBIT_LIMIT)
	if (PortalUtils.exists(limitClass.getDailyDebitLimitCount())) {
	    if ((PortalUtils.exists(limitClass.getMonthlyDebitLimitCount()) && limitClass
		    .getMonthlyDebitLimitCount() < limitClass
		    .getDailyDebitLimitCount())
		    || (PortalUtils.exists(limitClass
			    .getWeeklyDebitLimitCount()) && limitClass
			    .getWeeklyDebitLimitCount() < limitClass
			    .getDailyDebitLimitCount())
		    || (PortalUtils.exists(limitClass
			    .getAbsoluteDebitLimitCount()) && limitClass
			    .getAbsoluteDebitLimitCount() < limitClass
			    .getDailyDebitLimitCount())) {
		error(getLocalizer().getString("daily.debit.count.large", this));
		validate = false;
	    }
	}
	// and (INT_MONTHLY_DEBIT_LIMIT is null or INT_MONTHLY_DEBIT_LIMIT is
	// not null and INT_MONTHLY_DEBIT_LIMIT>=INT_WEEKLY_DEBIT_LIMIT)
	// and (INT_ABSOLUTE_DEBIT_LIMIT is null or INT_ABSOLUTE_DEBIT_LIMIT is
	// not null and INT_ABSOLUTE_DEBIT_LIMIT>=INT_WEEKLY_DEBIT_LIMIT)
	if (PortalUtils.exists(limitClass.getWeeklyDebitLimitCount())) {
	    if ((PortalUtils.exists(limitClass.getMonthlyDebitLimitCount()) && limitClass
		    .getMonthlyDebitLimitCount() < limitClass
		    .getWeeklyDebitLimitCount())
		    || (PortalUtils.exists(limitClass
			    .getAbsoluteDebitLimitCount()) && limitClass
			    .getAbsoluteDebitLimitCount() < limitClass
			    .getWeeklyDebitLimitCount())) {
		error(getLocalizer()
			.getString("weekly.debit.count.large", this));
		validate = false;
	    }
	}
	// (INT_ABSOLUTE_DEBIT_LIMIT is null or INT_ABSOLUTE_DEBIT_LIMIT is
	// not null and INT_ABSOLUTE_DEBIT_LIMIT>=INT_MONTHLY_DEBIT_LIMIT)
	if (PortalUtils.exists(limitClass.getMonthlyDebitLimitCount())) {
	    if (PortalUtils.exists(limitClass.getAbsoluteDebitLimitCount())
		    && limitClass.getAbsoluteDebitLimitCount() < limitClass
			    .getMonthlyDebitLimitCount()) {
		error(getLocalizer().getString("monthly.debit.count.large",
			this));
		validate = false;
	    }
	}
	// and (INT_WEEKLY_CREDIT_LIMIT is null or INT_WEEKLY_CREDIT_LIMIT is
	// not null and INT_WEEKLY_CREDIT_LIMIT>=INT_DAILY_CREDIT_LIMIT)
	// and (INT_MONTHLY_CREDIT_LIMIT is null or INT_MONTHLY_CREDIT_LIMIT is
	// not null and INT_MONTHLY_CREDIT_LIMIT>=INT_DAILY_CREDIT_LIMIT)
	// and (INT_ABSOLUTE_CREDIT_LIMIT is null or INT_ABSOLUTE_CREDIT_LIMIT
	// is not null and INT_ABSOLUTE_CREDIT_LIMIT>=INT_DAILY_CREDIT_LIMIT)
	if (PortalUtils.exists(limitClass.getDailyCreditLimitCount())) {
	    if ((PortalUtils.exists(limitClass.getMonthlyCreditLimitCount()) && limitClass
		    .getMonthlyCreditLimitCount() < limitClass
		    .getDailyCreditLimitCount())
		    || (PortalUtils.exists(limitClass
			    .getWeeklyCreditLimitCount()) && limitClass
			    .getWeeklyCreditLimitCount() < limitClass
			    .getDailyCreditLimitCount())
		    || (PortalUtils.exists(limitClass
			    .getAbsoluteCreditLimitCount()) && limitClass
			    .getAbsoluteCreditLimitCount() < limitClass
			    .getDailyCreditLimitCount())) {
		error(getLocalizer()
			.getString("daily.credit.count.large", this));
		validate = false;
	    }
	}
	// and (INT_MONTHLY_CREDIT_LIMIT is null or INT_MONTHLY_CREDIT_LIMIT is
	// not null and INT_MONTHLY_CREDIT_LIMIT>=INT_WEEKLY_CREDIT_LIMIT)
	// and (INT_ABSOLUTE_CREDIT_LIMIT is null or INT_ABSOLUTE_CREDIT_LIMIT
	// is not null and INT_ABSOLUTE_CREDIT_LIMIT>=INT_WEEKLY_CREDIT_LIMIT)
	if (PortalUtils.exists(limitClass.getWeeklyCreditLimitCount())) {
	    if ((PortalUtils.exists(limitClass.getMonthlyCreditLimitCount()) && limitClass
		    .getMonthlyCreditLimitCount() < limitClass
		    .getWeeklyCreditLimitCount())
		    || (PortalUtils.exists(limitClass
			    .getAbsoluteCreditLimitCount()) && limitClass
			    .getAbsoluteCreditLimitCount() < limitClass
			    .getWeeklyCreditLimitCount())) {
		error(getLocalizer().getString("weekly.credit.count.large",
			this));
		validate = false;
	    }
	}
	// and (INT_ABSOLUTE_CREDIT_LIMIT is null or INT_ABSOLUTE_CREDIT_LIMIT
	// is not null and INT_ABSOLUTE_CREDIT_LIMIT>=INT_MONTHLY_CREDIT_LIMIT)
	if (PortalUtils.exists(limitClass.getMonthlyCreditLimitCount())) {
	    if (PortalUtils.exists(limitClass.getAbsoluteCreditLimitCount())
		    && limitClass.getAbsoluteCreditLimitCount() < limitClass
			    .getMonthlyCreditLimitCount()) {
		error(getLocalizer().getString("monthly.credit.count.large",
			this));
		validate = false;
	    }
	}
	// and (AMNT_SINGLE_DEBIT_MINIMUM is null or
	// AMNT_SINGLE_DEBIT_MINIMUM is not null and
	// AMNT_SINGLE_DEBIT_MINIMUM <= AMNT_SINGLE_DEBIT_LIMIT)
	// and (AMNT_DAILY_DEBIT_LIMIT is null or AMNT_DAILY_DEBIT_LIMIT
	// is not null and AMNT_DAILY_DEBIT_LIMIT >= AMNT_SINGLE_DEBIT_LIMIT)
	// and (AMNT_WEEKLY_DEBIT_LIMIT is null or AMNT_WEEKLY_DEBIT_LIMIT
	// is not null and AMNT_WEEKLY_DEBIT_LIMIT >= AMNT_SINGLE_DEBIT_LIMIT)
	// and (AMNT_MONTHLY_DEBIT_LIMIT is null or
	// AMNT_MONTHLY_DEBIT_LIMIT is not null and
	// AMNT_MONTHLY_DEBIT_LIMIT >= AMNT_SINGLE_DEBIT_LIMIT)
	// and (AMNT_ABSOLUTE_DEBIT_LIMIT is null or
	// AMNT_ABSOLUTE_DEBIT_LIMIT is not null and
	// AMNT_ABSOLUTE_DEBIT_LIMIT >= AMNT_SINGLE_DEBIT_LIMIT)
	if (PortalUtils.exists(limitClass.getSingleDebitLimitAmount())) {
	    if ((PortalUtils.exists(limitClass.getSingleDebitMinimum()) && limitClass
		    .getSingleDebitMinimum() > limitClass
		    .getSingleDebitLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getDailyDebitLimitAmount()) && limitClass
			    .getDailyDebitLimitAmount() < limitClass
			    .getSingleDebitLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getWeeklyDebitLimitAmount()) && limitClass
			    .getWeeklyDebitLimitAmount() < limitClass
			    .getSingleDebitLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getMonthlyDebitLimitAmount()) && limitClass
			    .getMonthlyDebitLimitAmount() < limitClass
			    .getSingleDebitLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getAbsoluteDebitLimitAmount()) && limitClass
			    .getAbsoluteDebitLimitAmount() < limitClass
			    .getSingleDebitLimitAmount())) {
		error(getLocalizer().getString("single.debit.amount.large",
			this));
		validate = false;
	    }
	}
	// and (AMNT_DAILY_DEBIT_LIMIT is null or AMNT_DAILY_DEBIT_LIMIT
	// is not null and AMNT_SINGLE_DEBIT_MINIMUM <= AMNT_DAILY_DEBIT_LIMIT)
	// and (AMNT_WEEKLY_DEBIT_LIMIT is null or AMNT_WEEKLY_DEBIT_LIMIT
	// is not null and AMNT_WEEKLY_DEBIT_LIMIT >= AMNT_SINGLE_DEBIT_MINIMUM)
	// and (AMNT_MONTHLY_DEBIT_LIMIT is null or
	// AMNT_MONTHLY_DEBIT_LIMIT is not null and AMNT_MONTHLY_DEBIT_LIMIT >=
	// AMNT_SINGLE_DEBIT_MINIMUM)
	// and (AMNT_ABSOLUTE_DEBIT_LIMIT is null or
	// AMNT_ABSOLUTE_DEBIT_LIMIT is not null and AMNT_ABSOLUTE_DEBIT_LIMIT
	// >= AMNT_SINGLE_DEBIT_MINIMUM)
	if (PortalUtils.exists(limitClass.getSingleDebitMinimum())) {
	    if ((PortalUtils.exists(limitClass.getDailyDebitLimitAmount()) && limitClass
		    .getDailyDebitLimitAmount() < limitClass
		    .getSingleDebitMinimum())
		    || (PortalUtils.exists(limitClass
			    .getWeeklyDebitLimitAmount()) && limitClass
			    .getWeeklyDebitLimitAmount() < limitClass
			    .getSingleDebitMinimum())
		    || (PortalUtils.exists(limitClass
			    .getMonthlyDebitLimitAmount()) && limitClass
			    .getMonthlyDebitLimitAmount() < limitClass
			    .getSingleDebitMinimum())
		    || (PortalUtils.exists(limitClass
			    .getAbsoluteDebitLimitAmount()) && limitClass
			    .getAbsoluteDebitLimitAmount() < limitClass
			    .getSingleDebitMinimum())) {
		error(getLocalizer().getString("single.debit.minimum.large",
			this));
		validate = false;
	    }
	}
	// (AMNT_WEEKLY_DEBIT_LIMIT is null or AMNT_WEEKLY_DEBIT_LIMIT
	// is not null and AMNT_WEEKLY_DEBIT_LIMIT >= AMNT_DAILY_DEBIT_LIMIT)
	// and (AMNT_MONTHLY_DEBIT_LIMIT is null or
	// AMNT_MONTHLY_DEBIT_LIMIT is not null and AMNT_MONTHLY_DEBIT_LIMIT >=
	// AMNT_DAILY_DEBIT_LIMIT)
	// and (AMNT_ABSOLUTE_DEBIT_LIMIT is null or
	// AMNT_ABSOLUTE_DEBIT_LIMIT is not null and AMNT_ABSOLUTE_DEBIT_LIMIT
	// >= AMNT_DAILY_DEBIT_LIMIT)
	if (PortalUtils.exists(limitClass.getDailyDebitLimitAmount())) {
	    if ((PortalUtils.exists(limitClass.getWeeklyDebitLimitAmount()) && limitClass
		    .getWeeklyDebitLimitAmount() < limitClass
		    .getDailyDebitLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getMonthlyDebitLimitAmount()) && limitClass
			    .getMonthlyDebitLimitAmount() < limitClass
			    .getDailyDebitLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getAbsoluteDebitLimitAmount()) && limitClass
			    .getAbsoluteDebitLimitAmount() < limitClass
			    .getDailyDebitLimitAmount())) {
		error(getLocalizer()
			.getString("daily.debit.amount.large", this));
		validate = false;
	    }
	}
	// (AMNT_MONTHLY_DEBIT_LIMIT is null or
	// AMNT_MONTHLY_DEBIT_LIMIT is not null and AMNT_MONTHLY_DEBIT_LIMIT >=
	// AMNT_WEEKLY_DEBIT_LIMIT)
	// and (AMNT_ABSOLUTE_DEBIT_LIMIT is null or
	// AMNT_ABSOLUTE_DEBIT_LIMIT is not null and AMNT_ABSOLUTE_DEBIT_LIMIT
	// >= AMNT_WEEKLY_DEBIT_LIMIT)
	if (PortalUtils.exists(limitClass.getWeeklyDebitLimitAmount())) {
	    if ((PortalUtils.exists(limitClass.getMonthlyDebitLimitAmount()) && limitClass
		    .getMonthlyDebitLimitAmount() < limitClass
		    .getWeeklyDebitLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getAbsoluteDebitLimitAmount()) && limitClass
			    .getAbsoluteDebitLimitAmount() < limitClass
			    .getWeeklyDebitLimitAmount())) {
		error(getLocalizer().getString("weekly.debit.amount.large",
			this));
		validate = false;
	    }
	}
	// (AMNT_ABSOLUTE_DEBIT_LIMIT is null or
	// AMNT_ABSOLUTE_DEBIT_LIMIT is not null and AMNT_ABSOLUTE_DEBIT_LIMIT
	// >= AMNT_MONTHLY_DEBIT_LIMIT)
	if (PortalUtils.exists(limitClass.getMonthlyDebitLimitAmount())) {
	    if (PortalUtils.exists(limitClass.getAbsoluteDebitLimitAmount())
		    && limitClass.getAbsoluteDebitLimitAmount() < limitClass
			    .getMonthlyDebitLimitAmount()) {
		error(getLocalizer().getString("monthly.debit.amount.large",
			this));
		validate = false;
	    }
	}
	// Now validate CREDIT limit amounts
	if (PortalUtils.exists(limitClass.getSingleCreditLimitAmount())) {
	    if ((PortalUtils.exists(limitClass.getSingleCreditMinimum()) && limitClass
		    .getSingleCreditMinimum() > limitClass
		    .getSingleCreditLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getDailyCreditLimitAmount()) && limitClass
			    .getDailyCreditLimitAmount() < limitClass
			    .getSingleCreditLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getWeeklyCreditLimitAmount()) && limitClass
			    .getWeeklyCreditLimitAmount() < limitClass
			    .getSingleCreditLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getMonthlyCreditLimitAmount()) && limitClass
			    .getMonthlyCreditLimitAmount() < limitClass
			    .getSingleCreditLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getAbsoluteCreditLimitAmount()) && limitClass
			    .getAbsoluteCreditLimitAmount() < limitClass
			    .getSingleCreditLimitAmount())) {
		error(getLocalizer().getString("single.credit.amount.large",
			this));
		validate = false;
	    }
	}
	if (PortalUtils.exists(limitClass.getSingleCreditMinimum())) {
	    if ((PortalUtils.exists(limitClass.getDailyCreditLimitAmount()) && limitClass
		    .getDailyCreditLimitAmount() < limitClass
		    .getSingleCreditMinimum())
		    || (PortalUtils.exists(limitClass
			    .getWeeklyCreditLimitAmount()) && limitClass
			    .getWeeklyCreditLimitAmount() < limitClass
			    .getSingleCreditMinimum())
		    || (PortalUtils.exists(limitClass
			    .getMonthlyCreditLimitAmount()) && limitClass
			    .getMonthlyCreditLimitAmount() < limitClass
			    .getSingleCreditMinimum())
		    || (PortalUtils.exists(limitClass
			    .getAbsoluteCreditLimitAmount()) && limitClass
			    .getAbsoluteCreditLimitAmount() < limitClass
			    .getSingleCreditMinimum())) {
		error(getLocalizer().getString("single.credit.minimum.large",
			this));
		validate = false;
	    }
	}
	if (PortalUtils.exists(limitClass.getDailyCreditLimitAmount())) {
	    if ((PortalUtils.exists(limitClass.getWeeklyCreditLimitAmount()) && limitClass
		    .getWeeklyCreditLimitAmount() < limitClass
		    .getDailyCreditLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getMonthlyCreditLimitAmount()) && limitClass
			    .getMonthlyCreditLimitAmount() < limitClass
			    .getDailyCreditLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getAbsoluteCreditLimitAmount()) && limitClass
			    .getAbsoluteCreditLimitAmount() < limitClass
			    .getDailyCreditLimitAmount())) {
		error(getLocalizer().getString("daily.credit.amount.large",
			this));
		validate = false;
	    }
	}
	if (PortalUtils.exists(limitClass.getWeeklyCreditLimitAmount())) {
	    if ((PortalUtils.exists(limitClass.getMonthlyCreditLimitAmount()) && limitClass
		    .getMonthlyCreditLimitAmount() < limitClass
		    .getWeeklyCreditLimitAmount())
		    || (PortalUtils.exists(limitClass
			    .getAbsoluteCreditLimitAmount()) && limitClass
			    .getAbsoluteCreditLimitAmount() < limitClass
			    .getWeeklyCreditLimitAmount())) {
		error(getLocalizer().getString("weekly.credit.amount.large",
			this));
		validate = false;
	    }
	}
	if (PortalUtils.exists(limitClass.getMonthlyCreditLimitAmount())) {
	    if (PortalUtils.exists(limitClass.getAbsoluteCreditLimitAmount())
		    && limitClass.getAbsoluteCreditLimitAmount() < limitClass
			    .getMonthlyCreditLimitAmount()) {
		error(getLocalizer().getString("monthly.credit.amount.large",
			this));
		validate = false;
	    }
	}
	return validate;
    }

    private void addLimitClass() {

	try {
	    errorMessagePanel = new FeedbackPanel("errorMessages");
	    if (!errorMessagePanel.anyErrorMessage()) {
		errorMessagePanel = new FeedbackPanel("errorMessages");

		errorMessagePanel.setVisible(true);
		addOrReplace(errorMessagePanel);

		// Update
		if (PortalUtils.exists(limitClass)
			&& PortalUtils.exists(limitClass.getId())) {
		    limitClass = basePage.updateLimitClass(limitClass);
		} else {

		    if (individual) {
			limitClass = basePage
				.addLimitClass(limitClass, customer.getId(),
					Constants.LIMIT_CUSTOMER_TYPE);
			Customer cust = basePage.getCustomerDetails(customer
				.getId());
			customer.setLimitId(cust.getLimitSetId());
			customer.setIsIndividualLimitSet(true);
			customer.setOriginalLimitSetId(cust.getLimitSetId());
		    } else {
			limitClass = basePage.addLimitClass(limitClass, null,
				null);
		    }
		}

		if (individual) {
		    setResponsePage(new StandingDataPage(customer));
		} else {
		    createLimitClassDataView(dataViewContainer);
		    createLimitClassForm(false, false);
		}
	    }
	} catch (Exception e) {
	    LOG.error("# Error occurred while adding limit set" + e);
	    error(getLocalizer().getString("ERROR.LIMITSET_ADD", this));
	    createLimitClassForm(true, false);
	}

    }

    public String getUseCase() {
	return useCase;
    }

    public void setUseCase(String useCase) {
	this.useCase = useCase;
    }

}
