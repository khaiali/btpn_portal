package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerOtherIdentification;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.common.panels.alerts.OtherContactPointPanel;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.BaseSelfCarePage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author Sushil.agrawala
 */
@AuthorizeInstantiation(Constants.PRIV_CONTACT_POINT)
public class EditOtherContactPointPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "systemAuthAlertsClientLogic")
    protected AlertsClientLogic clientLogic;

    public EditOtherContactPointPage(
	    CustomerOtherIdentification customerOtherIdentification) {
	super();
	add(new OtherContactPointPanel("editOtherContactPanel", clientLogic,
		customerOtherIdentification, getMobiliserWebSession()
			.getLoggedInCustomer().getCustomerId(),
		Constants.OPERATION_EDIT, ContactPointsPage.class));
    }

    @Override
    protected Class<ContactPointsPage> getActiveMenu() {
	return ContactPointsPage.class;
    }

}
