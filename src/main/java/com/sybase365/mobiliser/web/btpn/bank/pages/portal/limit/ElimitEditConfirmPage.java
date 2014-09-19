package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitEditConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the ElimitEditConfirmPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitEditConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitEditConfirmPage() {
		super();
		initPageComponents();
	}
	
	
	public ElimitEditConfirmPage(ElimitBean bean){
		
		super();
		this.limitBean = bean;
		initPageComponents();
		
	}

	protected void initPageComponents() {
		add(new ElimitEditConfirmPanel("limitDetailsEditPanel", this, limitBean));
	}
	
	
	
}
