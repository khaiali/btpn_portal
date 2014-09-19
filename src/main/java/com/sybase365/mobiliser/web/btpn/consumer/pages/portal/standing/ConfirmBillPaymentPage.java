package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;


import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.BillPaymentConfirmPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;


public class ConfirmBillPaymentPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BillPaymentBean billPaymentBean;

	public ConfirmBillPaymentPage() {
		super();
		initPageComponents();
	}

	public ConfirmBillPaymentPage(BillPaymentBean billPaymentBean) {
		super();
		this.billPaymentBean = billPaymentBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BillPaymentConfirmPanel("billPaymentConfirmPanel", this, billPaymentBean));
	}

}
