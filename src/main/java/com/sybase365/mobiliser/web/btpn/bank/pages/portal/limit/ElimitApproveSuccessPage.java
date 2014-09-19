package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;


import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitApproveSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the ElimitApprovePage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitApproveSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitApproveSuccessPage() {
		super();
		initPageComponents();
	}
	
	public ElimitApproveSuccessPage(ElimitBean bean){		
		super();
		this.limitBean = bean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ElimitApproveSuccessPanel("limitApproveSuccessPanel", this, limitBean));
	}

}