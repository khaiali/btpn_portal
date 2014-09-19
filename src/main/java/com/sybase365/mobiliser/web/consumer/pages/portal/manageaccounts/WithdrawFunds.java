package com.sybase365.mobiliser.web.consumer.pages.portal.manageaccounts;

import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.web.common.panels.WithdrawFundsPanel;
import com.sybase365.mobiliser.web.consumer.pages.portal.transaction.BaseTransactionsPage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_ACCOUNTS)
public class WithdrawFunds extends BaseManageAccountsPage {

    private static final long serialVersionUID = 1L;

    public WithdrawFunds(final List<WalletEntry> bankAccounts) {
	super();
	add(new WithdrawFundsPanel("withdrawFundsPanel", this,
		ManageAccountPage.class, getMobiliserWebSession()
			.getLoggedInCustomer().getCustomerId(), bankAccounts));
    }

    @Override
    protected Class getActiveMenu() {
	return ManageAccountPage.class;
    }

}
