package com.sybase365.mobiliser.web.consumer.pages.portal.manageaccounts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.web.common.panels.SendMoneyBankPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_SEND_MONEY_BANK)
public class SendMoneyBankPage extends BaseManageAccountsPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(SendMoneyBankPage.class);

    public SendMoneyBankPage() {
	super();
    }

    public SendMoneyBankPage(WalletEntry walletEntry) {
	super();
	add(new SendMoneyBankPanel("sendMoneyBankPanel", this,
		ManageAccountPage.class, walletEntry));
    }

    @Override
    protected Class getActiveMenu() {
	return BankListPage.class;
    }

}
