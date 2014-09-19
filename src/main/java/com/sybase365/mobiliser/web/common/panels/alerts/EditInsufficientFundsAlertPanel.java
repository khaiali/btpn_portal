package com.sybase365.mobiliser.web.common.panels.alerts;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlertData;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutTime;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerContactPoint;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.AlertAccountChooserBean;
import com.sybase365.mobiliser.web.beans.AlertFrequencyChooserBean;
import com.sybase365.mobiliser.web.beans.MobileAlertsBean;
import com.sybase365.mobiliser.web.beans.SelectBean;
import com.sybase365.mobiliser.web.util.AlertsHelper;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author msw
 */
public class EditInsufficientFundsAlertPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(EditInsufficientFundsAlertPanel.class);

    private static final String WICKET_cancelAlertAction = "cancelAlertAction";
    private static final String WICKET_ID_editAlertAction = "editAlertAction";
    private static final String WICKET_ID_alertAccountChooserPanel = "alertAccountChooserPanel";
    private static final String WICKET_ID_alertContactPointPanel = "alertContactPointPanel";
    private static final String WICKET_ID_alertForm = "alertForm";
    private static final String WICKET_ID_alertFrequencyChooserPanel = "alertFrequencyChooserPanel";
    private static final String WICKET_ID_alertMessageChooserPanel = "alertMessageChooserPanel";

    private static String KEY_SUCCESS_MESSAGE = "manageAlerts.alertActionEditAlert.message";

    private AlertAccountChooserBean alertAccountChooserBean;
    private MobileAlertsBean mobileAlertsBean;
    private AlertFrequencyChooserBean alertFrequencyChooserBean;
    private SelectBean selectedMessageTypeBean;
    private List<CustomerContactPoint> selectedContactPointList;
    private List<CustomerBlackoutTime> customerBlackoutTimelist;
    private Form<EditInsufficientFundsAlertPanel> form;

    private AlertAccountChooserPanel alertAccountChooserPanel;
    private AlertContactPointChooserPanel alertContactPointPanel;
    private AlertFrequencyChooserPanel alertFrequencyChooserPanel;
    private AlertMessageTypeChooserPanel alertMessageChooserPanel;
    private AlertBlackOutPanel alertBlackOutPanel;

    protected MobiliserBasePage basePage;
    protected Class backPage;
    protected Class editPage;
    protected AlertsClientLogic clientLogic;
    protected long customerId;
    protected AlertType alertType;
    private CustomerAlert customerAlert = null;

    public EditInsufficientFundsAlertPanel(String id,
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
	this.customerBlackoutTimelist = new ArrayList<CustomerBlackoutTime>();

	selectedMessageTypeBean = new SelectBean();
	selectedMessageTypeBean.setId(String.valueOf(AlertsHelper
		.getAlertNotificationMessageTypeId(alertType, customerAlert
			.getAlertNotificationMsgId())));

	populateCustomerAlertData();

	form = new Form(
		WICKET_ID_alertForm,
		new CompoundPropertyModel<EditInsufficientFundsAlertPanel>(this));

	alertAccountChooserPanel = new AlertAccountChooserPanel(
		WICKET_ID_alertAccountChooserPanel, customerId, basePage,
		alertAccountChooserBean);

	alertContactPointPanel = new AlertContactPointChooserPanel(
		WICKET_ID_alertContactPointPanel, customerId, basePage,
		selectedContactPointList);

	alertFrequencyChooserPanel = new AlertFrequencyChooserPanel(
		WICKET_ID_alertFrequencyChooserPanel,
		alertFrequencyChooserBean, basePage);

	alertMessageChooserPanel = new AlertMessageTypeChooserPanel(
		WICKET_ID_alertMessageChooserPanel, selectedMessageTypeBean);

	form.add(alertAccountChooserPanel);
	form.add(alertMessageChooserPanel);
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
		handleEdit();
	    }
	});

	add(form);

	alertBlackOutPanel = new AlertBlackOutPanel("alertBlackOutPanel",
		basePage, clientLogic, customerAlert);

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
	customerAlert.setAlertNotificationMsgId(AlertsHelper
		.getAlertNotificationMessageId(alertType, Long.valueOf(
			alertMessageChooserPanel.getMessageTypeBean().getId())
			.longValue()));
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
		    alertFrequencyChooserBean.setDurationFrequency(AlertsHelper
			    .convertDurationFromStore(basePage, customerAlert
				    .getNotifMaxRecur()));
		    alertFrequencyChooserBean.setMaxFrequency(String
			    .valueOf(customerAlert.getNotifMaxCnt()));
		}
	    }

	    for (CustomerAlertData customerAlertData : listAlertData) {
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_PI_ID)) {
		    alertAccountChooserBean
			    .setPaymentInstrumentId(customerAlertData
				    .getValue());
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
	    }
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