package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.FundTransferPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This class is the home page BTPN Consumer Portal Application. This consists of the all the components for the fund
 * transfer page.
 * 
 * @author Narasa Reddy
 */
public class FundTransferPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnBaseConsumerPortalSelfCarePage basePage;

	FundTransferBean fundTransferBean;

	/**
	 * Constructor for the Home Page
	 */
	public FundTransferPage() {
		super();
		initPageComponents();
	}

	/**
	 * Constructor for the Page parameters
	 */
	public FundTransferPage(final PageParameters parameters) {
		super(parameters);
	}

	public FundTransferPage(FundTransferBean fundTransferBean) {
		super();
		this.fundTransferBean = fundTransferBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new FundTransferPanel("fundTransferPanel", this, this.fundTransferBean));
	}

}
