package com.sybase365.mobiliser.web.cst.pages.usermanager;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.common.panels.EditAgentRolePrivilegePanel;

public class EditAgentRolePrivilegePage extends UserManagerMenuGroup {

    public EditAgentRolePrivilegePage() {
	super();
	initPageComponents();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public EditAgentRolePrivilegePage(final PageParameters parameters) {
	super(parameters);
    }

    protected void initPageComponents() {
	add(new EditAgentRolePrivilegePanel("editRolePrivilegePanel", this,
		this.getClass()));
    }
}
