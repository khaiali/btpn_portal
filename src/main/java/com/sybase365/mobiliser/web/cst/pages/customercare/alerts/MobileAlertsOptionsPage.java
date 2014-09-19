package com.sybase365.mobiliser.web.cst.pages.customercare.alerts;

import org.apache.wicket.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.model.IAlertFilter;
import com.sybase365.mobiliser.web.common.panels.alerts.MobileAlertsOptionsPanel;
import com.sybase365.mobiliser.web.cst.pages.customercare.CustomerCareMenuGroup;
import com.sybase365.mobiliser.web.util.AuthorizeInstantiationAlertFilter;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.ServicePackageAlertFilter;

/**
 * @author msw
 * 
 */
public class MobileAlertsOptionsPage extends CustomerCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(MobileAlertsOptionsPage.class);

    private MobileAlertsOptionsPanel mobileAlertsOptionsPanel = null;

    @SpringBean(name = "systemAuthAlertsClientLogic")
    protected AlertsClientLogic clientLogic;

    @SpringBean(name = "smartAuthMBankingClientLogic")
    protected MBankingClientLogic mBankingClientLogic;

    public MobileAlertsOptionsPage() {
	super();
    }

    public MobileAlertsOptionsPage(PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	try {
	    IAlertFilter alertFilter;

	    // an mbanking customer will have available alerts restricted by
	    // service package
	    if (getMobiliserWebSession().getCustomer().getCustomerTypeId()
		    .intValue() == Constants.MBANKING_CUSTOMER_TYPE) {
		alertFilter = new ServicePackageAlertFilter(getWebSession(),
			this, mBankingClientLogic);

	    } else {
		alertFilter = new AuthorizeInstantiationAlertFilter(
			getWebSession(), this);
	    }

	    Class<MobileAlertsOptionsPanel> mobileAlertsOptionsPanelClass = MobileAlertsOptionsPanel.class;
	    Class<MobileAlertsPage> mobileAlertsPageClass = MobileAlertsPage.class;

	    mobileAlertsOptionsPanel = mobileAlertsOptionsPanelClass
		    .getConstructor(String.class, MobiliserBasePage.class,
			    AlertsClientLogic.class, Long.TYPE, Boolean.TYPE,
			    IAlertFilter.class, Class.class).newInstance(
			    "mobileAlertsOptionsPanel", this, clientLogic,
			    getMobiliserWebSession().getCustomer().getId(),
			    true, alertFilter, mobileAlertsPageClass);

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