package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.panels.BillConfigurationListPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CUST_WRITE)
public class BillConfigurationListPage extends CustomerCareMenuGroup {
    private static final Logger LOG = LoggerFactory
	    .getLogger(BillConfigurationListPage.class);

    public BillConfigurationListPage() {
	super();
    }

    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Long customerId = getMobiliserWebSession().getCustomer().getId();

	add(new BillConfigurationListPanel("billConfigurationListPanel",
		BillConfigurationListPage.this, customerId) {

	    @Override
	    public MobiliserBasePage getAddBillConfigurationPage() {
		return new AddBillConfigurationPage();
	    }

	    @Override
	    public MobiliserBasePage getBillConfigurationListPage() {
		return new BillConfigurationListPage();
	    }

	    @Override
	    public MobiliserBasePage getEditBillConfigurationPage(
		    InvoiceConfiguration entry) {
		return new EditBillConfigurationPage(entry);
	    }

	});
    }

}
