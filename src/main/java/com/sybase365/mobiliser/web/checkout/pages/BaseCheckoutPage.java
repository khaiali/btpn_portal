package com.sybase365.mobiliser.web.checkout.pages;

import java.util.LinkedList;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.application.pages.BaseApplicationPage;

public class BaseCheckoutPage extends BaseApplicationPage {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BaseCheckoutPage.class);

    public BaseCheckoutPage() {
	super();
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	getMobiliserWebSession().setLeftMenu(buildLeftMenu());

    }

    @Override
    protected String getApplicationName() {
	return getLocalizer().getString("checkout.application.name", this);
    }

    @Override
    protected Class getActiveMenu() {
	return null;
    }

    @Override
    protected boolean supportsSvaBalance() {
	return false;
    }

    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	return entries;
    }
}
