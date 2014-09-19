package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.markup.html.basic.Label;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PendingWalletEntry;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage;
import com.sybase365.mobiliser.web.common.panels.BankAccountDataPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class BankAccountDataPage extends CustomerCareMenuGroup {
    private static final long serialVersionUID = 1L;

    Class<? extends BasePage> returnPage;
    WalletEntry walletEntry;
    private boolean isForApproval = false;
    private String subTitle1 = getLocalizer().getString("manageAccounts.title",
	    this);

    private String subTitle2 = getLocalizer().getString(
	    "menu.cst.approve.wallets", this);

    private String subTitle2BrCrumbSep = getLocalizer().getString(
	    "application.breadcrumb.separator", this);

    private String subTitle3 = getLocalizer().getString(
	    "bankAccountData.title", this);

    private String helpMsg = getLocalizer().getString("bankAccountData.help",
	    this);

    public BankAccountDataPage(WalletEntry we,
	    Class<? extends BaseCustomerCarePage> returnPage) {
	super();
	this.returnPage = returnPage;
	this.walletEntry = we;
	if (walletEntry instanceof PendingWalletEntry) {
	    isForApproval = true;
	}

	initPageComponents();
    }

    protected void initPageComponents() {

	if (isForApproval) {
	    subTitle1 = getLocalizer().getString("menu.cst.approvals", this);

	    if (walletEntry.getBankAccount().getType() == Constants.PI_TYPE_BANK_ACCOUNT)
		subTitle3 = getLocalizer().getString(
			"pending.bankAccountData.title", this);
	    else
		subTitle3 = getLocalizer().getString(
			"pending.externalAccountData.title", this);

	}

	if (PortalUtils.exists(walletEntry)) {
	    subTitle3 = getLocalizer().getString("bankAccountEditData.title",
		    this);
	}

	add(new Label("subTitle1", subTitle1));

	add(new Label("subTitle2", subTitle2).setVisible(isForApproval));

	add(new Label("subTitle2BrCrumbSep", subTitle2BrCrumbSep)
		.setVisible(isForApproval));

	add(new Label("subTitle3", subTitle3));

	add(new Label("h3", getLocalizer().getString("bankAccountData.help",
		this)).setVisible(!isForApproval));

	add(new BankAccountDataPanel("bankAccountPanel", this, returnPage,
		null, walletEntry, getMobiliserWebSession().getCustomer()));
    }

    @Override
    protected Class getActiveMenu() {
	return ManageAccountPage.class;
    }

}
