package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;

import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentPerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.BillPaymentDetailsPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;


public class BillPaymentDetailsPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

//	BillPaymentBean billPaymentBean;
	BillPaymentPerformBean billPaymentBean;
	

	public BillPaymentDetailsPage() {
		super();
		initPageComponents();
	}

	public BillPaymentDetailsPage(BillPaymentPerformBean billPaymentBean) {
		super();
		this.billPaymentBean = billPaymentBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BillPaymentDetailsPanel("billPaymentDetailsPanel", this, billPaymentBean));
	}

}
