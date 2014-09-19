package com.sybase365.mobiliser.web.btpn.bank.pages.portal.salaryupload;

import java.util.List;

import com.sybase365.mobiliser.web.btpn.bank.beans.SalaryDataErrorBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.SalaryDataErrorPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Salary upload for Bank Portals.
 * 
 * @author Vikram Gunda
 */
public class SalaryDataErrorPage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public SalaryDataErrorPage(int fileTypeId, final String fileName, final List<SalaryDataErrorBean> errorList) {
		super();
		initThisPageComponents(fileName, errorList, fileTypeId);
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	protected void initThisPageComponents(final String fileName, final List<SalaryDataErrorBean> errorList,
		int fileTypeId) {
		add(new SalaryDataErrorPanel("salaryDataErrorPanel", this, fileName, errorList, fileTypeId));
	}
}
