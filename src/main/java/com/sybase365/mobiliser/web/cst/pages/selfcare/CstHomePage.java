package com.sybase365.mobiliser.web.cst.pages.selfcare;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CST_LOGIN)
public class CstHomePage extends CstHomeMenuGroup {

    private static final long serialVersionUID = 1L;

    public CstHomePage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public CstHomePage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	Customer customer = ((BaseWebSession) getWebSession())
		.getLoggedInCustomer();
	add(new Label("customerName", customer.getDisplayName()));
	add(new FeedbackPanel("errorMessages"));

    }
}
