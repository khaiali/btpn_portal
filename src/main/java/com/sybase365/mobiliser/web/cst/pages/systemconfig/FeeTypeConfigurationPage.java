package com.sybase365.mobiliser.web.cst.pages.systemconfig;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.system.CreateUseCaseFeeTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateUseCaseFeeTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteUseCaseFeeTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteUseCaseFeeTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetUseCasesFeeTypesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetUseCasesFeeTypesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateUseCaseFeeTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateUseCaseFeeTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.UseCaseFeeType;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.FeeTypeConfDataProvider;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class FeeTypeConfigurationPage extends BaseSystemConfigurationPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(FeeTypeConfigurationPage.class);

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    // Data Model for table list
    private FeeTypeConfDataProvider dataProvider;
    WebMarkupContainer useCaseConfigContainer;

    private List<UseCaseFeeType> useCaseFeeTypes;
    private UseCaseFeeType useCaseFeeType;

    public UseCaseFeeType getUseCaseFeeType() {
	return useCaseFeeType;
    }

    public void setUseCaseFeeType(UseCaseFeeType useCaseFeeType) {
	this.useCaseFeeType = useCaseFeeType;
    }

    private boolean forceReload = true;

    private int rowIndex = 1;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByUseCase = "orderByUseCase";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_id = "id";
    private static final String WICKET_ID_feeType = "feeType";
    private static final String WICKET_ID_useCase = "useCase";
    private static final String WICKET_ID_editAction = "editAction";
    private static final String WICKET_ID_removeAction = "removeAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    Form<?> feeConfigForm;

    private LocalizableLookupDropDownChoice<Integer> payerPITypesList;
    private LocalizableLookupDropDownChoice<Integer> payeePITypesList;
    private LocalizableLookupDropDownChoice<Integer> useCaseList;
    private LocalizableLookupDropDownChoice<String> payerOrgUnitsList;
    private LocalizableLookupDropDownChoice<String> payeeOrgUnitsList;
    private KeyValueDropDownChoice<Integer, String> feeTypesList;
    private KeyValueDropDownChoice<Boolean, String> payeeFeeOptions;
    private KeyValueDropDownChoice<Boolean, String> copyFromAuthOptions;
    private KeyValueDropDownChoice<Boolean, String> includeInLimitOptions;
    private LocalizableLookupDropDownChoice<Integer> subTxnTypesList;

    public FeeTypeConfigurationPage() {
	super();
	initPageComponents();
    }

    protected void initPageComponents() {
	feeConfigForm = new Form("feeConfigurationForm",
		new CompoundPropertyModel<FeeTypeConfigurationPage>(this));

	useCaseConfigContainer = new WebMarkupContainer(
		"useCaseConfigContainer");

	createUseCaseConfigContainer(feeConfigForm);

	add(feeConfigForm);

	feeConfigForm.add(new FeedbackPanel("errorMessages"));

	createFeeTypesDataView(feeConfigForm);
    }

    private void createFeeTypesDataView(Form form) {

	dataProvider = new FeeTypeConfDataProvider(WICKET_ID_id, this);

	useCaseFeeTypes = new ArrayList<UseCaseFeeType>();

	form.addOrReplace(new Button("addFeeType") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(new AddFeeTypePage());
	    };
	}.setDefaultFormProcessing(false));

	form.addOrReplace(new Button("addUseCaseConf") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		feeConfigForm.clearInput();
		setUseCaseFeeType(new UseCaseFeeType());
		useCaseConfigContainer.setVisible(true);
		feeConfigForm.addOrReplace(useCaseConfigContainer);
	    };
	}.setDefaultFormProcessing(false));

	final DataView<UseCaseFeeType> dataView = new DataView<UseCaseFeeType>(
		WICKET_ID_pageable, dataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadUseCaseFeeTypesList(forceReload);
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
		    error(getLocalizer().getString(
			    "useCaseFeeTypes.load.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<UseCaseFeeType> item) {

		final UseCaseFeeType entry = item.getModelObject();

		useCaseFeeTypes.add(entry);

		item.add(new Label(WICKET_ID_id, String.valueOf(entry.getId())));

		item.add(new Label(WICKET_ID_useCase, getDisplayValue(String.valueOf(entry.getUseCaseId()),Constants.RESOURCE_BUNDLE_USE_CASES)));

		item.add(new Label(WICKET_ID_feeType, getFeeType(entry
			.getFeeTypeId())));

		// Edit Action
		Link<UseCaseFeeType> editLink = new Link<UseCaseFeeType>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			setUseCaseFeeType(entry);
			useCaseConfigContainer.setVisible(true);
			feeConfigForm.addOrReplace(useCaseConfigContainer);
		    }
		};
		item.add(editLink);

		Link removeLink = new Link<UseCaseFeeType>(
			WICKET_ID_removeAction, item.getModel()) {
		    @Override
		    public void onClick() {
			UseCaseFeeType entry = (UseCaseFeeType) getModelObject();
			removeUseCaseFeeType(entry);
			forceReload = true;
		    }
		};

		removeLink
			.add(new SimpleAttributeModifier("onclick",
				"return confirm('"
					+ getLocalizer().getString(
						"feeType.remove.confirm", this)
					+ "');"));

		item.add(removeLink);

		// set items in even/odd rows to different css style classes
		item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
			true, new AbstractReadOnlyModel<String>() {
			    @Override
			    public String getObject() {
				return (item.getIndex() % 2 == 1) ? Constants.CSS_STYLE_ODD
					: Constants.CSS_STYLE_EVEN;
			    }
			}));

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
	form.addOrReplace(dataView);

	form.addOrReplace(new OrderByBorder(WICKET_ID_orderByUseCase,
		WICKET_ID_useCase, dataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the page is
		// loading
		// and the sort is clicked (clicking the name header), so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	form.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("feeTypesList.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString(
				"feeTypesList.addFeeTypeHelp", this)) {
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

    private void createUseCaseConfigContainer(Form<?> form) {

	useCaseConfigContainer.setOutputMarkupId(true);
	useCaseConfigContainer.setOutputMarkupPlaceholderTag(true);
	
	useCaseList=(LocalizableLookupDropDownChoice<Integer>)new LocalizableLookupDropDownChoice<Integer>(
		"useCaseFeeType.useCaseId", Integer.class, "usecases", this,
		false, true).setNullValid(true)
		.add(new ErrorIndicator());


	
	payeeOrgUnitsList=(LocalizableLookupDropDownChoice<String>)new LocalizableLookupDropDownChoice<String>(
		"useCaseFeeType.orgUnitPayee", String.class, "orgunits", this,
		false, true).setNullValid(true)
		.add(new ErrorIndicator());

	payerOrgUnitsList=(LocalizableLookupDropDownChoice<String>)new LocalizableLookupDropDownChoice<String>(
		"useCaseFeeType.orgUnitPayer", String.class, "orgunits", this,
		false, true).setNullValid(true)
		.add(new ErrorIndicator());
	
	
	payeePITypesList=(LocalizableLookupDropDownChoice<Integer>)new LocalizableLookupDropDownChoice<Integer>(
		"useCaseFeeType.payeePiType", Integer.class, "pitypes", this,
		false, true).setNullValid(true)
		.add(new ErrorIndicator());
	

	payerPITypesList=(LocalizableLookupDropDownChoice<Integer>)new LocalizableLookupDropDownChoice<Integer>(
		"useCaseFeeType.payerPiType", Integer.class, "pitypes", this,
		false, true).setNullValid(true)
		.add(new ErrorIndicator());

	feeTypesList = (KeyValueDropDownChoice<Integer, String>) new KeyValueDropDownChoice<Integer, String>(
		"useCaseFeeType.feeTypeId", getFeeTypesList()).setNullValid(
		true).add(new ErrorIndicator());
	
	subTxnTypesList=(LocalizableLookupDropDownChoice<Integer>)new LocalizableLookupDropDownChoice<Integer>(
		"useCaseFeeType.subTxnType", Integer.class, "subtxntypes", this,
		false, true).setNullValid(true)
		.add(new ErrorIndicator());


	
	payeeFeeOptions = (KeyValueDropDownChoice<Boolean, String>) new KeyValueDropDownChoice<Boolean, String>(
		"useCaseFeeType.payeeFee", getYesNoOptions())
		.setNullValid(true).add(new ErrorIndicator());

	copyFromAuthOptions = (KeyValueDropDownChoice<Boolean, String>) new KeyValueDropDownChoice<Boolean, String>(
		"useCaseFeeType.copyFromAuth", getYesNoOptions())
		.setNullValid(true);

	includeInLimitOptions = (KeyValueDropDownChoice<Boolean, String>) new KeyValueDropDownChoice<Boolean, String>(
		"useCaseFeeType.includeInLimit", getYesNoOptions())
		.setNullValid(true);

	useCaseList.setRequired(true);
	useCaseConfigContainer.add(useCaseList);

	payerOrgUnitsList.setRequired(true);
	useCaseConfigContainer.add(payerOrgUnitsList);

	payeeOrgUnitsList.setRequired(true);
	useCaseConfigContainer.add(payeeOrgUnitsList);

	useCaseConfigContainer.add(payerPITypesList);
	useCaseConfigContainer.add(payeePITypesList);

	feeTypesList.setRequired(true);
	useCaseConfigContainer.add(feeTypesList);

	payeeFeeOptions.setRequired(true);
	useCaseConfigContainer.add(payeeFeeOptions);

	useCaseConfigContainer.add(subTxnTypesList);

	copyFromAuthOptions.setRequired(true);
	useCaseConfigContainer.add(copyFromAuthOptions);

	includeInLimitOptions.setRequired(true);
	useCaseConfigContainer.add(includeInLimitOptions);

	useCaseConfigContainer.add(new Button("save") {
	    @Override
	    public void onSubmit() {
		handleSubmit();
	    }
	});
	useCaseConfigContainer.add(new AjaxLink("cancel") {

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		useCaseConfigContainer.setVisible(false);
		target.addComponent(useCaseConfigContainer);

	    }
	});

	add(useCaseConfigContainer.setVisible(false));

	form.addOrReplace(useCaseConfigContainer);

    }

    private void handleSubmit() {
	LOG.debug("# FeeTypeConfigurationPage.handleSubmit()");
	if (!PortalUtils.exists(getUseCaseFeeType().getId())) {
	    createUseCaseFeeType();
	} else {
	    updateUseCaseFeeType();
	}
	forceReload = true;
	createFeeTypesDataView(feeConfigForm);
	useCaseConfigContainer.setVisible(false);
    }

    private void createUseCaseFeeType() {
	LOG.debug("# FeeTypeConfigurationPage.createUseCaseFeeType()");
	CreateUseCaseFeeTypeResponse response = null;
	try {
	    CreateUseCaseFeeTypeRequest request = getNewMobiliserRequest(CreateUseCaseFeeTypeRequest.class);
	    request.setUseCaseFeeType(getUseCaseFeeType());
	    response = wsFeeConfClient.createUseCaseFeeType(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# Error occurred while updating use case configuration");
		return;
	    }
	    LOG.info("# Successfully created the new use case configuration");
	} catch (Exception e) {
	    LOG.error("# Error occurred while updating use case configuration");
	    error(getLocalizer().getString("create.usecaseconf.error", this));
	    return;
	}

    }

    private void updateUseCaseFeeType() {
	LOG.debug("# FeeTypeConfigurationPage.updateUseCaseFeeType()");
	UpdateUseCaseFeeTypeResponse response = null;
	try {
	    UpdateUseCaseFeeTypeRequest request = getNewMobiliserRequest(UpdateUseCaseFeeTypeRequest.class);
	    request.setUseCaseFeeType(getUseCaseFeeType());
	    response = wsFeeConfClient.updateUseCaseFeeType(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# Error occurred while updating use case configuration");
		return;
	    }
	    LOG.info("# Successfully updated use case configuration");
	} catch (Exception e) {
	    LOG.error("# Error occurred while updating use case configuration");
	    error(getLocalizer().getString("update.usecaseconf.error", this));
	    return;
	}

    }

    private void removeUseCaseFeeType(UseCaseFeeType useCaseFeeType) {
	LOG.debug("# FeeTypeConfigurationPage.removeUseCaseFeeType()");
	DeleteUseCaseFeeTypeResponse response = null;
	try {
	    DeleteUseCaseFeeTypeRequest request = getNewMobiliserRequest(DeleteUseCaseFeeTypeRequest.class);
	    request.setUseCaseFeeTypeId(useCaseFeeType.getId().longValue());
	    response = wsFeeConfClient.deleteUseCaseFeeType(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# Error occurred while deleting use case configuration");
		return;
	    }
	    LOG.info("# Successfully deleted use case configuration");

	} catch (Exception e) {
	    LOG.error("# Error occurred while deleting use case configuration");
	    error(getLocalizer().getString("delete.usecaseconf.error", this));
	    return;
	}

    }

    public List<UseCaseFeeType> getUseCaseFeeTypes()
	    throws DataProviderLoadException {

	LOG.debug("# MobiliserBasePage.getUseCaseFeeTypes()");
	GetUseCasesFeeTypesResponse response = null;
	try {
	    GetUseCasesFeeTypesRequest request = getNewMobiliserRequest(GetUseCasesFeeTypesRequest.class);

	    response = wsFeeConfClient.getUseCasesFeeTypes(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# Error while getting use case configurations");
		return null;
	    }
	} catch (Exception e) {
	    LOG.error("# Error while getting use case configurations", e);
	    throw new DataProviderLoadException();

	}
	return response.getUseCaseFeeTypes();
    }

}
