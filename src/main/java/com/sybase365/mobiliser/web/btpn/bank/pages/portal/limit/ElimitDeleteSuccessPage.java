package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitDeleteSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the ElimitDeleteSuccessPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitDeleteSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitDeleteSuccessPage() {
		super();
		initPageComponents();
	}
	
	public ElimitDeleteSuccessPage(ElimitBean limitBean){
		
		super();
		this.limitBean = limitBean;
		initPageComponents();
		
	}

	protected void initPageComponents() {
		add(new ElimitDeleteSuccessPanel("limitDeleteSuccessPanel", this, limitBean));
	}
	
}