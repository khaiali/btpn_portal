package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare;

import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * This class is the home page BTPN Consumer Portal Application. This consists of the all the components for the home
 * page.
 * 
 * @author Vikram Gunda
 */
public class ConsumerPortalHomePage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for the Home Page
	 */
	public ConsumerPortalHomePage() {
		add(new FeedbackPanel("errorMessages"));
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();

	}
}
