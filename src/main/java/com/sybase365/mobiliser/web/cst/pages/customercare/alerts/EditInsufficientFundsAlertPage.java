package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.common.panels.alerts.EditInsufficientFundsAlertPanel;

/**
 * This Page is for editing an Insufficient Funds alert
 * 
 * @author sushil.agrawala
 */
public class EditInsufficientFundsAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(EditInsufficientFundsAlertPage.class);

    @SuppressWarnings("unchecked")
    public EditInsufficientFundsAlertPage(final CustomerAlert customerAlert) {

	super(customerAlert.getAlertTypeId(), ACTION_EDIT);

	add(new EditInsufficientFundsAlertPanel("editAlertPanel", this,
		clientLogic, getMobiliserWebSession().getCustomer().getId(),
		alertType, customerAlert, MobileAlertsPage.class,
		MobileAlertsPage.class));
    }
}