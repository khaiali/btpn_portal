package com.sybase365.mobiliser.web.checkout.util;

import java.io.Serializable;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.WebContinue;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.WebContinueResponse;
import com.sybase365.mobiliser.web.checkout.models.Transaction;

public class SmsAuthenticationThread extends Thread implements Runnable,
	Serializable {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(SmsAuthenticationThread.class);

    private WebContinueResponse response = null;
    private Exception exception = null;
    private boolean isActive = false;
    private Customer customer;
    private String msisdn;
    private Transaction txn;
    private boolean secondRun;
    WebContinue webContinueReq;

    public SmsAuthenticationThread(Transaction txn, String msisdn,
	    WebContinue req) {
	super();
	this.txn = txn;
	this.msisdn = msisdn;
	this.secondRun = txn.getContinueRetryCounter() > 1;
	this.webContinueReq = req;
    }

    public void run() {
	isActive = true;

	if (LOG.isDebugEnabled())
	    LOG.debug("SMS Thread started.");

	try {
	    if (txn == null)
		throw new Exception("Transaction not set");
	    customer = (Customer) txn.getPayer();
	    if (customer == null)
		throw new Exception("Consumer was not set");

	    if (LOG.isDebugEnabled())
		LOG.debug("Processing transaction: [MSISDN=" + this.msisdn
			+ "] [TXN-ID=" + txn.getTxnId() + "]");

	    // Disarm Watchdog
	    this.txn.getWatchdog().disarm();

	    // Continue Transaction
	    WebContinueResponse response = this.txn.continueTransaction(
		    customer, webContinueReq);

	    // Set response for further processing
	    this.setResponse(response);

	    // Evaluate response
	    if (!(response.getStatus() != null && response.getStatus()
		    .getCode() != 0)) {

		// Transaction was successful
		if (LOG.isDebugEnabled())
		    LOG.debug("Thread [" + this.msisdn
			    + "] - Mobiliser response OK");
		return;
	    }

	    // Transaction Failed with an error
	    if (LOG.isDebugEnabled())
		LOG.debug("Thread [" + this.msisdn
			+ "] - Mobiliser response code: "
			+ response.getStatus().getCode() + " - "
			+ response.getStatus().getValue());

	    // Error 2853 is for ?timeout?
	    if (response.getStatus().getCode() != 2853)
		return;

	    // Check for second try
	    if (secondRun) {
		// Fail transaction if second try was completed
		this.txn.failTransaction(2853);
	    } else {
		// the watchdog will fail the transaction after 5
		// minutes if not intervened
		this.txn.getWatchdog().start();
	    }
	} catch (Exception e) {
	    if (LOG.isErrorEnabled())
		LOG.error("Thread - Exception: ", e);
	    this.setException(e);
	} finally {
	    isActive = false;
	    LOG.debug("Thread  [" + this.msisdn + "]  finished isActive = "
		    + isActive);
	}
    }

    public WebContinueResponse getResponse() {
	return response;
    }

    public void setResponse(WebContinueResponse response) {
	this.response = response;
    }

    public Exception getException() {
	return exception;
    }

    public void setException(Exception exception) {
	this.exception = exception;
    }

    public boolean isActive() {
	return isActive;
    }

    public void setActive(boolean isActive) {
	this.isActive = isActive;
    }

    public Customer getCustomer() {
	return customer;
    }

    public void setCustomer(Customer customer) {
	this.customer = customer;
    }

}
