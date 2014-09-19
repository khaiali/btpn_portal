package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.DetailedTransaction;
import com.sybase365.mobiliser.web.common.panels.TransactionDetailsPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_MERCHANT_TXN_HISTORY)
public class TransactionDetailsPage extends CustomerServicesMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TransactionDetailsPage.class);

    private DetailedTransaction transactionDetailsBean;
    private WebPage backPage;

    public TransactionDetailsPage(DetailedTransaction txnBean, WebPage backPage) {
	super();
	this.transactionDetailsBean = txnBean;
	this.backPage = backPage;
	initPageComponents();
    }

    protected void initPageComponents() {

	add(new TransactionDetailsPanel("txnDetailsPanel",
		transactionDetailsBean, this, backPage, null));

    }

    public void setTransactionDetailsBean(
	    DetailedTransaction transactionDetailsBean) {
	this.transactionDetailsBean = transactionDetailsBean;
    }

    public DetailedTransaction getTransactionDetailsBean() {
	return transactionDetailsBean;
    }

    @Override
    protected Class getActiveMenu() {
	return ViewTransactionsPage.class;
    }

}
