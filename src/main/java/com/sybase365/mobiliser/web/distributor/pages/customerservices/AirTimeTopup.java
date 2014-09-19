package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.AirtimeTopUpPanel;
import com.sybase365.mobiliser.web.distributor.pages.selfcare.SelfCareHomePage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_WALLET_SERVICES)
public class AirTimeTopup extends BaseCustomerServicesPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AirTimeTopup.class);

    public AirTimeTopup() {
	super();
	add(new AirtimeTopUpPanel("topUpPanel", this, SelfCareHomePage.class,
		getMobiliserWebSession().getLoggedInCustomer().getCustomerId(),
		getMobiliserWebSession().getLoggedInCustomer()
			.getCustomerTypeId(), null));
    }

}
