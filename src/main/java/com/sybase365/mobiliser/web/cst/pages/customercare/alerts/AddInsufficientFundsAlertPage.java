package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import com.sybase365.mobiliser.web.common.panels.alerts.AddInsufficientFundsAlertPanel;

/**
 * This Page is for adding an Insufficient Funds alert
 * 
 * @author sushil.agrawala
 */
public class AddInsufficientFundsAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddInsufficientFundsAlertPage.class);

    private static long ALERT_TYPE_ID = 5L;

    @SuppressWarnings("unchecked")
    public AddInsufficientFundsAlertPage() {

	super(ALERT_TYPE_ID, ACTION_ADD);

	add(new AddInsufficientFundsAlertPanel("addAlertPanel", this,
		clientLogic, getMobiliserWebSession().getCustomer().getId(),
		alertType, MobileAlertsOptionsPage.class,
		MobileAlertsPage.class));
	;
    }
}