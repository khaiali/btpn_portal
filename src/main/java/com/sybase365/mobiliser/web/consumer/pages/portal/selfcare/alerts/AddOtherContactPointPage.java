package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.common.panels.alerts.OtherContactPointPanel;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.BaseSelfCarePage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author Sushil.agrawala
 */
@AuthorizeInstantiation(Constants.PRIV_CONTACT_POINT)
public class AddOtherContactPointPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "systemAuthAlertsClientLogic")
    protected AlertsClientLogic clientLogic;

    public AddOtherContactPointPage() {
	super();
	add(new OtherContactPointPanel("addOtherContactPointPanel",
		clientLogic, null, getMobiliserWebSession()
			.getLoggedInCustomer().getCustomerId(),
		Constants.OPERATION_ADD, ContactPointsPage.class));
    }

    @Override
    protected Class<ContactPointsPage> getActiveMenu() {
	return ContactPointsPage.class;
    }

}
