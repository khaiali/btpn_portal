package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitRejectConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the ElimitRejectConfirmPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitRejectConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitRejectConfirmPage() {
		super();
		initPageComponents();
	}
	
	public ElimitRejectConfirmPage(ElimitBean limitBean){		
		super();
		this.limitBean = limitBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ElimitRejectConfirmPanel("limitRejectConfirmPanel", this, limitBean));
	}

}