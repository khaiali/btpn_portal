package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.FundTransferSuccessPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This class is the home page BTPN Consumer Portal Application. This consists of the all the components for the fund
 * transfer success page.
 * 
 * @author Narasa Reddy
 */
public class FundTransferSuccessPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnBaseConsumerPortalSelfCarePage basePage;

	FundTransferBean fundTransferBean;

	/**
	 * Constructor for the Home Page
	 */
	public FundTransferSuccessPage() {
		super();
		initPageComponents();
	}

	/**
	 * Constructor for the Page parameters
	 */
	public FundTransferSuccessPage(final PageParameters parameters) {
		super(parameters);
	}

	public FundTransferSuccessPage(FundTransferBean fundTransferBean) {
		super();
		this.fundTransferBean = fundTransferBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new FundTransferSuccessPanel("fundTransferSuccessPanel", this, this.fundTransferBean));
	}

}
