package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;


/**
 * This Page is for adding a Password Changed Alert
 * 
 * @author msw
 */
public class AddPasswordChangedAlertPage extends AddGenericAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddPasswordChangedAlertPage.class);

    private static long ALERT_TYPE_ID = 8L;

    public AddPasswordChangedAlertPage() {
	super(ALERT_TYPE_ID);
    }

    @Override
    protected long getAlertTypeId() {
	return ALERT_TYPE_ID;
    }

}