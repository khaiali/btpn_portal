package com.sybase365.mobiliser.web.btpn.bank.pages.portal;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.btpn.application.pages.BankPortalApplicationLoginPage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnBaseApplicationPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.common.components.MenuView;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnMobiliserWebSession;

/**
 * This class is the base page for BTPN Bank Application. This consists of the components related to bank like Header,
 * Footer, Top Menu. Any coomon components related to the Bank Portal pages can be placed here.
 * 
 * @author Vikram Gunda
 */
@AuthorizeInstantiation(BtpnConstants.PRIV_BTPN_BANK_PORTAL_LOGIN)
public abstract class BtpnBaseBankPortalPage extends BtpnBaseApplicationPage {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(BtpnBaseBankPortalPage.class);

	/**
	 * Constructor for this page.
	 */
	public BtpnBaseBankPortalPage() {
		super();
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public BtpnBaseBankPortalPage(final PageParameters parameters) {
		super(parameters);
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		LOG.debug("BtpnBaseBankPortalPage:initOwnPageComponents() ==> Start");
		super.initOwnPageComponents();
		addBaseBankPortalComponents();
		LOG.debug("BtpnBaseBankPortalPage:initOwnPageComponents() ==> End");
	}

	/**
	 * Adds bank portal base components
	 */
	protected void addBaseBankPortalComponents() {
		// Add Login Header Information
		final Customer customer = (Customer) this.getMobiliserWebSession().getBtpnLoggedInCustomer();
		final String loginInfo = getLocalizer().getString("header.login.info", this);
		add(new Label("header.login.info", String.format(loginInfo, customer.getDisplayName())).setRenderBodyOnly(true));
		add(new Label("header.login.customerid", String.valueOf(customer.getCustomerId() + " "))
			.setRenderBodyOnly(true));
		add(new Link<String>("header.logout") {

			private static final long serialVersionUID = 1L;

			public void onClick() {
				logoutCustomer(getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionId());
				BtpnBaseBankPortalPage.this.getSession().invalidate();
				setResponsePage(BankPortalApplicationLoginPage.class);
			}
		});

		add(new MenuView("mainMenu", new Model<SybaseMenu>(
			((BtpnMobiliserWebSession) getMobiliserWebSession()).getTopMenu()), this.getMobiliserWebSession()
			.getBtpnRoles()).setRenderBodyOnly(true));

		add(new Link<String>("header.logo") {

			private static final long serialVersionUID = 1L;

			public void onClick() {
				getWebSession().setLeftMenu(null);
				((BtpnMobiliserWebSession) getMobiliserWebSession()).setTopMenu(null);
				setResponsePage(BankPortalHomePage.class);
			}
		});
		add(new Label("footer.application.version", getBuildVersion()));
		add(new Label("application.session.timeout.seconds", String.valueOf(customer.getSessionTimeout())));
	}

	protected abstract LinkedList<IMenuEntry> buildLeftMenu();

}
