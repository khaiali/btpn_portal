package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.common.panels.alerts.EditAccountBalanceSummaryAlertPanel;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * This Page is for editing an Account Balance Summary Alert
 * 
 * @author sushil.agrawala
 */
@AuthorizeInstantiation(Constants.PRIV_SHOW_ALERT)
public class EditAccountBalanceSummaryAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(EditAccountBalanceSummaryAlertPage.class);

    @SuppressWarnings("unchecked")
    public EditAccountBalanceSummaryAlertPage(final CustomerAlert customerAlert) {

	super(customerAlert.getAlertTypeId(), ACTION_EDIT);

	add(new EditAccountBalanceSummaryAlertPanel("editAlertPanel", this,
		clientLogic, getMobiliserWebSession().getLoggedInCustomer()
			.getCustomerId(), alertType, customerAlert,
		MobileAlertsPage.class, MobileAlertsPage.class));
    }
}
