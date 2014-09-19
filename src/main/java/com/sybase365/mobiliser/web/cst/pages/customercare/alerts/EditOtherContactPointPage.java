package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerOtherIdentification;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.common.panels.alerts.OtherContactPointPanel;
import com.sybase365.mobiliser.web.cst.pages.customercare.CustomerCareMenuGroup;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author Sushil.agrawala
 */
public class EditOtherContactPointPage extends CustomerCareMenuGroup {

    @SpringBean(name = "systemAuthAlertsClientLogic")
    protected AlertsClientLogic clientLogic;

    public EditOtherContactPointPage(
	    CustomerOtherIdentification customerOtherIdentification) {
	super();
	add(new OtherContactPointPanel("editOtherContactPanel", clientLogic,
		customerOtherIdentification, getMobiliserWebSession()
			.getCustomer().getId(), Constants.OPERATION_EDIT,
		ContactPointsPage.class));
    }

    @Override
    protected Class<ContactPointsPage> getActiveMenu() {
	return ContactPointsPage.class;
    }

}
