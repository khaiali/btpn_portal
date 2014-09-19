package com.sybase365.mobiliser.web.common.model;

import org.apache.wicket.markup.html.WebPage;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.SimpleTransaction;

public interface ITransactionDetailViewer {

    public WebPage getTransactionDetailViewer(SimpleTransaction bean,
	    Long customerId, WebPage backPage);

}
