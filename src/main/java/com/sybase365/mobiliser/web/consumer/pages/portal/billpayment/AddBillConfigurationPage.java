package com.sybase365.mobiliser.web.consumer.pages.portal.billpayment;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.panels.BillConfigurationPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_BILL_PAYMENT)
public class AddBillConfigurationPage extends BaseBillPaymentPage {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(AddBillConfigurationPage.class);

    public AddBillConfigurationPage() {
	super();
	initPageComponents();
    }

    public AddBillConfigurationPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected Class getActiveMenu() {
	return BillConfigurationListPage.class;
    }

    protected void initPageComponents() {
	Long customerId = getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId();

	add(new BillConfigurationPanel("addBillConfigurationPanel", customerId,
		AddBillConfigurationPage.this, null) {

	    @Override
	    public MobiliserBasePage getBillConfigurationListPage() {
		return new BillConfigurationListPage();
	    }

	});

    }
}
