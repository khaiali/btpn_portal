package com.sybase365.mobiliser.web.distributor.pages.agentcare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.EditAgentRolePrivilegePanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_AGENTS)
public class AgentUmgrHandlingPage extends AgentCareMenuGroup {

    private static final long serialVersionUID = 1L;
    
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AgentUmgrHandlingPage.class);

    public AgentUmgrHandlingPage() {
	super();
	initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {
	add(new EditAgentRolePrivilegePanel("editRolePrivilegePanel", this,
		this.getClass()));
    }

}
