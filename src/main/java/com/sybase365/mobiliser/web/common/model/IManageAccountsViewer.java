package com.sybase365.mobiliser.web.common.model;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;

public interface IManageAccountsViewer {
    void addBankAccount();

    void editBankAccount(WalletEntry walletEntry);

    void addCreditCard();

    void editCreditCard(WalletEntry walletEntry);

    void addFunds();

    void withdrawFunds();

    void addExternalAccount();

    void editExternalAccount(WalletEntry walletEntry);

    void creditSva();

    void debitSva();

    void addBalanceAlert();
}
