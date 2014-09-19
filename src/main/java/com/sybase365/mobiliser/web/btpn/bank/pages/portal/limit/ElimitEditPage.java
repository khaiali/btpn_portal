package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitEditPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the ElimitEditPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitEditPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitEditPage() {
		super();
		initPageComponents();
	}
	
	
	public ElimitEditPage(ElimitBean bean){
		
		super();
		this.limitBean = bean;
		initPageComponents();
		
	}

	protected void initPageComponents() {
		add(new ElimitEditPanel("limitEditPanel", this, limitBean));
	}
	
	


	

	


	
	
}
