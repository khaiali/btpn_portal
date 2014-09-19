package com.sybase365.mobiliser.web.common.panels.alerts;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlertData;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutTime;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerContactPoint;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.AlertAccountChooserBean;
import com.sybase365.mobiliser.web.beans.AlertFrequencyChooserBean;
import com.sybase365.mobiliser.web.beans.MobileAlertsBean;
import com.sybase365.mobiliser.web.util.AlertsHelper;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author msw
 */
public class EditAccountBalanceThresholdAlertPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(EditAccountBalanceThresholdAlertPanel.class);

    private static final String WICKET_cancelAlertAction = "cancelAlertAction";
    private static final String WICKET_ID_addAlertAction = "addAlertAction";
    private static final String WICKET_ID_alertAccountChooserPanel = "alertAccountChooserPanel";
    private static final String WICKET_ID_alertContactPointPanel = "alertContactPointPanel";
    private static final String WICKET_ID_accountBalanceThresholdAlertForm = "accountBalanceThresholdAlertForm";
    private static final String WICKET_ID_minMaxAmountDiv = "minMaxAmountDiv";

    private static String KEY_SUCCESS_MESSAGE = "manageAlerts.alertActionEditAlert.message";

    private AlertAccountChooserBean alertAccountChooserBean;
    private MobileAlertsBean mobileAlertsBean;
    private AlertFrequencyChooserBean alertFrequencyChooserBean;
    private List<CustomerContactPoint> selectedContactPointList;
    private List<CustomerBlackoutTime> customerBlackoutTimelist;
    private Form<EditAccountBalanceThresholdAlertPanel> form;

    private AlertAccountChooserPanel alertAccountChooserPanel;
    private AlertContactPointChooserPanel alertContactPointPanel;
    private AlertFrequencyChooserPanel alertFrequencyChooserPanel;

    protected MobiliserBasePage basePage;
    protected Class backPage;
    protected Class editPage;
    protected AlertsClientLogic clientLogic;
    protected long customerId;
    protected AlertType alertType;
    private CustomerAlert customerAlert = null;

    public EditAccountBalanceThresholdAlertPanel(String id,
	    MobiliserBasePage mobBasePage, AlertsClientLogic clientLogic,
	    long customerId, AlertType alertType, CustomerAlert customerAlert,
	    Class backPage, Class editPage) {

	super(id);

	this.basePage = mobBasePage;
	this.backPage = backPage;
	this.editPage = editPage;
	this.clientLogic = clientLogic;
	this.customerId = customerId;
	this.alertType = alertType;
	this.customerAlert = customerAlert;

	this.constructPanel();
    }

    @SuppressWarnings( { "unchecked", "rawtypes" })
    private void constructPanel() {

	this.selectedContactPointList = AlertsHelper
		.getAlertContactPoints(customerAlert);
	this.alertAccountChooserBean = new AlertAccountChooserBean();
	this.alertFrequencyChooserBean = new AlertFrequencyChooserBean();
	this.mobileAlertsBean = new MobileAlertsBean();
	this.customerBlackoutTimelist = new ArrayList<CustomerBlackoutTime>();

	populateCustomerAlertData();

	form = new Form(
		WICKET_ID_accountBalanceThresholdAlertForm,
		new CompoundPropertyModel<EditAccountBalanceThresholdAlertPanel>(
			this));

	alertAccountChooserPanel = new AlertAccountChooserPanel(
		WICKET_ID_alertAccountChooserPanel, customerId, basePage,
		alertAccountChooserBean);

	alertContactPointPanel = new AlertContactPointChooserPanel(
		WICKET_ID_alertContactPointPanel, customerId, basePage,
		selectedContactPointList);

	alertFrequencyChooserPanel = new AlertFrequencyChooserPanel(
		"alertFrequencyChooserPanel", alertFrequencyChooserBean,
		basePage);

	WebMarkupContainer minMaxAmountDiv = new WebMarkupContainer(
		WICKET_ID_minMaxAmountDiv);
	minMaxAmountDiv
		.add(new TextField<String>("mobileAlertsBean.minAmount").add(
			new AmountValidator(basePage,
				Constants.REGEX_AMOUNT_16_2)).add(
			Constants.amountSimpleAttributeModifier).add(
			new ErrorIndicator()));
	minMaxAmountDiv
		.add(new TextField<String>("mobileAlertsBean.maxAmount").add(
			new AmountValidator(basePage,
				Constants.REGEX_AMOUNT_16_2)).add(
			Constants.amountSimpleAttributeModifier).add(
			new ErrorIndicator()));
	minMaxAmountDiv.add(new Label("currencyMaxAmount", basePage
		.getCurrencySymbol()));
	minMaxAmountDiv.add(new Label("currencyMinAmount", basePage
		.getCurrencySymbol()));
	form.add(alertAccountChooserPanel);
	form.add(minMaxAmountDiv);
	form.add(alertFrequencyChooserPanel);
	form.add(alertContactPointPanel);
	form.add(new Button(WICKET_cancelAlertAction) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false));

	form.add(new Button(WICKET_ID_addAlertAction) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (validateAlertData(mobileAlertsBean)) {
		    handleEdit();
		}
	    }
	});

	add(form);

	AlertBlackOutPanel alertBlackOutPanel = new AlertBlackOutPanel(
		"alertBlackOutPanel", basePage, clientLogic, customerAlert);

	add(alertBlackOutPanel);
    }

    @SuppressWarnings("unchecked")
    protected void handleBack() {
	setResponsePage(backPage);
    }

    @SuppressWarnings("unchecked")
    protected void handleEdit() {
	if (!alertFrequencyChooserBean.isFrequencyNoLimitCheck()) {
	    customerAlert.setNotifMaxCnt(0);
	} else {
	    customerAlert.setNotifMaxCnt(Integer
		    .valueOf(alertFrequencyChooserBean.getMaxFrequency()));
	    customerAlert.setNotifMaxRecur(AlertsHelper
		    .convertDurationToStore(alertFrequencyChooserBean
			    .getDurationFrequency()));
	}
	customerAlert.setContactPointList(alertContactPointPanel
		.getContactPointList(customerAlert.getId().longValue()));
	updateCustomerAlertData();
	int statusCode = clientLogic.updateCustomerAlert(customerAlert);
	if (statusCode == 0) {
	    getSession().info(
		    getLocalizer().getString(KEY_SUCCESS_MESSAGE, this));
	    setResponsePage(editPage);
	}
    }

    protected void populateCustomerAlertData() {

	LOG
		.debug("#Inside populateCustomerAlertData EditAccountBalanceThresholdAlertPage");

	if (customerAlert != null) {
	    if (customerAlert.getNotifMaxCnt() != null) {
		alertFrequencyChooserBean.setFrequencyNoLimitCheck(false);
		if (customerAlert.getNotifMaxCnt() != 0) {
		    alertFrequencyChooserBean.setFrequencyNoLimitCheck(true);
		    alertFrequencyChooserBean.setDurationFrequency(AlertsHelper
			    .convertDurationFromStore(basePage, customerAlert
				    .getNotifMaxRecur()));
		    alertFrequencyChooserBean.setMaxFrequency(String
			    .valueOf(customerAlert.getNotifMaxCnt()));
		}
	    }

	    List<CustomerAlertData> listAlertData = customerAlert
		    .getAlertDataList().getAlertData();

	    for (CustomerAlertData customerAlertData : listAlertData) {
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_PI_ID)) {
		    alertAccountChooserBean
			    .setPaymentInstrumentId(customerAlertData
				    .getValue());
		}
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_MIN_AMOUNT)) {
		    mobileAlertsBean.setMinAmount(AlertsHelper
			    .convertAmountFromStore(customerAlertData
				    .getValue(), basePage));
		}
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_MAX_AMOUNT)) {
		    mobileAlertsBean.setMaxAmount(AlertsHelper
			    .convertAmountFromStore(customerAlertData
				    .getValue(), basePage));
		}
	    }
	}
    }

    protected void updateCustomerAlertData() {
	if (customerAlert != null) {
	    List<CustomerAlertData> listCustomerAlertData = customerAlert
		    .getAlertDataList().getAlertData();

	    for (CustomerAlertData customerAlertData : listCustomerAlertData) {
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_PI_ID)) {
		    customerAlertData.setValue(alertAccountChooserBean
			    .getPaymentInstrumentId());
		}
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_MIN_AMOUNT)) {
		    customerAlertData
			    .setValue(AlertsHelper.convertAmountToStore(
				    mobileAlertsBean.getMinAmount(), basePage,
				    mobileAlertsBean));
		}
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_MAX_AMOUNT)) {
		    customerAlertData
			    .setValue(AlertsHelper.convertAmountToStore(
				    mobileAlertsBean.getMaxAmount(), basePage,
				    mobileAlertsBean));
		}

	    }
	}
    }

    protected boolean validateAlertData(MobileAlertsBean mobileAlertsBean) {
	if (mobileAlertsBean == null
		|| (mobileAlertsBean.getMaxAmount() == null && mobileAlertsBean
			.getMinAmount() == null)) {
	    error(getLocalizer().getString("minOrMax.amount.required.error",
		    this));
	    return false;
	}
	if (mobileAlertsBean.getMaxAmount() != null
		&& mobileAlertsBean.getMinAmount() != null) {
	    try {
		Float maxAmount = Float
			.valueOf(mobileAlertsBean.getMaxAmount());
		Float minAmount = Float
			.valueOf(mobileAlertsBean.getMinAmount());

		if (maxAmount < minAmount) {
		    error(getLocalizer().getString("amount.too.small.error",
			    this));
		    return false;
		}
	    } catch (NumberFormatException nfe) {
		return false;
	    }
	}

	return true;
    }

    public void setAlertAccountChooserBean(
	    AlertAccountChooserBean alertAccountChooserBean) {
	this.alertAccountChooserBean = alertAccountChooserBean;
    }

    public AlertAccountChooserBean getAlertAccountChooserBean() {
	return alertAccountChooserBean;
    }

    public void setMobileAlertsBean(MobileAlertsBean mobileAlertsBean) {
	this.mobileAlertsBean = mobileAlertsBean;
    }

    public MobileAlertsBean getMobileAlertsBean() {
	return mobileAlertsBean;
    }

    public void setCustomerBlackoutTimelist(
	    List<CustomerBlackoutTime> customerBlackoutTimelist) {
	this.customerBlackoutTimelist = customerBlackoutTimelist;
    }

    public List<CustomerBlackoutTime> getCustomerBlackoutTimelist() {
	return customerBlackoutTimelist;
    }

    public void setAlertFrequencyChooserBean(
	    AlertFrequencyChooserBean alertFrequencyChooserBean) {
	this.alertFrequencyChooserBean = alertFrequencyChooserBean;
    }

    public AlertFrequencyChooserBean getAlertFrequencyChooserBean() {
	return alertFrequencyChooserBean;
    }
}