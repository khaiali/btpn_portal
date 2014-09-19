package com.sybase365.mobiliser.web.consumer.pages.portal.manageaccounts;

import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.web.common.panels.AddFundsPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_ACCOUNTS)
public class AddFunds extends BaseManageAccountsPage {

    private static final long serialVersionUID = 1L;

    public AddFunds(final List<WalletEntry> bankAccounts,
	    final List<WalletEntry> creditCards) {
	super();
	add(new AddFundsPanel("addFundsPanel", this, ManageAccountPage.class,
		getMobiliserWebSession().getLoggedInCustomer().getCustomerId(),
		bankAccounts, creditCards));
    }

    @Override
    protected Class getActiveMenu() {
	return ManageAccountPage.class;
    }

}
