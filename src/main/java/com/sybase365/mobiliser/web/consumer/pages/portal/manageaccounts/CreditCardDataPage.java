package com.sybase365.mobiliser.web.consumer.pages.portal.manageaccounts;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.web.common.panels.CreditCardDataPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_ACCOUNTS)
public class CreditCardDataPage extends BaseManageAccountsPage {

    private static final long serialVersionUID = 1L;
    private String title = getLocalizer().getString("creditCardData.title",
	    this);

    public CreditCardDataPage() {
	super();
	initPageComponents();
    }

    public CreditCardDataPage(final PageParameters parameters) {
	super(parameters);
	initPageComponents();
    }

    protected void initPageComponents() {
	add(new CreditCardDataPanel("creditCardPanel", this,
		ManageAccountPage.class, null, Converter.getInstance()
			.getCustomerBeanFromCustomer(
				getMobiliserWebSession().getLoggedInCustomer())));

    }

    private WalletEntry walletEntry;

    @Override
    protected Class getActiveMenu() {
	return ManageAccountPage.class;
    }

    public void setWalletEntry(WalletEntry walletEntry) {
	this.walletEntry = walletEntry;
    }

}
