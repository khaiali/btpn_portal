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
import com.sybase365.mobiliser.web.beans.SelectBean;
import com.sybase365.mobiliser.web.util.AlertsHelper;

/**
 * 
 * @author msw
 */
public class AddInvalidAccountAccessAlertPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(AddInvalidAccountAccessAlertPanel.class);

    private static final String WICKET_cancelAlertAction = "cancelAlertAction";
    private static final String WICKET_ID_addAlertAction = "addAlertAction";
    private static final String WICKET_ID_alertContactPointPanel = "alertContactPointPanel";
    private static final String WICKET_ID_invalidAccountAccessAlertForm = "invalidAccountAccessAlertForm";

    private static String KEY_SUCCESS_MESSAGE = "manageAlerts.alertActionAddAlert.message";

    private SelectBean selectedMessageTypeBean;
    private List<CustomerContactPoint> selectedContactPointList;
    private Form<AddInvalidAccountAccessAlertPanel> form;
    private CustomerAlert customerAlert = null;

    private AlertContactPointChooserPanel alertContactPointPanel;
    private AlertMessageTypeChooserPanel alertMessageChooserPanel;

    protected MobiliserBasePage basePage;
    protected Class cancelPage;
    protected Class addPage;
    protected AlertsClientLogic clientLogic;
    protected long customerId;
    protected AlertType alertType;

    public AddInvalidAccountAccessAlertPanel(String id,
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void constructPanel() {

	this.customerAlert = new CustomerAlert();
	this.selectedContactPointList = new ArrayList<CustomerContactPoint>();
	this.selectedMessageTypeBean = new SelectBean();

	this.form = new Form(WICKET_ID_invalidAccountAccessAlertForm,
		new CompoundPropertyModel<AddInvalidAccountAccessAlertPanel>(
			this));

	this.alertContactPointPanel = new AlertContactPointChooserPanel(
		WICKET_ID_alertContactPointPanel, customerId, basePage,
		selectedContactPointList);

	this.alertMessageChooserPanel = new AlertMessageTypeChooserPanel(
		"alertMessageChooserPanel", selectedMessageTypeBean);
	form.add(alertMessageChooserPanel);
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
		.getAlertNotificationMessageId(
			alertType,
			Long.valueOf(
				alertMessageChooserPanel.getMessageTypeBean()
					.getId()).longValue()));
	customerAlert.setContactPointList(alertContactPointPanel
		.getContactPointList(alertType.getId().longValue()));

	Long alertId = clientLogic.createCustomerAlert(customerAlert);

	if (alertId != null) {
	    getSession().info(
		    getLocalizer().getString(KEY_SUCCESS_MESSAGE, this));
	    setResponsePage(addPage);
	}
    }
}