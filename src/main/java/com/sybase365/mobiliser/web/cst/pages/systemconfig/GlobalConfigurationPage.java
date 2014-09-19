package com.sybase365.mobiliser.web.cst.pages.systemconfig;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerType;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateCustomerTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateCustomerTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateOrgUnitRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateOrgUnitResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateRiskCategoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateRiskCategoryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSet;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.OrgUnit;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.RiskCategory;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.CustomerTypeConfigDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.OrgUnitConfigDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.RiskCategoryConfDataProvider;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class GlobalConfigurationPage extends BaseSystemConfigurationPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(GlobalConfigurationPage.class);

    List<LimitSet> limitSets = null;

    // Table Items
    private String ouTotalItemString = null;
    private int ouStartIndex = 0;
    private int ouEndIndex = 0;

    // Data Model for table list
    private OrgUnitConfigDataProvider orgUnitDataProvider;
    WebMarkupContainer orgUnitConfListContainer;
    WebMarkupContainer editOrgUnitConfContainer;
    private List<OrgUnit> orgUnitsList;

    private String ctTotalItemString = null;
    private int ctStartIndex = 0;
    private int ctEndIndex = 0;
    private CustomerTypeConfigDataProvider custTypeConfDataProvider;
    WebMarkupContainer custTypeConfListContainer;
    WebMarkupContainer editCustTypeConfContainer;
    private List<CustomerType> custTypeConfList;

    private String rcTotalItemString = null;
    private int rcStartIndex = 0;
    private int rcEndIndex = 0;
    private RiskCategoryConfDataProvider riskCatConfDataProvider;
    WebMarkupContainer riskCategoryConfListContainer;
    WebMarkupContainer editRiskCategoryConfContainer;
    private List<RiskCategory> riskCatConfList;

    private boolean forceReloadOrgUnits = true;
    private boolean forceReloadCustomerTypes = true;
    private boolean forceReloadRiskCat = true;

    private int rowIndex = 1;

    private static final String WICKET_ID_ouNavigator = "ouNavigator";
    private static final String WICKET_ID_ouTotalItems = "ouTotalItems";
    private static final String WICKET_ID_ouStartIndex = "ouStartIndex";
    private static final String WICKET_ID_ouEndIndex = "ouEndIndex";
    private static final String WICKET_ID_ouOrderByOrgUnit = "ouOrderByOrgUnit";
    private static final String WICKET_ID_ouPageable = "ouPageable";
    private static final String WICKET_ID_ouOrgUnit = "ouOrgUnit";
    private static final String WICKET_ID_ouFeeSet = "ouFeeSet";
    private static final String WICKET_ID_ouLimitSet = "ouLimitSet";
    private static final String WICKET_ID_ouEditAction = "ouEditAction";
    private static final String WICKET_ID_ouNoItemsMsg = "ouNoItemsMsg";

    private static final String WICKET_ID_ctNavigator = "ctNavigator";
    private static final String WICKET_ID_ctTotalItems = "ctTotalItems";
    private static final String WICKET_ID_ctStartIndex = "ctStartIndex";
    private static final String WICKET_ID_ctEndIndex = "ctEndIndex";
    private static final String WICKET_ID_ctOrderByCustomerType = "ctOrderByCustomerType";
    private static final String WICKET_ID_ctPageable = "ctPageable";
    private static final String WICKET_ID_ctCustType = "ctCustType";
    private static final String WICKET_ID_ctFeeSet = "ctFeeSet";
    private static final String WICKET_ID_ctRiskCategory = "ctRiskCategory";
    private static final String WICKET_ID_ctUmgrRole = "ctUmgrRole";
    private static final String WICKET_ID_ctEditAction = "ctEditAction";
    private static final String WICKET_ID_ctNoItemsMsg = "ctNoItemsMsg";

    private static final String WICKET_ID_rcNavigator = "rcNavigator";
    private static final String WICKET_ID_rcTotalItems = "rcTotalItems";
    private static final String WICKET_ID_rcStartIndex = "rcStartIndex";
    private static final String WICKET_ID_rcEndIndex = "rcEndIndex";
    private static final String WICKET_ID_rcOrderByRiskCat = "rcOrderByRiskCat";
    private static final String WICKET_ID_rcPageable = "rcPageable";
    private static final String WICKET_ID_rcRiskCategory = "rcRiskCategory";
    private static final String WICKET_ID_rcLimitSet = "rcLimitSet";
    private static final String WICKET_ID_rcUmgrRole = "rcUmgrRole";
    private static final String WICKET_ID_rcEditAction = "rcEditAction";
    private static final String WICKET_ID_rcNoItemsMsg = "rcNoItemsMsg";

    private OrgUnit orgUnitBean = new OrgUnit();

    public OrgUnit getOrgUnitBean() {
	return orgUnitBean;
    }

    public void setOrgUnitBean(OrgUnit orgUnitBean) {
	this.orgUnitBean = orgUnitBean;
    }

    private KeyValueDropDownChoice<Long, String> feeSetList;
    private KeyValueDropDownChoice<Long, String> limitSetList;

    private CustomerType custTypeBean = new CustomerType();

    public CustomerType getCustTypeBean() {
	return custTypeBean;
    }

    public void setCustTypeBean(CustomerType custTypeBean) {
	this.custTypeBean = custTypeBean;
    }

    private LocalizableLookupDropDownChoice<Integer> riskCategoryList;
    private LocalizableLookupDropDownChoice<String> umgrRolesList;

    private RiskCategory riskCatBean = new RiskCategory();

    public RiskCategory getRiskCatBean() {
	return riskCatBean;
    }

    public void setRiskCatBean(RiskCategory riskCatBean) {
	this.riskCatBean = riskCatBean;
    }

    Form<?> globalConfigForm;

    public GlobalConfigurationPage(final PageParameters parameters) {
	super(parameters);
	initPageComponents();
    }

    public GlobalConfigurationPage() {
	super();
	initPageComponents();
    }

    protected void initPageComponents() {
	globalConfigForm = new Form("globalConfigurationForm",
		new CompoundPropertyModel<GlobalConfigurationPage>(this));

	// forceReload = false;

	// for orgUnit
	orgUnitConfListContainer = new WebMarkupContainer(
		"orgUnitConfigListContainer");

	editOrgUnitConfContainer = new WebMarkupContainer(
		"editOrgUnitConfigContainer");

	createOrgUnitEditContainer(orgUnitConfListContainer);

	globalConfigForm.add(orgUnitConfListContainer);

	// for customer type

	custTypeConfListContainer = new WebMarkupContainer(
		"customerTypeConfigListContainer");

	editCustTypeConfContainer = new WebMarkupContainer(
		"editCustTypeConfContainer");

	createCustTypeEditContainer(custTypeConfListContainer);

	globalConfigForm.add(custTypeConfListContainer);

	// for risk category
	riskCategoryConfListContainer = new WebMarkupContainer(
		"riskCategoryConfListContainer");

	editRiskCategoryConfContainer = new WebMarkupContainer(
		"editRiskCategoryConfContainer");

	createRiskCatEditContainer(riskCategoryConfListContainer);

	globalConfigForm.add(riskCategoryConfListContainer);

	add(globalConfigForm);

	globalConfigForm.add(new FeedbackPanel("errorMessages"));

	createOrgUnitsDataView(orgUnitConfListContainer);
	createCustTypeDataView(custTypeConfListContainer);
	createRiskCatDateView(riskCategoryConfListContainer);
    }

    private void createOrgUnitsDataView(WebMarkupContainer parent) {

	orgUnitDataProvider = new OrgUnitConfigDataProvider(
		WICKET_ID_ouOrgUnit, this);

	orgUnitsList = new ArrayList<OrgUnit>();

	final DataView<OrgUnit> dataView = new DataView<OrgUnit>(
		WICKET_ID_ouPageable, orgUnitDataProvider) {

	    /**
		     * 
		     */
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    orgUnitDataProvider
			    .loadOrgUnitConfList(forceReloadOrgUnits);
		    forceReloadOrgUnits = false;
		    refreshTotalItemCount();
		    if (orgUnitDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }

		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading org unit configuration",
			    dple);
		    error(getLocalizer().getString("orgUnits.load.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<OrgUnit> item) {

		final OrgUnit entry = item.getModelObject();
		orgUnitsList.add(entry);
		item.add(new Label(WICKET_ID_ouOrgUnit, String.valueOf(entry
			.getName())));

		item.add(new Label(
			WICKET_ID_ouFeeSet,
			PortalUtils.exists(entry.getFeeSetId())
				&& entry.getFeeSetId().longValue() != 0 ? getFeeSetName(entry
				.getFeeSetId()) : ""));

		item.add(new Label(WICKET_ID_ouLimitSet,
			entry.getLimitSetId() != null ? getLimitSetName(entry
				.getLimitSetId()) : ""));
		// Edit Action
		Link<OrgUnit> editLink = new Link<OrgUnit>(
			WICKET_ID_ouEditAction, item.getModel()) {
		    @Override
		    public void onClick() {
			orgUnitBean = (OrgUnit) item.getModelObject();
			if (!PortalUtils.exists(orgUnitBean.getFeeSetId())) {
			    orgUnitBean.setFeeSetId(Long.valueOf(0));
			}
			editOrgUnitConfContainer.setVisible(true);
			orgUnitConfListContainer
				.addOrReplace(editOrgUnitConfContainer);

		    }
		};
		item.add(editLink);

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
		ouTotalItemString = Integer
			.toString(orgUnitDataProvider.size());
		int total = getItemCount();
		if (total > 0) {
		    ouStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    ouEndIndex = ouStartIndex + getItemsPerPage() - 1;
		    if (ouEndIndex > total)
			ouEndIndex = total;
		} else {
		    ouStartIndex = 0;
		    ouEndIndex = 0;
		}
	    }

	};

	dataView.setItemsPerPage(10);
	parent.add(dataView);

	parent.add(new OrderByBorder(WICKET_ID_ouOrderByOrgUnit,
		WICKET_ID_ouOrgUnit, orgUnitDataProvider) {
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

	parent.add(new MultiLineLabel(WICKET_ID_ouNoItemsMsg, getLocalizer()
		.getString("orgUnitsList.noItemsMsg", this)) {
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
	parent.add(new CustomPagingNavigator(WICKET_ID_ouNavigator, dataView));

	parent.add(new Label(WICKET_ID_ouTotalItems, new PropertyModel<String>(
		this, "ouTotalItemString")));

	parent.add(new Label(WICKET_ID_ouStartIndex, new PropertyModel(this,
		"ouStartIndex")));

	parent.add(new Label(WICKET_ID_ouEndIndex, new PropertyModel(this,
		"ouEndIndex")));

    }

    private void createCustTypeDataView(WebMarkupContainer parent) {

	custTypeConfDataProvider = new CustomerTypeConfigDataProvider(
		WICKET_ID_ctCustType, this);

	custTypeConfList = new ArrayList<CustomerType>();

	final DataView<CustomerType> dataView = new DataView<CustomerType>(
		WICKET_ID_ctPageable, custTypeConfDataProvider) {

	    /**
		     * 
		     */
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    custTypeConfDataProvider
			    .loadCustTypeConfList(forceReloadCustomerTypes);
		    forceReloadCustomerTypes = false;
		    refreshTotalItemCount();
		    if (custTypeConfDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }

		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading customer types",
			    dple);
		    error(getLocalizer().getString("customerTypes.load.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<CustomerType> item) {

		final CustomerType entry = item.getModelObject();
		custTypeConfList.add(entry);

		item.add(new Label(WICKET_ID_ctCustType, entry.getName()));

		item.add(new Label(
			WICKET_ID_ctFeeSet,
			PortalUtils.exists(entry.getFeeSetId())
				&& entry.getFeeSetId().longValue() != 0 ? getFeeSetName(entry
				.getFeeSetId()) : ""));

		item.add(new Label(WICKET_ID_ctRiskCategory, entry
			.getRiskCategoryId() != 0 ? getDisplayValue(
			String.valueOf(entry.getRiskCategoryId()),
			Constants.RESOURCE_BUNDLE_RISK_CATEGORIES) : ""));

		item.add(new Label(WICKET_ID_ctUmgrRole,
			entry.getRole() != null ? entry.getRole() : ""));

		// Edit Action
		Link<CustomerType> editLink = new Link<CustomerType>(
			WICKET_ID_ctEditAction, item.getModel()) {
		    @Override
		    public void onClick() {
			custTypeBean = (CustomerType) item.getModelObject();
			if (!PortalUtils.exists(custTypeBean.getFeeSetId())) {
			    custTypeBean.setFeeSetId(Long.valueOf(0));
			}
			editCustTypeConfContainer.setVisible(true);
			custTypeConfListContainer
				.addOrReplace(editCustTypeConfContainer);

		    }
		};
		item.add(editLink);

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
		ctTotalItemString = Integer.toString(custTypeConfDataProvider
			.size());
		int total = getItemCount();
		if (total > 0) {
		    ctStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    ctEndIndex = ctStartIndex + getItemsPerPage() - 1;
		    if (ctEndIndex > total)
			ctEndIndex = total;
		} else {
		    ctStartIndex = 0;
		    ctEndIndex = 0;
		}
	    }

	};

	dataView.setItemsPerPage(10);
	parent.add(dataView);

	parent.add(new OrderByBorder(WICKET_ID_ctOrderByCustomerType,
		WICKET_ID_ctCustType, custTypeConfDataProvider) {
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

	parent.add(new MultiLineLabel(WICKET_ID_ctNoItemsMsg, getLocalizer()
		.getString("customerTypesList.noItemsMsg", this)) {
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
	parent.add(new CustomPagingNavigator(WICKET_ID_ctNavigator, dataView));

	parent.add(new Label(WICKET_ID_ctTotalItems, new PropertyModel<String>(
		this, "ctTotalItemString")));

	parent.add(new Label(WICKET_ID_ctStartIndex, new PropertyModel(this,
		"ctStartIndex")));

	parent.add(new Label(WICKET_ID_ctEndIndex, new PropertyModel(this,
		"ctEndIndex")));

    }

    private void createRiskCatDateView(WebMarkupContainer parent) {

	riskCatConfDataProvider = new RiskCategoryConfDataProvider(
		WICKET_ID_rcRiskCategory, this);

	riskCatConfList = new ArrayList<RiskCategory>();

	final DataView<RiskCategory> dataView = new DataView<RiskCategory>(
		WICKET_ID_rcPageable, riskCatConfDataProvider) {

	    /**
		     * 
		     */
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    riskCatConfDataProvider
			    .loadRiskCatConfList(forceReloadRiskCat);
		    forceReloadRiskCat = false;
		    if (riskCatConfDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }

		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading risk categories",
			    dple);
		    error(getLocalizer().getString("riskCategories.load.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<RiskCategory> item) {

		final RiskCategory entry = item.getModelObject();
		riskCatConfList.add(entry);

		item.add(new Label(WICKET_ID_rcRiskCategory, entry.getName()));

		item.add(new Label(WICKET_ID_rcLimitSet,
			entry.getLimitSetId() != null ? getLimitSetName(entry
				.getLimitSetId()) : ""));

		item.add(new Label(WICKET_ID_rcUmgrRole, entry.getUmgrRole()));

		// Edit Action
		Link<RiskCategory> editLink = new Link<RiskCategory>(
			WICKET_ID_rcEditAction, item.getModel()) {
		    @Override
		    public void onClick() {
			riskCatBean = (RiskCategory) item.getModelObject();
			editRiskCategoryConfContainer.setVisible(true);
			riskCategoryConfListContainer
				.addOrReplace(editRiskCategoryConfContainer);

		    }
		};
		item.add(editLink);

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
		rcTotalItemString = Integer.toString(riskCatConfDataProvider
			.size());
		int total = getItemCount();
		if (total > 0) {
		    rcStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    rcEndIndex = rcStartIndex + getItemsPerPage() - 1;
		    if (rcEndIndex > total)
			rcEndIndex = total;
		} else {
		    rcStartIndex = 0;
		    rcEndIndex = 0;
		}
	    }

	};

	dataView.setItemsPerPage(10);
	parent.add(dataView);

	parent.add(new OrderByBorder(WICKET_ID_rcOrderByRiskCat,
		WICKET_ID_rcRiskCategory, riskCatConfDataProvider) {
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

	parent.add(new MultiLineLabel(WICKET_ID_rcNoItemsMsg, getLocalizer()
		.getString("riskCategoriesList.noItemsMsg", this)) {
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
	parent.add(new CustomPagingNavigator(WICKET_ID_rcNavigator, dataView));

	parent.add(new Label(WICKET_ID_rcTotalItems, new PropertyModel<String>(
		this, "rcTotalItemString")));

	parent.add(new Label(WICKET_ID_rcStartIndex, new PropertyModel(this,
		"rcStartIndex")));

	parent.add(new Label(WICKET_ID_rcEndIndex, new PropertyModel(this,
		"rcEndIndex")));

    }

    private void createOrgUnitEditContainer(WebMarkupContainer parent) {

	editOrgUnitConfContainer.setOutputMarkupId(true);
	editOrgUnitConfContainer.setOutputMarkupPlaceholderTag(true);

	editOrgUnitConfContainer.add(new Label("orgUnitBean.name"));

	feeSetList = (KeyValueDropDownChoice<Long, String>) new KeyValueDropDownChoice<Long, String>(
		"orgUnitBean.feeSetId", getFeeSets(null)) {
	    @Override
	    protected CharSequence getDefaultChoice(Object selected) {
		return null;
	    };
	}.setNullValid(false);

	editOrgUnitConfContainer.add(feeSetList);

	limitSetList = (KeyValueDropDownChoice<Long, String>) new KeyValueDropDownChoice<Long, String>(
		"orgUnitBean.limitSetId", getLimitSets()).setNullValid(true);

	editOrgUnitConfContainer.add(limitSetList);

	editOrgUnitConfContainer.add(new Button("save") {
	    @Override
	    public void onSubmit() {
		updateOrgUnit(getOrgUnitBean());
		forceReloadOrgUnits = true;
		editOrgUnitConfContainer.setVisible(false);
	    }
	});
	editOrgUnitConfContainer.add(new AjaxLink("cancel") {

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		editOrgUnitConfContainer.setVisible(false);
		target.addComponent(editOrgUnitConfContainer);

	    }
	});

	editOrgUnitConfContainer.setVisible(false);

	parent.addOrReplace(editOrgUnitConfContainer);
    }

    private void createCustTypeEditContainer(WebMarkupContainer parent) {

	editCustTypeConfContainer.setOutputMarkupId(true);
	editCustTypeConfContainer.setOutputMarkupPlaceholderTag(true);

	editCustTypeConfContainer.add(new Label("custTypeBean.name"));

	feeSetList = (KeyValueDropDownChoice<Long, String>) new KeyValueDropDownChoice<Long, String>(
		"custTypeBean.feeSetId", getFeeSets(null)) {
	    @Override
	    protected CharSequence getDefaultChoice(Object selected) {
		return null;
	    };
	}.setNullValid(false);

	editCustTypeConfContainer.add(feeSetList);

	riskCategoryList = (LocalizableLookupDropDownChoice<Integer>) new LocalizableLookupDropDownChoice<Integer>(
		"custTypeBean.riskCategoryId", Integer.class, "riskcategories",
		this, false, true).setNullValid(true).add(new ErrorIndicator());

	editCustTypeConfContainer.add(riskCategoryList);

	umgrRolesList = (LocalizableLookupDropDownChoice<String>) new LocalizableLookupDropDownChoice<String>(
		"custTypeBean.role", String.class, "umgrroles", this, false,
		true).setNullValid(true).add(new ErrorIndicator());

	editCustTypeConfContainer.add(umgrRolesList);

	editCustTypeConfContainer.add(new Button("save") {
	    @Override
	    public void onSubmit() {
		updateCustomerType(getCustTypeBean());
		forceReloadCustomerTypes = true;
		editCustTypeConfContainer.setVisible(false);
	    }
	});
	editCustTypeConfContainer.add(new AjaxLink("cancel") {

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		editCustTypeConfContainer.setVisible(false);
		target.addComponent(editCustTypeConfContainer);

	    }
	});

	editCustTypeConfContainer.setVisible(false);

	parent.addOrReplace(editCustTypeConfContainer);
    }

    private void createRiskCatEditContainer(WebMarkupContainer parent) {

	editRiskCategoryConfContainer.setOutputMarkupId(true);
	editRiskCategoryConfContainer.setOutputMarkupPlaceholderTag(true);

	editRiskCategoryConfContainer.add(new Label("riskCatBean.name"));

	limitSetList = (KeyValueDropDownChoice<Long, String>) new KeyValueDropDownChoice<Long, String>(
		"riskCatBean.limitSetId", getLimitSets()).setNullValid(true);

	editRiskCategoryConfContainer.add(limitSetList);

	umgrRolesList = (LocalizableLookupDropDownChoice<String>) new LocalizableLookupDropDownChoice<String>(
		"riskCatBean.umgrRole", String.class, "umgrroles", this, false,
		true).setNullValid(true).add(new ErrorIndicator());

	editRiskCategoryConfContainer.add(umgrRolesList);

	editRiskCategoryConfContainer.add(new Button("save") {
	    @Override
	    public void onSubmit() {
		updateRiskCategory(getRiskCatBean());
		forceReloadRiskCat = true;
		editRiskCategoryConfContainer.setVisible(false);
	    }
	});
	editRiskCategoryConfContainer.add(new AjaxLink("cancel") {

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		editRiskCategoryConfContainer.setVisible(false);
		target.addComponent(editRiskCategoryConfContainer);

	    }
	});

	editRiskCategoryConfContainer.setVisible(false);

	parent.addOrReplace(editRiskCategoryConfContainer);
    }

    private String getLimitSetName(Long limitSetId) {
	List<KeyValue<Long, String>> limitSets = getLimitSets();
	String limitSetName = "";
	for (KeyValue<Long, String> limit : limitSets) {
	    if (limit.getKey().longValue() == limitSetId) {
		limitSetName = limit.getValue().toString();
		break;
	    }
	}
	return limitSetName;
    }

    private void updateOrgUnit(OrgUnit orgUnit) {

	UpdateOrgUnitResponse response = null;
	try {
	    UpdateOrgUnitRequest request = getNewMobiliserRequest(UpdateOrgUnitRequest.class);
	    if (orgUnit.getFeeSetId() == 0) {
		orgUnit.setFeeSetId(null);
	    }
	    request.setOrgUnit(orgUnit);
	    response = wsSystemConfClient.updateOrgUnit(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("#An error occurred while fetching org unit");
	    }
	} catch (Exception e) {
	    LOG.error("#An error occurred while updating org unit");
	    error(getLocalizer().getString("globalConf.orgUnit.update.error",
		    this));

	}

    }

    private void updateCustomerType(CustomerType customerType) {
	UpdateCustomerTypeResponse response = null;
	try {
	    UpdateCustomerTypeRequest request = getNewMobiliserRequest(UpdateCustomerTypeRequest.class);
	    if (customerType.getFeeSetId() == 0) {
		customerType.setFeeSetId(null);
	    }
	    request.setCustomerType(customerType);
	    response = wsSystemConfClient.updateCustomerType(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("#An error occurred while updating customer type");
	    }
	} catch (Exception e) {
	    LOG.error("#An error occurred while fetching org units");
	    error(getLocalizer().getString(
		    "globalConf.customerType.update.error", this));

	}

    }

    private void updateRiskCategory(RiskCategory customerType) {
	UpdateRiskCategoryResponse response = null;
	try {
	    UpdateRiskCategoryRequest request = getNewMobiliserRequest(UpdateRiskCategoryRequest.class);
	    request.setRiskCategory(customerType);
	    response = wsSystemConfClient.updateRiskCategory(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("#An error occurred while updating risk category");
	    }
	} catch (Exception e) {
	    LOG.error("#An error occurred while updating risk category");
	    error(getLocalizer().getString(
		    "globalConf.riskCategory.update.error", this));

	}

    }

    public String getFeeSetName(Long feeSetId) {
	List<KeyValue<Long, String>> feeSetsList = getFeeSets(null);

	LOG.debug("# MobiliserBasePage.getFeeSetName()");
	for (KeyValue<Long, String> feeSet : feeSetsList) {
	    if (feeSet.getKey().longValue() == feeSetId.longValue())
		return feeSet.getValue();
	}

	return "";
    }

    public List<KeyValue<Long, String>> getLimitSets() {
	List<KeyValue<Long, String>> limtSetList = new ArrayList<KeyValue<Long, String>>();

	try {

	    if (!PortalUtils.exists(limitSets))
		limitSets = findLimitSet(null);
	} catch (Exception e) {
	    LOG.error("#An error occurred while fetching limit sets");
	}

	KeyValue<Long, String> keyValue;
	for (LimitSet limitSet : limitSets) {
	    keyValue = new KeyValue<Long, String>(limitSet.getId(),
		    limitSet.getName());
	    limtSetList.add(keyValue);
	}
	return limtSetList;

    }

}
