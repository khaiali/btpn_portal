package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitCreateConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the ElimitCreateConfirmPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitCreateConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitCreateConfirmPage() {
		super();
		initPageComponents();
	}
	
	public ElimitCreateConfirmPage(ElimitBean bean){		
		super();
		this.limitBean = bean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ElimitCreateConfirmPanel("limitCreateConfirmPanel", this, limitBean));
	}

}