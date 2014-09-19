package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the ElimitApprovePage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitApprovePage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	ElimitBean limitBean;

	public ElimitApprovePage() {
		super();
		initPageComponents();
	}
	
	
	public ElimitApprovePage(ElimitBean bean){
		
		super();
		this.limitBean = bean;
		initPageComponents();
		
	}

	protected void initPageComponents() {
		add(new ElimitApprovePanel("limitApprovePanel", this));
	}
	
}
