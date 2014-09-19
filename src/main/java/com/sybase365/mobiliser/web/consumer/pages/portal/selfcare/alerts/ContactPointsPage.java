package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.framework.contract.v5_0.base.Locale;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.common.panels.alerts.ContactPointsPanel;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.BaseSelfCarePage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author sagraw03
 */
@AuthorizeInstantiation(Constants.PRIV_CONTACT_POINT)
public class ContactPointsPage extends BaseSelfCarePage {

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
	locale.setCountry(getMobiliserWebSession().getLoggedInCustomer()
		.getCountry());
	locale.setLanguage(getMobiliserWebSession().getLoggedInCustomer()
		.getLanguage());

	contactPointsPanel = new ContactPointsPanel("contactPointsPanel", this,
		clientLogic, mBankingClientLogic, getMobiliserWebSession()
			.getLoggedInCustomer().getCustomerId(),
		getMobiliserWebSession().getLoggedInCustomer().getOrgUnitId(),
		locale, false, AddOtherContactPointPage.class,
		EditOtherContactPointPage.class);

	add(contactPointsPanel);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<ContactPointsPage> getActiveMenu() {
	return ContactPointsPage.class;
    }

}
