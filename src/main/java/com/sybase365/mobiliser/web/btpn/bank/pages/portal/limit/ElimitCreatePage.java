package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;


import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitCreatePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the ElimitCreatePage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitCreatePage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	private ElimitBean limitBean;

	public ElimitCreatePage() {
		super();
		initPageComponents();
		this.limitBean = new ElimitBean();
	}
	
	public ElimitCreatePage(ElimitBean limitBean) {
		super();
		this.limitBean = limitBean;
		initPageComponents();
	}
	
	protected void initPageComponents() {
		add(new ElimitCreatePanel("limitCreatePanel", this , limitBean));
	}
}