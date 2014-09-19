package com.sybase365.mobiliser.web.common.panels.alerts;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
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
import com.sybase365.mobiliser.web.beans.AlertFrequencyChooserBean;
import com.sybase365.mobiliser.web.beans.MobileAlertsBean;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.AlertsHelper;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author msw
 */
public class AddTransactionSummaryAlertPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(AddTransactionSummaryAlertPanel.class);

    private static final String WICKET_cancelAlertAction = "cancelAlertAction";
    private static final String WICKET_ID_addAlertAction = "addAlertAction";
    private static final String WICKET_ID_alertAccountChooserPanel = "alertAccountChooserPanel";
    private static final String WICKET_ID_alertContactPointPanel = "alertContactPointPanel";
    private static final String WICKET_ID_addTransactionAlertAlertForm = "addTransactionAlertAlertForm";
    private static final String WICKET_ID_transactionTypeDiv = "transactionTypeDiv";

    private static String KEY_SUCCESS_MESSAGE = "manageAlerts.alertActionAddAlert.message";
    private static String ACTION_ADD = "addAlertType";

    private AlertAccountChooserBean alertAccountChooserBean;
    private MobileAlertsBean mobileAlertsBean;
    private AlertFrequencyChooserBean alertFrequencyChooserBean;
    private List<CustomerContactPoint> selectedContactPointList;
    private Form<AddTransactionSummaryAlertPanel> form;
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

    public AddTransactionSummaryAlertPanel(String id,
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
	this.alertFrequencyChooserBean = new AlertFrequencyChooserBean();

	this.form = new Form(
		WICKET_ID_addTransactionAlertAlertForm,
		new CompoundPropertyModel<AddTransactionSummaryAlertPanel>(this));

	this.alertAccountChooserPanel = new AlertAccountChooserPanel(
		WICKET_ID_alertAccountChooserPanel, customerId, basePage,
		alertAccountChooserBean);

	this.alertContactPointPanel = new AlertContactPointChooserPanel(
		WICKET_ID_alertContactPointPanel, customerId, basePage,
		selectedContactPointList);

	this.alertFrequencyChooserPanel = new AlertFrequencyChooserPanel(
		"alertFrequencyChooserPanel", alertFrequencyChooserBean,
		basePage);

	WebMarkupContainer transactionTypeDiv = new WebMarkupContainer(
		WICKET_ID_transactionTypeDiv);

	transactionTypeDiv.add(new LocalizableLookupDropDownChoice<String>(
		"mobileAlertsBean.transactionType", String.class, "usecases",
		this, false, true).setRequired(true).add(new ErrorIndicator()));

	final LocalizableLookupDropDownChoice<String> amountTypes = (LocalizableLookupDropDownChoice<String>) new LocalizableLookupDropDownChoice<String>(
		"mobileAlertsBean.logicOperator", String.class,
		"logicOperators", this, false, true).setRequired(true).add(
		new ErrorIndicator());

	transactionTypeDiv.add(amountTypes);

	final TextField<String> transactionAmount = new TextField<String>(
		"mobileAlertsBean.amount");
	transactionAmount.setRequired(true).add(
		new AmountValidator(basePage, Constants.REGEX_AMOUNT_16_2))
		.add(new ErrorIndicator()).add(
			Constants.amountSimpleAttributeModifier);
	transactionTypeDiv.add(transactionAmount);
	transactionTypeDiv.add(new Label("currency", basePage
		.getCurrencySymbol()));
	transactionAmount.setOutputMarkupId(true);
	transactionAmount.setOutputMarkupPlaceholderTag(true);

	amountTypes.add(new AjaxFormComponentUpdatingBehavior("onchange") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		if (amountTypes.getModelObject().equalsIgnoreCase(
			Constants.TRANSACTION_AMOUNT_TYPE_ANY)) {
		    transactionAmount.setRequired(false);
		} else {
		    transactionAmount.setRequired(true);
		}
		target.addComponent(transactionAmount);
	    }
	});
	form.add(alertAccountChooserPanel);
	form.add(transactionTypeDiv);
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

    protected CustomerAlertDataList populateCustomerAlertDataList() {
	CustomerAlertDataList custDataList = new CustomerAlertDataList();
	CustomerAlertData piIdAlertData = new CustomerAlertData();
	piIdAlertData.setCustomerAlertId(alertType.getId());
	piIdAlertData.setKey(Constants.ALERT_DATA_KEY_PI_ID);
	piIdAlertData
		.setValue(alertAccountChooserBean.getPaymentInstrumentId());
	custDataList.getAlertData().add(piIdAlertData);

	CustomerAlertData txnTypeAlertData = new CustomerAlertData();
	txnTypeAlertData.setCustomerAlertId(alertType.getId());
	txnTypeAlertData.setKey(Constants.ALERT_DATA_KEY_TXN_TYPE);
	txnTypeAlertData.setValue(mobileAlertsBean.getTransactionType());
	custDataList.getAlertData().add(txnTypeAlertData);

	CustomerAlertData logicalOperatorAlertData = new CustomerAlertData();
	logicalOperatorAlertData.setCustomerAlertId(alertType.getId());
	logicalOperatorAlertData
		.setKey(Constants.ALERT_DATA_KEY_LOGIC_OPERATOR);
	if (mobileAlertsBean.getLogicOperator() != null
		&& mobileAlertsBean.getLogicOperator().equalsIgnoreCase(
			Constants.LESS_THAN_OPERATOR_STRING)) {
	    logicalOperatorAlertData.setValue(Constants.LESS_THAN_OPERATOR);
	} else if (mobileAlertsBean.getLogicOperator() != null
		&& mobileAlertsBean.getLogicOperator().equalsIgnoreCase(
			Constants.MORE_THAN_OPERATOR_STRING)) {
	    logicalOperatorAlertData.setValue(Constants.MORE_THAN_OPERATOR);
	} else {
	    logicalOperatorAlertData.setValue(mobileAlertsBean
		    .getLogicOperator());
	}
	custDataList.getAlertData().add(logicalOperatorAlertData);

	CustomerAlertData txnAmountAlertData = new CustomerAlertData();
	txnAmountAlertData.setCustomerAlertId(alertType.getId());
	txnAmountAlertData.setKey(Constants.ALERT_DATA_KEY_TXN_AMOUNT);
	txnAmountAlertData.setValue(AlertsHelper.convertAmountToStore(
		mobileAlertsBean.getAmount(), basePage, mobileAlertsBean));
	custDataList.getAlertData().add(txnAmountAlertData);

	return custDataList;
    }

    protected boolean validateAlertData(MobileAlertsBean mobileAlertsBean) {

	if (mobileAlertsBean.getLogicOperator() != null
		&& mobileAlertsBean.getLogicOperator().equalsIgnoreCase("any")) {
	    if (mobileAlertsBean.getAmount() != null) {
		error(getLocalizer().getString(
			"anyAmount.TransactionType.amountValidation.error",
			this));
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

    public void setAlertFrequencyChooserBean(
	    AlertFrequencyChooserBean alertFrequencyChooserBean) {
	this.alertFrequencyChooserBean = alertFrequencyChooserBean;
    }

    public AlertFrequencyChooserBean getAlertFrequencyChooserBean() {
	return alertFrequencyChooserBean;
    }
}