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
import com.sybase365.mobiliser.web.beans.AlertFrequencyChooserBean;
import com.sybase365.mobiliser.web.beans.SelectBean;
import com.sybase365.mobiliser.web.util.AlertsHelper;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author msw
 */
public class AddInsufficientFundsAlertPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(AddInsufficientFundsAlertPanel.class);

    private static final String WICKET_cancelAlertAction = "cancelAlertAction";
    private static final String WICKET_ID_addAlertAction = "addAlertAction";
    private static final String WICKET_ID_alertAccountChooserPanel = "alertAccountChooserPanel";
    private static final String WICKET_ID_alertContactPointPanel = "alertContactPointPanel";
    private static final String WICKET_ID_insufficientFundsAlertForm = "insufficientFundsAlertForm";

    private static String KEY_SUCCESS_MESSAGE = "manageAlerts.alertActionAddAlert.message";

    private AlertAccountChooserBean alertAccountChooserBean;
    private SelectBean selectedMessageTypeBean;
    private AlertFrequencyChooserBean alertFrequencyChooserBean;
    private List<CustomerContactPoint> selectedContactPointList;
    private Form<AddInsufficientFundsAlertPanel> form;
    private CustomerAlert customerAlert = null;

    private AlertAccountChooserPanel alertAccountChooserPanel;
    private AlertContactPointChooserPanel alertContactPointPanel;
    private AlertFrequencyChooserPanel alertFrequencyChooserPanel;
    private AlertMessageTypeChooserPanel alertMessageChooserPanel;

    protected MobiliserBasePage basePage;
    protected Class cancelPage;
    protected Class addPage;
    protected AlertsClientLogic clientLogic;
    protected long customerId;
    protected AlertType alertType;

    public AddInsufficientFundsAlertPanel(String id,
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
	this.selectedMessageTypeBean = new SelectBean();

	this.form = new Form(WICKET_ID_insufficientFundsAlertForm,
		new CompoundPropertyModel<AddInsufficientFundsAlertPanel>(this));

	this.alertAccountChooserPanel = new AlertAccountChooserPanel(
		WICKET_ID_alertAccountChooserPanel, customerId, basePage,
		alertAccountChooserBean);

	this.alertContactPointPanel = new AlertContactPointChooserPanel(
		WICKET_ID_alertContactPointPanel, customerId, basePage,
		selectedContactPointList);

	this.alertFrequencyChooserPanel = new AlertFrequencyChooserPanel(
		"alertFrequencyChooserPanel", alertFrequencyChooserBean,
		basePage);

	this.alertMessageChooserPanel = new AlertMessageTypeChooserPanel(
		"alertMessageChooserPanel", selectedMessageTypeBean);

	form.add(alertAccountChooserPanel);
	form.add(alertMessageChooserPanel);
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
		.getAlertNotificationMessageId(alertType, Long.valueOf(
			alertMessageChooserPanel.getMessageTypeBean().getId())
			.longValue()));
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
	return custDataList;
    }

    public void setAlertAccountChooserBean(
	    AlertAccountChooserBean alertAccountChooserBean) {
	this.alertAccountChooserBean = alertAccountChooserBean;
    }

    public AlertAccountChooserBean getAlertAccountChooserBean() {
	return alertAccountChooserBean;
    }

    public void setAlertFrequencyChooserBean(
	    AlertFrequencyChooserBean alertFrequencyChooserBean) {
	this.alertFrequencyChooserBean = alertFrequencyChooserBean;
    }

    public AlertFrequencyChooserBean getAlertFrequencyChooserBean() {
	return alertFrequencyChooserBean;
    }
}