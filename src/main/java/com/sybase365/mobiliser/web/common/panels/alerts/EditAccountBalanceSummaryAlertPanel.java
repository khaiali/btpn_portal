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
import com.sybase365.mobiliser.web.util.AlertsHelper;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author msw
 */
public class EditAccountBalanceSummaryAlertPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(EditAccountBalanceSummaryAlertPanel.class);

    private static final String WICKET_cancelAlertAction = "cancelAlertAction";
    private static final String WICKET_ID_addAlertAction = "addAlertAction";
    private static final String WICKET_ID_alertAccountChooserPanel = "alertAccountChooserPanel";
    private static final String WICKET_ID_alertContactPointPanel = "alertContactPointPanel";
    private static final String WICKET_ID_alertBlackOutPanel = "alertBlackOutPanel";
    private static final String WICKET_ID_accountBalanceSummaryAlertForm = "accountBalanceSummaryAlertForm";

    private static String KEY_SUCCESS_MESSAGE = "manageAlerts.alertActionEditAlert.message";

    private AlertAccountChooserBean alertAccountChooserBean;
    private List<CustomerContactPoint> selectedContactPointList;
    private List<CustomerBlackoutTime> customerBlackoutTimelist;
    private Form<EditAccountBalanceSummaryAlertPanel> form;

    private AlertAccountChooserPanel alertAccountChooserPanel;
    private AlertContactPointChooserPanel alertContactPointPanel;
    private AlertBlackOutPanel alertBlackOutPanel;

    protected MobiliserBasePage basePage;
    protected Class backPage;
    protected Class editPage;
    protected AlertsClientLogic clientLogic;
    protected long customerId;
    protected AlertType alertType;
    private CustomerAlert customerAlert = null;

    public EditAccountBalanceSummaryAlertPanel(String id,
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void constructPanel() {

	this.selectedContactPointList = AlertsHelper
		.getAlertContactPoints(customerAlert);
	this.alertAccountChooserBean = new AlertAccountChooserBean();
	this.customerBlackoutTimelist = new ArrayList<CustomerBlackoutTime>();

	populateCustomerAlertData();

	form = new Form(WICKET_ID_accountBalanceSummaryAlertForm,
		new CompoundPropertyModel<EditAccountBalanceSummaryAlertPanel>(
			this));

	alertAccountChooserPanel = new AlertAccountChooserPanel(
		WICKET_ID_alertAccountChooserPanel, customerId, basePage,
		alertAccountChooserBean);

	alertContactPointPanel = new AlertContactPointChooserPanel(
		WICKET_ID_alertContactPointPanel, customerId, basePage,
		selectedContactPointList);

	form.add(alertAccountChooserPanel);
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
		handleEdit();
	    }
	});

	add(form);

	alertBlackOutPanel = new AlertBlackOutPanel(
		WICKET_ID_alertBlackOutPanel, basePage, clientLogic,
		customerAlert);

	add(alertBlackOutPanel);
    }

    @SuppressWarnings("unchecked")
    protected void handleBack() {
	setResponsePage(backPage);
    }

    @SuppressWarnings("unchecked")
    protected void handleEdit() {
	updateCustomerAlertData();
	customerAlert.setContactPointList(alertContactPointPanel
		.getContactPointList(customerAlert.getId().longValue()));
	int statusCode = clientLogic.updateCustomerAlert(customerAlert);
	if (statusCode == 0) {
	    getSession().info(
		    getLocalizer().getString(KEY_SUCCESS_MESSAGE, this));
	    setResponsePage(editPage);
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
		    break;
		}
	    }
	}
    }

    protected void populateCustomerAlertData() {
	if (customerAlert != null) {
	    List<CustomerAlertData> listAlertData = customerAlert
		    .getAlertDataList().getAlertData();
	    for (CustomerAlertData customerAlertData : listAlertData) {
		if (customerAlertData.getKey().equalsIgnoreCase(
			Constants.ALERT_DATA_KEY_PI_ID)) {
		    alertAccountChooserBean
			    .setPaymentInstrumentId(customerAlertData
				    .getValue());
		    break;
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

    public void setCustomerBlackoutTimelist(
	    List<CustomerBlackoutTime> customerBlackoutTimelist) {
	this.customerBlackoutTimelist = customerBlackoutTimelist;
    }

    public List<CustomerBlackoutTime> getCustomerBlackoutTimelist() {
	return customerBlackoutTimelist;
    }
}