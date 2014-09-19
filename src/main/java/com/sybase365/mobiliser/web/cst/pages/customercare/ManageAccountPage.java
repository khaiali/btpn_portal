package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.web.common.model.IManageAccountsViewer;
import com.sybase365.mobiliser.web.common.panels.AccountsPanel;

public class ManageAccountPage extends CustomerCareMenuGroup implements
	IManageAccountsViewer {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(ManageAccountPage.class);

    private AccountsPanel accountsPanel;

    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	accountsPanel = new AccountsPanel("accountsPanel", this, this,
		getMobiliserWebSession().getCustomer().getId(), true);
	add(accountsPanel);
    }

    @Override
    public void addBankAccount() {
	LOG.debug("# ManageAccountPage.addBankAccount()");
	accountsPanel.walletEntries = null;
	setResponsePage(new BankAccountDataPage(null, ManageAccountPage.class));

    }

    @Override
    public void editBankAccount(WalletEntry walletEntry) {
	LOG.debug("# ManageAccountPage.editBankAccount()");
	accountsPanel.walletEntries = null;
	setResponsePage(new BankAccountDataPage(walletEntry,
		ManageAccountPage.class));
    }

    @Override
    public void addCreditCard() {
	LOG.debug("# ManageAccountPage.addCreditCard()");
	accountsPanel.walletEntries = null;
	setResponsePage(new CreditCardDataPage(null, ManageAccountPage.class));
    }

    @Override
    public void addFunds() {
	LOG.debug("# ManageAccountPage.addFunds()");
	accountsPanel.walletEntries = null;
	setResponsePage(new AddFundsPage(accountsPanel.getBankAccounts(),
		accountsPanel.getCreditCardAccounts()));
    }

    @Override
    public void withdrawFunds() {
	LOG.debug("# ManageAccountPage.withdrawFunds()");
	accountsPanel.walletEntries = null;
	setResponsePage(new WithdrawFundsPage(accountsPanel.getBankAccounts()));
    }

    @Override
    public void addExternalAccount() {
	LOG.debug("# ManageAccountPage.addExternalAccount()");
	accountsPanel.walletEntries = null;
	setResponsePage(new ExternalAccountDataPage(null,
		ManageAccountPage.class));
    }

    @Override
    public void editExternalAccount(WalletEntry walletEntry) {
	LOG.debug("# ManageAccountPage.editExternalAccount()");
	accountsPanel.walletEntries = null;
	setResponsePage(new ExternalAccountDataPage(walletEntry,
		ManageAccountPage.class));
    }

    @Override
    public void creditSva() {
	LOG.debug("# ManageAccountPage.creditSva()");
	accountsPanel.walletEntries = null;
	setResponsePage(CreditSvaPage.class);
    }

    @Override
    public void debitSva() {
	LOG.debug("# ManageAccountPage.debitSva()");
	accountsPanel.walletEntries = null;
	setResponsePage(DebitSvaPage.class);
    }

    @Override
    public void editCreditCard(WalletEntry walletEntry) {
	LOG.debug("# ManageAccountPage.editCreditCard()");
	accountsPanel.walletEntries = null;
	setResponsePage(new CreditCardDataPage(walletEntry,
		ManageAccountPage.class));
    }

    @Override
    public void addBalanceAlert() {
	LOG.debug("# ManageAccountPage.addBalanceAlert()");
	setResponsePage(BalanceAlertPage.class);

    }

}
