package com.sybase365.mobiliser.web.cst.pages.systemconfig.mbanking;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.ServicePackage;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.common.panels.mbanking.BankServicePackagePanel;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.BaseSystemConfigurationPage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author sushil.agrawala
 * 
 */
@AuthorizeInstantiation(Constants.PRIV_CST_MBANKING)
public class BankServicePackagePage extends BaseSystemConfigurationPage {

    @SpringBean(name = "systemAuthMBankingClientLogic")
    protected MBankingClientLogic mBankingClientLogic;

    private BankServicePackagePanel bankServicePackagePanel = null;

    public BankServicePackagePage(ServicePackage entry, String action) {
	super();
	bankServicePackagePanel = new BankServicePackagePanel(
		"bankServicePackagePanel", mBankingClientLogic, this, entry,
		action);
	add(bankServicePackagePanel);
    }

    public BankServicePackagePage(PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected Class<BankServicePackagePage> getActiveMenu() {
	return BankServicePackagePage.class;
    }

}
