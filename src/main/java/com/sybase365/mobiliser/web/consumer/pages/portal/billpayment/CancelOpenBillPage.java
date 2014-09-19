package com.sybase365.mobiliser.web.consumer.pages.portal.billpayment;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.Invoice;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.common.panels.OpenBillPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_BILL_PAYMENT)
public class CancelOpenBillPage extends BaseBillPaymentPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(PayOpenBillPage.class);

    InvoiceConfiguration invoiceConfiguration;
    String invoiceTypeName;
    Invoice invoice;
    boolean isOnDemand;

    public CancelOpenBillPage(InvoiceConfiguration invoiceConfiguration,
	    String invoiceTypeName, Invoice invoice, boolean isOnDemand) {
	this.invoiceConfiguration = invoiceConfiguration;
	this.invoiceTypeName = invoiceTypeName;
	this.invoice = invoice;
	this.isOnDemand = isOnDemand;
	initPageComponents();
    }

    @Override
    protected Class getActiveMenu() {
	if (isOnDemand)
	    return PayBillPage.class;
	else
	    return OpenBillsPage.class;
    }

    protected void initPageComponents() {
	Long customerId = getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId();

	add(new OpenBillPanel("cancelOpenBillPanel", customerId,
		CancelOpenBillPage.this, invoiceConfiguration, invoiceTypeName,
		invoice, null, false, false) {

	    @Override
	    public MobiliserBasePage getBillPaymentListPage() {
		if (isOnDemand)
		    return new PayBillPage();
		else
		    return new OpenBillsPage();
	    }

	    @Override
	    public MobiliserBasePage getConfirmPage(Invoice invoice,
		    TransactionBean txBean) {
		return null;
	    }

	    @Override
	    public MobiliserBasePage getStatusPage(Invoice invoice,
		    TransactionBean txBean) {
		return null;
	    }

	});

    }
}