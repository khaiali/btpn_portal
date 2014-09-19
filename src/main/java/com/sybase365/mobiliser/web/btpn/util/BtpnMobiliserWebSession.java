package com.sybase365.mobiliser.web.btpn.util;

import org.apache.wicket.Request;
import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class BtpnMobiliserWebSession extends MobiliserWebSession {

	private static final long serialVersionUID = 1L;

	private SybaseMenu topMenu;

	public BtpnMobiliserWebSession(Request request) {
		super(request);
	}

	@Override
	public Roles getRoles() {
		Roles roles = new Roles();
		roles = getBtpnRoles();
		return roles;
	}

	public final SybaseMenu getTopMenu() {
		if (topMenu == null) {
			topMenu = buildMenu();
		}

		return topMenu;
	}

	public final void setTopMenu(SybaseMenu topMenu) {
		this.topMenu = topMenu;
	}
}
