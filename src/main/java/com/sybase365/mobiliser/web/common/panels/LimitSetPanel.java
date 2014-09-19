package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.List;

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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitClass;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSet;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSetClass;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.LimitSetClassDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.LimitSetDataProvider;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class LimitSetPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(LimitSetPanel.class);

    private LimitSetDataProvider dataProvider;
    private LimitSetClassDataProvider consumerDataProvider;
    private List<LimitSet> limitSetList;
    private List<LimitSetClass> limitSetClassList;
    List<LimitClass> limitClassList = null;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private int rowIndex = 1;

    private static final String WICKET_ID_removeLink = "removeLink";
    private static final String WICKET_ID_editLink = "editLink";
    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_LName = "LName";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";
    private static final String WICKET_ID_LUseCase = "LUseCase";
    private static final String WICKET_ID_LLimitClass = "LLimitClass";

    private LimitSet limitSet;
    private LimitSetClass limitSetClass;
    private Form<?> limitSetForm;
    private WebMarkupContainer consumerLimitFormDiv;
    private WebMarkupContainer consumerLimitViewContainer;
    private WebMarkupContainer dataViewContainer;
    private MobiliserBasePage basePage;
    private CustomerBean customer;
    private boolean editLimitSetClass;

    public LimitSetPanel(String id, MobiliserBasePage basePage,
	    CustomerBean customer, Boolean isIndividual) {
	super(id);
	this.basePage = basePage;
	this.customer = customer;
	this.constructPanel();
    }

    private void constructPanel() {

	limitSetForm = new Form("limitSetForm",
		new CompoundPropertyModel<LimitSetPanel>(this));
	consumerLimitFormDiv = new WebMarkupContainer("consumerLimitFormDiv");
	add(new FeedbackPanel("errorMessages"));
	limitSetForm.setOutputMarkupId(true);
	limitSetForm.setOutputMarkupPlaceholderTag(true);
	consumerLimitFormDiv.setOutputMarkupId(true);
	consumerLimitFormDiv.setOutputMarkupPlaceholderTag(true);

	AjaxLink addLimitSetLink = new AjaxLink("addNewLimitSetLink") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		limitSet = null;
		editLimitSetClass = false;
		limitSetForm = (Form<?>) createLimitSetForm(true);
		try {
		    createConsumerLimitDataView(consumerLimitViewContainer,
			    false);
		    createConsumerLimitForm(false, false);
		} catch (Exception e) {
		    LOG.error(
			    "# An error occurred while creating limit set class data view",
			    e);
		}
		limitSet = null;
		target.addComponent(limitSetForm);
		target.addComponent(consumerLimitFormDiv);
		target.addComponent(consumerLimitViewContainer);
	    }
	};
	createLimitSetForm(false);
	add(addLimitSetLink);
	consumerLimitFormDiv.setOutputMarkupId(true);
	consumerLimitFormDiv.setOutputMarkupPlaceholderTag(true);
	createConsumerLimitForm(false, true);
	// Data view

	dataViewContainer = new WebMarkupContainer("limitSetDataViewContainer");

	add(dataViewContainer);
	consumerLimitViewContainer = new WebMarkupContainer(
		"consumerLimitDataViewContainer");
	try {
	    createLimitSetDataView(dataViewContainer);
	    createConsumerLimitDataView(consumerLimitViewContainer, false);
	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while creating limit set class data view",
		    e);
	}
	consumerLimitViewContainer.setOutputMarkupId(true);
	consumerLimitViewContainer.setOutputMarkupPlaceholderTag(true);

    }

    private void createLimitSetDataView(WebMarkupContainer dataViewContainer)
	    throws Exception {

	dataProvider = new LimitSetDataProvider(WICKET_ID_pageable, basePage);
	limitSetList = new ArrayList<LimitSet>();
	final DataView<LimitSet> dataView = new DataView<LimitSet>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    Long limitSetId = null;
		    if (PortalUtils.exists(customer)
			    && PortalUtils.exists(customer.getLimitId())) {
			limitSetId = customer.getLimitId();
		    }
		    dataProvider.findLimitSet(limitSetId, forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error("# An error occurred while finding limit sets",
			    dple);
		    error(getLocalizer().getString("ERROR.LIMITSET_GET", this));
		}

		refreshTotalItemCount();
		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		    createLimitSetForm(true);
		}
		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<LimitSet> item) {
		final LimitSet entry = item.getModelObject();

		limitSetList.add(entry);

		item.add(new Label(WICKET_ID_LName, entry.getName()));
		Link editLink = new Link<LimitSet>(WICKET_ID_editLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			editLimitSetClass = false;
			limitSetClass = null;
			limitSet = (LimitSet) item.getModelObject();
			createLimitSetForm(false);
			try {
			    createConsumerLimitDataView(
				    consumerLimitViewContainer, true);

			} catch (Exception e) {
			    LOG.error(
				    "# An error occurred while creating limit set class data view",
				    e);
			}
		    }
		};

		item.add(editLink);

		Link removeLink = new Link<LimitSet>(WICKET_ID_removeLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			limitSet = (LimitSet) item.getModelObject();
			editLimitSetClass = false;
			try {
			    if (basePage.removeLimitSet(limitSet.getId())) {
				info(getLocalizer().getString(
					"MESSAGE.LIMITSET_REMOVED", this));
				limitSet = null;
				createConsumerLimitDataView(
					consumerLimitViewContainer, false);
				createConsumerLimitForm(false, false);
			    }
			} catch (Exception e) {
			    LOG.error(
				    "# An error occurred while removing limit sets",
				    e);
			    error(getLocalizer().getString(
				    "ERROR.LIMITSET_REMOVE", this));
			}
		    }
		};
		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"limitSet.remove.confirm", this)
				+ "');"));
		item.add(removeLink);

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
	dataView.setVisible(true);
	dataView.setItemsPerPage(10);
	dataViewContainer.addOrReplace(dataView);

	dataViewContainer.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("limitSet.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString("limitSet.noItemsHelpMsg",
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
	dataViewContainer.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));

    }

    private void createConsumerLimitDataView(
	    WebMarkupContainer consumerLimitViewContainer, boolean visible)
	    throws Exception {

	consumerDataProvider = new LimitSetClassDataProvider(
		WICKET_ID_pageable, basePage);
	limitSetClassList = new ArrayList<LimitSetClass>();
	final DataView<LimitSetClass> dataView = new DataView<LimitSetClass>(
		WICKET_ID_pageable, consumerDataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    consumerDataProvider.findLimitSetClasses(limitSet.getId(),
			    Constants.LIMIT_LIMITSET_TYPE, forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while finding Limit set classes",
			    dple);
		    error(getLocalizer().getString("inactive.agent.load.error",
			    this));
		}

		refreshTotalItemCount();
		if (consumerDataProvider.size() > 0) {
		    consumerLimitFormDiv = createConsumerLimitForm(false, false);
		    setVisible(true);
		} else {
		    setVisible(false);
		    consumerLimitFormDiv = createConsumerLimitForm(true, true);
		}
		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<LimitSetClass> item) {
		final LimitSetClass entry = item.getModelObject();

		limitSetClassList.add(entry);

		item.add(new Label(WICKET_ID_LUseCase,
			entry.getUseCaseId() != null ? basePage
				.getDisplayValue(
					String.valueOf(entry.getUseCaseId()),
					Constants.RESOURCE_BUNDLE_USE_CASES)
				: ""));
		item.add(new Label(WICKET_ID_LLimitClass, entry.getLimitClass()
			.getName()));
		Link editLink = new Link<LimitSetClass>(WICKET_ID_editLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			editLimitSetClass = true;
			limitSetClass = (LimitSetClass) item.getModelObject();
			consumerLimitFormDiv = createConsumerLimitForm(true,
				false);
		    }
		};

		item.add(editLink);

		Link removeLink = new Link<LimitSetClass>(WICKET_ID_removeLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			editLimitSetClass = false;
			limitSetClass = (LimitSetClass) item.getModelObject();
			try {
			    if (basePage.removeLimitSetClass(limitSetClass
				    .getId())) {
				info(getLocalizer().getString(
					"MESSAGE.LIMITSET_CLASS.REMOVED", this));
				limitSetClass = null;
			    }
			} catch (Exception e) {
			    LOG.error(
				    "# An error occurred while removing limit sets class",
				    e);
			    error(getLocalizer().getString(
				    "ERROR.LIMITSET_CLASS.REMOVE", this));
			}
		    }
		};
		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"limitSetClass.remove.confirm", this)
				+ "');"));
		item.add(removeLink);

	    }

	    private void refreshTotalItemCount() {
		totalItemString = new Integer(consumerDataProvider.size())
			.toString();
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
	dataView.setVisible(true);
	dataView.setItemsPerPage(10);
	consumerLimitViewContainer.addOrReplace(dataView);

	consumerLimitViewContainer.addOrReplace(new MultiLineLabel(
		WICKET_ID_noItemsMsg, getLocalizer().getString(
			"limitSetClass.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString(
				"limitSetClass.noItemsHelpMsg", this)) {
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
	consumerLimitViewContainer.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	consumerLimitViewContainer.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	consumerLimitViewContainer.addOrReplace(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	consumerLimitViewContainer.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));

	AjaxLink addNewUseCaseLink = new AjaxLink("addNewUseCaseLink") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		limitSetClass = null;
		editLimitSetClass = false;
		consumerLimitFormDiv = createConsumerLimitForm(true, true);
		// limitSet = null;
		target.addComponent(consumerLimitFormDiv);
	    }
	};
	consumerLimitViewContainer.addOrReplace(addNewUseCaseLink);
	consumerLimitViewContainer.setVisible(visible);
	addOrReplace(consumerLimitViewContainer);
    }

    private WebMarkupContainer createLimitSetForm(boolean visible) {
	limitSetForm
		.addOrReplace(new RequiredTextField<String>("limitSet.name")
			.add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)
			.add(new ErrorIndicator()));
	limitSetForm.setVisible(visible);
	limitSetForm.addOrReplace(new Button("saveLimitSet") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		editLimitSetClass = false;
		try {

		    limitSet.setIndividual(Boolean.FALSE);
		    if (PortalUtils.exists(basePage.addLimitSet(limitSet))) {
			createLimitSetForm(false);
			createLimitSetDataView(dataViewContainer);
			createConsumerLimitDataView(consumerLimitViewContainer,
				true);
			limitSetClass = null;
		    }

		} catch (Exception e) {
		    LOG.error("# Error occurred while adding limit set" + e);
		    error(getLocalizer().getString("ERROR.LIMITSET_ADD", this));
		}
	    };
	});
	limitSetForm.addOrReplace(new Button("cancelLimitSet") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		editLimitSetClass = false;
		limitSetForm = (Form<?>) createLimitSetForm(false);
		limitSet = null;
	    };
	}.setDefaultFormProcessing(false));
	addOrReplace(limitSetForm);
	return limitSetForm;

    }

    private WebMarkupContainer createConsumerLimitForm(boolean visible,
	    boolean enabled) {
	Form<?> limitForm = new Form("consumerLimitForm",
		new CompoundPropertyModel<LimitSetPanel>(this));
	limitForm.add(new LocalizableLookupDropDownChoice<Integer>(
		"limitSetClass.useCaseId", Integer.class, "usecases", basePage,
		false, true).setRequired(false));
	limitForm.add(new KeyValueDropDownChoice<Long, String>(
		"limitSetClass.limitClass.id", getLimitClasses())
		.setNullValid(true).setRequired(true).setEnabled(enabled)
		.add(new ErrorIndicator()));
	limitForm.add(new Button("saveConsumerLimit") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		editLimitSetClass = false;
		try {
		    if (PortalUtils.exists(limitSetClass)
			    && PortalUtils.exists(limitSetClass.getId())) {
			basePage.updateLimitSetClass(limitSet.getId(),
				limitSetClass);
		    } else {
			limitSetClass = basePage.addLimitSetClass(
				limitSet.getId(), limitSetClass);
		    }
		    createConsumerLimitDataView(consumerLimitViewContainer,
			    true);
		} catch (Exception e) {
		    LOG.error("# Error occurred while saving limit set class"
			    + e);
		    error(getLocalizer().getString("ERROR.LIMITSET_CLASS.SAVE",
			    this));
		}
	    };
	});
	limitForm.add(new Button("cancelConsumerLimit") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		editLimitSetClass = false;
		consumerLimitFormDiv = createConsumerLimitForm(false, true);
		limitSetClass = null;
	    };
	}.setDefaultFormProcessing(false));
	consumerLimitFormDiv.setVisible(visible);
	if (editLimitSetClass) {
	    consumerLimitFormDiv.setVisible(editLimitSetClass);
	}
	consumerLimitFormDiv.addOrReplace(limitForm);
	add(consumerLimitFormDiv);
	return consumerLimitFormDiv;

    }

    public List<KeyValue<Long, String>> getLimitClasses() {
	List<KeyValue<Long, String>> limitClsKeyValueList = new ArrayList<KeyValue<Long, String>>();

	try {
	    if (!PortalUtils.exists(limitClassList)) {
		limitClassList = basePage.findLimitClassList();
	    }
	    KeyValue<Long, String> keyVal;
	    for (LimitClass limitCls : limitClassList) {
		keyVal = new KeyValue<Long, String>(limitCls.getId(),
			limitCls.getName());
		limitClsKeyValueList.add(keyVal);
	    }

	} catch (Exception e) {
	    error(getLocalizer().getString("limitclass.lookUp.error", this));
	    LOG.error("# Error occurred while getting limit classes");
	}

	return limitClsKeyValueList;
    }

    public void setLimitSet(LimitSet limitSet) {
	this.limitSet = limitSet;
    }

    public LimitSet getLimitSet() {
	return limitSet;
    }

}
