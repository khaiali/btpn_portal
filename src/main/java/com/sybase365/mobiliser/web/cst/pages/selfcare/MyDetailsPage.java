package com.sybase365.mobiliser.web.cst.pages.selfcare;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CST_LOGIN)
public class MyDetailsPage extends CstHomeMenuGroup {

    private static final long serialVersionUID = 1L;

    public MyDetailsPage() {
	super();
    }

    public MyDetailsPage(PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	Customer customer = ((BaseWebSession) getWebSession())
		.getLoggedInCustomer();
	add(new Label("id", String.valueOf(customer.getCustomerId())));
	add(new Label("displayName", customer.getDisplayName()));
	add(new Label("loginName", customer.getUsername()));
	add(new FeedbackPanel("errorMessages"));

    }
}
