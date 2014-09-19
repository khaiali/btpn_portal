package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitApproveConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the ElimitApproveConfirmPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitApproveConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitApproveConfirmPage() {
		super();
		initPageComponents();
	}
	
	public ElimitApproveConfirmPage(ElimitBean bean){		
		super();
		this.limitBean = bean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ElimitApproveConfirmPanel("limitApproveConfirmPanel", this, limitBean));
	}
}