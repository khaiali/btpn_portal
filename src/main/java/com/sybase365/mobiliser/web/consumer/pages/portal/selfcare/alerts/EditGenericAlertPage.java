package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.common.panels.alerts.EditGenericAlertPanel;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * This Page is for adding alerts that are configured with only a contact point
 * chooser panel.
 * 
 * @author sushil.agrawala
 */
@AuthorizeInstantiation(Constants.PRIV_CONSUMER_LOGIN)
public class EditGenericAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditGenericAlertPage.class);

    @SuppressWarnings("unchecked")
    public EditGenericAlertPage(final CustomerAlert customerAlert) {

	super(customerAlert.getAlertTypeId(), ACTION_EDIT);

	add(new EditGenericAlertPanel("editAlertPanel", this, clientLogic,
		getMobiliserWebSession().getLoggedInCustomer().getCustomerId(),
		alertType, customerAlert, MobileAlertsPage.class,
		MobileAlertsPage.class));
    }
}