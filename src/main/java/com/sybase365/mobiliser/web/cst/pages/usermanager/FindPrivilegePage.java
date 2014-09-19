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

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrPrivilege;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.PrivilegeDataProvider;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class FindPrivilegePage extends BaseUserManagerPage {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(FindPrivilegePage.class);
    private String privilege;
    private String description;
    private UmgrPrivilege umgrPrivilege;
    private List<UmgrPrivilege> umgrPrivilegeList;
    private PrivilegeDataProvider dataProvider;
    private boolean sortAsc = true;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_privilege = "privilege";
    private static final String WICKET_ID_orderByPrivilege = "orderByPrivilege";
    private static final String WICKET_ID_LPrivilegeLink = "LPrivilegeLink";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_Lprivilege = "Lprivilege";
    private static final String WICKET_ID_Ldescription = "Ldescription";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	final Form<?> form = new Form("findPrivilegeForm",
		new CompoundPropertyModel<FindPrivilegePage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new TextField<String>("privilege").add(new ErrorIndicator()));
	form.add(new TextField<String>("description").add(new ErrorIndicator()));

	final WebMarkupContainer dataViewContainer = new WebMarkupContainer(
		"dataViewContainer");
	form.add(dataViewContainer);
	form.add(new Button("findPrivilege") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		createFindPrivilegeDataView(dataViewContainer, true);
	    }
	});
	add(form);
	createFindPrivilegeDataView(dataViewContainer, false);
    }

    protected void createFindPrivilegeDataView(
	    final WebMarkupContainer dataViewContainer, final boolean isVisible) {
	dataProvider = new PrivilegeDataProvider(WICKET_ID_privilege, this,
		sortAsc);
	umgrPrivilegeList = new ArrayList<UmgrPrivilege>();
	final DataView<UmgrPrivilege> dataView = new DataView<UmgrPrivilege>(
		WICKET_ID_pageable, dataProvider) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    UmgrPrivilege privilegeCriteria = new UmgrPrivilege();
		    if (PortalUtils.exists(getPrivilege())) {
			privilegeCriteria.setPrivilege(getPrivilege()
				.replaceAll("\\*", "%"));
		    }
		    if (PortalUtils.exists(getDescription())) {
			privilegeCriteria.setDescription(getDescription()
				.replaceAll("\\*", "%"));
		    }
		    dataProvider
			    .loadUmgrPrivilegesList(privilegeCriteria, true);
		    refreshTotalItemCount();
		    if (dataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error("An error occured while loading Privilege list",
			    dple);
		    error(getLocalizer()
			    .getString("privilege.find.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @SuppressWarnings("rawtypes")
	    @Override
	    protected void populateItem(final Item<UmgrPrivilege> item) {
		final UmgrPrivilege entry = item.getModelObject();

		umgrPrivilegeList.add(entry);
		Link privilegeLink = new Link<UmgrPrivilege>(
			WICKET_ID_LPrivilegeLink, item.getModel()) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    public void onClick() {
			UmgrPrivilege cModel = (UmgrPrivilege) item
				.getModelObject();
			loadPrivilegeDetails(cModel);
		    }
		};
		item.add(privilegeLink);
		privilegeLink.add(new Label(WICKET_ID_Lprivilege, entry
			.getPrivilege()));
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

	dataViewContainer
		.addOrReplace(new OrderByBorder(WICKET_ID_orderByPrivilege,
			WICKET_ID_Lprivilege, dataProvider) {
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
			createFindPrivilegeDataView(dataViewContainer, true);
		    }
		});
	dataViewContainer
		.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
			getLocalizer().getString(
				"umgrPrivilegeList.noItemsMsg", this)) {
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

    public void loadPrivilegeDetails(UmgrPrivilege privilege) {
	setResponsePage(new EditPrivilegePage(privilege));
    }

    public UmgrPrivilege getUmgrPrivilege() {
	return umgrPrivilege;
    }

    public void setUmgrPrivilege(UmgrPrivilege umgrPrivilege) {
	this.umgrPrivilege = umgrPrivilege;
    }

    public String getPrivilege() {
	return privilege;
    }

    public void setPrivilege(String privilege) {
	this.privilege = privilege;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }
}
