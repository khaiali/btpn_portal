package com.sybase365.mobiliser.web.cst.pages.usermanager;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrPrivilege;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.common.panels.EditRolesPrivilegesPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class EditPrivilegePage extends UserManagerMenuGroup {

    UmgrPrivilege umgrPrivilege;

    public EditPrivilegePage() {
	super();
	initPageComponents();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public EditPrivilegePage(final PageParameters parameters) {
	super(parameters);
    }

    public EditPrivilegePage(UmgrPrivilege umgrPrivilege) {
	this.umgrPrivilege = umgrPrivilege;
	initPageComponents();
	buildLeftMenu();
    }

    protected void initPageComponents() {
	add(new EditRolesPrivilegesPanel("editRolesPrivilegesPanel", this,
		this.getClass(), Constants.PRIVILEGE_ASSIGN_ROLE, umgrPrivilege));
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	if (PortalUtils.exists(this.umgrPrivilege)) {
	    String item = "";
	    String name = "";
	    String id = "";
	    boolean labelVisible = false;

	    item = getLocalizer().getString("selected.context.privilege", this);
	    name = getLocalizer().getString("selected.context.name.key", this)
		    + this.umgrPrivilege.getPrivilege();
	    id = getLocalizer().getString("selected.context.description.key",
		    this)
		    + this.umgrPrivilege.getDescription();
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
