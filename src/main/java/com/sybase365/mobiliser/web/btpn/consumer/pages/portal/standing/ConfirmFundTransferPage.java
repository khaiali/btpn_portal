package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;


import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.ConfirmFundTransferPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;


public class ConfirmFundTransferPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	FundTransferBean cundTransferBean;
	String ftAccountType = "";
	
	public ConfirmFundTransferPage() {
		super();
		initPageComponents();
	}
	
	public ConfirmFundTransferPage(FundTransferBean cundTransferBean) {
		super();
		this.cundTransferBean = cundTransferBean;
		initPageComponents();
	}

	public ConfirmFundTransferPage(FundTransferBean cundTransferBean, String ftAccountType) {
		super();
		this.cundTransferBean = cundTransferBean;
		this.ftAccountType = ftAccountType;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ConfirmFundTransferPanel("confirmFundTransferPanel", this, cundTransferBean, ftAccountType));
	}

}
