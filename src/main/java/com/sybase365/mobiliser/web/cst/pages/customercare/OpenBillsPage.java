package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.Invoice;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.panels.OpenBillListPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CUST_WRITE)
public class OpenBillsPage extends CustomerCareMenuGroup {
    private static final Logger LOG = LoggerFactory
	    .getLogger(OpenBillsPage.class);

    public OpenBillsPage() {
	super();
    }

    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Long customerId = getMobiliserWebSession().getCustomer().getId();

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
