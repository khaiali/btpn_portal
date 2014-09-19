package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;


/**
 * This Page is for adding a Bank Inquiry Response Alert
 * 
 * @author msw
 */
public class AddBankInquiryResponseAlertPage extends AddGenericAlertPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddBankInquiryResponseAlertPage.class);

    private static long ALERT_TYPE_ID = 3L;

    public AddBankInquiryResponseAlertPage() {
	super(ALERT_TYPE_ID);
    }

    @Override
    protected long getAlertTypeId() {
	return ALERT_TYPE_ID;
    }
}