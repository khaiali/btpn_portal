package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.distributor.pages.BaseDistributorPage;

public class BaseCustomerServicesPage extends BaseDistributorPage {

    public BaseCustomerServicesPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BaseCustomerServicesPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	return entries;
    }

}
