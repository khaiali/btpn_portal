package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.alerts.AddInvalidAccountAccessAlertPanel;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * This Page is for adding an InvalidAccountAccess Alert
 * 
 * @author sushil.agrawala
 */
@AuthorizeInstantiation(Constants.PRIV_SHOW_ALERT)
public class AddInvalidAccountAccessAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddInvalidAccountAccessAlertPage.class);

    private static long ALERT_TYPE_ID = 6L;

    @SuppressWarnings("unchecked")
    public AddInvalidAccountAccessAlertPage() {

	super(ALERT_TYPE_ID, ACTION_ADD);

	add(new AddInvalidAccountAccessAlertPanel("addAlertPanel", this,
		clientLogic, getMobiliserWebSession().getLoggedInCustomer()
			.getCustomerId(), alertType,
		MobileAlertsOptionsPage.class, MobileAlertsPage.class));
    }
}