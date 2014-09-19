package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.common.panels.alerts.EditGenericAlertPanel;

/**
 * This Page is for adding alerts that are configured with only a contact point
 * chooser panel.
 * 
 * @author sushil.agrawala
 */
public class EditGenericAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditGenericAlertPage.class);

    @SuppressWarnings("unchecked")
    public EditGenericAlertPage(final CustomerAlert customerAlert) {

	super(customerAlert.getAlertTypeId(), ACTION_EDIT);

	add(new EditGenericAlertPanel("editAlertPanel", this, clientLogic,
		getMobiliserWebSession().getCustomer().getId(), alertType,
		customerAlert, MobileAlertsPage.class, MobileAlertsPage.class));
    }
}