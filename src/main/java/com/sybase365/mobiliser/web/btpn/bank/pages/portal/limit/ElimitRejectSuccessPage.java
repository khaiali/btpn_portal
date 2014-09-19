package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitRejectSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the ElimitRejectSuccessPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitRejectSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitRejectSuccessPage() {
		super();
		initPageComponents();
	}
	
	public ElimitRejectSuccessPage(ElimitBean limitBean){		
		super();
		this.limitBean = limitBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ElimitRejectSuccessPanel("limitRejectSuccessPanel", this, limitBean));
	}

}