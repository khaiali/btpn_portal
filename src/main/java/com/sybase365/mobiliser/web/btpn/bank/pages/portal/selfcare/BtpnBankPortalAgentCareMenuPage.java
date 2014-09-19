package com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalSearchMenuPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.BtpnBaseBankPortalPage;

public class BtpnBankPortalAgentCareMenuPage extends BtpnBaseBankPortalPage {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
		.getLogger(BtpnBankPortalCustomerCareMenuPage.class);

	/**
	 * Constructor for this page.
	 */
	public BtpnBankPortalAgentCareMenuPage() {
		super();
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public BtpnBankPortalAgentCareMenuPage(final PageParameters parameters) {
		super(parameters);
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		LOG.debug("###BtpnBasePage:initPageComponents()====> Start");
		super.initOwnPageComponents();
		addSelfCarePageComponents();
		LOG.debug("###BtpnBasePage:initPageComponents()====> End");
	}

	/**
	 * Add the bank portal self care page components
	 */
	// TODO Add the logic to fetch from session
	protected void addSelfCarePageComponents() {
		add(new FeedbackPanel("errorMessages"));
		Label headerLbl = new Label("headerLbl", getLocalizer().getString("agentheadermsg", this));
		add(new BankPortalSearchMenuPanel("agentCareMenu", "agent", this));
		add(headerLbl);
	}

	/**
	 * Build the left menu
	 * 
	 * @return LinkedList<IMenuEntry> list of left menu entries along with their priviliges
	 */
	protected LinkedList<IMenuEntry> buildLeftMenu() {
		return null;
	}

}
