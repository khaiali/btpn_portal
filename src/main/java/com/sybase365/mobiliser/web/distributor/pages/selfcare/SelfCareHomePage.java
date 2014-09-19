package com.sybase365.mobiliser.web.distributor.pages.selfcare;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_MERCHANT_LOGIN)
public class SelfCareHomePage extends SelfCareMenuGroup {

    private static final long serialVersionUID = 1L;

    public SelfCareHomePage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public SelfCareHomePage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Customer customer = ((BaseWebSession) getWebSession())
	        .getLoggedInCustomer();

	add(new FeedbackPanel("errorMessages"));

	// Make sure that the logged in agent has a meaningful localei n the
	// session context. Otherwise the amount formatting will fail
	// getWebSession().setLocale(
	// getLocale(customer.getLanguage(), customer.getCountry(), null));
    }

}
