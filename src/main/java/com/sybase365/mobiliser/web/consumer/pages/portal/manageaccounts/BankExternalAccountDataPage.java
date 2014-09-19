package com.sybase365.mobiliser.web.consumer.pages.portal.manageaccounts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PendingWalletEntry;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage;
import com.sybase365.mobiliser.web.common.panels.BankAccountDataPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_BANK_ACCOUNT_LIST)
public class BankExternalAccountDataPage extends BaseManageAccountsPage {

    private static final long serialVersionUID = 1L;
    private Integer accType;

    private boolean isPendingWallet;

    private WalletEntry we;
    private Class<? extends BasePage> returnPage;
    private String title = getLocalizer().getString("bankAccountAddData.title",
	    this);

    public BankExternalAccountDataPage(Class<? extends BasePage> returnPage,
	    WalletEntry we, Integer accType) {
	super();
	this.accType = accType;
	this.we = we;
	this.returnPage = returnPage;
	if (we instanceof PendingWalletEntry) {
	    isPendingWallet = true;
	}
	initPageComponents();
    }

    protected void initPageComponents() {

	if (PortalUtils.exists(we)) {
	    title = getLocalizer().getString("bankAccountEditData.title", this);
	}

	add(new Label("bankAccountData.title", title));

	add(new Label("helpMsg", getLocalizer().getString(
		"bankAccountData.help", this)).setVisible(!isPendingWallet));

	add(new BankAccountDataPanel("bankAccountPanel", this, returnPage,
		accType, we, Converter.getInstance()
			.getCustomerBeanFromCustomer(
				getMobiliserWebSession().getLoggedInCustomer())));
    }

    @Override
    protected Class getActiveMenu() {
	return BankListPage.class;
    }
}
