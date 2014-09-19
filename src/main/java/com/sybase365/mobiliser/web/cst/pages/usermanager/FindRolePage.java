package com.sybase365.mobiliser.web.cst.pages.usermanager;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrRole;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.RoleDataProvider;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class FindRolePage extends BaseUserManagerPage {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(FindRolePage.class);
    private String role;
    private String description;
    private UmgrRole umgrRole;
    private List<UmgrRole> umgrRoleList;
    private RoleDataProvider dataProvider;
    private boolean sortAsc = true;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_role = "role";
    private static final String WICKET_ID_orderByRole = "orderByRole";
    private static final String WICKET_ID_LRoleLink = "LRoleLink";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_Lrole = "Lrole";
    private static final String WICKET_ID_Ldescription = "Ldescription";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	final Form<?> form = new Form("findRoleForm",
		new CompoundPropertyModel<FindRolePage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new TextField<String>("role").add(new ErrorIndicator()));
	form.add(new TextField<String>("description").add(new ErrorIndicator()));

	final WebMarkupContainer dataViewContainer = new WebMarkupContainer(
		"dataViewContainer");
	form.add(dataViewContainer);
	form.add(new Button("findRole") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		createFindRoleDataView(dataViewContainer, true);
	    }
	});
	add(form);
	createFindRoleDataView(dataViewContainer, false);
    }

    protected void createFindRoleDataView(
	    final WebMarkupContainer dataViewContainer, final boolean isVisible) {
	dataProvider = new RoleDataProvider(WICKET_ID_role, this, sortAsc);
	umgrRoleList = new ArrayList<UmgrRole>();
	final DataView<UmgrRole> dataView = new DataView<UmgrRole>(
		WICKET_ID_pageable, dataProvider) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    UmgrRole roleCriteria = new UmgrRole();
		    if (PortalUtils.exists(getRole())) {
			roleCriteria.setRole(getRole().replaceAll("\\*", "%"));
		    }
		    if (PortalUtils.exists(getDescription())) {
			roleCriteria.setDescription(getDescription()
				.replaceAll("\\*", "%"));
		    }
		    dataProvider.loadUmgrRolesList(roleCriteria, true);
		    refreshTotalItemCount();
		    if (dataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error("An error occured while loading Role list", dple);
		    error(getLocalizer().getString("role.find.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @SuppressWarnings("rawtypes")
	    @Override
	    protected void populateItem(final Item<UmgrRole> item) {
		final UmgrRole entry = item.getModelObject();

		umgrRoleList.add(entry);
		Link roleLink = new Link<UmgrRole>(WICKET_ID_LRoleLink,
			item.getModel()) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {
			UmgrRole cModel = (UmgrRole) item.getModelObject();
			loadRoleDetails(cModel);
		    }
		};
		item.add(roleLink);
		roleLink.add(new Label(WICKET_ID_Lrole, entry.getRole()));
		item.add(new Label(WICKET_ID_Ldescription, String.valueOf(entry
			.getDescription())));

	    }

	    private void refreshTotalItemCount() {
		totalItemString = Integer.toString(dataProvider.size());
		int total = getItemCount();
		if (total > 0) {
		    startIndex = getCurrentPage() * getItemsPerPage() + 1;
		    endIndex = startIndex + getItemsPerPage() - 1;
		    if (endIndex > total)
			endIndex = total;
		} else {
		    startIndex = 0;
		    endIndex = 0;
		}
	    }

	};
	dataView.setItemsPerPage(10);
	dataViewContainer.addOrReplace(dataView);

	dataViewContainer.addOrReplace(new OrderByBorder(WICKET_ID_orderByRole,
		WICKET_ID_Lrole, dataProvider) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSortChanged() {
		sortAsc = !sortAsc;
		// For some reasons the dataView can be null when the
		// page is
		// loading
		// and the sort is clicked (clicking the name header),
		// so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
		createFindRoleDataView(dataViewContainer, true);

	    }
	});
	dataViewContainer.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("umgrRoleList.noItemsMsg", this)) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return false;
		} else {
		    return super.isVisible();
		}
	    }
	});

	// Navigator example: << < 1 2 > >>
	dataViewContainer.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));
	dataViewContainer.setVisible(isVisible);

    }

    public void loadRoleDetails(UmgrRole role) {
	setResponsePage(new EditRolePage(role));
    }

    public UmgrRole getUmgrRole() {
	return umgrRole;
    }

    public void setUmgrRole(UmgrRole umgrRole) {
	this.umgrRole = umgrRole;
    }

    public String getRole() {
	return role;
    }

    public void setRole(String role) {
	this.role = role;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

}
