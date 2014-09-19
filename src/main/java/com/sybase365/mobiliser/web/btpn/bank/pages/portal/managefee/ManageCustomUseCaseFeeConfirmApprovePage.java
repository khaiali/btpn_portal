package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomUseCaseFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomUseCaseFeeConfirmApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Fee details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomUseCaseFeeConfirmApprovePage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageCustomUseCaseFeeBean ucFeeBean;
	protected String flag;

	public ManageCustomUseCaseFeeConfirmApprovePage(ManageCustomUseCaseFeeBean ucFeeBean, String flag) {
		super();
		this.ucFeeBean = ucFeeBean;
		this.flag = flag;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageCustomUseCaseFeeConfirmApprovePanel("ucFeeConfAprPanel", this, ucFeeBean, flag));
	}
	
}
