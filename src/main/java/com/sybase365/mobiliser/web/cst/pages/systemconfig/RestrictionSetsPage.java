package com.sybase365.mobiliser.web.cst.pages.systemconfig;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.system.CreateRestrictionsGroupRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateRestrictionsGroupResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteRestrictionRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteRestrictionResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteRestrictionsGroupRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteRestrictionsGroupResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetRestrictionRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetRestrictionResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.SwapRestrictionRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.SwapRestrictionResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Restriction;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.RestrictionsGroup;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.RestrictionSetBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.RestrictionSetsDataProvider;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class RestrictionSetsPage extends BaseSystemConfigurationPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(RestrictionSetsPage.class);

    private RestrictionSetsDataProvider dataProvider;
    private List<RestrictionSetBean> restSetBeanList;
    private RestrictionsGroup restrictionSet;

    WebMarkupContainer addRestGrpContainer;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private int rowIndex = 1;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_selected = "selected";
    private static final String WICKET_ID_name = "restrictionSet.name";
    private static final String WICKET_ID_restriction = "restriction.name";
    private static final String WICKET_ID_restriction_priority = "restriction.priority";
    private static final String WICKET_ID_editAction = "editAction";
    private static final String WICKET_ID_removeRestGrpAction = "removeRestGrpAction";
    private static final String WICKET_ID_addAction = "addAction";
    private static final String WICKET_ID_removeRestAction = "removeRestAction";
    private static final String WICKET_ID_moveUpAction = "moveUpAction";
    private static final String WICKET_ID_moveDownAction = "moveDownAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Form<?> form = new Form("restrictionSetForm",
		new CompoundPropertyModel<RestrictionSetsPage>(this));

	add(form);

	form.add(new FeedbackPanel("errorMessages"));

	createRestrictionSetsDataView(form);

	createAddRestGrpContainer(form);
    }

    public void createRestrictionSetsDataView(final Form form) {

	dataProvider = new RestrictionSetsDataProvider(WICKET_ID_name, this);

	restSetBeanList = new ArrayList<RestrictionSetBean>();

	form.addOrReplace(new Button("addRestrictionGroup") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		form.clearInput();
		setRestrictionSet(new RestrictionsGroup());
		addRestGrpContainer.setVisible(Boolean.TRUE);
	    };
	}.setDefaultFormProcessing(false));

	final DataView<RestrictionSetBean> dataView = new DataView<RestrictionSetBean>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadRestrictionSetBeansList(forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    if (dataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }

		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading restriction groups",
			    dple);
		    error(getLocalizer().getString(
			    "restrictionGroups.load.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<RestrictionSetBean> item) {

		final RestrictionSetBean entry = item.getModelObject();

		restSetBeanList.add(entry);

		item.add(new Label(WICKET_ID_name, PortalUtils.exists(entry
			.getRestriction()) ? "" : entry.getRestrictionSet()
			.getName()));

		item.add(new Label(WICKET_ID_restriction, PortalUtils
			.exists(entry.getRestriction()) ? String.valueOf(entry
			.getRestriction().getName()) : ""));

		// Remove Action
		Link removeRestGrpLink = new Link<RestrictionSetBean>(
			WICKET_ID_removeRestGrpAction, item.getModel()) {
		    @Override
		    public void onClick() {
			removeRestrictionSet((RestrictionSetBean) item
				.getModelObject());
			forceReload = true;
			createRestrictionSetsDataView(form);

		    }
		};

		removeRestGrpLink
			.add(new SimpleAttributeModifier(
				"onclick",
				"return confirm('"
					+ getLocalizer()
						.getString(
							"restrictionGroup.remove.confirm",
							this) + "');"));

		item.add(removeRestGrpLink);

		Link addLink = new Link<RestrictionSetBean>(
			WICKET_ID_addAction, item.getModel()) {
		    @Override
		    public void onClick() {
			setResponsePage(new RestrictionPage(entry
				.getRestrictionSet().getGroupId(), null));
		    }
		};
		item.add(addLink);

		// Edit Action
		Link<RestrictionSetBean> editLink = new Link<RestrictionSetBean>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			editRestriction((RestrictionSetBean) item
				.getModelObject());
		    }
		};
		item.add(editLink);

		Link removeRestLink = new Link<RestrictionSetBean>(
			WICKET_ID_removeRestAction, item.getModel()) {
		    @Override
		    public void onClick() {
			removeRestriction((RestrictionSetBean) item
				.getModelObject());
			forceReload = true;
			createRestrictionSetsDataView(form);
		    }
		};

		removeRestLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"restriction.remove.confirm", this)
				+ "');"));

		item.add(removeRestLink);

		// Move Up Action
		Link moveUpLink = new Link<RestrictionSetBean>(
			WICKET_ID_moveUpAction, item.getModel()) {
		    @Override
		    public void onClick() {
			swapRestriction(entry, true);
			forceReload = true;
			createRestrictionSetsDataView(form);
		    }
		};
		item.add(moveUpLink);

		// Move Down Action
		Link moveDownLink = new Link<RestrictionSetBean>(
			WICKET_ID_moveDownAction, item.getModel()) {
		    @Override
		    public void onClick() {
			swapRestriction(entry, false);
			forceReload = true;
			createRestrictionSetsDataView(form);
		    }
		};
		item.add(moveDownLink);

		if (!PortalUtils.exists(entry.getRestriction())) {
		    editLink.setVisible(Boolean.FALSE);
		    removeRestLink.setVisible(Boolean.FALSE);
		    moveUpLink.setVisible(Boolean.FALSE);
		    moveDownLink.setVisible(Boolean.FALSE);

		} else {
		    addLink.setVisible(Boolean.FALSE);
		    removeRestGrpLink.setVisible(Boolean.FALSE);
		    if (!entry.isShowMoveUpLink())
			moveUpLink.setVisible(Boolean.FALSE);
		    if (!entry.isShowMoveDownLink())
			moveDownLink.setVisible(Boolean.FALSE);
		}

		// set items in even/odd rows to different css style classes
		item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
			true, new AbstractReadOnlyModel<String>() {
			    @Override
			    public String getObject() {
				return entry.getCssStyle();
			    }
			}));
	    }

	    private void refreshTotalItemCount() {
		totalItemString = new Integer(dataProvider.size()).toString();
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
	form.addOrReplace(dataView);

	form.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("restrictionGroupsList.noItemsMsg",
			this)
			+ "\n"
			+ getLocalizer().getString(
				"restrictionGroupsList.addRestrictionGrpHelp",
				this)) {
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
	form.addOrReplace(new CustomPagingNavigator(WICKET_ID_navigator,
		dataView));

	form.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	form.addOrReplace(new Label(WICKET_ID_startIndex, new PropertyModel(
		this, "startIndex")));

	form.addOrReplace(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));

    }

    public void createAddRestGrpContainer(final Form form) {
	addRestGrpContainer = new WebMarkupContainer("addRestGrpContainer");

	addRestGrpContainer.setOutputMarkupId(true);
	addRestGrpContainer.setOutputMarkupPlaceholderTag(true);

	addRestGrpContainer.add(new RequiredTextField<Integer>(
		"restrictionSet.groupId").add(
		Constants.isShortAttributeModifier).add(new ErrorIndicator()));

	addRestGrpContainer.add(new RequiredTextField<String>(
		"restrictionSet.name").add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	addRestGrpContainer.add(new Button("save") {
	    @Override
	    public void onSubmit() {
		createRestrictionSet();
		addRestGrpContainer.setVisible(false);
		forceReload = true;
		createRestrictionSetsDataView(form);
	    }
	});
	addRestGrpContainer.add(new AjaxLink("cancel") {

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		addRestGrpContainer.setVisible(false);
		target.addComponent(addRestGrpContainer);

	    }
	});

	form.add(addRestGrpContainer.setVisible(false));

	form.addOrReplace(addRestGrpContainer);

    }

    private void createRestrictionSet() {
	LOG.debug("# RestrictionSetsPage.createRestrictionSet()");
	CreateRestrictionsGroupResponse response;
	try {
	    CreateRestrictionsGroupRequest request = getNewMobiliserRequest(CreateRestrictionsGroupRequest.class);
	    request.setGroupId(getRestrictionSet().getGroupId());
	    request.setName(getRestrictionSet().getName());
	    response = wsTxnRestrictionClient.createRestrictionsGroup(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while creating new restriction group");
		return;
	    }
	} catch (Exception e) {
	    LOG.debug("# An error occurred while creating new restriction group");
	    error(getLocalizer().getString("restrictionGroup.create.error",
		    this));

	}
    }

    private void removeRestrictionSet(RestrictionSetBean restBean) {
	LOG.debug("# RestrictionSetsPage.removeRestrictionSet()");
	DeleteRestrictionsGroupResponse response;
	try {
	    DeleteRestrictionsGroupRequest request = getNewMobiliserRequest(DeleteRestrictionsGroupRequest.class);
	    request.setGroupId(restBean.getRestrictionSet().getGroupId());
	    response = wsTxnRestrictionClient.deleteRestrictionsGroup(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while removing restriction group");
		return;
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred while removing restriction group");
	    error(getLocalizer().getString("restrictionGroup.remove.error",
		    this));
	    return;

	}
    }

    private void removeRestriction(RestrictionSetBean restBean) {
	LOG.debug("# RestrictionSetsPage.removeRestriction()");
	DeleteRestrictionResponse response;
	try {
	    DeleteRestrictionRequest request = getNewMobiliserRequest(DeleteRestrictionRequest.class);
	    request.setRestrictionId(restBean.getRestriction()
		    .getRestrictionID().intValue());
	    response = wsTxnRestrictionClient.deleteRestriction(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while sremoving restriction");
		return;
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred while removing restriction");
	    error(getLocalizer().getString("restriction.remove.error", this));

	}
    }

    private void editRestriction(RestrictionSetBean restSetBean) {
	GetRestrictionResponse response;
	Restriction restriction;
	try {
	    GetRestrictionRequest request = getNewMobiliserRequest(GetRestrictionRequest.class);
	    request.setRestrictionId(restSetBean.getRestriction()
		    .getRestrictionID().longValue());
	    response = wsTxnRestrictionClient.getRestriction(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while fetching restriction");
		return;
	    }
	    restriction = response.getRestriction();
	} catch (Exception e) {
	    LOG.error("# An error occurred while fetching restriction");
	    error(getLocalizer().getString("restriction.fetch.error", this));
	    return;
	}
	setResponsePage(new RestrictionPage(
		restriction.getRestrictionGroupId(), restriction));

    }

    private void swapRestriction(RestrictionSetBean restSetBean,
	    boolean isMoveUp) {
	LOG.debug("# RestrictionSetsPage.swapRestriction()");

	int index = restSetBeanList.indexOf(restSetBean);

	SwapRestrictionResponse response;

	try {
	    SwapRestrictionRequest request = getNewMobiliserRequest(SwapRestrictionRequest.class);

	    request.getRestrictionId().add(
		    restSetBean.getRestriction().getRestrictionID());
	    if (isMoveUp) {
		request.getRestrictionId().add(
			restSetBeanList.get(index - 1).getRestriction()
				.getRestrictionID());
	    } else {
		request.getRestrictionId().add(
			restSetBeanList.get(index + 1).getRestriction()
				.getRestrictionID());
	    }

	    response = wsTxnRestrictionClient.swapRestrictionsPriority(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while swapping the priority of the restriction");
		return;
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred while swapping the priority of the restriction");
	    error(getLocalizer().getString("restriction.swap.priority.error",
		    this));
	    return;
	}

    }

    public RestrictionsGroup getRestrictionSet() {
	return restrictionSet;
    }

    public void setRestrictionSet(RestrictionsGroup restrictionSet) {
	this.restrictionSet = restrictionSet;
    }

}
