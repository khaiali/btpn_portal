package com.sybase365.mobiliser.web.common.panels;

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
import org.apache.wicket.markup.html.form.CheckBox;
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
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreateBalanceAlertRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreateBalanceAlertResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.DeleteBalanceAlertRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.DeleteBalanceAlertResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetBalanceAlertsByPaymentInstrumentRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetBalanceAlertsByPaymentInstrumentResponseType;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdateBalanceAlertRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdateBalanceAlertResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.BalanceAlert;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.BalanceAlertDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.AmountConverter;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class BalanceAlertPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
	    .getLogger(BalanceAlertPanel.class);

    private BalanceAlert balanceAlert;
    private FeedbackPanel balanceAlertErrorMessages;
    private MobiliserBasePage basePage;
    private Long paymentInstrumentId = null;
    private RequiredTextField threshold;
    private CustomerBean customer;
    // Data View
    private BalanceAlertDataProvider dataProvider;
    private List<BalanceAlert> balanceAlerList;
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private int rowIndex = 1;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByTemplateName = "orderByTemplateName";
    private static final String WICKET_ID_removeLink = "removeLink";
    private static final String WICKET_ID_removeText = "removeText";
    private static final String WICKET_ID_editLink = "editLink";
    private static final String WICKET_ID_editText = "editText";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_threshold = "threshold";
    private static final String WICKET_ID_onlyTransition = "onlyTransition";
    private static final String WICKET_ID_templateName = "templateName";
    private static final String WICKET_ID_msisdn = "msisdns";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    private PrivilegedBehavior portalLogin;
    private PrivilegedBehavior merchantLogin;

    public BalanceAlertPanel(String id, MobiliserBasePage basePage,
	    CustomerBean customer) {
	super(id);
	this.basePage = basePage;
	this.customer = customer;
	portalLogin = new PrivilegedBehavior(basePage,
		Constants.PRIV_CONSUMER_LOGIN);
	merchantLogin = new PrivilegedBehavior(basePage,
		Constants.PRIV_MERCHANT_LOGIN);
	constructPanel();
    }

    private void constructPanel() {
	final Form balanceAlertForm = new Form("balanceAlertForm",
		new CompoundPropertyModel<BalanceAlertPanel>(this));
	balanceAlertErrorMessages = (FeedbackPanel) new FeedbackPanel(
		"balanceAlertErrorMessages").setVisible(true);
	WebMarkupContainer bAlertGrid = new WebMarkupContainer(
		"balanceAlertGrid");
	balanceAlertForm.addOrReplace(balanceAlertErrorMessages);
	getPaymentInstrumentID();
	basePage.getMobiliserWebSession().setBalanceAlertList(
		getBalanceAlertfromDB(paymentInstrumentId));
	createMarkupContainer(bAlertGrid, balanceAlertForm, "add");

    }

    private WebMarkupContainer createMarkupContainer(
	    final WebMarkupContainer bAlertGrid,
	    final Form<?> balanceAlertForm, final String mode) {
	bAlertGrid.setOutputMarkupId(true);
	bAlertGrid.setOutputMarkupPlaceholderTag(true);
	balanceAlertForm.setOutputMarkupId(true);
	balanceAlertForm.setOutputMarkupPlaceholderTag(true);
	threshold = new RequiredTextField<Long>("balanceAlert.threshold") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(basePage, "balanceAlert.threshold");
	    };

	};
	bAlertGrid.addOrReplace(threshold.add(new ErrorIndicator()));
	bAlertGrid.addOrReplace(new TextField<String>("balanceAlert.emails")
		.add(new PatternValidator(Constants.REGEX_EMAILS))
		.add(Constants.largeStringValidator)
		.add(Constants.largeSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	bAlertGrid.addOrReplace(new TextField<String>("balanceAlert.msisdns")
		.add(new PatternValidator(Constants.REGEX_PHONE_NUMBERS))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	bAlertGrid.addOrReplace(new CheckBox("balanceAlert.onlyTransition"));

	bAlertGrid.add(new LocalizableLookupDropDownChoice<String>(
		"balanceAlert.language", String.class, "languages", this,
		false, true).setNullValid(true).add(new ErrorIndicator()));

	bAlertGrid.add(new LocalizableLookupDropDownChoice<String>(
		"balanceAlert.country", String.class, "countries", this, false,
		true).setNullValid(true).add(new ErrorIndicator()));

	bAlertGrid.addOrReplace(new Button("cancelBalanceAlert") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		balanceAlert = null;
		bAlertGrid.setVisible(false);
	    };
	}.setDefaultFormProcessing(false));
	toggleButtons(bAlertGrid, mode);

	balanceAlertForm.addOrReplace(new AjaxLink("balanceAlertAddLink") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(AjaxRequestTarget target) {

		toggleButtons(bAlertGrid, "ADD");
		bAlertGrid.setVisible(true);
		target.addComponent(bAlertGrid);
		balanceAlertForm.clearInput();
		balanceAlert = new BalanceAlert();
		balanceAlert.setCountry(customer.getKvCountry());
		balanceAlert.setLanguage(customer.getLanguage());
		threshold.clearInput();
		target.addComponent(balanceAlertForm);

	    }
	});
	balanceAlertForm.addOrReplace(bAlertGrid);
	balanceAlertForm.addOrReplace(balanceAlertErrorMessages);
	bAlertGrid.setVisible(false);
	createBalanceAlertDataView(balanceAlertForm);
	add(balanceAlertForm);

	return bAlertGrid;
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
		balanceAlertErrorMessages.setVisible(true);

		BalanceAlert bAlert = getBalanceAlert();
		if (!balanceAlertErrorMessages.anyErrorMessage()) {
		    addBalanceAlert(bAlert);
		    balanceAlertErrorMessages = new FeedbackPanel(
			    "balanceAlertErrorMessages");

		    balanceAlertErrorMessages.setVisible(true);
		    this.getForm().addOrReplace(balanceAlertErrorMessages);
		    balanceAlert = null;
		    bAlertGrid.setVisible(false);
		    createBalanceAlertDataView(this.getForm());
		}
	    }
	}.setVisible(visible));

	bAlertGrid.addOrReplace(new Button("updateBalanceAlert") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		balanceAlertErrorMessages.setVisible(true);

		BalanceAlert bAlert = getBalanceAlert();
		if (!balanceAlertErrorMessages.anyErrorMessage()) {
		    updateBalanceAlert(bAlert);
		    balanceAlertErrorMessages = new FeedbackPanel(
			    "balanceAlertErrorMessages");

		    balanceAlertErrorMessages.setVisible(true);
		    this.getForm().addOrReplace(balanceAlertErrorMessages);
		    balanceAlert = null;
		    bAlertGrid.setVisible(false);
		    createBalanceAlertDataView(this.getForm());
		}
	    };
	}.setVisible(!visible));

    }

    private void createBalanceAlertDataView(final Form bAlertForm) {
	final WebMarkupContainer balanceAlertListGrid = new WebMarkupContainer(
		"balanceAlertListGrid");
	bAlertForm.addOrReplace(balanceAlertListGrid);
	dataProvider = new BalanceAlertDataProvider(WICKET_ID_pageable,
		basePage);
	balanceAlerList = new ArrayList<BalanceAlert>();
	final DataView<BalanceAlert> dataView = new DataView<BalanceAlert>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.getBalanceAlerts(basePage
			    .getMobiliserWebSession());
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading balance alerts",
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
			String.valueOf(PortalUtils.exists(entry.getThreshold()) ? basePage
				.convertAmountToString(entry.getThreshold())
				: "")));

		item.add(new Label(WICKET_ID_onlyTransition, entry
			.isOnlyTransition() ? "Yes" : "No"));
		item.addOrReplace(new Label(WICKET_ID_msisdn, entry
			.getMsisdns()).add(portalLogin));
		item.add(new Label(WICKET_ID_templateName, entry
			.getTemplateName()).add(merchantLogin));

		Link editLink = new Link<BalanceAlert>(WICKET_ID_editLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			balanceAlert = (BalanceAlert) item.getModelObject();
			WebMarkupContainer bAlertGrid = new WebMarkupContainer(
				"balanceAlertGrid");
			bAlertGrid = createMarkupContainer(bAlertGrid,
				bAlertForm, "EDIT");
			bAlertGrid.setVisible(true);
			bAlertForm.addOrReplace(bAlertGrid);
		    }
		};

		item.add(editLink);

		Link removeLink = new Link<BalanceAlert>(WICKET_ID_removeLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			balanceAlertErrorMessages.setVisible(true);
			BalanceAlert balAlart = (BalanceAlert) item
				.getModelObject();
			removeBalanceAlert(balAlart);
			WebMarkupContainer bAlertGrid = new WebMarkupContainer(
				"balanceAlertGrid");
			bAlertGrid = createMarkupContainer(bAlertGrid,
				bAlertForm, "");
			bAlertGrid.setVisible(false);
			balanceAlert = null;
			bAlertForm.addOrReplace(bAlertGrid);
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
		WICKET_ID_noItemsMsg, basePage.getLocalizer().getString(
			"balance.alert.noItemsMsg", basePage)
			+ "\n"
			+ basePage.getLocalizer().getString(
				"balance.alert.noItemsHelpMsg", basePage)) {
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

	balanceAlertListGrid.addOrReplace(new Label("balertPanel.templateName",
		getLocalizer().getString(
			"balertPanel.balanceAlertTable.th.templateName", this))
		.add(merchantLogin));

	balanceAlertListGrid.addOrReplace(new Label("balertPanel.msisdn",
		getLocalizer().getString(
			"balertPanel.balanceAlertTable.th.msisdn", this))
		.add(portalLogin));

	balanceAlertListGrid.setVisible(true);

    }

    public void addBalanceAlert(BalanceAlert newBA) {
	getPaymentInstrumentID();
	// Newly added Balance Alert Entry
	CreateBalanceAlertRequest balAlertReq = null;
	try {
	    balAlertReq = basePage
		    .getNewMobiliserRequest(CreateBalanceAlertRequest.class);
	    newBA.setPaymentInstrumentId(paymentInstrumentId);

	    if (!PortalUtils.exists(newBA.getCountry())) {
		newBA.setCountry(null);
	    }
	    if (!PortalUtils.exists(newBA.getLanguage())) {
		newBA.setLanguage(null);
	    }
	    newBA.setTemplateName(basePage.getConfiguration()
		    .getBalanceAlertTemplate());
	    balAlertReq.setBalanceAlert(newBA);
	    CreateBalanceAlertResponse balAlertResp = basePage.wsBalanceAlertClient
		    .createBalanceAlert(balAlertReq);
	    if (!basePage.evaluateMobiliserResponse(balAlertResp))
		return;
	    basePage.getMobiliserWebSession().setBalanceAlertList(
		    getBalanceAlertfromDB(paymentInstrumentId));
	    balanceAlertErrorMessages.info(basePage.getLocalizer().getString(
		    "MESSAGE.BALANCE_ALERTS_SAVED", basePage));
	    LOG.info("# Successfully added Balance Alert data for[{}]",
		    balAlertResp.getBalanceAlertId());

	} catch (Exception e) {
	    balanceAlertErrorMessages.error(basePage.getLocalizer().getString(
		    "ERROR.ADD_BALANCE_ALERTS_FAILURE", basePage));
	    LOG.error("# An error occurred while adding Balance alert data.", e);
	}

    }

    private void updateBalanceAlert(BalanceAlert updateBA) {

	getPaymentInstrumentID();

	// Existing Balance Alert Entry changed
	UpdateBalanceAlertRequest updateBalReq = null;
	try {
	    updateBalReq = basePage
		    .getNewMobiliserRequest(UpdateBalanceAlertRequest.class);
	    updateBA.setPaymentInstrumentId(paymentInstrumentId);

	    if (!PortalUtils.exists(updateBA.getCountry())) {
		updateBA.setCountry(null);
	    }
	    if (!PortalUtils.exists(updateBA.getLanguage())) {
		updateBA.setLanguage(null);
	    }
	    updateBalReq.setBalanceAlert(updateBA);
	    UpdateBalanceAlertResponse updateBalResp = basePage.wsBalanceAlertClient
		    .updateBalanceAlert(updateBalReq);
	    if (!basePage.evaluateMobiliserResponse(updateBalResp))
		return;
	    LOG.info("# Successfully updated Balance Alert data for[{}]",
		    updateBA.getId());
	    basePage.getMobiliserWebSession().setBalanceAlertList(
		    getBalanceAlertfromDB(paymentInstrumentId));
	    balanceAlertErrorMessages.info(basePage.getLocalizer().getString(
		    "MESSAGE.BALANCE_ALERTS_UPDATED", basePage));
	} catch (Exception e) {
	    error(basePage.getLocalizer().getString(
		    "ERROR.ADD_BALANCE_ALERTS_FAILURE", basePage));
	    LOG.error("# An error occurred while updating Balance alert data.",
		    e);
	}

    }

    public void removeBalanceAlert(BalanceAlert oldBA) {
	getPaymentInstrumentID();
	DeleteBalanceAlertRequest deleteBalReq = null;
	try {
	    deleteBalReq = basePage
		    .getNewMobiliserRequest(DeleteBalanceAlertRequest.class);
	    deleteBalReq.setBalanceAlertId(oldBA.getId());
	    DeleteBalanceAlertResponse deleteBalResp = basePage.wsBalanceAlertClient
		    .deleteBalanceAlert(deleteBalReq);
	    if (!basePage.evaluateMobiliserResponse(deleteBalResp))
		return;
	    basePage.getMobiliserWebSession().setBalanceAlertList(
		    getBalanceAlertfromDB(paymentInstrumentId));
	    balanceAlertErrorMessages.info(basePage.getLocalizer().getString(
		    "MESSAGE.BALANCE_ALERTS_REMOVED", basePage));
	    LOG.info("# Successfully deleted Balance Alert data for[{}]",
		    oldBA.getId());
	} catch (Exception e) {
	    balanceAlertErrorMessages.error(basePage.getLocalizer().getString(
		    "ERROR.DELETE_BALANCE_ALERTS_FAILURE", basePage));
	    LOG.error("# An error occurred while deleting Balance alert data.",
		    e);
	}

    }

    private void getPaymentInstrumentID() {
	if (paymentInstrumentId == null) {

	    if (PortalUtils.exists(customer)) {
		List<WalletEntry> walletList = basePage.getWalletEntryList(
			customer.getId(), null, Constants.PI_TYPE_DEFAULT_SVA);

		if (PortalUtils.exists(walletList)) {
		    paymentInstrumentId = walletList.get(0)
			    .getPaymentInstrumentId();
		}
	    }
	}
    }

    private List<BalanceAlert> getBalanceAlertfromDB(Long paymentInstrumentId) {
	try {
	    GetBalanceAlertsByPaymentInstrumentRequest getBalAlertReq = basePage
		    .getNewMobiliserRequest(GetBalanceAlertsByPaymentInstrumentRequest.class);
	    getBalAlertReq.setPaymentInstrumentId(paymentInstrumentId);
	    GetBalanceAlertsByPaymentInstrumentResponseType getBalAlertResp = basePage.wsBalanceAlertClient
		    .getBalanceAlertByPaymentInstrument(getBalAlertReq);

	    if (!basePage.evaluateMobiliserResponse(getBalAlertResp))
		return null;

	    return getBalAlertResp.getBalanceAlerts();
	} catch (Exception e) {
	    error(getLocalizer().getString("ERROR.GET_BALANCE_ALERT_FAILURE",
		    this));
	    LOG.error("# An error occurred while getting Balance alert data.",
		    e);
	}

	return null;

    }

    public BalanceAlert getBalanceAlert() {
	return balanceAlert;
    }

    public void setBalanceAlert(BalanceAlert balanceAlert) {
	this.balanceAlert = balanceAlert;
    }

}
