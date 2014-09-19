package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageBillPaymentFeeConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Fee details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Feny Yanti
 */
public class ManageBillPaymentFeeConfirmPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageBillPaymentFeeBean feeBean;
	protected String flag;

	public ManageBillPaymentFeeConfirmPage(ManageBillPaymentFeeBean feeBean, String flag) {
		super();
		this.feeBean = feeBean;
		this.flag = flag;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageBillPaymentFeeConfirmPanel("manageBillPaymentFeeConfirmPanel", this, feeBean, flag));
	}
	
}
