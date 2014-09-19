package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.util.Constants;

/**
 * This Page is for adding a Customer Lockout Alert
 * 
 * @author msw
 */
@AuthorizeInstantiation(Constants.PRIV_SHOW_ALERT)
public class AddCustomerLockoutAlertPage extends AddGenericAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddCustomerLockoutAlertPage.class);

    private static long ALERT_TYPE_ID = 4L;

    public AddCustomerLockoutAlertPage() {
	super(ALERT_TYPE_ID);
    }

    @Override
    protected long getAlertTypeId() {
	return ALERT_TYPE_ID;
    }
}