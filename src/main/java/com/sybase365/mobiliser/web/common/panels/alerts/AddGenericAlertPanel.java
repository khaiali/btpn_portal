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
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerContactPoint;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.AlertsHelper;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author msw
 */
public class AddGenericAlertPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(AddGenericAlertPanel.class);

    private static final String WICKET_cancelAlertAction = "cancelAlertAction";
    private static final String WICKET_ID_addAlertAction = "addAlertAction";
    private static final String WICKET_ID_alertContactPointPanel = "alertContactPointPanel";
    private static final String WICKET_ID_alertForm = "addAlertForm";

    private static String KEY_SUCCESS_MESSAGE = "manageAlerts.alertActionAddAlert.message";

    private List<CustomerContactPoint> selectedContactPointList;
    private Form<?> form;
    private CustomerAlert customerAlert = null;

    private AlertContactPointChooserPanel alertContactPointPanel;

    protected MobiliserBasePage basePage;
    protected Class cancelPage;
    protected Class addPage;
    protected AlertsClientLogic clientLogic;
    protected long customerId;
    protected AlertType alertType;

    public AddGenericAlertPanel(String id, MobiliserBasePage mobBasePage,
	    AlertsClientLogic clientLogic, long customerId,
	    AlertType alertType, Class mobCancelPage, Class mobAddPage) {

	super(id);

	this.basePage = mobBasePage;
	this.cancelPage = mobCancelPage;
	this.addPage = mobAddPage;
	this.clientLogic = clientLogic;
	this.customerId = customerId;
	this.alertType = alertType;

	this.constructPanel();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void constructPanel() {

	form = new Form<AddGenericAlertPanel>(WICKET_ID_alertForm,
		new CompoundPropertyModel<AddGenericAlertPanel>(this));

	this.customerAlert = new CustomerAlert();
	this.selectedContactPointList = new ArrayList<CustomerContactPoint>();

	this.form = new Form(WICKET_ID_alertForm,
		new CompoundPropertyModel<AddGenericAlertPanel>(this));

	this.alertContactPointPanel = new AlertContactPointChooserPanel(
		WICKET_ID_alertContactPointPanel, customerId, basePage,
		selectedContactPointList);

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
	customerAlert.setAlertNotificationMsgId(AlertsHelper
		.getAlertNotificationMessageId(alertType,
			Constants.ALERT_NOTIF_MSG_TYPE_TEXT));
	customerAlert.setContactPointList(alertContactPointPanel
		.getContactPointList(alertType.getId().longValue()));

	Long customerAlertId = clientLogic.createCustomerAlert(customerAlert);

	if (customerAlertId != null) {
	    getSession().info(
		    getLocalizer().getString(KEY_SUCCESS_MESSAGE, this));
	    setResponsePage(addPage);
	}
    }
}