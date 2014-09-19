package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.AirtimeTopUpPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_AIRTIME_TOPUP)
public class AirTimeTopupFriendPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;
    private String msisdn;

    public AirTimeTopupFriendPage(String msisdn) {
	super();
	this.msisdn = msisdn;
	add(new AirtimeTopUpPanel("topUpPanel", this, FriendsListPage.class,
		getMobiliserWebSession().getLoggedInCustomer().getCustomerId(),
		getMobiliserWebSession().getLoggedInCustomer()
			.getCustomerTypeId(), this.msisdn));
    }

    public AirTimeTopupFriendPage() {
	super();
	add(new AirtimeTopUpPanel("topUpPanel", this, FriendsListPage.class,
		getMobiliserWebSession().getLoggedInCustomer().getCustomerId(),
		getMobiliserWebSession().getLoggedInCustomer()
			.getCustomerTypeId(), this.msisdn));
    }

    @Override
    protected Class getActiveMenu() {
	return FriendsListPage.class;
    }

}
