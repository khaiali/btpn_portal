package com.sybase365.mobiliser.web.consumer.pages.portal.billpayment;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.Invoice;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.common.panels.OpenBillPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_BILL_PAYMENT)
public class ConfirmBillPaymentPage extends BaseBillPaymentPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(ConfirmBillPaymentPage.class);

    InvoiceConfiguration invoiceConfiguration;
    String invoiceTypeName;
    Invoice invoice;
    TransactionBean txBean;
    boolean isOnDemand;

    public ConfirmBillPaymentPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public ConfirmBillPaymentPage(final PageParameters parameters) {
	super(parameters);
    }

    public ConfirmBillPaymentPage(InvoiceConfiguration invoiceConfiguration,
	    String invoiceTypeName, Invoice invoice, TransactionBean txBean,
	    boolean isOnDemand) {
	this.invoiceConfiguration = invoiceConfiguration;
	this.invoiceTypeName = invoiceTypeName;
	this.invoice = invoice;
	this.txBean = txBean;
	this.isOnDemand = isOnDemand;
	initPageComponents();
    }

    protected void initPageComponents() {
	Long customerId = getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId();

	String title = isOnDemand ? getLocalizer().getString("payBill.title",
		this) : getLocalizer().getString("openBills.title", this);

	add(new Label("startTitle", title));

	add(new OpenBillPanel("confirmBillPaymentPanel", customerId,
		ConfirmBillPaymentPage.this, invoiceConfiguration,
		invoiceTypeName, invoice, txBean, true, isOnDemand()) {

	    @Override
	    public MobiliserBasePage getBillPaymentListPage() {
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
		return new BillPaymentStatusPage(invoiceConfiguration,
			invoiceTypeName, invoice, txBean, isOnDemand()) {
		    @Override
		    protected Class getActiveMenu() {
			if (isOnDemand())
			    return PayBillPage.class;
			else
			    return OpenBillsPage.class;
		    }

		};
	    }

	});

    }

    private boolean isOnDemand() {
	return this.isOnDemand;
    }

}
