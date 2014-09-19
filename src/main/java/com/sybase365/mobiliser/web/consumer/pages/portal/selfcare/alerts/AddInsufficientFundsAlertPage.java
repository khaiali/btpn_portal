package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.alerts.AddInsufficientFundsAlertPanel;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * This Page is for adding an Insufficient Funds alert
 * 
 * @author sushil.agrawala
 */
@AuthorizeInstantiation(Constants.PRIV_SHOW_MBANKING_ALERT)
public class AddInsufficientFundsAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddInsufficientFundsAlertPage.class);

    private static long ALERT_TYPE_ID = 5L;

    @SuppressWarnings("unchecked")
    public AddInsufficientFundsAlertPage() {

	super(ALERT_TYPE_ID, ACTION_ADD);

	add(new AddInsufficientFundsAlertPanel("addAlertPanel", this,
		clientLogic, getMobiliserWebSession().getLoggedInCustomer()
			.getCustomerId(), alertType,
		MobileAlertsOptionsPage.class, MobileAlertsPage.class));
    }
}