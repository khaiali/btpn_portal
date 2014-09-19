package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the ElimitDetailsPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitDetailsPage() {
		super();
		initPageComponents();
	}
	
	
	public ElimitDetailsPage(ElimitBean bean){
		
		super();
		this.limitBean = bean;
		initPageComponents();
		
	}

	protected void initPageComponents() {
		add(new ElimitDetailsPanel("limitDetailsPanel", this, limitBean));
	}
			
}