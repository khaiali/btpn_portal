package com.sybase365.mobiliser.web.consumer.pages.portal.billpayment;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.Invoice;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.panels.OpenBillListPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_BILL_PAYMENT)
public class OpenBillsPage extends BaseBillPaymentPage {
    private static final Logger LOG = LoggerFactory
	    .getLogger(BillConfigurationListPage.class);

    public OpenBillsPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public OpenBillsPage(final PageParameters parameters) {
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

	add(new OpenBillListPanel("openBillListPanel", OpenBillsPage.this,
		customerId) {

	    @Override
	    public MobiliserBasePage getPayBillPage(InvoiceConfiguration ic,
		    String itName, Invoice invoice) {
		return new PayOpenBillPage(ic, itName, invoice);
	    }

	    @Override
	    public MobiliserBasePage getCancelBillPage(InvoiceConfiguration ic,
		    String itName, Invoice invoice) {

		return new CancelOpenBillPage(ic, itName, invoice, false);
	    }

	});

    }
}
