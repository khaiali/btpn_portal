package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateWrkCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateWrkCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteWrkCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteWrkCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateWrkCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateWrkCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.WrkCustomer;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateUseCasePrivilegeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateUseCasePrivilegeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateWrkBulkRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateWrkBulkResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteUseCasePrivilegeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteUseCasePrivilegeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteWrkBulkRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteWrkBulkResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetBulkFileTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetBulkFileTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateUseCasePrivilegeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateUseCasePrivilegeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateWrkBulkRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateWrkBulkResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.BulkFileType;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.UseCasePrivilege;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.WrkBulk;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreateWrkWalletEntryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreateWrkWalletEntryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.DeleteWrkWalletEntryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.DeleteWrkWalletEntryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdateWrkWalletEntryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdateWrkWalletEntryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WrkWalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.ApprovalConfBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.ApprovalConfDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.CustomerApprovalConfPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.FileApprovalConfPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.TransactionApprovalConfPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.WalletApprovalConfPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class ApprovalConfigurationPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ApprovalConfigurationPanel.class);

    MobiliserBasePage basePage;
    private ApprovalConfBean approvalConfigObj;
    private WrkCustomer wrkCustomer;
    private WrkWalletEntry wrkWalletEntry;
    private UseCasePrivilege useCasePrivilege;
    private WrkBulk wrkBulk;
    private String customerTypeId;
    private String piTypeId;
    private String bulkFileTypeId;
    private boolean sortAsc = true;
    private List<WrkCustomer> wrkCustomersList;
    List<WrkWalletEntry> wrkWalletsList;
    List<WrkBulk> wrkBulkList;
    List<UseCasePrivilege> wrkUseCasePrivsList;
    List<WrkBulk> wrkBulksList;
    LocalizableLookupDropDownChoice<Integer> custTypeChoice;
    LocalizableLookupDropDownChoice<Integer> piTypeChoice;
    LocalizableLookupDropDownChoice<Integer> useCaseTypeChoice;
    KeyValueDropDownChoice<Integer, String> bulkFileTypeChoice;

    private boolean isEdit = false;

    Form<?> approvalConfigForm;
    private ApprovalConfDataProvider dataProvider;
    private List<ApprovalConfBean> approvalConfigs;
    private WebMarkupContainer approvalConfigContainer;
    private String WICKET_ID_type;
    private String type;
    String delConfirmMsgKey = "approvalConfig.cust.remove.confirm";
    String noItemMsgKey = "approvals.noCustItemsMsg";
    String noItemHelpMsgKey = "approvals.noCustItemsHelpMsg";

    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;
    private int rowIndex = 1;
    private String totalItemString = null;
    private static final String WICKET_ID_editAction = "editAction";
    private static final String WICKET_ID_deleteAction = "removeAction";
    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByCustType = "orderByCustType";
    private static final String WICKET_ID_orderByPiType = "orderByPiType";
    private static final String WICKET_ID_orderByUseCaseType = "orderByUseCaseType";
    private static final String WICKET_ID_orderByFileType = "orderByFileType";
    private static final String WICKET_ID_customerType = "customerType";
    private static final String WICKET_ID_piType = "piType";
    private static final String WICKET_ID_useCaseType = "useCaseType";
    private static final String WICKET_ID_fileType = "fileType";

    private static final String WICKET_ID_payerPrivilege = "payerPrivilege";
    private static final String WICKET_ID_payeePrivilege = "payeePrivilege";
    private static final String WICKET_ID_callerPrivilege = "callerPrivilege";
    private static final String WICKET_ID_parentPrivilege = "parentPrivilege";
    private static final String WICKET_ID_selfPrivilege = "selfPrivilege";

    private static final String WICKET_ID_makerPrivilege = "makerPrivilege";
    private static final String WICKET_ID_checkerPrivilege = "checkerPrivilege";
    private static final String WICKET_ID_executePrivilege = "executePrivilege";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_active = "active";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public ApprovalConfigurationPanel(String id, MobiliserBasePage basePage,
	    ApprovalConfBean approvalConfig, String type) {
	super(id);
	this.basePage = basePage;
	this.approvalConfigObj = approvalConfig;
	this.type = type;
	this.constructPanel();
    }

    private void constructPanel() {

	approvalConfigForm = new Form("approvalConfigForm",
		new CompoundPropertyModel<ApprovalConfigurationPanel>(this));

	approvalConfigForm.add(new FeedbackPanel("errorMessages"));

	createApprovalConfigDataView(approvalConfigForm);
	approvalConfigContainer = new WebMarkupContainer(
		"approvalConfigContainer");
	createApprovalConfigContainer(approvalConfigForm);

	add(approvalConfigForm);

    }

    private void createApprovalConfigContainer(Form form) {
	WebMarkupContainer custTypeDiv;
	WebMarkupContainer walletTypeDiv;
	WebMarkupContainer useCaseTypeDiv;
	WebMarkupContainer bulkFileTypeDiv;

	approvalConfigContainer.setOutputMarkupId(true);
	approvalConfigContainer.setOutputMarkupPlaceholderTag(true);

	custTypeDiv = new WebMarkupContainer("custTypeDiv");

	custTypeChoice = new LocalizableLookupDropDownChoice<Integer>(
		"approvalConfigObj.customerTypeId", Integer.class,
		Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE, this, Boolean.FALSE,
		true, getAvaliableTypes(new ArrayList<Integer>(),
			Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE));

	custTypeChoice.setNullValid(false).setRequired(true)
		.add(new ErrorIndicator());

	custTypeDiv.addOrReplace(custTypeChoice);

	approvalConfigContainer.addOrReplace(custTypeDiv);

	walletTypeDiv = new WebMarkupContainer("walletTypeDiv");

	piTypeChoice = new LocalizableLookupDropDownChoice<Integer>(
		"approvalConfigObj.piTypeId", Integer.class,
		Constants.RESOURCE_BUNDLE_PI_TYPES, this, false, true,
		getAvaliableTypes(new ArrayList<Integer>(),
			Constants.RESOURCE_BUNDLE_PI_TYPES));

	piTypeChoice.setNullValid(true).setRequired(true)
		.add(new ErrorIndicator());

	walletTypeDiv.addOrReplace(piTypeChoice);

	approvalConfigContainer.addOrReplace(walletTypeDiv);

	useCaseTypeDiv = new WebMarkupContainer("useCaseTypeDiv");

	useCaseTypeChoice = new LocalizableLookupDropDownChoice<Integer>(
		"approvalConfigObj.useCaseId", Integer.class,
		Constants.RESOURCE_BUNDLE_USE_CASES, this, false, true,
		getAvaliableTypes(new ArrayList<Integer>(),
			Constants.RESOURCE_BUNDLE_USE_CASES));

	useCaseTypeChoice.setNullValid(true).setRequired(true)
		.add(new ErrorIndicator());

	bulkFileTypeDiv = new WebMarkupContainer("bulkFileTypeDiv");
	bulkFileTypeChoice = new KeyValueDropDownChoice<Integer, String>(
		"approvalConfigObj.bulkFileTypeId", getMimeTypeList());
	bulkFileTypeChoice.setRequired(true).add(new ErrorIndicator());
	bulkFileTypeDiv.add(bulkFileTypeChoice);
	approvalConfigContainer.addOrReplace(bulkFileTypeDiv);

	if (isEdit) {
	    custTypeChoice.add(new SimpleAttributeModifier("readonly",
		    "readonly"));
	    custTypeChoice.add(new SimpleAttributeModifier("style",
		    "background-color: #E6E6E6;"));

	    custTypeChoice.setEnabled(false);

	    piTypeChoice
		    .add(new SimpleAttributeModifier("readonly", "readonly"));
	    piTypeChoice.add(new SimpleAttributeModifier("style",
		    "background-color: #E6E6E6;"));

	    piTypeChoice.setEnabled(false);

	    useCaseTypeChoice.add(new SimpleAttributeModifier("readonly",
		    "readonly"));

	    useCaseTypeChoice.add(new SimpleAttributeModifier("style",
		    "background-color: #E6E6E6;"));

	    useCaseTypeChoice.setEnabled(false);

	    bulkFileTypeChoice.add(new SimpleAttributeModifier("readonly",
		    "readonly"));

	    bulkFileTypeChoice.add(new SimpleAttributeModifier("style",
		    "background-color: #E6E6E6;"));

	    bulkFileTypeChoice.setEnabled(false);

	}

	useCaseTypeDiv.addOrReplace(useCaseTypeChoice);

	useCaseTypeDiv
		.addOrReplace(new LocalizableLookupDropDownChoice<String>(
			"approvalConfigObj.payerPrivilege", String.class,
			Constants.RESOURCE_BUNDLE_UMGR_PRIV, this, false, true)
			.setNullValid(true).add(new ErrorIndicator()));
	useCaseTypeDiv
		.addOrReplace(new LocalizableLookupDropDownChoice<String>(
			"approvalConfigObj.payeePrivilege", String.class,
			Constants.RESOURCE_BUNDLE_UMGR_PRIV, this, false, true)
			.setNullValid(true).add(new ErrorIndicator()));
	useCaseTypeDiv
		.addOrReplace(new LocalizableLookupDropDownChoice<String>(
			"approvalConfigObj.callerPrivilege", String.class,
			Constants.RESOURCE_BUNDLE_UMGR_PRIV, this, false, true)
			.setNullValid(true).setRequired(true)
			.add(new ErrorIndicator()));
	useCaseTypeDiv
		.addOrReplace(new LocalizableLookupDropDownChoice<String>(
			"approvalConfigObj.callerParentPrivilege",
			String.class, Constants.RESOURCE_BUNDLE_UMGR_PRIV,
			this, false, true).setNullValid(true).setRequired(true)
			.add(new ErrorIndicator()));
	useCaseTypeDiv
		.addOrReplace(new LocalizableLookupDropDownChoice<String>(
			"approvalConfigObj.callerSelfPrivilege", String.class,
			Constants.RESOURCE_BUNDLE_UMGR_PRIV, this, false, true)
			.setNullValid(true).setRequired(true)
			.add(new ErrorIndicator()));

	approvalConfigContainer.addOrReplace(useCaseTypeDiv);

	approvalConfigContainer
		.addOrReplace(new LocalizableLookupDropDownChoice<String>(
			"approvalConfigObj.makerPrivilege", String.class,
			Constants.RESOURCE_BUNDLE_UMGR_PRIV, this, false, true)
			.setNullValid(false).setRequired(true)
			.add(new ErrorIndicator()));
	approvalConfigContainer
		.addOrReplace(new LocalizableLookupDropDownChoice<String>(
			"approvalConfigObj.checkerPrivilege", String.class,
			Constants.RESOURCE_BUNDLE_UMGR_PRIV, this, false, true)
			.setNullValid(false).setRequired(true)
			.add(new ErrorIndicator()));
	approvalConfigContainer
		.addOrReplace(new LocalizableLookupDropDownChoice<String>(
			"approvalConfigObj.executePrivilege", String.class,
			Constants.RESOURCE_BUNDLE_UMGR_PRIV, this, false, true)
			.setNullValid(false).setRequired(true)
			.add(new ErrorIndicator()));

	if (Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE.equals(type)) {
	    custTypeDiv.setVisible(true);
	    walletTypeDiv.setVisible(false);
	    useCaseTypeDiv.setVisible(false);
	    bulkFileTypeDiv.setVisible(false);
	} else if (Constants.RESOURCE_BUNDLE_PI_TYPES.equals(type)) {
	    custTypeDiv.setVisible(false);
	    walletTypeDiv.setVisible(true);
	    useCaseTypeDiv.setVisible(false);
	    bulkFileTypeDiv.setVisible(false);
	} else if (Constants.RESOURCE_BUNDLE_USE_CASES.equals(type)) {
	    custTypeDiv.setVisible(false);
	    walletTypeDiv.setVisible(false);
	    useCaseTypeDiv.setVisible(true);
	    bulkFileTypeDiv.setVisible(false);
	} else {
	    custTypeDiv.setVisible(false);
	    walletTypeDiv.setVisible(false);
	    useCaseTypeDiv.setVisible(false);
	    bulkFileTypeDiv.setVisible(true);
	}

	approvalConfigContainer.addOrReplace(new Button("save") {
	    @Override
	    public void onSubmit() {
		handleSubmit();
	    }
	});
	approvalConfigContainer.addOrReplace(new AjaxLink("cancel") {

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		approvalConfigContainer.setVisible(false);
		target.addComponent(approvalConfigContainer);

	    }
	});

	addOrReplace(approvalConfigContainer.setVisible(false));

	form.addOrReplace(approvalConfigContainer);

    }

    private void createApprovalConfigDataView(Form form) {

	WICKET_ID_type = WICKET_ID_customerType;

	if (Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE.equals(type)) {
	    dataProvider = new ApprovalConfDataProvider(WICKET_ID_customerType,
		    basePage, sortAsc);

	} else if (Constants.RESOURCE_BUNDLE_PI_TYPES.equals(type)) {
	    WICKET_ID_type = WICKET_ID_piType;
	    delConfirmMsgKey = "approvalConfig.wallet.remove.confirm";
	    dataProvider = new ApprovalConfDataProvider(WICKET_ID_piType,
		    basePage, sortAsc);

	} else if (Constants.RESOURCE_BUNDLE_USE_CASES.equals(type)) {
	    WICKET_ID_type = WICKET_ID_useCaseType;
	    delConfirmMsgKey = "approvalConfig.usecase.remove.confirm";
	    dataProvider = new ApprovalConfDataProvider(WICKET_ID_useCaseType,
		    basePage, sortAsc);

	} else {
	    WICKET_ID_type = WICKET_ID_fileType;
	    delConfirmMsgKey = "approvalConfig.file.remove.confirm";
	    dataProvider = new ApprovalConfDataProvider(WICKET_ID_fileType,
		    basePage, sortAsc);
	}
	loadConfigurations();

	approvalConfigs = new ArrayList<ApprovalConfBean>();

	form.addOrReplace(new Button("addConf") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		isEdit = false;
		setApprovalConfigObj(new ApprovalConfBean());
		createApprovalConfigContainer(approvalConfigForm);
		approvalConfigContainer.setVisible(true);
		approvalConfigForm.clearInput();

	    };
	}.setDefaultFormProcessing(false));

	final DataView<ApprovalConfBean> dataView = new DataView<ApprovalConfBean>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {
		loadConfigurations();
		// reset rowIndex
		rowIndex = 1;

		refreshTotalItemCount();
		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}

		refreshTotalItemCount();

		super.onBeforeRender();
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

	    @Override
	    protected void populateItem(final Item<ApprovalConfBean> item) {
		final ApprovalConfBean entry = item.getModelObject();
		approvalConfigs.add(entry);

		item.add(new Label(WICKET_ID_customerType, getBasePage()
			.getDisplayValue(
				String.valueOf(entry.getCustomerTypeId()),
				Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE))
			.setVisible(Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE
				.equals(type)));

		item.add(new Label(WICKET_ID_piType, getBasePage()
			.getDisplayValue(String.valueOf(entry.getPiTypeId()),
				Constants.RESOURCE_BUNDLE_PI_TYPES))
			.setVisible(Constants.RESOURCE_BUNDLE_PI_TYPES
				.equals(type)));

		item.add(new Label(WICKET_ID_useCaseType, getBasePage()
			.getDisplayValue(String.valueOf(entry.getUseCaseId()),
				Constants.RESOURCE_BUNDLE_USE_CASES))
			.setVisible(Constants.RESOURCE_BUNDLE_USE_CASES
				.equals(type)));

		item.add(new Label(WICKET_ID_fileType, getBasePage()
			.getDisplayValue(
				String.valueOf(entry.getBulkFileTypeId()),
				"bulkFileType"))
			.setVisible(Constants.RESOURCE_BUNDLE_FILE_TYPES
				.equals(type)));

		item.add(new Label(WICKET_ID_payerPrivilege, entry
			.getPayerPrivilege())
			.setVisible(Constants.RESOURCE_BUNDLE_USE_CASES
				.equals(type)));

		item.add(new Label(WICKET_ID_payeePrivilege, entry
			.getPayeePrivilege())
			.setVisible(Constants.RESOURCE_BUNDLE_USE_CASES
				.equals(type)));

		item.add(new Label(WICKET_ID_callerPrivilege, entry
			.getCallerPrivilege())
			.setVisible(Constants.RESOURCE_BUNDLE_USE_CASES
				.equals(type)));

		item.add(new Label(WICKET_ID_makerPrivilege, entry
			.getMakerPrivilege())
			.setVisible(!Constants.RESOURCE_BUNDLE_USE_CASES
				.equals(type)));
		item.add(new Label(WICKET_ID_checkerPrivilege, entry
			.getCheckerPrivilege())
			.setVisible(!Constants.RESOURCE_BUNDLE_USE_CASES
				.equals(type)));
		item.add(new Label(WICKET_ID_executePrivilege, entry
			.getExecutePrivilege())
			.setVisible(!Constants.RESOURCE_BUNDLE_USE_CASES
				.equals(type)));

		Link<ApprovalConfBean> editLink = new Link<ApprovalConfBean>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			isEdit = true;
			setApprovalConfigObj(entry);
			createApprovalConfigContainer(approvalConfigForm);
			approvalConfigContainer.setVisible(true);
		    }
		};

		item.add(editLink);

		Link<ApprovalConfBean> deleteLink = new Link<ApprovalConfBean>(
			WICKET_ID_deleteAction, item.getModel()) {
		    @Override
		    public void onClick() {
			approvalConfigContainer.setVisible(false);
			forceReload = true;
			setApprovalConfigObj(entry);
			removeApprovalConfig();
		    }
		};

		deleteLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(delConfirmMsgKey,
					this) + "');"));

		item.add(deleteLink);

	    }

	};
	dataView.setItemsPerPage(10);
	form.addOrReplace(dataView);

	OrderByBorder custType = new OrderByBorder(WICKET_ID_orderByCustType,
		WICKET_ID_active, dataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the page is
		// loading
		// and the sort is clicked (clicking the name header), so handle
		// it
		sortAsc = !sortAsc;

		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}

		createApprovalConfigDataView(approvalConfigForm);
	    }
	};

	form.addOrReplace(custType.setVisible(false));

	OrderByBorder piType = new OrderByBorder(WICKET_ID_orderByPiType,
		WICKET_ID_active, dataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the page is
		// loading
		// and the sort is clicked (clicking the name header), so handle
		// it
		sortAsc = !sortAsc;
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
		createApprovalConfigDataView(approvalConfigForm);
	    }
	};

	form.addOrReplace(piType.setVisible(false));

	OrderByBorder useCaseType = new OrderByBorder(
		WICKET_ID_orderByUseCaseType, WICKET_ID_active, dataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the page is
		// loading
		// and the sort is clicked (clicking the name header), so handle
		// it
		sortAsc = !sortAsc;
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
		createApprovalConfigDataView(approvalConfigForm);
	    }
	};
	form.addOrReplace(useCaseType.setVisible(false));

	OrderByBorder fileType = new OrderByBorder(WICKET_ID_orderByFileType,
		WICKET_ID_active, dataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the page is
		// loading
		// and the sort is clicked (clicking the name header), so handle
		// it
		sortAsc = !sortAsc;
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
		createApprovalConfigDataView(approvalConfigForm);
	    }
	};
	form.addOrReplace(fileType.setVisible(false));

	Label payerPrivHeader = new Label("HpayerPrivilege", getLocalizer()
		.getString("approvalConfig.table.PayerPrivilege", this));
	form.addOrReplace(payerPrivHeader.setVisible(false));

	Label payeePrivHeader = new Label("HpayeePrivilege", getLocalizer()
		.getString("approvalConfig.table.PayeePrivilege", this));
	form.addOrReplace(payeePrivHeader.setVisible(false));

	Label callerPrivHeader = new Label("HcallerPrivilege", getLocalizer()
		.getString("approvalConfig.table.CallerPrivilege", this));
	form.addOrReplace(callerPrivHeader.setVisible(false));

	Label makerPrivHeader = new Label("HmakerPrivilege", getLocalizer()
		.getString("approvalConfig.table.makerPrivilege", this));
	form.addOrReplace(makerPrivHeader.setVisible(true));

	Label checkerPrivHeader = new Label("HcheckerPrivilege", getLocalizer()
		.getString("approvalConfig.table.checkerPrivilege", this));
	form.addOrReplace(checkerPrivHeader.setVisible(true));

	Label executePrivHeader = new Label("HexecutePrivilege", getLocalizer()
		.getString("approvalConfig.table.executePrivilege", this));
	form.addOrReplace(executePrivHeader.setVisible(true));

	if (Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE.equals(type)) {
	    custType.setVisible(true);
	} else if (Constants.RESOURCE_BUNDLE_PI_TYPES.equals(type)) {
	    piType.setVisible(true);
	    noItemMsgKey = "approvals.noWalletItemsMsg";
	    noItemHelpMsgKey = "approvals.noWalletItemsHelpMsg";
	} else if (Constants.RESOURCE_BUNDLE_USE_CASES.equals(type)) {
	    useCaseType.setVisible(true);
	    payerPrivHeader.setVisible(true);
	    payeePrivHeader.setVisible(true);
	    callerPrivHeader.setVisible(true);
	    makerPrivHeader.setVisible(false);
	    checkerPrivHeader.setVisible(false);
	    executePrivHeader.setVisible(false);

	    noItemMsgKey = "approvals.noUseCaseItemsMsg";
	    noItemHelpMsgKey = "approvals.noUseCaseItemsHelpMsg";
	} else {
	    fileType.setVisible(true);
	    noItemMsgKey = "approvals.noFileItemsMsg";
	    noItemHelpMsgKey = "approvals.noFileItemsHelpMsg";
	}

	form.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString(noItemMsgKey, this) + "\n"
			+ getLocalizer().getString(noItemHelpMsgKey, this)) {
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

	// End of Pagenation
    }

    private void handleSubmit() {
	LOG.debug("# ApprovalConfigurationPanel.handleSubmit()");
	convert();

	if (Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE.equals(type)) {
	    saveCustomer();
	} else if (Constants.RESOURCE_BUNDLE_PI_TYPES.equals(type)) {
	    saveWallet();
	} else if (Constants.RESOURCE_BUNDLE_USE_CASES.equals(type)) {
	    saveUseCase();
	} else {
	    saveFileType();
	}

	forceReload = true;
	createApprovalConfigDataView(approvalConfigForm);
	approvalConfigContainer.setVisible(false);
    }

    private void saveCustomer() {
	LOG.debug("# ApprovalConfigurationPanel.saveCustomer()");

	if (!isEdit) {
	    try {
		CreateWrkCustomerResponse response;
		CreateWrkCustomerRequest request = getBasePage()
			.getNewMobiliserRequest(CreateWrkCustomerRequest.class);
		request.setWrkCustomer(getWrkCustomer());
		response = getBasePage().wsCustomerClient
			.createWrkCustomer(request);
		if (!getBasePage().evaluateMobiliserResponse(response)) {
		    LOG.warn("# An error occurred while saving new customer approval configuration");
		    return;
		}
	    } catch (Exception e) {
		LOG.error("# An error occurred while saving new customer approval configuration");
		error(getLocalizer().getString(
			"create.customer.approveConf.error", this));
	    }
	} else {
	    LOG.debug("# ApprovalConfigurationPnel.updateUseCaseFeeType()");
	    UpdateWrkCustomerResponse response = null;
	    try {
		UpdateWrkCustomerRequest request = getBasePage()
			.getNewMobiliserRequest(UpdateWrkCustomerRequest.class);
		request.setWrkCustomer(getWrkCustomer());
		response = getBasePage().wsCustomerClient
			.updateWrkCustomer(request);
		if (!getBasePage().evaluateMobiliserResponse(response)) {
		    LOG.warn("# Error occurred while updating customer approval configuration");
		    return;
		}
		LOG.info("# Successfully updated customer approval configuration");
	    } catch (Exception e) {
		LOG.error("# Error occurred while updating customer approval configuration");
		error(getLocalizer().getString(
			"update.customer.approveConf.error", this));
		return;
	    }

	}

	setResponsePage(new CustomerApprovalConfPage());
    }

    private void saveWallet() {
	LOG.debug("# ApprovalConfigurationPanel.saveWallet()");
	if (!isEdit) {
	    try {
		CreateWrkWalletEntryResponse response;
		CreateWrkWalletEntryRequest request = getBasePage()
			.getNewMobiliserRequest(
				CreateWrkWalletEntryRequest.class);
		request.setWrkWalletEntry(getWrkWalletEntry());
		response = getBasePage().wsWalletClient
			.createWrkWalletEntry(request);
		if (!getBasePage().evaluateMobiliserResponse(response)) {
		    LOG.warn("# An error occurred while saving new wallet approval configuration");
		    return;
		}
	    } catch (Exception e) {
		LOG.error("# An error occurred while saving new wallet approval configuration");
		error(getLocalizer().getString(
			"create.wallet.approveConf.error", this));
	    }
	} else {
	    LOG.debug("# ApprovalConfigurationPanel.updateWalletConfig()");
	    UpdateWrkWalletEntryResponse response = null;
	    try {
		UpdateWrkWalletEntryRequest request = getBasePage()
			.getNewMobiliserRequest(
				UpdateWrkWalletEntryRequest.class);
		request.setWrkWalletEntry(getWrkWalletEntry());
		response = getBasePage().wsWalletClient
			.updateWrkWalletEntry(request);
		if (!getBasePage().evaluateMobiliserResponse(response)) {
		    LOG.warn("# Error occurred while updating wallet approval configuration");
		    return;
		}
		LOG.info("# Successfully updated wallet approval configuration");
	    } catch (Exception e) {
		LOG.error("# Error occurred while updating wallet approval configuration");
		error(getLocalizer().getString(
			"update.wallet.approveConf.error", this));
		return;
	    }
	}
	setResponsePage(new WalletApprovalConfPage());
    }

    private void saveUseCase() {
	LOG.debug("# ApprovalConfigurationPanel.saveUseCase()");
	if (!isEdit) {
	    try {
		CreateUseCasePrivilegeResponse response;
		CreateUseCasePrivilegeRequest request = getBasePage()
			.getNewMobiliserRequest(
				CreateUseCasePrivilegeRequest.class);
		request.setUseCasePrivilege(getUseCasePrivilege());
		response = getBasePage().wsSystemConfClient
			.createUseCasePrivilege(request);

		if (!getBasePage().evaluateMobiliserResponse(response)) {
		    LOG.warn("# An error occurred while saving new usecase approval configuration");
		    return;
		}
	    } catch (Exception e) {
		LOG.error("# An error occurred while saving new usecase approval configuration");
		error(getLocalizer().getString(
			"create.usecase.approveConf.error", this));
	    }
	} else {
	    LOG.debug("# ApprovalConfigurationPanel.updateUseCaseConfig()");
	    UpdateUseCasePrivilegeResponse response = null;
	    try {
		UpdateUseCasePrivilegeRequest request = getBasePage()
			.getNewMobiliserRequest(
				UpdateUseCasePrivilegeRequest.class);
		request.setUseCasePrivilege(getUseCasePrivilege());
		response = getBasePage().wsSystemConfClient
			.updateUseCasePrivilege(request);
		if (!getBasePage().evaluateMobiliserResponse(response)) {
		    LOG.warn("# Error occurred while updating usecase approval configuration");
		    return;
		}
		LOG.info("# Successfully updated usecase approval configuration");
	    } catch (Exception e) {
		LOG.error("# Error occurred while updating usecase approval configuration");
		error(getLocalizer().getString(
			"update.usecase.approveConf.error", this));
		return;
	    }
	}

	setResponsePage(new TransactionApprovalConfPage());
    }

    private void saveFileType() {
	LOG.debug("# ApprovalConfigurationPanel.saveFileType()");
	if (!isEdit) {
	    try {
		CreateWrkBulkResponse response;
		CreateWrkBulkRequest request = getBasePage()
			.getNewMobiliserRequest(CreateWrkBulkRequest.class);
		request.setWrkBulk(getWrkBulk());

		response = getBasePage().wsBulkProcessingClient
			.createWrkBulk(request);

		if (!getBasePage().evaluateMobiliserResponse(response)) {
		    LOG.warn("# An error occurred while saving new file approval configuration");
		    return;
		}
	    } catch (Exception e) {
		LOG.error("# An error occurred while saving new file approval configuration");
		error(getLocalizer().getString(
			"create.usecase.approveConf.error", this));
	    }
	} else {
	    LOG.debug("# ApprovalConfigurationPanel.updateFileConfig()");
	    UpdateWrkBulkResponse response = null;
	    try {
		UpdateWrkBulkRequest request = getBasePage()
			.getNewMobiliserRequest(UpdateWrkBulkRequest.class);
		request.setWrkBulk(getWrkBulk());

		response = getBasePage().wsBulkProcessingClient
			.updateWrkBulk(request);
		if (!getBasePage().evaluateMobiliserResponse(response)) {
		    LOG.warn("# Error occurred while updating file approval configuration");
		    return;
		}
		LOG.info("# Successfully updated file approval configuration");
	    } catch (Exception e) {
		LOG.error("# Error occurred while updating file approval configuration");
		error(getLocalizer().getString("update.file.approveConf.error",
			this));
		return;
	    }
	}

	setResponsePage(new FileApprovalConfPage());
    }

    private void removeApprovalConfig() {
	LOG.debug("# ApprovalConfigurationPanel removeApprovalConfig()");
	if (Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE.equals(type)) {
	    removeCustomerConfig();
	} else if (Constants.RESOURCE_BUNDLE_PI_TYPES.equals(type)) {
	    removeWalletConfig();
	} else if (Constants.RESOURCE_BUNDLE_USE_CASES.equals(type)) {
	    removeUseCaseConfig();
	} else {
	    removeFileConfig();
	}

    }

    private void removeCustomerConfig() {
	LOG.debug("# ApprovaleConfigurationPanel.removeCustomerConfig()");
	DeleteWrkCustomerRequest request;
	try {
	    request = basePage
		    .getNewMobiliserRequest(DeleteWrkCustomerRequest.class);
	    request.setCustomerTypeId(getApprovalConfigObj()
		    .getCustomerTypeId().intValue());
	    DeleteWrkCustomerResponse response = basePage.wsCustomerClient
		    .deleteWrkCustomer(request);
	    if (!basePage.evaluateMobiliserResponse(response))
		return;
	    LOG.info("# Customer Configuration deleted successfully");
	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while deleting Customer Configuration",
		    e);
	    error(getLocalizer().getString("delete.customer.approveConf.error",
		    this));
	}
	setResponsePage(new CustomerApprovalConfPage());
    }

    private void removeWalletConfig() {
	LOG.debug("# ApprovaleConfigurationPanel.removeWalletConfig()");
	DeleteWrkWalletEntryRequest request;
	try {
	    request = basePage
		    .getNewMobiliserRequest(DeleteWrkWalletEntryRequest.class);
	    request.setPiTypeId(getApprovalConfigObj().getPiTypeId().intValue());
	    DeleteWrkWalletEntryResponse response = basePage.wsWalletClient
		    .deleteWrkWalletEntry(request);
	    if (!basePage.evaluateMobiliserResponse(response))
		return;
	    LOG.info("# Wallet Configuration deleted successfully");
	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while deleting Wallet Configuration",
		    e);
	    error(getLocalizer().getString("delete.wallet.approveConf.error",
		    this));
	}
	setResponsePage(new WalletApprovalConfPage());

    }

    private void removeUseCaseConfig() {
	LOG.debug("# ApprovaleConfigurationPanel.removeUseCaseConfig()");

	DeleteUseCasePrivilegeRequest request;
	try {
	    request = basePage
		    .getNewMobiliserRequest(DeleteUseCasePrivilegeRequest.class);
	    request.setUseCaseId(getApprovalConfigObj().getUseCaseId()
		    .intValue());
	    DeleteUseCasePrivilegeResponse response = basePage.wsSystemConfClient
		    .deleteUseCasePrivilege(request);
	    if (!basePage.evaluateMobiliserResponse(response))
		return;
	    LOG.info("# Use case Configuration deleted successfully");
	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while deleting Use case Configuration",
		    e);
	    error(getLocalizer().getString("delete.usecase.approveConf.error",
		    this));
	}
	setResponsePage(new TransactionApprovalConfPage());
    }

    private void removeFileConfig() {
	LOG.debug("# ApprovaleConfigurationPanel.removeFileConfig()");

	DeleteWrkBulkRequest request;
	try {
	    request = basePage
		    .getNewMobiliserRequest(DeleteWrkBulkRequest.class);
	    request.setFileTypeId(getApprovalConfigObj().getBulkFileTypeId()
		    .intValue());
	    DeleteWrkBulkResponse response = basePage.wsBulkProcessingClient
		    .deleteWrkBulk(request);
	    if (!basePage.evaluateMobiliserResponse(response))
		return;
	    LOG.info("# file Configuration deleted successfully");
	} catch (Exception e) {
	    LOG.error("# An error occurred while deleting file Configuration",
		    e);
	    error(getLocalizer().getString("delete.file.approveConf.error",
		    this));
	}
	setResponsePage(new FileApprovalConfPage());
    }

    private void convert() {

	ApprovalConfBean appConfObj = getApprovalConfigObj();
	if (Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE.equals(type)) {

	    setWrkCustomer(new WrkCustomer());

	    getWrkCustomer().setCustomerTypeId(
		    appConfObj.getCustomerTypeId().intValue());
	    getWrkCustomer()
		    .setMakerPrivilege((appConfObj.getMakerPrivilege()));
	    getWrkCustomer().setCheckerPrivilege(
		    appConfObj.getCheckerPrivilege());
	    getWrkCustomer().setExecutePrivilege(
		    appConfObj.getExecutePrivilege());

	} else if (Constants.RESOURCE_BUNDLE_PI_TYPES.equals(type)) {

	    setWrkWalletEntry(new WrkWalletEntry());

	    getWrkWalletEntry()
		    .setPiTypeId(appConfObj.getPiTypeId().intValue());
	    getWrkWalletEntry().setMakerPrivilege(
		    (appConfObj.getMakerPrivilege()));
	    getWrkWalletEntry().setCheckerPrivilege(
		    appConfObj.getCheckerPrivilege());
	    getWrkWalletEntry().setExecutePrivilege(
		    appConfObj.getExecutePrivilege());

	} else if (Constants.RESOURCE_BUNDLE_USE_CASES.equals(type)) {
	    setUseCasePrivilege(new UseCasePrivilege());
	    getUseCasePrivilege().setUseCaseId(appConfObj.getUseCaseId());
	    getUseCasePrivilege().setMakerPrivilege(
		    appConfObj.getMakerPrivilege());
	    getUseCasePrivilege().setCheckerPrivilege(
		    appConfObj.getCheckerPrivilege());
	    getUseCasePrivilege().setExecutePrivilege(
		    appConfObj.getExecutePrivilege());
	    getUseCasePrivilege().setPayeePrivilege(
		    appConfObj.getPayeePrivilege());
	    getUseCasePrivilege().setPayerPrivilege(
		    appConfObj.getPayerPrivilege());
	    getUseCasePrivilege().setCallerPrivilege(
		    appConfObj.getCallerPrivilege());
	    getUseCasePrivilege().setCallerParentPrivilege(
		    appConfObj.getCallerParentPrivilege());
	    getUseCasePrivilege().setCallerSelfPrivilege(
		    appConfObj.getCallerSelfPrivilege());

	} else {
	    setWrkBulk(new WrkBulk());

	    getWrkBulk().setBulkTypeId(
		    appConfObj.getBulkFileTypeId().intValue());
	    getWrkBulk().setMakerPrivilege((appConfObj.getMakerPrivilege()));
	    getWrkBulk().setCheckerPrivilege(appConfObj.getCheckerPrivilege());
	    getWrkBulk().setExecutePrivilege(appConfObj.getExecutePrivilege());
	}
    }

    private List<Integer> getAvaliableTypes(List<Integer> confTypeList,
	    String lookUpName) {
	List<KeyValue<String, String>> lookUpEntries = null;
	List<Integer> removeList = new ArrayList<Integer>();
	KeyValue<String, String> entry;

	if (PortalUtils.exists(approvalConfigs)) {
	    if (!isEdit) {
		Iterator<ApprovalConfBean> iter = approvalConfigs.iterator();
		while (iter.hasNext()) {
		    ApprovalConfBean appBean = iter.next();
		    if (Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE.equals(type)) {
			lookUpEntries = basePage.fetchLookupEntries(type,
				"approvaltype.load.error");
			if (PortalUtils.exists(appBean.getCustomerTypeId())) {
			    removeList.add(appBean.getCustomerTypeId());
			} else {
			    break;
			}
		    } else if (Constants.RESOURCE_BUNDLE_PI_TYPES.equals(type)) {
			lookUpEntries = basePage.fetchLookupEntries(type,
				"approvaltype.load.error");
			if (PortalUtils.exists(appBean.getPiTypeId())) {
			    removeList.add(appBean.getPiTypeId());
			} else {
			    break;
			}
		    } else if (Constants.RESOURCE_BUNDLE_USE_CASES.equals(type)) {
			lookUpEntries = basePage.fetchLookupEntries(type,
				"approvaltype.load.error");
			if (PortalUtils.exists(appBean.getUseCaseId())) {
			    removeList.add(appBean.getUseCaseId());
			} else {
			    break;
			}
		    }
		}
	    }
	}
	if (PortalUtils.exists(lookUpEntries)) {
	    Iterator<KeyValue<String, String>> iter = lookUpEntries.iterator();
	    while (iter.hasNext()) {
		entry = iter.next();
		if (!removeList.contains(Integer.valueOf(entry.getKey())))
		    confTypeList.add(Integer.valueOf(entry.getKey()));
	    }
	}

	return confTypeList;
    }

    private void loadConfigurations() {

	try {
	    if (Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE.equals(type)) {
		wrkCustomersList = dataProvider
			.loadWrkCustomersList(forceReload);

	    } else if (Constants.RESOURCE_BUNDLE_PI_TYPES.equals(type)) {
		wrkWalletsList = dataProvider.loadWrkWalletsList(forceReload);
	    } else if (Constants.RESOURCE_BUNDLE_USE_CASES.equals(type)) {
		wrkUseCasePrivsList = dataProvider
			.loadWrkUseCaseList(forceReload);
	    } else {
		wrkBulksList = dataProvider.loadWrkFilesList(forceReload);
	    }
	    forceReload = false;

	} catch (DataProviderLoadException dple) {
	    LOG.error("# An error occurred while loading approval types", dple);
	    error(getLocalizer().getString("approvaltype.load.error", this));
	}

    }

    private List<KeyValue<Integer, String>> getMimeTypeList() {
	LOG.debug("# FileUploadPanel.getMimeTypeList()");
	List<KeyValue<Integer, String>> mimeTypes = new ArrayList<KeyValue<Integer, String>>();
	List<KeyValue<Integer, String>> mimeTypes1 = new ArrayList<KeyValue<Integer, String>>();
	List<BulkFileType> typeList;
	try {
	    GetBulkFileTypeRequest request = basePage
		    .getNewMobiliserRequest(GetBulkFileTypeRequest.class);
	    GetBulkFileTypeResponse response = basePage.wsBulkProcessingClient
		    .getBulkFileType(request);
	    if (basePage.evaluateMobiliserResponse(response)) {
		typeList = response.getWrkBulkFileType();
		Iterator<BulkFileType> iter = typeList.iterator();
		while (iter.hasNext()) {
		    BulkFileType bulkFile = iter.next();
		    mimeTypes.add(new KeyValue<Integer, String>(bulkFile
			    .getBulkTypeId(), bulkFile.getFileTypeDesc()));
		}

		if (Constants.RESOURCE_BUNDLE_FILE_TYPES.equals(type)) {
		    wrkBulkList = dataProvider.loadWrkFilesList(true);
		    Iterator<WrkBulk> iterWrk = wrkBulkList.iterator();
		    while (iterWrk.hasNext()) {
			WrkBulk wrkBulk = iterWrk.next();
			Iterator<BulkFileType> iterBulk = typeList.iterator();
			while (iterBulk.hasNext()) {
			    BulkFileType bulkFile = iterBulk.next();
			    if (bulkFile.getBulkTypeId() == wrkBulk
				    .getBulkTypeId())
				mimeTypes1.add(new KeyValue<Integer, String>(
					bulkFile.getBulkTypeId(), bulkFile
						.getFileTypeDesc()));
			}
		    }
		    mimeTypes.removeAll(mimeTypes1);
		}
	    }
	} catch (Exception e) {
	    LOG.error("An error occurred in getting the mime type list", e);
	    error(getLocalizer().getString(
		    "bulk.processing.load.filetypes.error", this));
	}

	return mimeTypes;
    }

    public MobiliserBasePage getBasePage() {
	return basePage;
    }

    public void setBasePage(MobiliserBasePage basePage) {
	this.basePage = basePage;
    }

    public WrkCustomer getWrkCustomer() {
	return wrkCustomer;
    }

    public ApprovalConfBean getApprovalConfigObj() {
	return approvalConfigObj;
    }

    public void setApprovalConfigObj(ApprovalConfBean approvalConfigObj) {
	this.approvalConfigObj = approvalConfigObj;
    }

    public void setWrkCustomer(WrkCustomer wrkCustomer) {
	this.wrkCustomer = wrkCustomer;
    }

    public WrkWalletEntry getWrkWalletEntry() {
	return wrkWalletEntry;
    }

    public void setWrkWalletEntry(WrkWalletEntry wrkWalletEntry) {
	this.wrkWalletEntry = wrkWalletEntry;
    }

    public UseCasePrivilege getUseCasePrivilege() {
	return useCasePrivilege;
    }

    public void setUseCasePrivilege(UseCasePrivilege useCasePrivilege) {
	this.useCasePrivilege = useCasePrivilege;
    }

    public WrkBulk getWrkBulk() {
	return wrkBulk;
    }

    public void setWrkBulk(WrkBulk wrkBulk) {
	this.wrkBulk = wrkBulk;
    }

    public String getCustomerTypeId() {
	return customerTypeId;
    }

    public void setCustomerTypeId(String customerTypeId) {
	this.customerTypeId = customerTypeId;
    }

    public String getPiTypeId() {
	return piTypeId;
    }

    public void setPiTypeId(String piTypeId) {
	this.piTypeId = piTypeId;
    }
}
