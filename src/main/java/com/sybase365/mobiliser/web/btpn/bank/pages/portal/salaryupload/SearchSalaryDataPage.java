package com.sybase365.mobiliser.web.btpn.bank.pages.portal.salaryupload;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.SearchSalaryDataPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the Salary upload for Bank Portals.
 * 
 * @author Vikram Gunda
 */
public class SearchSalaryDataPage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public SearchSalaryDataPage() {
		super();
		initThisPageComponents(BtpnConstants.FILE_TYPE_SALARY_UPLOAD);
	}
	
	/**
	 * Constructor for this page.
	 */
	public SearchSalaryDataPage(int fileTypeId) {
		super();
		initThisPageComponents(fileTypeId);
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();

	}

	protected void initThisPageComponents(int fileTypeId) {
		add(new SearchSalaryDataPanel("searchSalaryDataPanel", this, fileTypeId));
	}

}
