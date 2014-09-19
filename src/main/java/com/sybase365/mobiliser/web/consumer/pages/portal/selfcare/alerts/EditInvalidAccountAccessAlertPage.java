package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.common.panels.alerts.EditInvalidAccountAccessAlertPanel;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * This Page is for editing an Invalid Account Access alert
 * 
 * @author sushil.agrawala
 */
@AuthorizeInstantiation(Constants.PRIV_SHOW_ALERT)
public class EditInvalidAccountAccessAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditInvalidAccountAccessAlertPage.class);

    @SuppressWarnings("unchecked")
    public EditInvalidAccountAccessAlertPage(final CustomerAlert customerAlert) {

	super(customerAlert.getAlertTypeId(), ACTION_EDIT);

	add(new EditInvalidAccountAccessAlertPanel("editAlertPanel", this,
		clientLogic, getMobiliserWebSession().getLoggedInCustomer()
			.getCustomerId(), alertType, customerAlert,
		MobileAlertsPage.class, MobileAlertsPage.class));
    }
}