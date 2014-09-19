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
import com.sybase365.mobiliser.web.common.panels.alerts.MobileAlertsPanel;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.BaseSelfCarePage;
import com.sybase365.mobiliser.web.util.AuthorizeInstantiationAlertFilter;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.ServicePackageAlertFilter;

/**
 * @author Pavan Raya
 * 
 */
@AuthorizeInstantiation(Constants.PRIV_SHOW_ALERT)
public class MobileAlertsPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(MobileAlertsPage.class);

    private MobileAlertsPanel mobileAlertsPanel = null;

    @SpringBean(name = "systemAuthAlertsClientLogic")
    protected AlertsClientLogic clientLogic;

    @SpringBean(name = "smartAuthMBankingClientLogic")
    protected MBankingClientLogic mBankingClientLogic;

    public MobileAlertsPage() {
	super();
    }

    public MobileAlertsPage(PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	try {
	    IAlertFilter alertFilter;

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

	    Class<MobileAlertsPanel> mobileAlertsPanelClass = MobileAlertsPanel.class;
	    Class<MobileAlertsOptionsPage> mobileAlertsOptionsPageClass = MobileAlertsOptionsPage.class;

	    mobileAlertsPanel = mobileAlertsPanelClass.getConstructor(
		    String.class, MobiliserBasePage.class,
		    AlertsClientLogic.class, Long.TYPE, Boolean.TYPE,
		    IAlertFilter.class, Class.class).newInstance(
		    "mobileAlertsPanel",
		    this,
		    clientLogic,
		    Long.valueOf(getMobiliserWebSession().getLoggedInCustomer()
			    .getCustomerId()), Boolean.FALSE, alertFilter,
		    mobileAlertsOptionsPageClass);

	    add(mobileAlertsPanel);
	} catch (Exception e) {
	    LOG.warn("Couldn't get a MobileAlertsPanel class", e);
	}
    }

    @Override
    protected Class<MobileAlertsPage> getActiveMenu() {
	return MobileAlertsPage.class;
    }

}
