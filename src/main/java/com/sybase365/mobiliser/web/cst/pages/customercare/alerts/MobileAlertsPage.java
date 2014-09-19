package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import org.apache.wicket.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.model.IAlertFilter;
import com.sybase365.mobiliser.web.common.panels.alerts.MobileAlertsPanel;
import com.sybase365.mobiliser.web.cst.pages.customercare.CustomerCareMenuGroup;
import com.sybase365.mobiliser.web.util.AuthorizeInstantiationAlertFilter;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.ServicePackageAlertFilter;

/**
 * @author Pavan Raya
 * 
 */
public class MobileAlertsPage extends CustomerCareMenuGroup {
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

	    // an mbanking customer will have available alerts restricted by
	    // service // package
	    if (getMobiliserWebSession().getCustomer().getCustomerTypeId()
		    .intValue() == Constants.MBANKING_CUSTOMER_TYPE) {
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
		    "mobileAlertsPanel", this, clientLogic,
		    getMobiliserWebSession().getCustomer().getId(),
		    Boolean.TRUE, alertFilter, mobileAlertsOptionsPageClass);

	    addOrReplace(mobileAlertsPanel);

	} catch (Exception e) {
	    LOG.warn("Couldn't get a MobileAlertsPanel class", e);
	}
    }

    @Override
    protected Class<MobileAlertsPage> getActiveMenu() {
	return MobileAlertsPage.class;
    }

}
