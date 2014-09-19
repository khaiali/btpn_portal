package com.sybase365.mobiliser.web.consumer.pages.portal.transaction;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.SimpleTransaction;
import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.beans.SearchTransactionCriteria;
import com.sybase365.mobiliser.web.common.model.ITransactionDetailViewer;
import com.sybase365.mobiliser.web.common.panels.TransactionHistoryPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;

@AuthorizeInstantiation(Constants.PRIV_CONSUMER_TXN_HISTORY)
public class ViewTransactionsPage extends BaseTransactionsPage implements
	ITransactionDetailViewer {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ViewTransactionsPage.class);

    public ViewTransactionsPage() {

	super();

	LOG.info("Created new ViewTransactionPage");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	Customer customer = ((BaseWebSession) getWebSession())
		.getLoggedInCustomer();
	CustomerBean customerBean = Converter.getInstance()
		.getCustomerBeanFromCustomer(customer);
	SearchTransactionCriteria criteria = new SearchTransactionCriteria();
	criteria.setCustomerId(customerBean.getId());
	criteria.setShowFaulty(Boolean.FALSE);
	criteria.setShowInitial(Boolean.FALSE);
	criteria.setShowAuthorizedCancel(false);
	// get transaction details for currently logged in customer
	add(new TransactionHistoryPanel("txnhistorypanel", criteria,
		customerBean, this, this, true, true));
    }

    @Override
    public WebPage getTransactionDetailViewer(SimpleTransaction bean,
	    Long customerId, WebPage backPage) {
	return new TransactionDetailsPage(bean, customerId);
    }
}
