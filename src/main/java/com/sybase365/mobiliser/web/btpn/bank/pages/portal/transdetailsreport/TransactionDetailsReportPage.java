package com.sybase365.mobiliser.web.btpn.bank.pages.portal.transdetailsreport;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.TransactionDetailsReportPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This class is Transaction details page in bank portal for Agent.
 * 
 * page displays the transaction details.
 * 
 * @author Vikram Gunda
 */
public class TransactionDetailsReportPage extends
		BtpnBaseBankPortalSelfCarePage {

	public TransactionDetailsReportPage() {
		initPageComponents();
	}

	public void initPageComponents() {
		add(new TransactionDetailsReportPanel("txnDetailsReportPanel", this));
	}
}
