package com.sybase365.mobiliser.web.cst.pages.systemconfig.mbanking;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.common.panels.mbanking.ServicePackagesPanel;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.BaseSystemConfigurationPage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * @author sushil.agrawala
 * 
 */
@AuthorizeInstantiation(Constants.PRIV_CST_MBANKING)
public class ManageServicePackagesPage extends BaseSystemConfigurationPage {

    @SpringBean(name = "systemAuthMBankingClientLogic")
    protected MBankingClientLogic clientLogic;

    private ServicePackagesPanel servicePackagesPannel = null;

    public ManageServicePackagesPage() {
	super();
    }

    public ManageServicePackagesPage(PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();
	servicePackagesPannel = new ServicePackagesPanel("servicePackagesPannel", this, clientLogic, getMobiliserWebSession()
		.getLoggedInCustomer().getOrgUnitId(), false);
	add(servicePackagesPannel);

    }

    @Override
    protected Class<ManageServicePackagesPage> getActiveMenu() {
	return ManageServicePackagesPage.class;
    }

}
