package com.sybase365.mobiliser.web.checkout.pages;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.web.checkout.exceptions.ServicesException;
import com.sybase365.mobiliser.web.checkout.exceptions.TransactionNotFoundException;
import com.sybase365.mobiliser.web.checkout.models.Transaction;
import com.sybase365.mobiliser.web.util.Constants;

public class FirstContactPage extends BaseCheckoutPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(FirstContactPage.class);
    private Long systemId;

    public FirstContactPage(Long systemId) {
	super();
	this.systemId = systemId;
	initPageComponents();
    }

    public FirstContactPage(PageParameters param) {
	super();
	this.systemId = Long.valueOf(param.getString("systemId"));
	initPageComponents();
    }

    protected void initPageComponents() {
	initiateCheckout();
    }

    private void initiateCheckout() {

	try {
	    if (this.systemId == null) {
		LOG.error("Transaction ID was not set from Merchant [txnID = NULL]");
		error(getLocalizer().getString("transactionIDNotSet", this));
		setResponsePage(ErrorPage.class);
	    }

	    Transaction transaction = new Transaction(this.systemId, this);

	    if (transaction.getTxnDetail().getErrorCode() != 0
		    || transaction.getTxnDetail().getStatus() != 0) {
		error(getLocalizer().getString("transactionHasWrongStatus",
			this)
			+ transaction.getTxnDetail().getId() + "]");
		LOG.error("Fraud suspected: Transaction is in invalid status");
		setResponsePage(ErrorPage.class);
	    }

	    Customer txnPayee = getCustomerByIdentification(
		    Constants.IDENT_TYPE_CUST_ID,
		    String.valueOf(transaction.getTxnDetail().getPayeeId()));

	    if (LOG.isDebugEnabled())
		LOG.debug("Transaction request from customer (payee) [ID="
			+ txnPayee.getId() + "]");

	    if (LOG.isDebugEnabled())
		LOG.debug("->Waiting for user input");

	    getMobiliserWebSession().setTransaction(transaction);
	    getMobiliserWebSession().setTxnPayee(txnPayee);
	    setResponsePage(GetMsisdnPage.class);

	} catch (TransactionNotFoundException tnfe) {
	    LOG.error("Transaction ID was not found.[TransactionID=" + systemId
		    + "]", tnfe);
	    error(getLocalizer().getString("transactionNotFound", this));
	    setResponsePage(ErrorPage.class);
	    return;
	} catch (ServicesException se) {
	    LOG.error("ServicesException", se);
	    setResponsePage(ErrorPage.class);
	    return;
	} catch (Exception e) {
	    error(getLocalizer().getString("generalException" + e.getMessage(),
		    this));
	    LOG.error("Exception", e);
	    setResponsePage(ErrorPage.class);
	}

    }

}
