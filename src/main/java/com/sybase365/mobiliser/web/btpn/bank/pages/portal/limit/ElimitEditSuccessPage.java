package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitEditSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the ElimitEditSuccessPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitEditSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitEditSuccessPage() {
		super();
		initPageComponents();
	}
	
	
	public ElimitEditSuccessPage(ElimitBean bean){
		
		super();
		this.limitBean = bean;
		initPageComponents();
		
	}

	protected void initPageComponents() {
		add(new ElimitEditSuccessPanel("limitEditSuccessPanel", this, limitBean));
	}
	
	


	

	


	
	
}
