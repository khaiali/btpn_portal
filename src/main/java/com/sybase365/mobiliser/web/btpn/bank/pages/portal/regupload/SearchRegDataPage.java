package com.sybase365.mobiliser.web.btpn.bank.pages.portal.regupload;

import com.sybase365.mobiliser.web.btpn.bank.pages.portal.salaryupload.SearchSalaryDataPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the page for searching the bulk registration data uploaded using a CSV file. It shows the validation status
 * and file name.
 * 
 * @author Vikram Gunda
 */
public class SearchRegDataPage extends SearchSalaryDataPage {

	/**
	 * Constructor for this page.
	 */
	public SearchRegDataPage() {
		super(BtpnConstants.FILE_TYPE_REG_UPLOAD);
	}

}
