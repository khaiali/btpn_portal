package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.BillHistoryPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CUST_WRITE)
public class BillHistoryPage extends CustomerCareMenuGroup {
    public BillHistoryPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BillHistoryPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected Class getActiveMenu() {
	return this.getClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Long customerId = getMobiliserWebSession().getCustomer().getId();

	BillHistoryPanel historyPanel = new BillHistoryPanel(
		"billHistoryPanel", BillHistoryPage.this, customerId);
	add(historyPanel);

    }
}
