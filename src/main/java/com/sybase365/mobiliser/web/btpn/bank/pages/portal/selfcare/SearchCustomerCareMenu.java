package com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare;

import java.util.LinkedList;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.CustomerDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.BtpnBaseBankPortalPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.CustomerDetails;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.TransactionSearchPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.blockcustomer.BlockCustomerPage;
import com.sybase365.mobiliser.web.btpn.common.components.LeftMenuView;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

public class SearchCustomerCareMenu extends BtpnBaseBankPortalPage {

	private static final long serialVersionUID = 1L;

	private static final String TYPE_AGENT = "agent";
	
	private static final String TYPE_CUSTOMER = "customer";

	private CustomerRegistrationBean registrationBean;
	
//	CustomerSearchBean searchBean;

	private WebMarkupContainer customerDivMarkup;

	private boolean searchCustPanel = false;

	private String searchFor;

	private String type;

	public SearchCustomerCareMenu() {
		searchCustPanel = false;
		
		searchFor = "";
		
		type = TYPE_CUSTOMER;
		
		registrationBean = getMobiliserWebSession().getCustomerRegistrationBean();
		
		initOwnComponents();
	}

	public SearchCustomerCareMenu(boolean searchPanel, String searchFor, String type) {
		this.searchCustPanel = searchPanel;
		
		this.searchFor = searchFor;
		
		this.type = type;
		
		this.registrationBean = getMobiliserWebSession().getCustomerRegistrationBean();
		
		initOwnComponents();
	}

	protected LinkedList<IMenuEntry> buildLeftMenu() {
		final LinkedList<IMenuEntry> menuItems = new LinkedList<IMenuEntry>();

		// Main Data
		final MenuEntry hmMenuEntry = new MenuEntry("left.menu.maindata", BtpnConstants.PRIV_UI_BANK_PORTAL_MAIN_DATA, CustomerDetails.class);
		hmMenuEntry.setActive(true);
		menuItems.add(hmMenuEntry);

		// Transactions
		final MenuEntry mpMenuEntry = new MenuEntry("left.menu.transactions", BtpnConstants.PRIV_UI_BANK_PORTAL_TRANSACTIONS, TransactionSearchPage.class);
		mpMenuEntry.setActive(false);

		final int customerTypeId = this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerTypeId();

		if (!TYPE_AGENT.equals(type)) {
			final boolean transactionLink = BtpnConstants.CUSTOMER_TYPES_TRANSACTION_LINK.contains(Integer.valueOf(customerTypeId));
			
			if (transactionLink && (registrationBean.getProductCategory() == BtpnConstants.PRODUCT_CATEGORY_ZERO)) {
				menuItems.add(mpMenuEntry);
			} else if (!transactionLink) {
				menuItems.add(mpMenuEntry);
			}
		}

		// Change Mobile Number
		final MenuEntry approvalRegPage = new MenuEntry("left.menu.changemobile", BtpnConstants.PRIV_UI_MSISDN_CHANGE_REQUESTS, ChangeMsisdnPage.class);
		approvalRegPage.setActive(false);
		menuItems.add(approvalRegPage);
		
		// Block Customer
		String blrid = getMobiliserWebSession().getCustomerRegistrationBean().getBlackListReason().getId();
		if ("0".equals(blrid)) { 
			final MenuEntry blacklistCustomerPage = new MenuEntry("left.menu.blockCustomer",
				"UI_BANK_PORTAL_BLOCK_CUSTOMER", BlockCustomerPage.class);
			
			blacklistCustomerPage.setActive(false);
			
			menuItems.add(blacklistCustomerPage);
		}

		return menuItems;
	}

	protected void addSelfCarePageComponents() {
		LinkedList<IMenuEntry> menuEntries = getMobiliserWebSession().getLeftMenu();
		
		if ((menuEntries == null) || menuEntries.isEmpty()) {
			menuEntries = buildLeftMenu();
			
			getMobiliserWebSession().setLeftMenu(menuEntries);
		}
		
		add(new LeftMenuView("leftMenu", new Model<LinkedList<IMenuEntry>>(menuEntries), getMobiliserWebSession().getBtpnRoles()).setRenderBodyOnly(true));
	}

	protected void initOwnComponents() {
		Form<SearchCustomerCareMenu> form = new Form<SearchCustomerCareMenu>("form");
		
		// final Label msisdnLbl = new Label("msisdnLbl", registrationBean.getMobileNumber());
		// final Label customerId = new Label("customerIdLbl", registrationBean.getCustomerId());
		// final Label nameLbl = new Label("nameLbl", registrationBean.getName());
		// final Label mobileAccLbl = new Label("mobileAccLbl", registrationBean.getSvaNumber());
		
		form.add(new Label("msisdnLbl", registrationBean.getMobileNumber()));
		form.add(new Label("customerIdLbl", registrationBean.getCustomerId()));
		form.add(new Label("nameLbl", registrationBean.getName()));
		
		add(form);
		
		addSelfCarePageComponents();
		
		customerDivMarkup = new WebMarkupContainer("customerDetailsDiv");

		// Add CustomerDetailsPanel
		customerDivMarkup.add(new CustomerDetailsPanel("customerDetailsPanel", this, registrationBean, searchFor, type));

		if (searchCustPanel) {
			customerDivMarkup.setVisible(true);
		} else {
			customerDivMarkup.setVisible(false);
		}
		
		add(customerDivMarkup);
	}
}
