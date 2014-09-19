package com.sybase365.mobiliser.web.cst.pages.selfcare;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerRole;
import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CST_LOGIN)
public class MyPrivilegesPage extends CstHomeMenuGroup {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(MyPrivilegesPage.class);

    public MyPrivilegesPage() {
	super();
    }

    public MyPrivilegesPage(PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	Customer customer = ((BaseWebSession) getWebSession())
		.getLoggedInCustomer();
	try {
	    List<String> privList = getAllCustomerPrivileges(customer
		    .getCustomerId());
	    add(new ListView<String>("privilegeView", privList) {

		@Override
		protected void populateItem(ListItem<String> item) {

		    item.add(new Label("privilege", (String) item
			    .getModelObject()));

		}

	    });

	    List<CustomerRole> roles = getRoles(customer.getCustomerId());
	    List<String> rolesToDisplay = new ArrayList<String>();
	    for (CustomerRole role : roles) {
		rolesToDisplay.add(role.getRole());
	    }
	    add(new ListView<String>("roleView", rolesToDisplay) {

		@Override
		protected void populateItem(ListItem<String> item) {

		    item.add(new Label("role", (String) item.getModelObject()));

		}

	    });
	} catch (Exception e) {
	    LOG.error("# Error while getting all available roles/privileges.",
		    e);

	}
	add(new FeedbackPanel("errorMessages"));

    }

}
