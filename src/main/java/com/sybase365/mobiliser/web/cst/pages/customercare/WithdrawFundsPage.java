package com.sybase365.mobiliser.web.cst.pages.customercare;

import java.util.List;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.web.common.panels.WithdrawFundsPanel;

public class WithdrawFundsPage extends CustomerCareMenuGroup {

    public WithdrawFundsPage(List<WalletEntry> bankAccounts) {
	super();
	add(new WithdrawFundsPanel("withdrawFundsPanel", this,
		ManageAccountPage.class, getMobiliserWebSession().getCustomer()
			.getId(), bankAccounts));
    }

    @Override
    protected Class getActiveMenu() {
	return ManageAccountPage.class;
    }

}
