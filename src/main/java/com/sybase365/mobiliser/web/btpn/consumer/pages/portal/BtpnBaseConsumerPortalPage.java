package com.sybase365.mobiliser.web.btpn.consumer.pages.portal;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnBaseApplicationPage;
import com.sybase365.mobiliser.web.btpn.application.pages.ConsumerPortalApplicationLoginPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerLoginOtpPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerPortalHomePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;

/**
 * This class is base page for BTPN Consumer Application. All the common code components related to Consumer Portal
 * Applications can be placed here.
 * 
 * @author Vikram Gunda
 */
public abstract class BtpnBaseConsumerPortalPage extends BtpnBaseApplicationPage {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BtpnBaseConsumerPortalPage.class);

	public BtpnBaseConsumerPortalPage() {
		super();
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public BtpnBaseConsumerPortalPage(final PageParameters parameters) {
		super(parameters);
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		BtpnCustomer loggedIn = getMobiliserWebSession().getBtpnLoggedInCustomer();
		
		if ((loggedIn != null) && (loggedIn.getOtpPrivilege() != null)) {
			getRequestCycle().setRedirect(true);
			
			setResponsePage(ConsumerLoginOtpPage.class);
		} else {
			super.initOwnPageComponents();
			
			addBaseBankPortalComponents();
		}
	}

	/**
	 * Adds bank portal base components
	 */
	protected void addBaseBankPortalComponents() {

		// Add Login Header Information
		final BtpnCustomer customer = this.getMobiliserWebSession().getBtpnLoggedInCustomer();
		
		final String loginInfo = getLocalizer().getString("header.login.info", this);
		
		// final String svaBalance = getLocalizer().getString("header.sva.balance", this);
		add(new Label("header.login.info", customer == null ? "" : String.format(loginInfo, customer.getDisplayName()))
			.setRenderBodyOnly(true)
			.setVisible(customer != null));

		// show SVA balance
		String balance = null;
		
		if (customer != null) {
			balance = String.valueOf(displayAmount(getSvaBalance(customer.getUsername())));
		}
		
		add(new Label("svaBalanceLabel", balance).setVisible(getVisible() && (balance != null)));

		add(new WebMarkupContainer("navBar").setVisible(getVisible()));
		
		add(new WebMarkupContainer("appNameBar").setVisible(getVisible()));
		
		// add(new Label("svaBalanceLabel", String.format(svaBalance, balance)));

		add(new Label("header.login.customerid", String.valueOf(customer.getCustomerId() + " "))
			.setRenderBodyOnly(true));
		
		add(new Link<String>("header.logout") {

			private static final long serialVersionUID = 1L;

			public void onClick() {
				logoutCustomer(getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionId());
				getMobiliserWebSession().invalidate();
				getRequestCycle().setRedirect(true);
				setResponsePage(ConsumerPortalApplicationLoginPage.class);
			}
		});
		add(new Link<String>("header.logo") {

			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(handleLogoClick());
			}
		});
		add(new Label("application.session.timeout.seconds", String.valueOf(customer.getSessionTimeout())));
	}

	protected abstract LinkedList<IMenuEntry> buildLeftMenu();

	protected Class handleLogoClick() {
		return ConsumerPortalHomePage.class;
	}

	protected boolean getVisible() {
		return true;
	}

}
