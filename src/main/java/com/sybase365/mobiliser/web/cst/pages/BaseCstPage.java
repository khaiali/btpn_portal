package com.sybase365.mobiliser.web.cst.pages;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponCategory;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponType;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseLeftMenuView;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.reports.AbstractReportEnabledPage;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.CouponCategoryMenuGroup;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.CouponTypeMenuGroup;
import com.sybase365.mobiliser.web.cst.pages.customercare.ContactNotePage;
import com.sybase365.mobiliser.web.cst.pages.customercare.CustomerCareMenuGroup;
import com.sybase365.mobiliser.web.cst.pages.usermanager.UserManagerMenuGroup;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_CST_LOGIN)
public abstract class BaseCstPage extends AbstractReportEnabledPage {
    private static final long serialVersionUID = 1L;

    public BaseCstPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BaseCstPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected String getApplicationName() {

	return getLocalizer().getString("cst.application.name", this);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	getMobiliserWebSession().setLeftMenu(buildLeftMenu());

	String item = "";
	String name = "";
	String id = "";
	boolean labelVisible = false;

	if (this instanceof CustomerCareMenuGroup) {
	    CustomerBean customer = getMobiliserWebSession().getCustomer();
	    if (PortalUtils.exists(customer)
		    && !PortalUtils.exists(customer.getTaskId())) {
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
	} else if (this instanceof UserManagerMenuGroup) {
	    CustomerBean customer = getMobiliserWebSession().getCustomer();
	    if (PortalUtils.exists(customer) && !customer.isPendingApproval()) {
		item = getLocalizer().getString("selected.context.agent", this);
		name = getLocalizer().getString("selected.context.name.key",
			this)
			+ getMobiliserWebSession().getCustomer()
				.getDisplayName();
		id = getLocalizer().getString("selected.context.id.key", this)
			+ getMobiliserWebSession().getCustomer().getId();
		labelVisible = true;
	    }
	} else if (this instanceof CouponTypeMenuGroup) {
	    CouponType couponType = getMobiliserWebSession().getCouponType();
	    if (PortalUtils.exists(couponType)) {
		item = getLocalizer().getString("selected.context.couponType",
			this);
		name = getLocalizer().getString("selected.context.name.key",
			this)
			+ getMobiliserWebSession().getCouponType().getName();
		id = getLocalizer().getString("selected.context.id.key", this)
			+ getMobiliserWebSession().getCouponType().getId();
		labelVisible = true;
	    }
	} else if (this instanceof CouponCategoryMenuGroup) {
	    CouponCategory couponCategory = getMobiliserWebSession()
		    .getCouponCategory();
	    if (PortalUtils.exists(couponCategory)) {
		item = getLocalizer().getString(
			"selected.context.couponCategory", this);
		name = getLocalizer().getString(
			"selected.context.name.couponCategoryNamekey", this)
			+ getMobiliserWebSession().getCouponCategory()
				.getName();
		id = getLocalizer().getString(
			"selected.context.id.couponCategorykey", this)
			+ getMobiliserWebSession().getCouponCategory().getId();
		labelVisible = true;
	    }
	} else {
	    // checks whether to create a contact note or not
	    if (!this.getClass().equals(ContactNotePage.class)
		    && getMobiliserWebSession().isShowContact()
		    && getMobiliserWebSession().hasPrivilege(
			    Constants.PRIV_NOTE_READ)) {
		setResponsePage(new ContactNotePage(this.getPage()));
	    }
	}
	addOrReplace(new Label("selected.context.item", item)
		.setVisible(labelVisible));
	addOrReplace(new Label("selected.context.name", name)
		.setVisible(labelVisible));
	addOrReplace(new Label("selected.context.id", id)
		.setVisible(labelVisible));
	add(new SybaseLeftMenuView("leftMenu",
		new Model<LinkedList<IMenuEntry>>(getMobiliserWebSession()
			.getLeftMenu())));
    }

    public abstract LinkedList<IMenuEntry> buildLeftMenu();

    @Override
    protected Class getActiveMenu() {
	return this.getClass();
    }

    @Override
    protected boolean supportsSvaBalance() {
	return true;
    }
}
