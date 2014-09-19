package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.DetailedTransaction;
import com.sybase365.mobiliser.web.common.panels.TransactionDetailsPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CST_LOGIN)
public class AgentTxnDetailsPage extends CustomerCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private DetailedTransaction transactionDetailsBean;
    private WebPage backPage;

    public AgentTxnDetailsPage(DetailedTransaction txnBean, WebPage backPage) {
	super();
	this.transactionDetailsBean = txnBean;
	this.backPage = backPage;
	initPageComponents();
    }

    protected void initPageComponents() {

	add(new TransactionDetailsPanel("txnDetailsPanel",
		transactionDetailsBean, this, backPage, null));

    }

    @Override
    protected Class getActiveMenu() {

	return AgentTransactionsPage.class;

    }

    public void setTransactionDetailsBean(
	    DetailedTransaction transactionDetailsBean) {
	this.transactionDetailsBean = transactionDetailsBean;
    }

    public DetailedTransaction getTransactionDetailsBean() {
	return transactionDetailsBean;
    }

}
