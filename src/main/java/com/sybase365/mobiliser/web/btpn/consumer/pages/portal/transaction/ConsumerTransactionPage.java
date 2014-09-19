package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.transaction;

import com.sybase365.mobiliser.web.btpn.consumer.common.panels.ConsumerTransactionDetailPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This class is View Transactions Page for Consumer Portal. The transactions page displays the transaction details.
 * 
 * @author Vikram Gunda
 */
public class ConsumerTransactionPage extends BtpnBaseConsumerPortalSelfCarePage {

	public ConsumerTransactionPage() {
		initPageComponents();
	}

	public void initPageComponents() {
		add(new ConsumerTransactionDetailPanel("consumerTxnDetailsPanel", this));
	}
}
