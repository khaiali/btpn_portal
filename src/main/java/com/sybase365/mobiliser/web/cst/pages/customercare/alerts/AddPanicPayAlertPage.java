package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import com.sybase365.mobiliser.web.common.panels.alerts.AddPanicPayAlertPanel;

/**
 * This Page is for adding a Panic Pay Alert
 * 
 * @author sushil.agrawala
 */
public class AddPanicPayAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddPanicPayAlertPage.class);

    private static long ALERT_TYPE_ID = 7L;

    @SuppressWarnings("unchecked")
    public AddPanicPayAlertPage() {

	super(ALERT_TYPE_ID, ACTION_ADD);

	add(new AddPanicPayAlertPanel("addAlertPanel", this, clientLogic,
		getMobiliserWebSession().getCustomer().getId(), alertType,
		MobileAlertsOptionsPage.class, MobileAlertsPage.class));
    }
}