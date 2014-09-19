package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import com.sybase365.mobiliser.web.common.panels.alerts.AddGenericAlertPanel;

/**
 * This Page is for adding alerts that are configured with only a contact point
 * chooser panel.
 * 
 * @author sushil.agrawala
 */
public abstract class AddGenericAlertPage extends BaseAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddGenericAlertPage.class);

    protected abstract long getAlertTypeId();

    @SuppressWarnings("unchecked")
    public AddGenericAlertPage(final long alertTypeId) {

	super(alertTypeId, ACTION_ADD);

	add(new AddGenericAlertPanel("addAlertPanel", this, clientLogic,
		getMobiliserWebSession().getCustomer().getId(), alertType,
		MobileAlertsOptionsPage.class, MobileAlertsPage.class));
    }
}