package com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankCheckerApprovalPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the ConsumerApprovalPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankCheckerApprovalPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	public BankCheckerApprovalPage() {
		super();
		
		initPageComponents();
	}

	public BankCheckerApprovalPage(final PageParameters parameters) {
		super(parameters);
		
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankCheckerApprovalPanel("consumerApprovalPanel", this, getSelectedMenu()));
	}

	private String getSelectedMenu() {
		LinkedList<IMenuEntry> menuEntries = getWebSession().getLeftMenu();
		for (IMenuEntry menuEntry : menuEntries) {
			final String name = menuEntry.getName();
			final boolean active = menuEntry.isActive();
			if (active) {
				if (name.equals("left.menu.consumer.approval")) {
					return BtpnConstants.REG_CONSUMER;
				} else if (name.equals("left.menu.officer.approval")) {
					return BtpnConstants.REG_TOPUP_AGENT;
				}
			}
		}
		return null;
	}
}
