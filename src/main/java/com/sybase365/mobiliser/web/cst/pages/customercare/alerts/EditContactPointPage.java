package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerOtherIdentification;
import com.sybase365.mobiliser.web.common.panels.alerts.EditContactPointPanel;
import com.sybase365.mobiliser.web.cst.pages.customercare.CustomerCareMenuGroup;

/**
 * @author Pavan Raya
 */
public class EditContactPointPage extends CustomerCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private EditContactPointPanel editContactPointPanel;
    private CustomerOtherIdentification entry;

    public EditContactPointPage() {
	super();
    }

    public EditContactPointPage(CustomerOtherIdentification entry) {
	this.entry = entry;
	editContactPointPanel = new EditContactPointPanel(
		"editContactPointPanel", this, entry);
	add(editContactPointPanel);
    }

    @Override
    protected Class<ContactPointsPage> getActiveMenu() {
	return ContactPointsPage.class;
    }
}
