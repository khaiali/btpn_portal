package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.markup.html.WebPage;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetTransactionDetailsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetTransactionDetailsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.DetailedTransaction;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.SimpleTransaction;
import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.beans.SearchTransactionCriteria;
import com.sybase365.mobiliser.web.common.model.ITransactionDetailViewer;
import com.sybase365.mobiliser.web.common.panels.TransactionHistoryPanel;
import com.sybase365.mobiliser.web.util.Constants;

public class AgentTransactionsPage extends CustomerCareMenuGroup implements
	ITransactionDetailViewer {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AgentTransactionsPage.class);

    public AgentTransactionsPage() {

	super();

	LOG.info("Created new AgentTransactionsPage");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	Customer customer = ((BaseWebSession) getWebSession())
		.getLoggedInCustomer();
	CustomerBean searchCustomer = getMobiliserWebSession().getCustomer();

	Boolean requiredStatus = true;
	if (getMobiliserWebSession().hasPrivilege(Constants.PRIV_CST_LOGIN)) {
	    requiredStatus = false;
	}
	// get transaction details for currently logged in customer
	SearchTransactionCriteria criteria = new SearchTransactionCriteria();
	criteria.setCustomerId(searchCustomer.getId());
	criteria.setShowFaulty(Boolean.TRUE);
	criteria.setShowInitial(Boolean.TRUE);
	criteria.setCallerId(getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId());
	add(new TransactionHistoryPanel("txnhistorypanel", criteria,
		searchCustomer, this, this, false, requiredStatus));
    }

    @Override
    public WebPage getTransactionDetailViewer(SimpleTransaction bean,
	    Long customerId, WebPage backPage) {
	DetailedTransaction detailedTransaction = getTransactionDetails(bean
		.getId());
	return new AgentTxnDetailsPage(detailedTransaction, backPage);
    }

    private DetailedTransaction getTransactionDetails(long txnId) {
	try {
	    GetTransactionDetailsRequest request = getNewMobiliserRequest(GetTransactionDetailsRequest.class);
	    request.setTxnId(txnId);
	    GetTransactionDetailsResponse response = wsTransactionsClient
		    .getTransactionDetails(request);
	    if (evaluateMobiliserResponse(response)) {
		return response.getTransaction();
	    }
	} catch (Exception e) {
	    LOG.error("# Exception getting detailed transaction info", e);
	}
	return null;
    }

}
