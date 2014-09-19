package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.common.panels.alerts.EditPanicPayAlertPanel;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * This Page is for editing a Panic Pay Alert
 * 
 * @author sushil.agrawala
 */
@AuthorizeInstantiation(Constants.PRIV_SHOW_MBANKING_ALERT)
public class EditPanicPayAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(EditPanicPayAlertPage.class);

    @SuppressWarnings("unchecked")
    public EditPanicPayAlertPage(final CustomerAlert customerAlert) {

	super(customerAlert.getAlertTypeId(), ACTION_EDIT);

	add(new EditPanicPayAlertPanel("editAlertPanel", this, clientLogic,
		getMobiliserWebSession().getLoggedInCustomer().getCustomerId(),
		alertType, customerAlert, MobileAlertsPage.class,
		MobileAlertsPage.class));
    }
}