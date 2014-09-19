package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomUseCaseFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomUseCaseFeeAddPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Fee details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomUseCaseFeeAddPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	
	protected ManageCustomUseCaseFeeBean ucFeeBean;

	public ManageCustomUseCaseFeeAddPage() {
		super();
		initPageComponent();
	}
	
	public ManageCustomUseCaseFeeAddPage(ManageCustomUseCaseFeeBean ucFeeBean) {
		super();
		this.ucFeeBean = ucFeeBean;
		initPageComponents();
	}
	
	protected void initPageComponent(){
		add(new ManageCustomUseCaseFeeAddPanel("ucFeeAddPanel", this));
	}
	
	protected void initPageComponents(){
		add(new ManageCustomUseCaseFeeAddPanel("ucFeeAddPanel", this, ucFeeBean));
	}
	
}
