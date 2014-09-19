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
public class MakePaymentPage extends BaseBillPaymentPage {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(MakePaymentPage.class);

    InvoiceConfiguration invoiceConfiguration;
    String invoiceTypeName;

    public MakePaymentPage(InvoiceConfiguration invoiceConfiguration,
	    String invoiceTypeName) {
	this.invoiceConfiguration = invoiceConfiguration;
	this.invoiceTypeName = invoiceTypeName;

	initPageComponents();
    }

    @Override
    protected Class getActiveMenu() {
	return PayBillPage.class;
    }

    protected void initPageComponents() {
	Long customerId = getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId();

	add(new OpenBillPanel("payOpenBillPanel", customerId,
		MakePaymentPage.this, invoiceConfiguration, invoiceTypeName,
		null, null, true, true) {

	    @Override
	    public MobiliserBasePage getBillPaymentListPage() {
		return new PayBillPage();
	    }

	    @Override
	    public MobiliserBasePage getConfirmPage(Invoice invoice,
		    TransactionBean txBean) {

		return new ConfirmBillPaymentPage(invoiceConfiguration,
			invoiceTypeName, invoice, txBean, true) {
		    @Override
		    protected Class getActiveMenu() {
			return PayBillPage.class;
		    }
		};
	    }

	    @Override
	    public MobiliserBasePage getStatusPage(Invoice invoice,
		    TransactionBean txBean) {
		return null;
	    }

	});

    }

}
