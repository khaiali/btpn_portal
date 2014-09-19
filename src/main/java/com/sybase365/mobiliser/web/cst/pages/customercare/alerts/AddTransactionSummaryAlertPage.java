package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import com.sybase365.mobiliser.web.common.panels.alerts.AddTransactionSummaryAlertPanel;

/**
 * This Page is for adding a Transaction Summary Alert
 * 
 * @author sushil.agrawala
 */
public class AddTransactionSummaryAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddTransactionSummaryAlertPage.class);

    private static long ALERT_TYPE_ID = 9L;

    @SuppressWarnings("unchecked")
    public AddTransactionSummaryAlertPage() {

	super(ALERT_TYPE_ID, ACTION_ADD);

	add(new AddTransactionSummaryAlertPanel("addAlertPanel", this,
		clientLogic, getMobiliserWebSession().getCustomer().getId(),
		alertType, MobileAlertsOptionsPage.class,
		MobileAlertsPage.class));
    }
}