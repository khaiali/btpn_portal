package com.sybase365.mobiliser.web.common.panels.mbanking;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.ServicePackage;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.SupportedAlert;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.SupportedChannel;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.SupportedOperation;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.cst.pages.customercare.mbanking.CustomerServicePackagePage;
import com.sybase365.mobiliser.web.util.ServicePackageAlertFilter;

public class CustomerServicePackagePanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = LoggerFactory
	    .getLogger(CustomerServicePackagePanel.class);

    private MobiliserBasePage mobBasePage;
    private long customerId;
    private List<ServicePackage> bankServicePackages;
    private ServicePackage customerServicePackage;
    private String customerOrgUnitId;
    private String servicePackageId;
    private String oldServicePackageId;
    private MBankingClientLogic mBankingClientLogic;
    protected AlertsClientLogic alertsClientLogic;

    private KeyValueDropDownChoice<String, String> csServicePackage;
    private List<AlertType> customerAlertsTypes;
    private ListMultipleChoice<String> supportedAlerts;
    private ListMultipleChoice<String> supportedOperations;
    private ListMultipleChoice<String> supportedChannels;

    private List<String> csSupportedAlerts;
    private List<String> csSupportedOperations;
    private List<String> csSupportedChannels;

    private static final String WICKET_ID_csPackageDiv = "csPackageDiv";
    private static final String WICKET_ID_csPackageForm = "csPackageForm";
    private static final String WICKET_ID_csSupportedAlerts = "csSupportedAlerts";
    private static final String WICKET_ID_csSupportedOperations = "csSupportedOperations";
    private static final String WICKET_ID_csSupportedChannels = "csSupportedChannels";
    private static final String WICKET_ID_csConfirm = "csConfirm";

    public CustomerServicePackagePanel(String id,
	    MobiliserBasePage mobBasePage,
	    MBankingClientLogic mBankingClientLogic,
	    AlertsClientLogic alertsClientLogic,
	    ServicePackageAlertFilter alertFilter, long customerId,
	    String customerOrgUnitId) {
	super(id);
	this.mobBasePage = mobBasePage;
	this.mBankingClientLogic = mBankingClientLogic;
	this.alertsClientLogic = alertsClientLogic;
	this.customerId = customerId;
	this.customerOrgUnitId = customerOrgUnitId;
	this.constructPanel();
    }

    private void constructPanel() {
	WebMarkupContainer markupContainer = new WebMarkupContainer(
		WICKET_ID_csPackageDiv);

	final Form<CustomerServicePackagePanel> form = new Form<CustomerServicePackagePanel>(
		WICKET_ID_csPackageForm,
		new CompoundPropertyModel<CustomerServicePackagePanel>(this));
	form.add(new FeedbackPanel("errorMessages"));

	this.createCsServicePackageView(form);

	form.add(new Button(WICKET_ID_csConfirm) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		LOG.debug("Updating Service Package from {} to {}",
			oldServicePackageId, servicePackageId);
		int statusCode = -1;
		if (oldServicePackageId == null) {
		    statusCode = mBankingClientLogic.addCustomerServicePackage(
			    customerId, servicePackageId);
		} else {
		    statusCode = mBankingClientLogic
			    .updateCustomerServicePackage(customerId,
				    oldServicePackageId, servicePackageId);
		}
		if (statusCode == 0) {
		    getSession().info(
			    getLocalizer().getString(
				    "servicePackages.update.message", this));
		    customerAlertsTypes = alertsClientLogic.findAlertTypes();
		    filterAlerts(customerAlertsTypes, customerId);
		    List<CustomerAlert> customerAlerts = alertsClientLogic
			    .findCustomerAlertByCustomer(customerId);

		    ListIterator<CustomerAlert> listIterator = customerAlerts
			    .listIterator();
		    while (listIterator.hasNext()) {
			CustomerAlert customerAlert = listIterator.next();
			if (!alertTypesContains(customerAlert.getAlertTypeId())) {
			    alertsClientLogic.deleteCustomerAlert(customerAlert
				    .getId());
			}
		    }
		     setResponsePage(CustomerServicePackagePage.class);
		}
	    };
	});

	markupContainer.add(form);
	add(markupContainer);
    }

    private void createCsServicePackageView(
	    Form<CustomerServicePackagePanel> form) {

	this.bankServicePackages = mBankingClientLogic
		.getBankServicePackages(customerOrgUnitId);
	this.customerServicePackage = mBankingClientLogic
		.getCustomerServicePackage(customerId);
	this.servicePackageId = customerServicePackage != null ? customerServicePackage
		.getServicePackageId() : null;
	this.oldServicePackageId = servicePackageId;

	csServicePackage = (KeyValueDropDownChoice<String, String>) new KeyValueDropDownChoice<String, String>(
		"servicePackageId", getCsServicePackageList());

	csServicePackage.add(new AjaxFormComponentUpdatingBehavior("onchange") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		supportedAlerts.setChoices(getCsSupportedAlerts());
		supportedOperations.setChoices(getCsSupportedOperations());
		supportedChannels.setChoices(getCsSupportedChannels());
		target.addComponent(supportedAlerts);
		target.addComponent(supportedOperations);
		target.addComponent(supportedChannels);
	    }
	});

	form.add(csServicePackage);

	ArrayList<String> slelectedAlert = new ArrayList<String>();

	supportedAlerts = new ListMultipleChoice<String>(
		WICKET_ID_csSupportedAlerts, new Model<ArrayList<String>>(
			slelectedAlert), getCsSupportedAlerts());
	supportedAlerts.setOutputMarkupId(true);
	form.add(supportedAlerts);

	ArrayList<String> slelectedOperations = new ArrayList<String>();

	supportedOperations = new ListMultipleChoice<String>(
		WICKET_ID_csSupportedOperations, new Model<ArrayList<String>>(
			slelectedOperations), getCsSupportedOperations());

	form.add(supportedOperations);
	supportedOperations.setOutputMarkupId(true);

	ArrayList<String> slelectedChannels = new ArrayList<String>();

	supportedChannels = new ListMultipleChoice<String>(
		WICKET_ID_csSupportedChannels, new Model<ArrayList<String>>(
			slelectedChannels), getCsSupportedChannels());
	supportedChannels.setOutputMarkupId(true);
	form.add(supportedChannels);

    }

    private List<KeyValue<String, String>> getCsServicePackageList() {
	List<KeyValue<String, String>> result = new ArrayList<KeyValue<String, String>>();
	for (ServicePackage servicePackage : bankServicePackages) {
	    result.add(new KeyValue<String, String>(servicePackage
		    .getServicePackageId(), servicePackage.getName()));
	}
	return result;
    }

    private List<String> getCsSupportedAlerts() {
	this.csSupportedAlerts = new ArrayList<String>();
	for (ServicePackage servicePackage : bankServicePackages) {
	    if (servicePackageId != null
		    && servicePackageId.equals(servicePackage
			    .getServicePackageId())) {
		List<SupportedAlert> supportedAlertsList = servicePackage
			.getSupportedAlertList().getSupportedAlert();
		for (SupportedAlert supportedAlert : supportedAlertsList) {
		    csSupportedAlerts.add(getLocalizer().getString(
			    "servicePackage." + supportedAlert.getAlertName(),
			    this));
		}
	    }
	}
	return csSupportedAlerts;
    }

    private List<String> getCsSupportedOperations() {
	this.csSupportedOperations = new ArrayList<String>();
	for (ServicePackage servicePackage : bankServicePackages) {
	    if (servicePackageId != null
		    && servicePackageId.equals(servicePackage
			    .getServicePackageId())) {
		List<SupportedOperation> supportedOperationList = servicePackage
			.getSupportedOperationList().getSupportedOperation();
		for (SupportedOperation supportedOperation : supportedOperationList) {
		    csSupportedOperations.add(supportedOperation
			    .getOperationName());
		}
	    }
	}

	return csSupportedOperations;
    }

    private List<String> getCsSupportedChannels() {
	this.csSupportedChannels = new ArrayList<String>();
	for (ServicePackage servicePackage : bankServicePackages) {
	    if (servicePackageId != null
		    && servicePackageId.equals(servicePackage
			    .getServicePackageId())) {
		List<SupportedChannel> supportedChannelList = servicePackage
			.getSupportedChannelList().getSupportedChannel();
		for (SupportedChannel supportedChannel : supportedChannelList) {
		    csSupportedChannels.add(supportedChannel.getChannelName());
		}
	    }
	}
	return csSupportedChannels;
    }

    private boolean alertTypesContains(long alertTypeId) {
	if (customerAlertsTypes != null) {
	    for (AlertType alertType : customerAlertsTypes) {
		if (alertType.getId().longValue() == alertTypeId) {
		    return true;
		}
	    }
	}
	return false;
    }

    public void filterAlerts(List<AlertType> alertTypeList, long customerId) {

	LOG.debug("ServicePackageAlertFilter called for customer {}",
		Long.valueOf(customerId));

	if (alertTypeList != null) {

	    ServicePackage custSP = mBankingClientLogic
		    .getCustomerServicePackage(customerId);

	    if (custSP != null) {
		List<SupportedAlert> alertListForSP = custSP
			.getSupportedAlertList().getSupportedAlert();

		List<AlertType> alertTypeListCopy = new ArrayList<AlertType>(
			alertTypeList);

		for (AlertType alertType : alertTypeListCopy) {
		    if (!contains(alertListForSP, alertType.getName())) {
			LOG.debug(
				"No alert named {} in customer service package, remove from alert type list",
				alertType.getName());
			alertTypeList.remove(alertType);
		    }
		}
	    } else {
		alertTypeList
			.removeAll(new ArrayList<AlertType>(alertTypeList));
	    }
	}
    }

    private boolean contains(List<SupportedAlert> saList, String alertName) {
	if (saList != null) {
	    for (SupportedAlert sa : saList) {
		if (sa.getAlertName().equals(alertName)) {
		    return true;
		}
	    }
	}
	return false;
    }

}
