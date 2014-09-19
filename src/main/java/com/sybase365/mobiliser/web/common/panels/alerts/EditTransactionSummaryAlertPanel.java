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
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.AlertsHelper;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author msw
 */
public class EditTransactionSummaryAlertPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditTransactionSummaryAlertPanel.class);

    private static final String WICKET_cancelAlertAction = "cancelAlertAction";
    private static final String WICKET_ID_editAlertAction = "editAlertAction";
    private static final String WICKET_ID_alertAccountChooserPanel = "alertAccountChooserPanel";
    private static final String WICKET_ID_alertContactPointPanel = "alertContactPointPanel";
    private static final String WICKET_ID_alertForm = "alertForm";
    private static final String WICKET_ID_transactionTypeDiv = "transactionTypeDiv";

    private static String KEY_SUCCESS_MESSAGE = "manageAlerts.alertActionEditAlert.message";

    private AlertAccountChooserBean alertAccountChooserBean;
    private MobileAlertsBean mobileAlertsBean;
    private AlertFrequencyChooserBean alertFrequencyChooserBean;
    private List<CustomerBlackoutTime> customerBlackoutTimelist;
    private List<CustomerContactPoint> selectedContactPointList;
    private Form<EditTransactionSummaryAlertPanel> form;

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

    public EditTransactionSummaryAlertPanel(String id,
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

	form = new Form(WICKET_ID_alertForm,
		new CompoundPropertyModel<EditTransactionSummaryAlertPanel>(
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

	WebMarkupContainer txnTypeDiv = new WebMarkupContainer(
		WICKET_ID_transactionTypeDiv);

	txnTypeDiv.add(new LocalizableLookupDropDownChoice<String>(
		"mobileAlertsBean.transactionType", String.class, "usecases",
		this, false, true).setRequired(true).add(new ErrorIndicator()));

	final LocalizableLookupDropDownChoice<String> amountTypes = (LocalizableLookupDropDownChoice<String>) new LocalizableLookupDropDownChoice<String>(
		"mobileAlertsBean.logicOperator", String.class,
		"logicOperators", this, false, true).setRequired(true).add(
		new ErrorIndicator());

	txnTypeDiv.add(amountTypes);
	final TextField<String> transactionAmount = new TextField<String>(
		"mobileAlertsBean.amount");
	transactionAmount.setRequired(true).add(
		new AmountValidator(basePage, Constants.REGEX_AMOUNT_16_2))
		.add(new ErrorIndicator()).add(
			Constants.amountSimpleAttributeModifier);
	txnTypeDiv.add(transactionAmount);
	txnTypeDiv.add(new Label("currency", basePage.getCurrencySymbol()));
	transactionAmount.setOutputMarkupId(true);
	transactionAmount.setOutputMarkupPlaceholderTag(true);

	if (Constants.TRANSACTION_AMOUNT_TYPE_ANY
		.equalsIgnoreCase(mobileAlertsBean.getLogicOperator())) {
	    transactionAmount.setRequired(false);
	}

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
	form.add(txnTypeDiv);
	form.add(alertFrequencyChooserPanel);
	form.add(alertContactPointPanel);
	form.add(new Button(WICKET_cancelAlertAction) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false));

	form.add(new Button(WICKET_ID_editAlertAction) {
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
	if (customerAlert != null) {
	    List<CustomerAlertData> listAlertData = customerAlert
		    .getAlertDataList().getAlertData();

	    if (customerAlert.getNotifMaxCnt() != null) {
		alertFrequencyChooserBean.setFrequencyNoLimitCheck(false);
		if (customerAlert.getNotifMaxCnt() != 0) {
		    alertFrequencyChooserBean.setFrequencyNoLimitCheck(true);
		    alertFrequencyChooserBean.setMaxFrequency(String
			    .valueOf(customerAlert.getNotifMaxCnt()));
		    alertFrequencyChooserBean.setDurationFrequency(AlertsHelper
			    .convertDurationFromStore(basePage, customerAlert
				    .getNotifMaxRecur()));
		}
	    }

	    for (CustomerAlertData customerAlertData : listAlertData) {
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_PI_ID)) {
		    alertAccountChooserBean
			    .setPaymentInstrumentId(customerAlertData
				    .getValue());
		}
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_TXN_TYPE)) {
		    mobileAlertsBean.setTransactionType(customerAlertData
			    .getValue());
		}
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_TXN_AMOUNT)) {
		    mobileAlertsBean.setAmount(AlertsHelper
			    .convertAmountFromStore(customerAlertData
				    .getValue(), basePage));
		}
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_LOGIC_OPERATOR)) {

		    if (Constants.LESS_THAN_OPERATOR
			    .equalsIgnoreCase(customerAlertData.getValue())) {
			mobileAlertsBean
				.setLogicOperator(Constants.LESS_THAN_OPERATOR_STRING);
		    } else if (Constants.MORE_THAN_OPERATOR
			    .equalsIgnoreCase(customerAlertData.getValue())) {
			mobileAlertsBean
				.setLogicOperator(Constants.MORE_THAN_OPERATOR_STRING);
		    } else {
			mobileAlertsBean.setLogicOperator(customerAlertData
				.getValue());
		    }
		}
	    }
	}
    }

    protected void updateCustomerAlertData() {
	if (customerAlert != null) {
	    List<CustomerAlertData> listCustomerAlertData = customerAlert
		    .getAlertDataList().getAlertData();
	    if (!alertFrequencyChooserBean.isFrequencyNoLimitCheck()) {
		customerAlert.setNotifMaxCnt(0);
	    } else {
		customerAlert.setNotifMaxCnt(Integer
			.valueOf(alertFrequencyChooserBean.getMaxFrequency()));
		customerAlert.setNotifMaxRecur(AlertsHelper
			.convertDurationToStore(alertFrequencyChooserBean
				.getDurationFrequency()));
	    }
	    for (CustomerAlertData customerAlertData : listCustomerAlertData) {
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_PI_ID)) {
		    customerAlertData.setValue(alertAccountChooserBean
			    .getPaymentInstrumentId());
		} else if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_TXN_TYPE)) {
		    customerAlertData.setValue(mobileAlertsBean
			    .getTransactionType());
		} else if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_TXN_AMOUNT)) {
		    customerAlertData.setValue(AlertsHelper
			    .convertAmountToStore(mobileAlertsBean.getAmount(),
				    basePage, mobileAlertsBean));
		} else if (mobileAlertsBean.getLogicOperator() != null) {
		    if (mobileAlertsBean.getLogicOperator().equalsIgnoreCase(
			    Constants.LESS_THAN_OPERATOR_STRING)) {
			customerAlertData
				.setValue(Constants.LESS_THAN_OPERATOR);
		    } else if (mobileAlertsBean.getLogicOperator()
			    .equalsIgnoreCase(
				    Constants.LESS_THAN_OPERATOR_STRING)) {
			customerAlertData
				.setValue(Constants.MORE_THAN_OPERATOR);
		    } else {
			customerAlertData.setValue(mobileAlertsBean
				.getLogicOperator());
		    }
		}
	    }
	}
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