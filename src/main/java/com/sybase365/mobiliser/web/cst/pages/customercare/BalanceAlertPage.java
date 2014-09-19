package com.sybase365.mobiliser.web.cst.pages.customercare;

import com.sybase365.mobiliser.web.common.panels.BalanceAlertPanel;

public class BalanceAlertPage extends CustomerCareMenuGroup {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BalanceAlertPage.class);

    private static final long serialVersionUID = 1L;

    private BalanceAlertPanel balanceAlertPanel;

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	balanceAlertPanel = new BalanceAlertPanel("balanceAlert.panel", this,
		getMobiliserWebSession().getCustomer());

	add(balanceAlertPanel);
    }

    @Override
    protected Class getActiveMenu() {
	return ManageAccountPage.class;
    }
}
