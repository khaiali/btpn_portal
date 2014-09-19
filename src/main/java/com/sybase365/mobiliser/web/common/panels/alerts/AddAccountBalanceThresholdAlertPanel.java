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
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlertDataList;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerContactPoint;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.AlertAccountChooserBean;
import com.sybase365.mobiliser.web.beans.AlertContactPointBean;
import com.sybase365.mobiliser.web.beans.AlertFrequencyChooserBean;
import com.sybase365.mobiliser.web.beans.MobileAlertsBean;
import com.sybase365.mobiliser.web.beans.SelectBean;
import com.sybase365.mobiliser.web.util.AlertsHelper;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author msw
 */
public class AddAccountBalanceThresholdAlertPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(AddAccountBalanceThresholdAlertPanel.class);

    private static final String WICKET_cancelAlertAction = "cancelAlertAction";
    private static final String WICKET_ID_addAlertAction = "addAlertAction";
    private static final String WICKET_ID_alertAccountChooserPanel = "alertAccountChooserPanel";
    private static final String WICKET_ID_alertContactPointPanel = "alertContactPointPanel";
    private static final String WICKET_ID_accountBalanceThresholdAlertForm = "accountBalanceThresholdAlertForm";
    private static final String WICKET_ID_minMaxAmountDiv = "minMaxAmountDiv";

    private static String KEY_SUCCESS_MESSAGE = "manageAlerts.alertActionAddAlert.message";

    private AlertAccountChooserBean alertAccountChooserBean;
    private MobileAlertsBean mobileAlertsBean;
    private AlertFrequencyChooserBean alertFrequencyChooserBean;
    private List<CustomerContactPoint> selectedContactPointList;
    List<AlertContactPointBean> newContactPointList;
    private Form<AddAccountBalanceThresholdAlertPanel> form;
    private CustomerAlert customerAlert = null;

    private AlertContactPointChooserPanel alertContactPointPanel;
    private AlertAccountChooserPanel alertAccountChooserPanel;
    private AlertFrequencyChooserPanel alertFrequencyChooserPanel;

    protected MobiliserBasePage basePage;
    protected Class cancelPage;
    protected Class addPage;
    protected AlertsClientLogic clientLogic;
    protected long customerId;
    protected AlertType alertType;

    public AddAccountBalanceThresholdAlertPanel(String id,
	    MobiliserBasePage mobBasePage, AlertsClientLogic clientLogic,
	    long customerId, AlertType alertType, Class mobCancelPage,
	    Class mobAddPage) {

	super(id);

	this.basePage = mobBasePage;
	this.cancelPage = mobCancelPage;
	this.addPage = mobAddPage;
	this.clientLogic = clientLogic;
	this.customerId = customerId;
	this.alertType = alertType;

	this.constructPanel();
    }

    @SuppressWarnings( { "unchecked", "rawtypes" })
    private void constructPanel() {

	this.customerAlert = new CustomerAlert();
	this.selectedContactPointList = new ArrayList<CustomerContactPoint>();
	new SelectBean();
	this.alertFrequencyChooserBean = new AlertFrequencyChooserBean();

	this.form = new Form(
		WICKET_ID_accountBalanceThresholdAlertForm,
		new CompoundPropertyModel<AddAccountBalanceThresholdAlertPanel>(
			this));

	this.alertAccountChooserPanel = new AlertAccountChooserPanel(
		WICKET_ID_alertAccountChooserPanel, customerId, basePage,
		alertAccountChooserBean);

	this.alertContactPointPanel = new AlertContactPointChooserPanel(
		WICKET_ID_alertContactPointPanel, customerId, basePage,
		selectedContactPointList);

	this.alertFrequencyChooserPanel = new AlertFrequencyChooserPanel(
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
	add(form);

	form.add(new Button(WICKET_cancelAlertAction) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleCancel();
	    };
	}.setDefaultFormProcessing(false));

	form.add(new Button(WICKET_ID_addAlertAction) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		boolean validateTransactionData = validateAlertData(mobileAlertsBean);
		if (validateTransactionData) {
		    handleAdd();
		}
	    }
	});

    }

    @SuppressWarnings("unchecked")
    protected void handleCancel() {
	setResponsePage(cancelPage);
    }

    @SuppressWarnings("unchecked")
    protected void handleAdd() {

	customerAlert.setCustomerId(customerId);
	customerAlert.setAlertTypeId(alertType.getId().longValue());
	customerAlert.setAlertDataList(populateCustomerAlertDataList());
	customerAlert.setAlertNotificationMsgId(AlertsHelper
		.getAlertNotificationMessageId(alertType,
			Constants.ALERT_NOTIF_MSG_TYPE_TEXT));

	customerAlert.setContactPointList(alertContactPointPanel
		.getContactPointList(alertType.getId().longValue()));
	if (!alertFrequencyChooserBean.isFrequencyNoLimitCheck()) {
	    customerAlert.setNotifMaxCnt(0);
	} else {
	    customerAlert.setNotifMaxCnt(Integer
		    .valueOf(alertFrequencyChooserBean.getMaxFrequency()));
	    customerAlert.setNotifMaxRecur(AlertsHelper
		    .convertDurationToStore(alertFrequencyChooserBean
			    .getDurationFrequency()));
	}
	Long alertId = clientLogic.createCustomerAlert(customerAlert);

	if (alertId != null) {
	    getSession().info(
		    getLocalizer().getString(KEY_SUCCESS_MESSAGE, this));
	    setResponsePage(addPage);
	}

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

    protected CustomerAlertDataList populateCustomerAlertDataList() {
	CustomerAlertDataList custDataList = new CustomerAlertDataList();
	CustomerAlertData piIdAlertData = new CustomerAlertData();
	piIdAlertData.setCustomerAlertId(alertType.getId());
	piIdAlertData.setKey(Constants.ALERT_DATA_KEY_PI_ID);
	piIdAlertData
		.setValue(alertAccountChooserBean.getPaymentInstrumentId());
	custDataList.getAlertData().add(piIdAlertData);

	CustomerAlertData minAmountAlertData = new CustomerAlertData();
	minAmountAlertData.setCustomerAlertId(alertType.getId());

	minAmountAlertData.setKey(Constants.ALERT_DATA_KEY_MIN_AMOUNT);
	minAmountAlertData.setValue(AlertsHelper.convertAmountToStore(
		mobileAlertsBean.getMinAmount(), basePage, mobileAlertsBean));
	custDataList.getAlertData().add(minAmountAlertData);

	CustomerAlertData maxAmountAlertData = new CustomerAlertData();
	maxAmountAlertData.setCustomerAlertId(alertType.getId());
	maxAmountAlertData.setKey(Constants.ALERT_DATA_KEY_MAX_AMOUNT);
	maxAmountAlertData.setValue(String.valueOf(AlertsHelper
		.convertAmountToStore(mobileAlertsBean.getMaxAmount(),
			basePage, mobileAlertsBean)));
	custDataList.getAlertData().add(maxAmountAlertData);

	return custDataList;
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

    public void setAlertFrequencyChooserBean(
	    AlertFrequencyChooserBean alertFrequencyChooserBean) {
	this.alertFrequencyChooserBean = alertFrequencyChooserBean;
    }

    public AlertFrequencyChooserBean getAlertFrequencyChooserBean() {
	return alertFrequencyChooserBean;
    }

}