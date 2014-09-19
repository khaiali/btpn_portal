package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import org.apache.wicket.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.framework.contract.v5_0.base.Locale;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.common.panels.alerts.ContactPointsPanel;
import com.sybase365.mobiliser.web.cst.pages.customercare.CustomerCareMenuGroup;

/**
 * @author sagraw03
 */
public class ContactPointsPage extends CustomerCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ContactPointsPage.class);

    @SpringBean(name = "systemAuthAlertsClientLogic")
    protected AlertsClientLogic clientLogic;

    @SpringBean(name = "smartAuthMBankingClientLogic")
    protected MBankingClientLogic mBankingClientLogic;

    private ContactPointsPanel contactPointsPanel;

    public ContactPointsPage() {
	super();
    }

    public ContactPointsPage(PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	LOG.debug("# Inside ContactPointsPage initOwnPageComponents");
	Locale locale = new com.sybase365.mobiliser.framework.contract.v5_0.base.Locale();
	locale.setCountry(getMobiliserWebSession().getCustomer().getAddress()
		.getKvCountry());
	locale
		.setLanguage(getMobiliserWebSession().getCustomer()
			.getLanguage());

	contactPointsPanel = new ContactPointsPanel("contactPointsPanel", this,
		clientLogic, mBankingClientLogic, getMobiliserWebSession()
			.getCustomer().getId(), getMobiliserWebSession()
			.getCustomer().getOrgUnitId(), locale, false,
		AddOtherContactPointPage.class, EditOtherContactPointPage.class);
	add(contactPointsPanel);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<ContactPointsPage> getActiveMenu() {
	return ContactPointsPage.class;
    }
}
