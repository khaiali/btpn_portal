package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.FundTransferConfirmPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This class is the home page BTPN Consumer Portal Application. This consists of the all the components for the fund
 * transfer confirm page.
 * 
 * @author Narasa Reddy
 */
public class FundTransferConfirmPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnBaseConsumerPortalSelfCarePage basePage;

	FundTransferBean fundTransferBean;

	/**
	 * Constructor for the Home Page
	 */
	public FundTransferConfirmPage() {
		super();
		initPageComponents();
	}

	/**
	 * Constructor for the Page parameters
	 */
	public FundTransferConfirmPage(final PageParameters parameters) {
		super(parameters);
	}

	public FundTransferConfirmPage(FundTransferBean fundTransferBean) {
		super();
		this.fundTransferBean = fundTransferBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new FundTransferConfirmPanel("fundTransferConfirmPanel", this, this.fundTransferBean));
	}

}
