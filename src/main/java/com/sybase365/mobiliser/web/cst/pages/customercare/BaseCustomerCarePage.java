package com.sybase365.mobiliser.web.cst.pages.customercare;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.cst.pages.BaseCstPage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CST_LOGIN)
public class BaseCustomerCarePage extends BaseCstPage {

    public BaseCustomerCarePage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BaseCustomerCarePage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	return entries;
    }

}
