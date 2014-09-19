package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.common.panels.alerts.OtherContactPointPanel;
import com.sybase365.mobiliser.web.cst.pages.customercare.CustomerCareMenuGroup;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author Sushil.agrawala
 */
public class AddOtherContactPointPage extends CustomerCareMenuGroup {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "systemAuthAlertsClientLogic")
    protected AlertsClientLogic clientLogic;

    public AddOtherContactPointPage() {
	super();
	add(new OtherContactPointPanel("addOtherContactPointPanel",
		clientLogic, null, getMobiliserWebSession().getCustomer()
			.getId(), Constants.OPERATION_ADD,
		ContactPointsPage.class));
    }

    @Override
    protected Class<ContactPointsPage> getActiveMenu() {
	return ContactPointsPage.class;
    }

}
