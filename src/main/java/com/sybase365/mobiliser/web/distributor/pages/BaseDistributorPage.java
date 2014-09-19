package com.sybase365.mobiliser.web.distributor.pages;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseLeftMenuView;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.reports.AbstractReportEnabledPage;
import com.sybase365.mobiliser.web.distributor.pages.agentcare.AgentCareMenuGroup;
import com.sybase365.mobiliser.web.distributor.pages.customerservices.CustomerServicesMenuGroup;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_MERCHANT_LOGIN)
public abstract class BaseDistributorPage extends AbstractReportEnabledPage {

    private static final long serialVersionUID = 1L;
    private boolean isFromTopMenu;

    public BaseDistributorPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BaseDistributorPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	// if (getMobiliserWebSession().getLeftMenu() == null) {
	getMobiliserWebSession().setLeftMenu(buildLeftMenu());
	// }
	String item = "";
	String name = "";
	String id = "";
	boolean labelVisible = false;
	if (this instanceof AgentCareMenuGroup) {
	    CustomerBean customer = getMobiliserWebSession().getCustomer();
	    PageParameters parameters = super.getPageParameters();
	    if (PortalUtils.exists(parameters)) {
		if (PortalUtils.exists(parameters.getString("isFromTopMenu"))) {
		    this.isFromTopMenu = true;
		} else {
		    this.isFromTopMenu = false;
		}
	    }
	    if (PortalUtils.exists(customer) && !isFromTopMenu) {
		item = getLocalizer().getString("selected.context.agent", this);
		name = getLocalizer().getString("selected.context.name.key",
			this)
			+ getMobiliserWebSession().getCustomer()
				.getDisplayName();
		id = getLocalizer().getString("selected.context.id.key", this)
			+ getMobiliserWebSession().getCustomer().getId();
		labelVisible = true;
	    }
	} else if (this instanceof CustomerServicesMenuGroup) {
	    CustomerBean customer = getMobiliserWebSession().getCustomer();
	    if (PortalUtils.exists(customer)) {
		item = getLocalizer().getString("selected.context.customer",
			this);
		name = getLocalizer().getString("selected.context.name.key",
			this)
			+ getMobiliserWebSession().getCustomer()
				.getDisplayName();
		id = getLocalizer().getString("selected.context.id.key", this)
			+ getMobiliserWebSession().getCustomer().getId();
		labelVisible = true;
	    }
	}
	addOrReplace(new Label("selected.context.item", item)
		.setVisible(labelVisible));
	addOrReplace(new Label("selected.context.name", name)
		.setVisible(labelVisible));
	addOrReplace(new Label("selected.context.id", id)
		.setVisible(labelVisible));
	addOrReplace(new SybaseLeftMenuView("leftMenu",
		new Model<LinkedList<IMenuEntry>>(getMobiliserWebSession()
			.getLeftMenu())));
    }

    public abstract LinkedList<IMenuEntry> buildLeftMenu();

    @Override
    public String getApplicationName() {
	return getLocalizer().getString("distributor.page.title", this);
    }

    @Override
    protected Class getActiveMenu() {
	return this.getClass();
    }

    @Override
    protected boolean supportsSvaBalance() {
	return true;
    }
}
