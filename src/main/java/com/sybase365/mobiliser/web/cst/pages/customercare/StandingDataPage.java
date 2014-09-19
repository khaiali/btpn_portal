package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.IHeaderResponse;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.panels.StandingDataPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CUST_READ)
public class StandingDataPage extends CustomerCareMenuGroup {

    CustomerBean customer;

    public StandingDataPage() {
	super();
	this.customer = getCustomer();
	initPageComponents();
    }

    public StandingDataPage(CustomerBean customer) {
	super();
	this.customer = customer;
	initPageComponents();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public StandingDataPage(final PageParameters parameters) {
	super(parameters);
    }

    public void renderHead(IHeaderResponse response) {

    }

    protected void initPageComponents() {
	add(new StandingDataPanel("standingDataPanel", this, this.customer));
    }

    public CustomerBean getCustomer() {
	return getMobiliserWebSession().getCustomer();
    }
}
