package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.DetailedTransaction;
import com.sybase365.mobiliser.web.common.panels.TransactionDetailsPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CST_LOGIN)
public class TxnDetailsPage extends CustomerCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private DetailedTransaction transactionDetailsBean;
    private WebPage backPage;
    private String taskId;

    public TxnDetailsPage(DetailedTransaction txnBean, WebPage backPage,
	    String taskId) {
	super();
	this.transactionDetailsBean = txnBean;
	this.backPage = backPage;
	this.taskId = taskId;
	initPageComponents();
    }

    protected void initPageComponents() {

	add(new TransactionDetailsPanel("txnDetailsPanel",
		transactionDetailsBean, this, backPage, this.taskId));

    }

    @Override
    protected Class getActiveMenu() {

	return TransactionsPage.class;

    }

    public void setTransactionDetailsBean(
	    DetailedTransaction transactionDetailsBean) {
	this.transactionDetailsBean = transactionDetailsBean;
    }

    public DetailedTransaction getTransactionDetailsBean() {
	return transactionDetailsBean;
    }

}
