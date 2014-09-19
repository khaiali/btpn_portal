package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitCreateSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the ElimitCreateSuccessPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitCreateSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	ElimitBean limitBean;

	public ElimitCreateSuccessPage() {
		super();
		initPageComponents();
	}
	
	public ElimitCreateSuccessPage(ElimitBean limitBean){		
		super();
		this.limitBean = limitBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ElimitCreateSuccessPanel("limitCreateSuccessPanel", this, limitBean));
	}

}