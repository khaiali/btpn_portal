package com.sybase365.mobiliser.web.cst.pages.usermanager;

import java.util.LinkedList;

import org.apache.wicket.markup.html.basic.Label;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrRole;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.common.panels.EditRolesPrivilegesPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class EditRolePage extends UserManagerMenuGroup {
    UmgrRole umgrRole;

    public EditRolePage() {
	super();
	initPageComponents();
    }

    public EditRolePage(UmgrRole umgrRole) {
	this.umgrRole = umgrRole;
	initPageComponents();
	buildLeftMenu();
    }

    protected void initPageComponents() {
	add(new EditRolesPrivilegesPanel("editRolesPrivilegesPanel", this,
		this.getClass(), Constants.ROLE_ASSIGN_PRIVILEGE, umgrRole));
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {

	if (PortalUtils.exists(this.umgrRole)) {
	    String item = "";
	    String name = "";
	    String id = "";
	    boolean labelVisible = false;

	    item = getLocalizer().getString("selected.context.role", this);
	    name = getLocalizer().getString("selected.context.name.key", this)
		    + this.umgrRole.getRole();
	    id = getLocalizer().getString("selected.context.description.key",
		    this)
		    + this.umgrRole.getDescription();
	    labelVisible = true;

	    addOrReplace(new Label("selected.context.item", item)
		    .setVisible(labelVisible));

	    addOrReplace(new Label("selected.context.name", name)
		    .setVisible(labelVisible));

	    addOrReplace(new Label("selected.context.id", id));
	}
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	return entries;

    }
}
