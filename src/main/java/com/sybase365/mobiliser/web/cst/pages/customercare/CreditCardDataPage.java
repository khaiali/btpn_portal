package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.markup.html.basic.Label;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PendingWalletEntry;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage;
import com.sybase365.mobiliser.web.common.panels.CreditCardDataPanel;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CreditCardDataPage extends CustomerCareMenuGroup {

    WalletEntry walletEntry;
    Class<? extends BasePage> returnPage;
    private boolean isForApproval = false;

    private String subTitle1 = getLocalizer().getString("manageAccounts.title",
	    this);

    private String subTitle2 = getLocalizer().getString(
	    "menu.cst.approve.wallets", this);

    private String subTitle2BrCrumbSep = getLocalizer().getString(
	    "application.breadcrumb.separator", this);

    private String subTitle3 = getLocalizer().getString("creditCardData.title",
	    this);

    public CreditCardDataPage(WalletEntry we,
	    Class<? extends BasePage> returnPage) {
	super();
	this.walletEntry = we;
	if (walletEntry instanceof PendingWalletEntry) {
	    isForApproval = true;
	}
	initPageComponents();

    }

    protected void initPageComponents() {

	if (isForApproval) {
	    subTitle1 = getLocalizer().getString("menu.cst.approvals", this);
	    subTitle3 = getLocalizer().getString(
		    "pending.creditCardData.title", this);

	}

	if (PortalUtils.exists(walletEntry)) {
	    subTitle3 = getLocalizer().getString("creditCardEditData.title",
		    this);
	}

	add(new Label("subTitle1", subTitle1));

	add(new Label("subTitle2", subTitle2).setVisible(isForApproval));

	add(new Label("subTitle2BrCrumbSep", subTitle2BrCrumbSep)
		.setVisible(isForApproval));

	add(new Label("subTitle3", subTitle3));

	add(new Label("h3", getLocalizer().getString("creditCardData.help",
		this)).setVisible(!isForApproval));

	add(new CreditCardDataPanel("creditCardPanel", this,
		ManageAccountPage.class, walletEntry, getMobiliserWebSession()
			.getCustomer()));
    }

    @Override
    protected Class getActiveMenu() {
	return returnPage;
    }

}
