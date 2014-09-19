package com.sybase365.mobiliser.web.checkout.models;

import java.io.Serializable;
import java.util.Date;

import org.apache.wicket.markup.html.WebPage;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetWebTxnDetailsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetWebTxnDetailsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.TransactionCancel;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.TransactionCancelResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.WebContinue;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.WebContinueResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.DetailedWebTransaction;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.checkout.exceptions.ServicesException;
import com.sybase365.mobiliser.web.checkout.exceptions.TransactionNotFoundException;

public class Transaction implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(Transaction.class);

    private Long systemId;
    private MobiliserBasePage mobBasePage;
    DetailedWebTransaction txnDetail;
    private Customer payer;
    private int statusCode;
    private int continueRetryCounter = 0;
    protected Date continueStartDate;
    private TxnWatchdog watchdog;

    public Transaction(Long systemId, WebPage mobBasePage)
	    throws TransactionNotFoundException, ServicesException, Exception {
	if (LOG.isDebugEnabled())
	    LOG.debug("Retrieving transaction details for transaction ["
		    + this.systemId + "]");
	this.systemId = systemId;
	this.mobBasePage = (MobiliserBasePage) mobBasePage;
	this.txnDetail = getTransactionDetail();
	if (this.txnDetail == null) {
	    LOG.error("Transaction was not found.");
	    throw new TransactionNotFoundException();
	}

	if (LOG.isDebugEnabled())
	    LOG.debug("Transaction found in Mobiliser. [TransactionID="
		    + this.txnDetail.getId() + "]");
    }

    private DetailedWebTransaction getTransactionDetail()
	    throws ServicesException, Exception {

	GetWebTxnDetailsRequest request = mobBasePage
		.getNewMobiliserRequest(GetWebTxnDetailsRequest.class);
	request.setSystemId(this.systemId);
	GetWebTxnDetailsResponse response = mobBasePage.wsTransactionsClient
		.getWebTxnDetails(request);
	if (!mobBasePage.evaluateMobiliserResponse(response)) {
	    throw new ServicesException("Service returned an error:"
		    + response.getStatus().getValue(), response.getStatus());
	}
	// appending the transaction id in the uRL
	response.getTransaction().setReturnUrl(
		response.getTransaction().getReturnUrl() + "&txnId="
			+ this.systemId);
	return response.getTransaction();

    }

    public void failTransaction(int errorCode) throws Exception {
	DetailedWebTransaction txn = getTxnDetail();
	if (txn != null) {
	    if (txn.getStatus() == 0 && txn.getErrorCode() == 0) {
		com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Transaction transaction = new com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Transaction();
		transaction.setSystemId(this.systemId);
		TransactionCancel request = mobBasePage
			.getNewMobiliserRequest(TransactionCancel.class);
		request.setCode(errorCode);
		request.setReferenceTransaction(transaction);
		TransactionCancelResponse response = mobBasePage.wsTxnCancelClient
			.transactionCancel(request);

		if (response.getStatus() != null
			&& response.getStatus().getCode() != 0) {
		    LOG.warn("failTransaction(" + txn.getId()
			    + ") failed with code "
			    + response.getStatus().getCode() + " - "
			    + response.getStatus().getValue());
		} else {
		    LOG.info("failTransaction(" + txn.getId()
			    + ") successfully failed transaction.");
		}
	    } else {
		LOG.info("failTransaction("
			+ txn.getId()
			+ ") was called but transaction is already failed or continued.");
	    }
	} else {
	    LOG.info("can not find transaction to fail!!");
	}
	this.getWatchdog().disarm();

    }

    public Long getTxnId() {
	return systemId;
    }

    public void setTxnId(Long systemId) {
	this.systemId = systemId;
    }

    public DetailedWebTransaction getTxnDetail() {
	return txnDetail;
    }

    public void setTxnDetail(DetailedWebTransaction txnDetail) {
	this.txnDetail = txnDetail;
    }

    public String getReturnUrl() {
	return this.txnDetail.getReturnUrl();
    }

    public Customer getPayer() {
	return payer;
    }

    public void setPayer(Customer payer) {
	this.payer = payer;
    }

    public int getStatusCode() {
	return statusCode;
    }

    public Long getSystemId() {
	return systemId;
    }

    public void setSystemId(Long systemId) {
	this.systemId = systemId;
    }

    public void setStatusCode(int statusCode) {
	this.statusCode = statusCode;
    }

    public int getContinueRetryCounter() {
	return continueRetryCounter;
    }

    public void setContinueRetryCounter(int continueRetryCounter) {
	this.continueRetryCounter = continueRetryCounter;
    }

    public TxnWatchdog getWatchdog() {
	if (this.watchdog == null) {
	    // Init default Watchdog with 5 Minutes Delay
	    this.watchdog = new TxnWatchdog();
	    this.watchdog.setWaitInSeconds(5 * 60);
	}
	return this.watchdog;
    }

    public Date getContinueStartDate() {
	return continueStartDate;
    }

    public WebContinueResponse continueTransaction(Customer consumer,
	    WebContinue webContinueReq) throws Exception {
	// Current Date
	this.continueStartDate = new Date();

	// increase continue counter
	this.continueRetryCounter++;
	WebContinueResponse response = mobBasePage.wsWebContinueClient
		.webContinue(webContinueReq);
	return response;
    }

    public class TxnWatchdog extends Thread {

	private int errorCode;
	private long waitInSeconds;
	private boolean armed = true;

	/**
	 * Constructor for TxnWatchdog
	 * 
	 * @param txnId
	 *            Transaction ID
	 * @param token
	 *            Token to retrieve transaction
	 * @param msisdn
	 *            Optional MSISDN of customer
	 * @param errorCode
	 *            Transaction will be failed with this error code
	 * @param waitInSeconds
	 *            Time to wait before the transaction is failed
	 */
	public TxnWatchdog() {
	    super();

	}

	@Override
	public void run() {

	    if (LOG.isInfoEnabled())
		LOG.info("started TxnWatchdog for transaction " + systemId
			+ " will be triggert in [Seconds=" + this.waitInSeconds
			+ "]");

	    try {

		Thread.sleep(waitInSeconds * 1000L);
	    } catch (InterruptedException ie) {
		LOG.warn("Thread sleep was interrupted: " + ie, ie);
	    }
	    if (armed) {
		try {
		    // Fail Transaction
		    failTransaction(errorCode);
		} catch (Exception e) {
		    LOG.error("fail transaction failed: " + e, e);
		}
	    } else {
		if (LOG.isInfoEnabled())
		    LOG.info("TxnWatchdog was disarmed.");
	    }

	}

	/**
	 * Disarm watchdog. Transaction will not be failed anymore.
	 */
	public void disarm() {
	    this.armed = false;
	    if (LOG.isDebugEnabled())
		LOG.debug("Transaction watchdog disarmed.");
	    watchdog = null;
	}

	public int getErrorCode() {
	    return errorCode;
	}

	public void setErrorCode(int errorCode) {
	    this.errorCode = errorCode;
	}

	public long getWaitInSeconds() {
	    return waitInSeconds;
	}

	public void setWaitInSeconds(long waitInSeconds) {
	    this.waitInSeconds = waitInSeconds;
	}

    }

}
