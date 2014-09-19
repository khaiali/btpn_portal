package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.util.Constants;

/**
 * This Page is for adding an Unread Bank Message Alert
 * 
 * @author msw
 */
@AuthorizeInstantiation(Constants.PRIV_SHOW_MBANKING_ALERT)
public class AddUnreadBankMessageAlertPage extends AddGenericAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddUnreadBankMessageAlertPage.class);

    private static long ALERT_TYPE_ID = 10L;

    public AddUnreadBankMessageAlertPage() {
	super(ALERT_TYPE_ID);
    }

    @Override
    protected long getAlertTypeId() {
	return ALERT_TYPE_ID;
    }

}