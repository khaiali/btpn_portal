package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.panels.BillConfigurationPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CUST_WRITE)
public class AddBillConfigurationPage extends CustomerCareMenuGroup {
    private static final Logger LOG = LoggerFactory
	    .getLogger(AddBillConfigurationPage.class);

    public AddBillConfigurationPage() {
	super();
    }

    @Override
    protected Class getActiveMenu() {
	return BillConfigurationListPage.class;
    }

    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	Long customerId = getMobiliserWebSession().getCustomer().getId();

	add(new BillConfigurationPanel("addBillConfigurationPanel", customerId,
		AddBillConfigurationPage.this, null) {

	    @Override
	    public MobiliserBasePage getBillConfigurationListPage() {
		return new BillConfigurationListPage();
	    }

	});
    }
}
