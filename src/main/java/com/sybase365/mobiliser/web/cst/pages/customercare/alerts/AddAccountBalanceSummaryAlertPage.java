package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import com.sybase365.mobiliser.web.common.panels.alerts.AddAccountBalanceSummaryAlertPanel;

/**
 * This Page is for adding an AccountBalanceSummary Alert
 * 
 * @author sushil.agrawala
 */
public class AddAccountBalanceSummaryAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddAccountBalanceSummaryAlertPage.class);

    private static long ALERT_TYPE_ID = 1L;

    @SuppressWarnings("unchecked")
    public AddAccountBalanceSummaryAlertPage() {

	super(ALERT_TYPE_ID, ACTION_ADD);

	add(new AddAccountBalanceSummaryAlertPanel("addAlertPanel", this,
		clientLogic, getMobiliserWebSession().getCustomer().getId(),
		alertType, MobileAlertsOptionsPage.class,
		MobileAlertsPage.class));
    }

}