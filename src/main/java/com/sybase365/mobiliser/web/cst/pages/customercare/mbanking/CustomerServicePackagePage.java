package com.sybase365.mobiliser.web.cst.pages.customercare.mbanking;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.common.panels.mbanking.CustomerServicePackagePanel;
import com.sybase365.mobiliser.web.cst.pages.customercare.CustomerCareMenuGroup;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.ServicePackageAlertFilter;

@AuthorizeInstantiation(Constants.PRIV_CST_MBANKING)
public class CustomerServicePackagePage extends CustomerCareMenuGroup {

    @SpringBean(name = "systemAuthMBankingClientLogic")
    protected MBankingClientLogic mBankingClientLogic;

    @SpringBean(name = "systemAuthAlertsClientLogic")
    protected AlertsClientLogic alertsClientLogic;

    public CustomerServicePackagePage() {
	super();
	ServicePackageAlertFilter alertFilter = new ServicePackageAlertFilter(
		getWebSession(), this, mBankingClientLogic);

	add(new CustomerServicePackagePanel("servicePackagesPanel", this,
		mBankingClientLogic, alertsClientLogic, alertFilter,
		getMobiliserWebSession().getCustomer().getId(),
		getMobiliserWebSession().getCustomer().getOrgUnitId()));
    }

    @Override
    protected Class<CustomerServicePackagePage> getActiveMenu() {
	return CustomerServicePackagePage.class;
    }

}