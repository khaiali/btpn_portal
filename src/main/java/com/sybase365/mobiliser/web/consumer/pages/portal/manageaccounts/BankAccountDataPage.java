package com.sybase365.mobiliser.web.consumer.pages.portal.manageaccounts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage;
import com.sybase365.mobiliser.web.common.panels.BankAccountDataPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_ACCOUNTS)
public class BankAccountDataPage extends BaseManageAccountsPage {

    private static final long serialVersionUID = 1L;
    private Integer accType;
    private WalletEntry we;
    private Class<? extends BasePage> returnPage;
    private String title = getLocalizer().getString("bankAccountAddData.title",
	    this);

    public BankAccountDataPage(Class<? extends BasePage> returnPage,
	    WalletEntry we, Integer accType) {
	super();
	this.accType = accType;
	this.we = we;
	this.returnPage = returnPage;
	initPageComponents();
    }

    protected void initPageComponents() {

	if (PortalUtils.exists(we)) {
	    title = getLocalizer().getString("bankAccountEditData.title", this);
	}

	add(new Label("bankAccountData.title", title));

	add(new BankAccountDataPanel("bankAccountPanel", this, returnPage,
		accType, we, Converter.getInstance()
			.getCustomerBeanFromCustomer(
				getMobiliserWebSession().getLoggedInCustomer())));
    }

    @Override
    protected Class getActiveMenu() {
	return ManageAccountPage.class;
    }
}
