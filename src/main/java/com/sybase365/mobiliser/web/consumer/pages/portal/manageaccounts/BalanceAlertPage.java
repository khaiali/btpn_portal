package com.sybase365.mobiliser.web.consumer.pages.portal.manageaccounts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.panels.BalanceAlertPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_ACCOUNTS)
public class BalanceAlertPage extends BaseManageAccountsPage {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BalanceAlertPage.class);

    private static final long serialVersionUID = 1L;

    private BalanceAlertPanel balanceAlertPanel;

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	CustomerBean customer = Converter.getInstance(getConfiguration())
		.getCustomerBeanFromCustomer(
			getMobiliserWebSession().getLoggedInCustomer());
	balanceAlertPanel = new BalanceAlertPanel("balanceAlert.panel", this,
		customer);

	add(balanceAlertPanel);
    }

    @Override
    protected Class getActiveMenu() {
	return ManageAccountPage.class;
    }
}