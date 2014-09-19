package com.sybase365.mobiliser.web.consumer.pages.portal.transaction;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.AirtimeTopUpPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_AIRTIME_TOPUP)
public class AirTimeTopupPage extends BaseTransactionsPage {

    private static final long serialVersionUID = 1L;
    private String msisdn;

    public AirTimeTopupPage(String msisdn) {
	super();
	this.msisdn = msisdn;
	add(new AirtimeTopUpPanel("topUpPanel", this, ViewTransactionsPage.class,
		getMobiliserWebSession().getLoggedInCustomer().getCustomerId(),
		getMobiliserWebSession().getLoggedInCustomer()
			.getCustomerTypeId(), this.msisdn));
    }

    public AirTimeTopupPage() {
	super();
	add(new AirtimeTopUpPanel("topUpPanel", this, ViewTransactionsPage.class,
		getMobiliserWebSession().getLoggedInCustomer().getCustomerId(),
		getMobiliserWebSession().getLoggedInCustomer()
			.getCustomerTypeId(), this.msisdn));
    }

    @Override
    protected Class getActiveMenu() {
	return AirTimeTopupPage.class;
    }

}
