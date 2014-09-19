package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.panels.BillConfigurationPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CUST_WRITE)
public class EditBillConfigurationPage extends CustomerCareMenuGroup {
    private static final Logger LOG = LoggerFactory
	    .getLogger(AddBillConfigurationPage.class);

    InvoiceConfiguration invoiceConfiguration;

    public EditBillConfigurationPage(InvoiceConfiguration invoiceConfiguration) {
	super();
	this.invoiceConfiguration = invoiceConfiguration;
	initPageComponents();
    }

    protected void initPageComponents() {
	Long customerId = getMobiliserWebSession().getCustomer().getId();

	add(new BillConfigurationPanel("editBillConfigurationPanel",
		customerId, EditBillConfigurationPage.this,
		invoiceConfiguration) {

	    @Override
	    public MobiliserBasePage getBillConfigurationListPage() {
		return new BillConfigurationListPage();
	    }

	});

    }

    @Override
    protected Class getActiveMenu() {
	return BillConfigurationListPage.class;
    }
}
