package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import com.btpnwow.core.debit.services.contract.v1_0.DebitInquiryRequest;
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.SubAccountTransferPinPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the SubAccountTransferPinPage page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class SubAccountTransferPinPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private final DebitInquiryRequest inquiryRequest;

	SubAccountsBean subAccountBean;

	String selectedTransferType;

	public SubAccountTransferPinPage() {
		this.inquiryRequest = null;
		initPageComponents();
	}

	public SubAccountTransferPinPage(DebitInquiryRequest inquiryRequest, SubAccountsBean subAccountBean, String type) {
		this.inquiryRequest = inquiryRequest;
		this.subAccountBean = subAccountBean;
		this.selectedTransferType = type;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new SubAccountTransferPinPanel("subAccountTransferPinPanel", this, inquiryRequest, subAccountBean, selectedTransferType));
	}

}
