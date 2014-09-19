package com.sybase365.mobiliser.web.distributor.pages.agentcare;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_AGENTS)
public class AgentCareMenuGroup extends BaseAgentCarePage {

    private static final long serialVersionUID = 1L;
    private boolean isFromTopMenu;

    public AgentCareMenuGroup() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public AgentCareMenuGroup(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	PageParameters parameters = getPageParameters();
	if (PortalUtils.exists(parameters)) {
	    if (PortalUtils.exists(parameters.getString("isFromTopMenu"))) {
		this.isFromTopMenu = true;
	    } else {
		this.isFromTopMenu = false;
	    }
	}
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	if (getMobiliserWebSession().getCustomer() != null && !isFromTopMenu) {
	    PageParameters params = new PageParameters();
	    params.put("action", "edit");

	    entries.add(new MenuEntry("menu.edit.agent",
		    Constants.PRIV_MANAGE_AGENTS, AgentEditPage.class, params));
	    entries.add(new MenuEntry("menu.edit.rolesPrivilege.agent",
		    Constants.PRIV_MANAGE_AGENTS, AgentUmgrHandlingPage.class));
	    if ((getMobiliserWebSession().getMaxCreateLevel() == -1 || getMobiliserWebSession()
		    .getMaxCreateLevel() > getMobiliserWebSession()
		    .getCustomer().getHierarchyLevel())
		    && (PortalUtils
			    .exists(getAllowedChildRoles(getMobiliserWebSession()
				    .getCustomer().getId())))) {
		entries.add(new MenuEntry("menu.create.sub.agent",
			Constants.PRIV_MANAGE_AGENTS, AgentCreatePage.class,
			params));
	    }

	    entries.add(new MenuEntry("menu.agent.commissionManagement",
		    Constants.PRIV_VIEW_COMMISSION_MGMT,
		    CommissionManagementPage.class));
	    if (getMobiliserWebSession().hasM2MRole(
		    getMobiliserWebSession().getCustomer())
		    && getMobiliserWebSession().hasM2MRole(
			    getMobiliserWebSession().getLoggedInCustomer())) {
		entries.add(new MenuEntry("menu.agent.transferMoney",
			Constants.PRIV_MERCHANT_TRANSACTION,
			AgentTransferMoneyPage.class));
	    }
	}

	for (IMenuEntry entry : entries) {
	    if (entry instanceof MenuEntry) {
		if (((MenuEntry) entry).getPage().equals(getActiveMenu())) {
		    entry.setActive(true);
		}
	    }
	}

	return entries;
    }

}
