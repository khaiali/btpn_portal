package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import com.sybase365.mobiliser.web.util.Converter;

@AuthorizeInstantiation(Constants.PRIV_MERCHANT_TXN_HISTORY)
public class ViewTransactionsPage extends CustomerServicesMenuGroup implements
	ITransactionDetailViewer {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ViewTransactionsPage.class);

    public ViewTransactionsPage() {

	super();

	LOG.info("Created new ViewTransactionsPage");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	Customer customer = ((BaseWebSession) getWebSession())
		.getLoggedInCustomer();
	CustomerBean customerBean = Converter.getInstance()
		.getCustomerBeanFromCustomer(customer);
	// get transaction history for selected customer
	SearchTransactionCriteria criteria = new SearchTransactionCriteria();
	criteria.setCustomerId(customerBean.getId());
	criteria.setShowFaulty(Boolean.TRUE);
	criteria.setShowInitial(Boolean.FALSE);
	criteria.setJoinedCustomerId(getMobiliserWebSession().getCustomer()
		.getId());
	add(new TransactionHistoryPanel("txnhistorypanel", criteria,
		customerBean, this, this, false, true));
    }

    @Override
    public WebPage getTransactionDetailViewer(SimpleTransaction bean,
	    Long customerId, WebPage backPage) {
	DetailedTransaction detailedTransaction = getTransactionDetails(bean
		.getId());
	return new TransactionDetailsPage(detailedTransaction, backPage);
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
