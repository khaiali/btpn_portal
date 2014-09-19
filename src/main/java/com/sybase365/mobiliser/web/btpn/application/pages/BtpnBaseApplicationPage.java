package com.sybase365.mobiliser.web.btpn.application.pages;

import org.apache.wicket.PageParameters;

/**
 * This class is the base page for all BTPN Portal Application. This consists of the all the components common to all
 * the BTPN Applications.
 * 
 * @author Vikram Gunda
 */
public class BtpnBaseApplicationPage extends BtpnMobiliserBasePage {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BtpnBaseLoginPage.class);

	/**
	 * Constructor that is invoked.
	 */
	public BtpnBaseApplicationPage() {
		super();
		LOG.debug("Created new BtpnBaseApplicationPage");
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public BtpnBaseApplicationPage(final PageParameters parameters) {
		super(parameters);
		LOG.debug("Created new BtpnBaseApplicationPage");
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
	}

}