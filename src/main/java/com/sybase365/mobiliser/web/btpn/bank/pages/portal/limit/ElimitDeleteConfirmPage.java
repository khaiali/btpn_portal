package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitDeleteConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the ElimitDeleteConfirmPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitDeleteConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitDeleteConfirmPage() {
		super();
		initPageComponents();
	}
	
	public ElimitDeleteConfirmPage(ElimitBean bean){	
		super();
		this.limitBean = bean;
		initPageComponents();		
	}

	protected void initPageComponents() {
		add(new ElimitDeleteConfirmPanel("limitDeleteConfirmPanel", this, limitBean));
	}
		
}
