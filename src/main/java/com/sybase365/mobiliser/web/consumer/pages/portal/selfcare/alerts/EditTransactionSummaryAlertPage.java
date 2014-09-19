package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.common.panels.alerts.EditTransactionSummaryAlertPanel;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * This Page is for editing a Transaction Summary Alert
 * 
 * @author sushil.agrawala
 */
@AuthorizeInstantiation(Constants.PRIV_SHOW_ALERT)
public class EditTransactionSummaryAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditTransactionSummaryAlertPage.class);

    @SuppressWarnings("unchecked")
    public EditTransactionSummaryAlertPage(final CustomerAlert customerAlert) {

	super(customerAlert.getAlertTypeId(), ACTION_EDIT);

	add(new EditTransactionSummaryAlertPanel("editAlertPanel", this,
		clientLogic, getMobiliserWebSession().getLoggedInCustomer()
			.getCustomerId(), alertType, customerAlert,
		MobileAlertsPage.class, MobileAlertsPage.class));
    }
}