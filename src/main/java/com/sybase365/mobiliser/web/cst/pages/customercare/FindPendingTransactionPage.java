package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.markup.html.WebPage;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetTransactionDetailsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetTransactionDetailsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetTransactionTaskIdRequest;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetTransactionTaskIdResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.DetailedTransaction;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.SimpleTransaction;
import com.sybase365.mobiliser.web.beans.SearchTransactionCriteria;
import com.sybase365.mobiliser.web.common.model.ITransactionDetailViewer;
import com.sybase365.mobiliser.web.common.panels.TransactionHistoryPanel;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class FindPendingTransactionPage extends BaseCustomerCarePage implements
	ITransactionDetailViewer {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(FindPendingTransactionPage.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();
	SearchTransactionCriteria criteria = new SearchTransactionCriteria();
	criteria.setShowFaulty(Boolean.FALSE);
	criteria.setShowInitial(Boolean.TRUE);

	add(new TransactionHistoryPanel("txnhistorypanel", criteria, null,
		this, this, false, false));
    }

    @Override
    public WebPage getTransactionDetailViewer(SimpleTransaction bean,
	    Long customerId, WebPage backPage) {
	DetailedTransaction detailedTransaction = getPendingTxnDetails(bean
		.getId());

	// The following service is invoked just to avoid
	// displaying of transaction details in case user
	// does not have checker privilege for the transaction
	String txnTaskId = getTxnTaskId(bean.getId());
	if (!PortalUtils.exists(txnTaskId))
	    return this;

	return new TxnDetailsPage(detailedTransaction, backPage, txnTaskId);
    }

    private DetailedTransaction getPendingTxnDetails(long txnId) {
	LOG.debug("# ApproveTransactionsPage.getPendingTxnDetails(...)");
	GetTransactionDetailsResponse response;
	DetailedTransaction txnDetail = null;
	try {
	    GetTransactionDetailsRequest request = getNewMobiliserRequest(GetTransactionDetailsRequest.class);
	    request.setTxnId(txnId);
	    response = wsTransactionsClient.getTransactionDetails(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while loading details of pending transaction");
		return null;

	    }
	    txnDetail = response.getTransaction();

	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while loading details of pending transaction",
		    e);
	}

	return txnDetail;

    }

    private String getTxnTaskId(long txnId) {
	LOG.debug("# FindPendingTransactionPage.getTxnTaskId()");
	GetTransactionTaskIdResponse response = null;
	try {
	    GetTransactionTaskIdRequest request = getNewMobiliserRequest(GetTransactionTaskIdRequest.class);
	    request.setTxnId(txnId);
	    response = wsTransactionsClient.getTransactionTaskId(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while fetching task id of pending transaction");
		return null;
	    }

	} catch (Exception e) {
	    error(getLocalizer().getString("taskId.fetch.error", this));
	    LOG.error("# An error occurred while fetching task id of pending transaction");

	}
	return response.getTaskId();

    }
}
