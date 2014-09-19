package com.sybase365.mobiliser.web.consumer.pages.portal.transaction;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.common.panels.SendMoneyBankPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_SEND_MONEY_BANK)
public class SendMoneyBankPage extends BaseTransactionsPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(SendMoneyBankPage.class);

    public SendMoneyBankPage() {
	super();
	add(new SendMoneyBankPanel("sendMoneyBankPanel", this,
		ViewTransactionsPage.class, null));
    }

}
