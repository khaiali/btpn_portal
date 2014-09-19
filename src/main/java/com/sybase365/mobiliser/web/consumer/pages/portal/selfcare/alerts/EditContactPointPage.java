package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerOtherIdentification;
import com.sybase365.mobiliser.web.common.panels.alerts.EditContactPointPanel;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.BaseSelfCarePage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author Pavan Raya
 */
@AuthorizeInstantiation(Constants.PRIV_CONTACT_POINT)
public class EditContactPointPage extends BaseSelfCarePage {

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
