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
public class PayOpenBillPage extends BaseBillPaymentPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(PayOpenBillPage.class);

    InvoiceConfiguration invoiceConfiguration;
    String invoiceTypeName;
    Invoice invoice;

    public PayOpenBillPage(InvoiceConfiguration invoiceConfiguration,
	    String invoiceTypeName, Invoice invoice) {
	this.invoiceConfiguration = invoiceConfiguration;
	this.invoiceTypeName = invoiceTypeName;
	this.invoice = invoice;
	initPageComponents();
    }

    @Override
    protected Class getActiveMenu() {
	return OpenBillsPage.class;
    }

    protected void initPageComponents() {
	Long customerId = getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId();

	OpenBillPanel openBillPanel = new OpenBillPanel("payOpenBillPanel",
		customerId, PayOpenBillPage.this, invoiceConfiguration,
		invoiceTypeName, invoice, null, true, false) {

	    @Override
	    public MobiliserBasePage getBillPaymentListPage() {
		return new OpenBillsPage();
	    }

	    @Override
	    public MobiliserBasePage getConfirmPage(Invoice invoice,
		    TransactionBean txBean) {

		return new ConfirmBillPaymentPage(invoiceConfiguration,
			invoiceTypeName, invoice, txBean, false) {
		    @Override
		    protected Class getActiveMenu() {
			return OpenBillsPage.class;
		    }
		};

	    }

	    @Override
	    public MobiliserBasePage getStatusPage(Invoice invoice,
		    TransactionBean txBean) {
		return null;
	    }

	};

	add(openBillPanel);

    }
}
