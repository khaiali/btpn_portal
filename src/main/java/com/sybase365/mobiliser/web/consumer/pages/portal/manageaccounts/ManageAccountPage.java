package com.sybase365.mobiliser.web.consumer.pages.portal.manageaccounts;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.web.common.model.IManageAccountsViewer;
import com.sybase365.mobiliser.web.common.panels.AccountsPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_ACCOUNTS)
public class ManageAccountPage extends BaseManageAccountsPage implements
	IManageAccountsViewer {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ManageAccountPage.class);
    private AccountsPanel accountsPanel;

    public ManageAccountPage() {
	super();
    }

    public ManageAccountPage(PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();
	accountsPanel = new AccountsPanel("accountsPanel", this, this,
		getMobiliserWebSession().getLoggedInCustomer().getCustomerId(),
		false);
	add(accountsPanel);

    }

    @Override
    protected Class getActiveMenu() {
	return ManageAccountPage.class;
    }

    public void addBankAccount() {
	LOG.debug("# ManageAccountPage.addBankAccount()");
	accountsPanel.walletEntries = null;
	setResponsePage(new BankAccountDataPage(ManageAccountPage.class, null,
		null));
    }

    public void editBankAccount(WalletEntry walletEntry) {
	LOG.debug("# ManageAccountPage.editBankAccount()");
	accountsPanel.walletEntries = null;
	setResponsePage(new BankAccountDataPage(ManageAccountPage.class,
		walletEntry, null));
    }

    public void addCreditCard() {
	LOG.debug("# ManageAccountPage.addCreditCard()");
	accountsPanel.walletEntries = null;
	setResponsePage(new CreditCardDataPage());
    }

    public void addFunds() {
	LOG.debug("# ManageAccountPage.addFunds()");
	setResponsePage(new AddFunds(accountsPanel.getBankAccounts(),
		accountsPanel.getCreditCardAccounts()));
    }

    public void withdrawFunds() {
	LOG.debug("# ManageAccountPage.withdrawFunds()");
	setResponsePage(new WithdrawFunds(accountsPanel.getBankAccounts()));
    }

    @Override
    public void addExternalAccount() {
	// nothing to do here
    }

    @Override
    public void editExternalAccount(WalletEntry walletEntry) {
	// nothing to do here
    }

    @Override
    public void creditSva() {
	// nothing to do here
    }

    @Override
    public void debitSva() {
	// nothing to do here
    }

    @Override
    public void editCreditCard(WalletEntry walletEntry) {
	// nothing to do here
    }

    @Override
    public void addBalanceAlert() {
	LOG.debug("# ManageAccountPage.addBalanceAlert()");
	setResponsePage(BalanceAlertPage.class);
    }

}
