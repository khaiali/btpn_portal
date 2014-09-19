package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;


import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitApproveDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the ElimitApproveDetailsPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitApproveDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitApproveDetailsPage() {
		super();
		initPageComponents();
	}
	
	
	public ElimitApproveDetailsPage(ElimitBean limitBean){
		
		super();
		this.limitBean = limitBean;
		initPageComponents();
		
	}

	protected void initPageComponents() {
		add(new ElimitApproveDetailsPanel("limitApproveDetailsPanel", this, limitBean));
	}
	
	


	

	


	
	
}
