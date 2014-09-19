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
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutTime;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerContactPoint;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.AlertsHelper;

/**
 * 
 * @author msw
 */
public class EditGenericAlertPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(EditGenericAlertPanel.class);

    private static final String WICKET_cancelAlertAction = "cancelAlertAction";
    private static final String WICKET_ID_editAlertAction = "editAlertAction";
    private static final String WICKET_ID_alertContactPointPanel = "alertContactPointPanel";
    private static final String WICKET_ID_alertBlackOutPanel = "alertBlackOutPanel";
    private static final String WICKET_ID_alertForm = "editAlertForm";

    private static String KEY_SUCCESS_MESSAGE = "manageAlerts.alertActionEditAlert.message";

    private List<CustomerContactPoint> selectedContactPointList;
    private List<CustomerBlackoutTime> customerBlackoutTimelist;
    private Form<?> form;

    private AlertContactPointChooserPanel alertContactPointPanel;
    private AlertBlackOutPanel alertBlackOutPanel;

    protected MobiliserBasePage basePage;
    protected Class backPage;
    protected Class editPage;
    protected AlertsClientLogic clientLogic;
    protected long customerId;
    protected AlertType alertType;
    private CustomerAlert customerAlert = null;

    public EditGenericAlertPanel(String id, MobiliserBasePage mobBasePage,
	    AlertsClientLogic clientLogic, long customerId,
	    AlertType alertType, CustomerAlert customerAlert, Class backPage,
	    Class editPage) {

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

	form = new Form<EditGenericAlertPanel>(WICKET_ID_alertForm,
		new CompoundPropertyModel<EditGenericAlertPanel>(this));

	this.selectedContactPointList = AlertsHelper
		.getAlertContactPoints(customerAlert);
	this.customerBlackoutTimelist = new ArrayList<CustomerBlackoutTime>();

	alertContactPointPanel = new AlertContactPointChooserPanel(
		WICKET_ID_alertContactPointPanel, customerId, basePage,
		selectedContactPointList);

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

	customerAlert.setContactPointList(alertContactPointPanel
		.getContactPointList(customerAlert.getId().longValue()));

	int statusCode = clientLogic.updateCustomerAlert(customerAlert);

	if (statusCode == 0) {
	    getSession().info(
		    getLocalizer().getString(KEY_SUCCESS_MESSAGE, this));
	    setResponsePage(editPage);
	}

    }

    public void setCustomerBlackoutTimelist(
	    List<CustomerBlackoutTime> customerBlackoutTimelist) {
	this.customerBlackoutTimelist = customerBlackoutTimelist;
    }

    public List<CustomerBlackoutTime> getCustomerBlackoutTimelist() {
	return customerBlackoutTimelist;
    }
}