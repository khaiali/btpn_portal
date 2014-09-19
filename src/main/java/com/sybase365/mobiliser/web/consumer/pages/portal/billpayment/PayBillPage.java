package com.sybase365.mobiliser.web.consumer.pages.portal.billpayment;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.panels.BillConfigurationListForPaymentPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_BILL_PAYMENT)
public class PayBillPage extends BaseBillPaymentPage {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(PayBillPage.class);

    public PayBillPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public PayBillPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected Class getActiveMenu() {
	return this.getClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Long customerId = getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId();

	add(new BillConfigurationListForPaymentPanel(
		"billConfigListForPaymentPanel", PayBillPage.this, customerId) {

	    @Override
	    public MobiliserBasePage getPayOnDemandPage(
		    InvoiceConfiguration entry, String invoiceTypeName) {
		return new MakePaymentPage(entry, invoiceTypeName);
	    }

	});

    }

}
