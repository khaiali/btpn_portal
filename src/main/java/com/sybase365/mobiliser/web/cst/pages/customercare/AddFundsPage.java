package com.sybase365.mobiliser.web.cst.pages.customercare;

import java.util.List;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.web.common.panels.AddFundsPanel;

public class AddFundsPage extends CustomerCareMenuGroup {
    private List<WalletEntry> bankAccounts;
    private List<WalletEntry> creditCards;

    public AddFundsPage(final List<WalletEntry> bankAccounts,
	    final List<WalletEntry> creditCards) {
	super();
	this.bankAccounts = bankAccounts;
	this.creditCards = creditCards;
	initPageComponents();
    }

    protected void initPageComponents() {
	add(new AddFundsPanel("addFundsPanel", this, ManageAccountPage.class,
		getMobiliserWebSession().getCustomer().getId(), bankAccounts,
		creditCards));
    }

    @Override
    protected Class getActiveMenu() {
	return ManageAccountPage.class;
    }
}
