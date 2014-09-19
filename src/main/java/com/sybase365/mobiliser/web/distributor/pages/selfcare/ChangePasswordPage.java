package com.sybase365.mobiliser.web.distributor.pages.selfcare;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.ChangePasswordPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_MERCHANT_LOGIN)
public class ChangePasswordPage extends SelfCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ChangePasswordPage.class);

    public ChangePasswordPage() {
	super();

	LOG.info("Created new ChangePasswordPage");
    }

    public ChangePasswordPage(final PageParameters parameters) {
	super(parameters);

	LOG.info("Created new ChangePasswordPage");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	ChangePasswordPanel panel = new ChangePasswordPanel(
		"changePasswordPanel", getWebSession().getLoggedInCustomer(),
		this, SelfCareHomePage.class);

	add(panel);
    }

    @Override
    protected Class getActiveMenu() {
	return ChangePasswordPage.class;
    }
}
