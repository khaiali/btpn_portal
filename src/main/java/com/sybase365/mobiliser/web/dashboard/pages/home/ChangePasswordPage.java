package com.sybase365.mobiliser.web.dashboard.pages.home;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.ChangePasswordPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_LOGIN)
public class ChangePasswordPage extends HomeMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ChangePasswordPage.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	ChangePasswordPanel panel = new ChangePasswordPanel(
		"changePasswordPanel", getWebSession().getLoggedInCustomer(),
		this, DashboardHomePage.class);

	add(panel);
    }
}
