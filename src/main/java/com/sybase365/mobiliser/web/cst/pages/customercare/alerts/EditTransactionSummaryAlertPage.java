package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.common.panels.alerts.EditTransactionSummaryAlertPanel;

/**
 * This Page is for editing a Transaction Summary Alert
 * 
 * @author sushil.agrawala
 */
public class EditTransactionSummaryAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditTransactionSummaryAlertPage.class);

    @SuppressWarnings("unchecked")
    public EditTransactionSummaryAlertPage(final CustomerAlert customerAlert) {

	super(customerAlert.getAlertTypeId(), ACTION_EDIT);

	add(new EditTransactionSummaryAlertPanel("editAlertPanel", this,
		clientLogic, getMobiliserWebSession().getCustomer().getId(),
		alertType, customerAlert, MobileAlertsPage.class,
		MobileAlertsPage.class));
    }
}