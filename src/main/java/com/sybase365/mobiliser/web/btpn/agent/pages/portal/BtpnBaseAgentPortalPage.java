package com.sybase365.mobiliser.web.btpn.agent.pages.portal;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.slf4j.Logger;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.AgentLoginOtpPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.AgentPortalHomePage;
import com.sybase365.mobiliser.web.btpn.application.pages.AgentPortalApplicationLoginPage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnBaseApplicationPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.BtpnBaseBankPortalPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;

/**
 * This class is the base page for BTPN Agent Portal Application. This page can contain the functionality common to this
 * application.
 * 
 * @author Vikram Gunda
 */
@AuthorizeInstantiation(BtpnConstants.PRIV_BTPN_AGENT_LOGIN)
public abstract class BtpnBaseAgentPortalPage extends BtpnBaseApplicationPage {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(BtpnBaseBankPortalPage.class);

	/**
	 * Constructor that is invoked
	 * 
	 * @param parameters Page parameters
	 */
	public BtpnBaseAgentPortalPage() {
		super();
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public BtpnBaseAgentPortalPage(final PageParameters parameters) {
		super(parameters);
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		if (getMobiliserWebSession().getBtpnLoggedInCustomer().getOtpPrivilege() != null) {
			getRequestCycle().setRedirect(true);
			
			setResponsePage(AgentLoginOtpPage.class);
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
		add(new Label("header.login.info", String.format(loginInfo, customer.getDisplayName())).setRenderBodyOnly(true));
		add(new Label("header.login.customerid", String.valueOf(customer.getCustomerId())).setRenderBodyOnly(true));
		add(new Link<String>("header.logout") {

			private static final long serialVersionUID = 1L;

			public void onClick() {
				logoutCustomer(getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionId());
				getMobiliserWebSession().invalidate();
				getRequestCycle().setRedirect(true);
				setResponsePage(AgentPortalApplicationLoginPage.class);
			}
		});

		add(new Link<String>("header.logo") {

			private static final long serialVersionUID = 1L;

			public void onClick() {
				getWebSession().setLeftMenu(null);
				setResponsePage(AgentPortalHomePage.class);
			}
		});
		add(new Label("application.session.timeout.seconds", String.valueOf(customer.getSessionTimeout())));
	}

	protected abstract LinkedList<IMenuEntry> buildLeftMenu();

}
