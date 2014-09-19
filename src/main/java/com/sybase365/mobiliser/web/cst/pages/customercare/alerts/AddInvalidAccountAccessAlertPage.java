package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import com.sybase365.mobiliser.web.common.panels.alerts.AddInvalidAccountAccessAlertPanel;

/**
 * This Page is for adding an InvalidAccountAccess Alert
 * 
 * @author sushil.agrawala
 */
public class AddInvalidAccountAccessAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddInvalidAccountAccessAlertPage.class);

    private static long ALERT_TYPE_ID = 6L;

    @SuppressWarnings("unchecked")
    public AddInvalidAccountAccessAlertPage() {

	super(ALERT_TYPE_ID, ACTION_ADD);

	add(new AddInvalidAccountAccessAlertPanel("addAlertPanel", this,
		clientLogic, getMobiliserWebSession().getCustomer().getId(),
		alertType, MobileAlertsOptionsPage.class,
		MobileAlertsPage.class));
	;
    }
}