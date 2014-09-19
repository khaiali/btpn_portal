package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.common.panels.alerts.EditInvalidAccountAccessAlertPanel;

/**
 * This Page is for editing an Invalid Account Access alert
 * 
 * @author sushil.agrawala
 */
public class EditInvalidAccountAccessAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditInvalidAccountAccessAlertPage.class);

    @SuppressWarnings("unchecked")
    public EditInvalidAccountAccessAlertPage(final CustomerAlert customerAlert) {

	super(customerAlert.getAlertTypeId(), ACTION_EDIT);

	add(new EditInvalidAccountAccessAlertPanel("editAlertPanel", this,
		clientLogic, getMobiliserWebSession().getCustomer().getId(),
		alertType, customerAlert, MobileAlertsPage.class,
		MobileAlertsPage.class));
    }
}