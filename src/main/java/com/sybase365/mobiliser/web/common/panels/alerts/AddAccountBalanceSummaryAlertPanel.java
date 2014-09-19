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
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlertDataList;
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
public class AddAccountBalanceSummaryAlertPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(AddAccountBalanceSummaryAlertPanel.class);

    private static final String WICKET_cancelAlertAction = "cancelAlertAction";
    private static final String WICKET_ID_addAlertAction = "addAlertAction";
    private static final String WICKET_ID_alertAccountChooserPanel = "alertAccountChooserPanel";
    private static final String WICKET_ID_alertContactPointPanel = "alertContactPointPanel";
    private static final String WICKET_ID_accountBalanceSummaryAlertForm = "accountBalanceSummaryAlertForm";

    private static String KEY_SUCCESS_MESSAGE = "manageAlerts.alertActionAddAlert.message";

    private AlertAccountChooserBean alertAccountChooserBean;
    private List<CustomerContactPoint> selectedContactPointList;
    private Form<AddAccountBalanceSummaryAlertPanel> form;
    private CustomerAlert customerAlert = null;

    private AlertContactPointChooserPanel alertContactPointPanel;
    private AlertAccountChooserPanel alertAccountChooserPanel;

    protected MobiliserBasePage basePage;
    protected Class cancelPage;
    protected Class addPage;
    protected AlertsClientLogic clientLogic;
    protected long customerId;
    protected AlertType alertType;

    public AddAccountBalanceSummaryAlertPanel(String id,
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

	this.form = new Form(WICKET_ID_accountBalanceSummaryAlertForm,
		new CompoundPropertyModel<AddAccountBalanceSummaryAlertPanel>(
			this));

	this.alertAccountChooserPanel = new AlertAccountChooserPanel(
		WICKET_ID_alertAccountChooserPanel, customerId, basePage,
		alertAccountChooserBean);

	this.alertContactPointPanel = new AlertContactPointChooserPanel(
		WICKET_ID_alertContactPointPanel, customerId, basePage,
		selectedContactPointList);

	form.add(alertAccountChooserPanel);
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
		handleAdd();
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
	return custDataList;
    }

    public void setAlertAccountChooserBean(
	    AlertAccountChooserBean alertAccountChooserBean) {
	this.alertAccountChooserBean = alertAccountChooserBean;
    }

    public AlertAccountChooserBean getAlertAccountChooserBean() {
	return alertAccountChooserBean;
    }
}