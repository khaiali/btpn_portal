package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.alerts;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.model.IAlertFilter;
import com.sybase365.mobiliser.web.common.panels.alerts.MobileAlertsOptionsPanel;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.BaseSelfCarePage;
import com.sybase365.mobiliser.web.util.AuthorizeInstantiationAlertFilter;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.ServicePackageAlertFilter;

/**
 * @author msw
 * 
 */
@AuthorizeInstantiation(Constants.PRIV_SHOW_ALERT)
public class MobileAlertsOptionsPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(MobileAlertsOptionsPage.class);

    private MobileAlertsOptionsPanel mobileAlertsOptionsPanel = null;

    @SpringBean(name = "systemAuthAlertsClientLogic")
    protected AlertsClientLogic clientLogic;

    @SpringBean(name = "systemAuthMBankingClientLogic")
    protected MBankingClientLogic mBankingClientLogic;

    public MobileAlertsOptionsPage() {
	super();
    }

    public MobileAlertsOptionsPage(PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	try {
	    AuthorizeInstantiationAlertFilter alertFilter;

	    // an mbanking consumer will restrict alerts shown by service
	    // package
	    if (getMobiliserWebSession().getLoggedInCustomer()
		    .getCustomerTypeId() == Constants.MBANKING_CUSTOMER_TYPE) {
		alertFilter = new ServicePackageAlertFilter(getWebSession(),
			this, mBankingClientLogic);

	    } else {
		alertFilter = new AuthorizeInstantiationAlertFilter(
			getWebSession(), this);
	    }

	    LOG.trace("inside MobileAlertsOptionsPage ********************");
	    Class<MobileAlertsOptionsPanel> mobileAlertsOptionsPanelClass = MobileAlertsOptionsPanel.class;
	    Class<MobileAlertsPage> mobileAlertsPageClass = MobileAlertsPage.class;

	    mobileAlertsOptionsPanel = mobileAlertsOptionsPanelClass
		    .getConstructor(String.class, MobiliserBasePage.class,
			    AlertsClientLogic.class, Long.TYPE, Boolean.TYPE,
			    IAlertFilter.class, Class.class).newInstance(
			    "mobileAlertsOptionsPanel",
			    this,
			    clientLogic,
			    Long.valueOf(getMobiliserWebSession()
				    .getLoggedInCustomer().getCustomerId()),
			    Boolean.FALSE, alertFilter, mobileAlertsPageClass);

	    addOrReplace(mobileAlertsOptionsPanel);

	} catch (Exception e) {
	    LOG.warn("Couldn't get a MobileAlertsOptionsPanel class", e);
	}
    }

    @Override
    protected Class<MobileAlertsPage> getActiveMenu() {
	return MobileAlertsPage.class;
    }

}