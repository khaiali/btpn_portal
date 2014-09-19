package com.sybase365.mobiliser.web.distributor.pages.agentcare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Alignment;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Unit;
import org.apache.wicket.extensions.markup.html.tree.table.IColumn;
import org.apache.wicket.extensions.markup.html.tree.table.PropertyTreeColumn;
import org.apache.wicket.extensions.markup.html.tree.table.TreeTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.customer.FindHierarchicalCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitClass;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSetClass;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreateBalanceAlertRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreateBalanceAlertResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.DeleteBalanceAlertRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.DeleteBalanceAlertResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetBalanceAlertsByPaymentInstrumentRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetBalanceAlertsByPaymentInstrumentResponseType;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdateBalanceAlertRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdateBalanceAlertResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.BalanceAlert;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.treetable.utils.TreeTableUtils;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.BalanceAlertDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.AmountConverter;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_AGENTS)
public class AgentEditPage extends AgentCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AgentEditPage.class);

    private CustomerBean customer;
    private String roleDescription;
    private List<BalanceAlert> balanceAlerts;
    private BalanceAlert balanceAlert;
    private LimitClass limitClass;
    private String confirmPassword;
    private FeedbackPanel personalDataErrorMessages;
    private FeedbackPanel balanceAlertErrorMessages;
    private FeedbackPanel agentSettingErrorMessage;
    private FeedbackPanel loginDataErrorMessages;
    private Long paymentInstrumentId = null;
    private Button deleteButton;
    private Button activateButton;
    private Button changeButton;
    private Button saveAgentSetting;
    private LocalizableLookupDropDownChoice blockedStatusChoice;

    private boolean isVisible = true;
    private boolean canActivate = false;
    private boolean deleteVisible = false;

    private boolean editMode = false;
    private WebMarkupContainer editAgentDiv;
    private String actionMode;

    // Data View
    private BalanceAlertDataProvider dataProvider;
    private List<BalanceAlert> balanceAlerList;
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private int rowIndex = 1;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByTemplateName = "orderByTemplateName";
    private static final String WICKET_ID_removeLink = "removeLink";
    private static final String WICKET_ID_removeText = "removeText";
    private static final String WICKET_ID_editLink = "editLink";
    private static final String WICKET_ID_editText = "editText";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_threshold = "threshold";
    private static final String WICKET_ID_onlyTransition = "onlyTransition";
    private static final String WICKET_ID_templateName = "templateName";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    private boolean isFromTopMenu = false;

    public AgentEditPage(final PageParameters parameters) {
	super(parameters);
	if (PortalUtils.exists(parameters)) {
	    this.actionMode = parameters.getString("action");
	    if (PortalUtils.exists(parameters.getString("isFromTopMenu"))) {
		this.isFromTopMenu = true;
	    } else {
		this.isFromTopMenu = false;
	    }
	}
	initPageComponents();
    }

    public AgentEditPage(String mode) {
	super();
	this.actionMode = mode;
	this.isFromTopMenu = false;
	initPageComponents();
    }

    public AgentEditPage() {
	super();
	PageParameters params = super.getPageParameters();
	if (PortalUtils.exists(params)) {
	    this.actionMode = params.getString("action");
	    if (PortalUtils.exists(params.getString("isFromTopMenu"))) {
		this.isFromTopMenu = true;
	    } else {
		this.isFromTopMenu = false;
	    }
	}
	initPageComponents();
    }

    protected void initPageComponents() {
	getMobiliserWebSession().setLeftMenu(null);
	if (PortalUtils.exists(actionMode) && "edit".equals(actionMode)) {
	    if (getMobiliserWebSession().getMaxMaintainLevel() == -1
		    || getMobiliserWebSession().getMaxMaintainLevel() >= getMobiliserWebSession()
			    .getCustomer().getHierarchyLevel()) {
		setEditMode(true);
	    } else {
		setEditMode(false);
	    }
	} else {
	    setEditMode(false);
	}
	addOrReplace((FeedbackPanel) (new FeedbackPanel("globalerrorMessages")
		.setVisible(false)));

	if (isFromTopMenu) {
	    add(new Label("agentEditTitle", getLocalizer().getString(
		    "agentHierarchy.title", this)));
	} else {
	    add(new Label("agentEditTitle", getLocalizer().getString(
		    "agentEdit.title", this)));
	}
	addAgentTreeDiv();
	addEditAgentDiv();

    }

    private void addAgentTreeDiv() {
	WebMarkupContainer agentTreeDiv = new WebMarkupContainer("agentTreeDiv");
	agentTreeDiv.setVisible(false);
	try {
	    CustomerBean parent = null;
	    FindHierarchicalCustomerRequest request = getNewMobiliserRequest(FindHierarchicalCustomerRequest.class);
	    // if agent is logged in then use parent id to get the children list
	    // get the agent type id list acting on behalf of agents from prefs
	    String agentTypeIdsStr = getConfiguration()
		    .getMerchantAgentTypeIds();
	    String[] agentTypeIdsStrArr = agentTypeIdsStr.split(",");
	    for (String agentTypeId : agentTypeIdsStrArr) {
		if (PortalUtils.exists(agentTypeId)
			&& Long.parseLong(agentTypeId.trim()) == getMobiliserWebSession()
				.getLoggedInCustomer().getCustomerTypeId()) {
		    request.setAgentId(getMobiliserWebSession()
			    .getLoggedInCustomer().getParentId());
		    parent = new CustomerBean();
		    parent.setId(getMobiliserWebSession().getLoggedInCustomer()
			    .getParentId());

		    continue;
		}
	    }
	    List<CustomerBean> agentBeans = findCustomer(request);

	    if (!PortalUtils.exists(agentBeans)) {
		info(getLocalizer().getString("chidren.not.found", this));
	    } else {
		if (!PortalUtils.exists(parent))
		    parent = Converter.getInstance()
			    .getCustomerBeanFromCustomer(
				    getMobiliserWebSession()
					    .getLoggedInCustomer());

		agentBeans = sortAgents(agentBeans);
		parent.setHierarchyLevel(0);
		parent = makeAgentHierarchy(parent, agentBeans);
		TreeModel treeModel = TreeTableUtils.buildModel(parent);
		List<IColumn> columns = new ArrayList<IColumn>();
		columns.add(new PropertyTreeColumn(new ColumnLocation(
			Alignment.LEFT, 722, Unit.PX), getMobiliserWebSession()
			.getLoggedInCustomer().getUsername(),
			"userObject.displayName"));
		TreeTable agentTreeTable = new TreeTable("agentTree",
			treeModel, columns.toArray(new IColumn[columns.size()])) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    protected void onNodeLinkClicked(AjaxRequestTarget target,
			    TreeNode node) {
			super.onNodeLinkClicked(target, node);

			// access the agent behind the node
			CustomerBean currentagent = (CustomerBean) ((DefaultMutableTreeNode) node)
				.getUserObject();

			// call a method which does the actual work
			loadAgentDetails(currentagent);

			// see that only one branch is expanded at all times
			TreeTableUtils.expandPath(getTreeState(), node);
			updateTree(target);
		    }

		};
		// in this case we do not want the root node to be shown
		// since
		// it is
		// just a pseude elemnt anyway.
		agentTreeTable.setRootLess(true);
		agentTreeTable.getTreeState().setAllowSelectMultiple(false);
		agentTreeTable.getTreeState().collapseAll();

		// add the tree table component to the page's component tree
		agentTreeDiv.setVisible(true);
		agentTreeDiv.addOrReplace(agentTreeTable);
	    }

	} catch (Exception e) {
	    LOG.error(
		    "# An error occuurred while loading agent Hierarchy page",
		    e);
	    error(getLocalizer().getString("error.agent.hierarchy", this));
	}
	add(agentTreeDiv);

    }

    protected void loadAgentDetails(CustomerBean agent) {
	Address address = getCustomerAddress(agent.getId());
	agent.setAddress(Converter.getInstance().getAddressBeanFromAddress(
		address));
	try {
	    Identification agentIdentification = getCustomerIdentification(
		    agent.getId(), Constants.IDENT_TYPE_USERNAME);
	    if (agentIdentification != null)
		agent.setUserName(agentIdentification.getIdentification());
	} catch (Exception e) {
	    LOG.error("# Error in getCustomerAddress of loadAgentDetails", e);
	}
	try {
	    List<LimitSetClass> limits = getLimitSetClassList(agent.getId(),
		    Constants.LIMIT_CUSTOMER_TYPE);
	    if (PortalUtils.exists(limits)) {
		agent.setLimitId(limits.get(0).getId());
		agent.setLimitClass(limits.get(0).getLimitClass());
	    }
	} catch (Exception e) {
	    LOG.error("# Error in getLimitSet of loadAgentDetails", e);
	}
	if (PortalUtils.exists(getMobiliserWebSession().getCustomer())
		&& PortalUtils.exists(agent)
		&& getMobiliserWebSession().getCustomer().getId().longValue() == agent
			.getId().longValue()) {
	    agent.setBlackListReason(getMobiliserWebSession().getCustomer()
		    .getBlackListReason());
	}
	getMobiliserWebSession().setCustomer(agent);

	setResponsePage(new AgentEditPage("edit"));
    }

    private List<CustomerBean> sortAgents(List<CustomerBean> customers) {
	Collections.sort(customers);
	return customers;
    }

    private CustomerBean makeAgentHierarchy(CustomerBean root,
	    List<CustomerBean> customers) {
	for (int i = 0; i < customers.size(); i++) {
	    CustomerBean customer = customers.get(i);
	    if (root.getId().longValue() == customer.getParentId().longValue()) {
		if (!root.getChildren().contains(customer)) {
		    customer.setHierarchyLevel(root.getHierarchyLevel() + 1);
		    root.getChildren().add(customer);
		}
	    } else {

		if (!root.getChildren().isEmpty()) {
		    for (CustomerBean child : root.getChildren()) {
			List<CustomerBean> subList = customers.subList(i,
				customers.size());
			makeAgentHierarchy(child, subList);
		    }
		} else {
		    break;
		}
	    }
	}
	return root;

    }

    @SuppressWarnings("unchecked")
    private void addEditAgentDiv() {
	editAgentDiv = new WebMarkupContainer("editAgentDiv");
	if (!isEditMode()) {
	    editAgentDiv.setVisible(false);
	    addOrReplace((FeedbackPanel) (new FeedbackPanel(
		    "globalerrorMessages").setVisible(true)));
	} else {
	    roleDescription = "";
	    personalDataErrorMessages = (FeedbackPanel) new FeedbackPanel(
		    "personalDataErrorMessages").setVisible(true);
	    balanceAlertErrorMessages = (FeedbackPanel) new FeedbackPanel(
		    "balanceAlertErrorMessages").setVisible(false);
	    agentSettingErrorMessage = (FeedbackPanel) new FeedbackPanel(
		    "AgentSettingErrorMessage").setVisible(false);
	    loginDataErrorMessages = (FeedbackPanel) new FeedbackPanel(
		    "loginDataErrorMessages").setVisible(false);

	    deleteButton = new Button("deleteAgent") {

		private static final long serialVersionUID = 1L;

		@Override
		public void onSubmit() {
		    personalDataErrorMessages.setVisible(true);
		    balanceAlertErrorMessages.setVisible(false);
		    agentSettingErrorMessage.setVisible(false);
		    loginDataErrorMessages.setVisible(false);
		    deleteAgent();
		}

	    };
	    activateButton = new Button("activateAgent") {

		private static final long serialVersionUID = 1L;

		@Override
		public void onSubmit() {
		    personalDataErrorMessages.setVisible(true);
		    balanceAlertErrorMessages.setVisible(false);
		    agentSettingErrorMessage.setVisible(false);
		    loginDataErrorMessages.setVisible(false);
		    activateAgent();
		}

	    };
	    final PasswordTextField oldPassword = (PasswordTextField) new PasswordTextField(
		    "customer.password").add(Constants.largeStringValidator)
		    .setRequired(false)
		    .add(new SimpleAttributeModifier("autocomplete", "off"))
		    .add(Constants.largeSimpleAttributeModifier)
		    .add(new ErrorIndicator());
	    final PasswordTextField newPassword = (PasswordTextField) new PasswordTextField(
		    "confirmPassword").add(Constants.largeStringValidator)
		    .setRequired(false)
		    .add(new SimpleAttributeModifier("autocomplete", "off"))
		    .add(Constants.largeSimpleAttributeModifier)
		    .add(new ErrorIndicator());
	    changeButton = new Button("savePassword") {

		private static final long serialVersionUID = 1L;

		@Override
		protected void onBeforeRender() {
		    super.onBeforeRender();
		    oldPassword.setRequired(false);
		    newPassword.setRequired(false);
		    oldPassword.validate();
		    newPassword.validate();
		}

		@Override
		public void onSubmit() {
		    personalDataErrorMessages.setVisible(false);
		    balanceAlertErrorMessages.setVisible(false);
		    agentSettingErrorMessage.setVisible(false);
		    loginDataErrorMessages.setVisible(true);
		    changePassword(oldPassword, newPassword);
		}

	    };

	    blockedStatusChoice = (LocalizableLookupDropDownChoice) new LocalizableLookupDropDownChoice<Integer>(
		    "customer.blackListReason", Integer.class,
		    "blackListReasons", this, true, true);

	    Form<?> personalDataForm = new Form("personalDataForm",
		    new CompoundPropertyModel<AgentEditPage>(this)) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onError() {
		    personalDataErrorMessages.setVisible(true);
		    balanceAlertErrorMessages.setVisible(false);
		    agentSettingErrorMessage.setVisible(false);
		    loginDataErrorMessages.setVisible(false);
		}
	    };
	    personalDataForm
		    .addOrReplace(
			    new RequiredTextField<String>(
				    "customer.address.firstName")
				    .add(new PatternValidator(
					    Constants.REGEX_FIRSTNAME))
				    .add(Constants.mediumStringValidator)
				    .setRequired(true)
				    .add(Constants.mediumSimpleAttributeModifier))
		    .add(new ErrorIndicator());
	    personalDataForm
		    .addOrReplace(
			    new RequiredTextField<String>(
				    "customer.address.lastName")
				    .add(new PatternValidator(
					    Constants.REGEX_FIRSTNAME))
				    .add(Constants.mediumStringValidator)
				    .setRequired(true)
				    .add(Constants.mediumSimpleAttributeModifier))
		    .add(new ErrorIndicator());
	    personalDataForm.addOrReplace(new TextField<String>(
		    "customer.address.company")
		    .add(new PatternValidator(Constants.REGEX_COMPANY))
		    .add(Constants.mediumStringValidator)
		    .add(Constants.mediumSimpleAttributeModifier)
		    .add(new ErrorIndicator()));
	    personalDataForm.addOrReplace(new TextField<String>(
		    "customer.address.position")
		    .add(new PatternValidator(Constants.REGEX_POSITION))
		    .add(Constants.mediumStringValidator)
		    .add(Constants.mediumSimpleAttributeModifier)
		    .add(new ErrorIndicator()));
	    personalDataForm.addOrReplace(new RequiredTextField<String>(
		    "customer.address.email")
		    .add(Constants.mediumStringValidator)
		    .add(new PatternValidator(Constants.REGEX_EMAIL))
		    .add(Constants.mediumSimpleAttributeModifier)
		    .add(new ErrorIndicator()));
	    personalDataForm
		    .addOrReplace(new LocalizableLookupDropDownChoice<String>(
			    "customer.timeZone", String.class, "timezones",
			    this, false, true).add(new ErrorIndicator()));
	    personalDataForm.addOrReplace(new Button("savePersData") {
		private static final long serialVersionUID = 1L;

		@Override
		public void onSubmit() {
		    personalDataErrorMessages.setVisible(true);
		    balanceAlertErrorMessages.setVisible(false);
		    agentSettingErrorMessage.setVisible(false);
		    loginDataErrorMessages.setVisible(false);
		    updatePersonalData();
		}

		private void updatePersonalData() {
		    try {
			updateCustomerAddress(customer);
			customer.setDisplayName(createDisplayName(customer
				.getAddress().getFirstName(), customer
				.getAddress().getLastName()));
			if (updateCustomerDetail(customer)) {
			    loadAgentDetails(customer);
			    personalDataErrorMessages
				    .info(getLocalizer()
					    .getString(
						    "MESSAGE.EDIT_AGENT_PERSONALDATA_UPDATED",
						    this));
			}
		    } catch (Exception e) {
			LOG.error(
				"# An error occurred whilte saving Personal data, with agent Id {}",
				customer.getId(), e);
			personalDataErrorMessages
				.error(getLocalizer()
					.getString(
						"ERROR.EDIT_AGENT_PERSONALDATA_FAILURE",
						this));
		    }
		}
	    });
	    personalDataForm.addOrReplace(personalDataErrorMessages);
	    editAgentDiv.add(personalDataForm);

	    // Balance Alert Form
	    Form<?> balanceAlertForm = new Form("agentEditForm_balanceAlert",
		    new CompoundPropertyModel<AgentEditPage>(this)) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onError() {
		    personalDataErrorMessages.setVisible(false);
		    balanceAlertErrorMessages.setVisible(true);
		    agentSettingErrorMessage.setVisible(false);
		    loginDataErrorMessages.setVisible(false);
		}
	    };
	    balanceAlertForm.addOrReplace(balanceAlertErrorMessages);

	    WebMarkupContainer bAlertGrid = new WebMarkupContainer(
		    "balanceAlertGrid");
	    bAlertGrid = createMarkupContainer(bAlertGrid, balanceAlertForm,
		    "ADD");

	    // Get paymentInstrument ID for EditAgent :

	    getPaymentInstrumentID();

	    // getBalanceAlert data from DB...
	    if (PortalUtils.exists(paymentInstrumentId)) {
		getMobiliserWebSession().setBalanceAlertList(
			getBalanceAlertfromDB(paymentInstrumentId));
	    }

	    createBalanceAlertDataView(balanceAlertForm);
	    balanceAlertForm.addOrReplace(bAlertGrid);

	    editAgentDiv.add(balanceAlertForm);

	    // Agent setting Form

	    WebMarkupContainer limitArea_1 = new WebMarkupContainer(
		    "limitArea_1");
	    WebMarkupContainer limitArea_2 = new WebMarkupContainer(
		    "limitArea_2");

	    Form<?> agentSettingsForm = new Form("agentSettingsForm",
		    new CompoundPropertyModel<AgentEditPage>(this)) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onError() {
		    personalDataErrorMessages.setVisible(false);
		    balanceAlertErrorMessages.setVisible(false);
		    agentSettingErrorMessage.setVisible(true);
		    loginDataErrorMessages.setVisible(false);
		}
	    };
	    agentSettingsForm.addOrReplace(agentSettingErrorMessage);
	    agentSettingsForm.add(new Label("roleDescription"));
	    limitArea_1.add(new TextField<Long>(
		    "customer.limitClass.dailyCreditLimitAmount") {

		private static final long serialVersionUID = 1L;

		@Override
		public IConverter getConverter(Class<?> type) {
		    return new AmountConverter(AgentEditPage.this,
			    "agentEdit.limitClass.dailyCreditLimitAmount");
		};

	    }.add(Constants.amountSimpleAttributeModifier).add(
		    new ErrorIndicator()));
	    limitArea_1.add(new TextField<Long>(
		    "customer.limitClass.weeklyCreditLimitAmount") {

		private static final long serialVersionUID = 1L;

		@Override
		public IConverter getConverter(Class<?> type) {
		    return new AmountConverter(AgentEditPage.this,
			    "agentEdit.limitClass.weeklyCreditLimitAmount");
		};

	    }.add(Constants.amountSimpleAttributeModifier).add(
		    new ErrorIndicator()));
	    limitArea_1.add(new TextField<Long>(
		    "customer.limitClass.monthlyCreditLimitAmount") {

		private static final long serialVersionUID = 1L;

		@Override
		public IConverter getConverter(Class<?> type) {
		    return new AmountConverter(AgentEditPage.this,
			    "agentEdit.limitClass.monthlyCreditLimitAmount");
		};

	    }.add(Constants.amountSimpleAttributeModifier).add(
		    new ErrorIndicator()));
	    limitArea_1.add(new TextField<Long>(
		    "customer.limitClass.absoluteCreditLimitAmount") {

		private static final long serialVersionUID = 1L;

		@Override
		public IConverter getConverter(Class<?> type) {
		    return new AmountConverter(AgentEditPage.this,
			    "agentEdit.limitClass.absoluteCreditLimitAmount");
		};

	    }.add(Constants.amountSimpleAttributeModifier).add(
		    new ErrorIndicator()));
	    limitArea_2.add(new TextField<Long>(
		    "customer.limitClass.dailyDebitLimitAmount") {

		private static final long serialVersionUID = 1L;

		@Override
		public IConverter getConverter(Class<?> type) {
		    return new AmountConverter(AgentEditPage.this,
			    "agentEdit.limitClass.dailyDebitLimitAmount");
		};

	    }.add(Constants.amountSimpleAttributeModifier).add(
		    new ErrorIndicator()));
	    limitArea_2.add(new TextField<Long>(
		    "customer.limitClass.weeklyDebitLimitAmount") {

		private static final long serialVersionUID = 1L;

		@Override
		public IConverter getConverter(Class<?> type) {
		    return new AmountConverter(AgentEditPage.this,
			    "agentEdit.limitClass.weeklyDebitLimitAmount");
		};

	    }.add(Constants.amountSimpleAttributeModifier).add(
		    new ErrorIndicator()));
	    limitArea_2.add(new TextField<Long>(
		    "customer.limitClass.monthlyDebitLimitAmount") {

		private static final long serialVersionUID = 1L;

		@Override
		public IConverter getConverter(Class<?> type) {
		    return new AmountConverter(AgentEditPage.this,
			    "agentEdit.limitClass.monthlyDebitLimitAmount");
		};

	    }.add(Constants.amountSimpleAttributeModifier).add(
		    new ErrorIndicator()));
	    limitArea_2.add(new TextField<Long>(
		    "customer.limitClass.absoluteDebitLimitAmount") {
		private static final long serialVersionUID = 1L;

		public IConverter getConverter(Class<?> type) {
		    return new AmountConverter(AgentEditPage.this,
			    "agentEdit.limitClass.absoluteDebitLimitAmount");
		};
	    }.add(Constants.amountSimpleAttributeModifier).add(
		    new ErrorIndicator()));
	    saveAgentSetting = new Button("saveAgentSettings") {
		private static final long serialVersionUID = 1L;

		@Override
		public void onSubmit() {

		    personalDataErrorMessages.setVisible(false);
		    balanceAlertErrorMessages.setVisible(false);
		    agentSettingErrorMessage.setVisible(true);
		    loginDataErrorMessages.setVisible(false);
		    if (PortalUtils.exists(customer.getLimitClass())
			    && PortalUtils.exists(customer.getLimitClass()
				    .getId())) {
			try {
			    if (!agentSettingErrorMessage.anyErrorMessage()) {
				if (PortalUtils
					.exists(customer.getLimitClass())) {
				    updateAgentLimitSetting(customer);

				    agentSettingErrorMessage = new FeedbackPanel(
					    "AgentSettingErrorMessage");

				    agentSettingErrorMessage.setVisible(true);
				    this.getForm().addOrReplace(
					    agentSettingErrorMessage);
				}
			    }
			} catch (Exception e) {
			    LOG.error(
				    "# An error occurred whilte updating Limit setting data, with agent Id {}",
				    customer.getId(), e);
			    agentSettingErrorMessage.error(getLocalizer()
				    .getString("ERROR.CHANGE_AGENT_SETTINGS",
					    this));
			}
		    } else {
			try {
			    if (!agentSettingErrorMessage.anyErrorMessage()) {
				if (PortalUtils
					.exists(customer.getLimitClass())) {
				    customer = createAgentLimitSetting(customer);
				    agentSettingErrorMessage = new FeedbackPanel(
					    "AgentSettingErrorMessage");

				    agentSettingErrorMessage.setVisible(true);
				    this.getForm().addOrReplace(
					    agentSettingErrorMessage);
				}
			    }

			} catch (Exception e) {
			    LOG.error(
				    "# An error occurred whilte saving Limit setting data, with agent Id {}",
				    customer.getId(), e);
			    agentSettingErrorMessage.error(getLocalizer()
				    .getString("ERROR.CHANGE_AGENT_SETTINGS",
					    this));
			}
		    }

		}
	    };
	    agentSettingsForm.addOrReplace(saveAgentSetting.setVisible(true));
	    customer = getMobiliserWebSession().getCustomer();
	    if (customer != null && customer.getCustomerTypeId() != null) {
		Integer roleKey = customer.getCustomerTypeId();

		roleDescription = getDisplayValue(
			String.valueOf(customer.getCustomerTypeId()),
			Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE);

		if (Constants.CUSTOMER_ROLE_MONEY_MERCHANT == roleKey) {
		    limitArea_1.setVisible(true);
		    limitArea_2.setVisible(true);
		    agentSettingsForm.addOrReplace(saveAgentSetting
			    .setVisible(true));
		    balanceAlertForm.setVisible(true);
		} else if (Constants.CUSTOMER_ROLE_MONEY_MERCHANT_AGENT == roleKey) {
		    limitArea_1.setVisible(false);
		    limitArea_2.setVisible(false);
		    agentSettingsForm.addOrReplace(saveAgentSetting
			    .setVisible(false));
		    balanceAlertForm.setVisible(false);
		} else {
		    limitArea_1.setVisible(true);
		    limitArea_2.setVisible(true);
		    agentSettingsForm.addOrReplace(saveAgentSetting
			    .setVisible(true));
		    balanceAlertForm.setVisible(false);
		}
	    }
	    agentSettingsForm.add(limitArea_1);
	    agentSettingsForm.add(limitArea_2);
	    editAgentDiv.add(agentSettingsForm);

	    // Login Data Form
	    Form<?> loginDataForm = new Form("agentEditForm_loginData",
		    new CompoundPropertyModel<AgentEditPage>(this)) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onError() {
		    personalDataErrorMessages.setVisible(false);
		    balanceAlertErrorMessages.setVisible(false);
		    agentSettingErrorMessage.setVisible(false);
		    loginDataErrorMessages.setVisible(true);
		}
	    };
	    checkButtonVisibality();
	    loginDataForm.addOrReplace(loginDataErrorMessages);
	    loginDataForm.addOrReplace(new Label("customer.userName"));

	    loginDataForm.addOrReplace(oldPassword.setRequired(false));
	    loginDataForm.addOrReplace(newPassword.setRequired(false));
	    loginDataForm.add(new EqualPasswordInputValidator(oldPassword,
		    newPassword));

	    loginDataForm.addOrReplace(blockedStatusChoice
		    .setEnabled(isVisible).add(new ErrorIndicator()));

	    loginDataForm.add(changeButton);
	    loginDataForm.add(deleteButton.setDefaultFormProcessing(false)
		    .setVisible(deleteVisible));
	    loginDataForm.add(activateButton.setDefaultFormProcessing(false)
		    .setVisible(canActivate));
	    editAgentDiv.addOrReplace(loginDataForm);
	    editAgentDiv.setVisible(true);
	}
	add(editAgentDiv);
    }

    @SuppressWarnings({ "unchecked" })
    private WebMarkupContainer createMarkupContainer(
	    final WebMarkupContainer bAlertGrid,
	    final Form<?> balanceAlertForm, String mode) {
	bAlertGrid.setOutputMarkupId(true);
	bAlertGrid.setOutputMarkupPlaceholderTag(true);
	balanceAlertForm.setOutputMarkupId(true);
	balanceAlertForm.setOutputMarkupPlaceholderTag(true);
	bAlertGrid.addOrReplace(new TextField<Long>("balanceAlert.threshold") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public IConverter getConverter(Class<?> type) {
		return new AmountConverter(AgentEditPage.this,
			"balanceAlert.threshold");
	    };

	}.add(new ErrorIndicator()));
	TextField<String> eMail = new TextField<String>("balanceAlert.emails");
	bAlertGrid.addOrReplace(eMail
		.add(new PatternValidator(Constants.REGEX_EMAILS))
		.add(Constants.largeStringValidator)
		.add(Constants.largeSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	TextField<String> bMsisdn = new TextField<String>(
		"balanceAlert.msisdns");
	bAlertGrid.addOrReplace(bMsisdn
		.add(new PatternValidator(Constants.REGEX_PHONE_NUMBERS))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	bAlertGrid.addOrReplace(new RequiredTextField<String>(
		"balanceAlert.templateName")
		.add(new PatternValidator(Constants.REGEX_TEMPLATE))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	bAlertGrid.addOrReplace(new CheckBox("balanceAlert.onlyTransition"));

	bAlertGrid.add(new LocalizableLookupDropDownChoice<String>(
		"balanceAlert.language", String.class, "languages", this,
		false, true).setNullValid(true).add(new ErrorIndicator()));

	bAlertGrid.add(new LocalizableLookupDropDownChoice<String>(
		"balanceAlert.country", String.class, "countries", this, false,
		true).setNullValid(true).add(new ErrorIndicator()));

	bAlertGrid.addOrReplace(new Button("cancelBalanceAlert") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		balanceAlert = null;
		bAlertGrid.setVisible(false);
	    };
	}.setDefaultFormProcessing(false));
	toggleButtons(bAlertGrid, mode);

	balanceAlertForm.addOrReplace(new AjaxLink("balanceAlertAddLink") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(AjaxRequestTarget target) {

		toggleButtons(bAlertGrid, "ADD");
		bAlertGrid.setVisible(true);
		target.addComponent(bAlertGrid);
		balanceAlertForm.clearInput();
		balanceAlert = null;
		target.addComponent(balanceAlertForm);

	    }
	});
	balanceAlertForm.addOrReplace(bAlertGrid);
	bAlertGrid.setVisible(false);
	return bAlertGrid;
    }

    private void getPaymentInstrumentID() {
	if (paymentInstrumentId == null) {
	    List<WalletEntry> walletList = getWalletEntryList(
		    getMobiliserWebSession().getCustomer().getId(), null,
		    Constants.PI_TYPE_DEFAULT_SVA);
	    if (PortalUtils.exists(walletList)) {
		paymentInstrumentId = walletList.get(0)
			.getPaymentInstrumentId();
	    }

	}
    }

    private void toggleButtons(final WebMarkupContainer bAlertGrid, String mode) {
	boolean visible;
	if ("EDIT".equalsIgnoreCase(mode)) {
	    visible = false;
	} else {
	    visible = true;
	}

	bAlertGrid.addOrReplace(new Button("addBalanceAlert") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		personalDataErrorMessages.setVisible(false);
		balanceAlertErrorMessages.setVisible(true);
		agentSettingErrorMessage.setVisible(false);
		loginDataErrorMessages.setVisible(false);
		BalanceAlert bAlert = getBalanceAlert();
		if (!balanceAlertErrorMessages.anyErrorMessage()) {
		    addBalanceAlert(bAlert);
		    balanceAlertErrorMessages = new FeedbackPanel(
			    "balanceAlertErrorMessages");

		    balanceAlertErrorMessages.setVisible(true);
		    this.getForm().addOrReplace(balanceAlertErrorMessages);
		    balanceAlert = null;
		    bAlertGrid.setVisible(false);
		    createBalanceAlertDataView(this.getForm());
		}
	    }
	}.setVisible(visible));

	bAlertGrid.addOrReplace(new Button("updateBalanceAlert") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		personalDataErrorMessages.setVisible(false);
		balanceAlertErrorMessages.setVisible(true);
		agentSettingErrorMessage.setVisible(false);
		loginDataErrorMessages.setVisible(false);
		BalanceAlert bAlert = getBalanceAlert();
		if (!balanceAlertErrorMessages.anyErrorMessage()) {
		    updateBalanceAlert(bAlert);
		    balanceAlertErrorMessages = new FeedbackPanel(
			    "balanceAlertErrorMessages");

		    balanceAlertErrorMessages.setVisible(true);
		    this.getForm().addOrReplace(balanceAlertErrorMessages);
		    balanceAlert = null;
		    bAlertGrid.setVisible(false);
		    createBalanceAlertDataView(this.getForm());
		}
	    };
	}.setVisible(!visible));

    }

    private List<BalanceAlert> getBalanceAlertfromDB(Long paymentInstrumentId) {
	try {
	    GetBalanceAlertsByPaymentInstrumentRequest getBalAlertReq = getNewMobiliserRequest(GetBalanceAlertsByPaymentInstrumentRequest.class);
	    getBalAlertReq.setPaymentInstrumentId(paymentInstrumentId);
	    GetBalanceAlertsByPaymentInstrumentResponseType getBalAlertResp = wsBalanceAlertClient
		    .getBalanceAlertByPaymentInstrument(getBalAlertReq);

	    if (!evaluateMobiliserResponse(getBalAlertResp))
		return null;

	    return getBalAlertResp.getBalanceAlerts();
	} catch (Exception e) {
	    error(getLocalizer().getString("ERROR.GET_BALANCE_ALERT_FAILURE",
		    this));
	    LOG.error("# An error occurred while getting Balance alert data.",
		    e);
	}

	return null;

    }

    private boolean validatePassword(PasswordTextField oldPassword,
	    PasswordTextField newPassword) {

	if (!PortalUtils.exists(customer.getPassword())
		&& !PortalUtils.exists(getConfirmPassword())) {
	    return false;
	} else {
	    newPassword.setRequired(true).add(

	    new ErrorIndicator());

	    oldPassword.setRequired(true).add(

	    new ErrorIndicator());
	    oldPassword.validate();
	    newPassword.validate();
	    if (oldPassword.hasErrorMessage() || newPassword.hasErrorMessage()) {
		return false;
	    }

	    return valedatePasswordStrength(getConfirmPassword(),
		    Constants.CREDENTIAL_TYPE_PASSWORD);
	}
    };

    private void deleteAgent() {
	LOG.debug("# AgentEditPage.deleteAgent");
	// set active flag to false and update customer
	try {
	    if (customer.getBlackListReason() != null) {
		if (customer.getBlackListReason() == Constants.PENDING_REG_MERCHANT_BLACKLSTREASON) {

		    deleteCustomer(getMobiliserWebSession().getCustomer()
			    .getId());
		    getMobiliserWebSession().info(
			    getLocalizer().getString("MESSAGE.AGENT_DELETED",
				    this));
		    PageParameters params = new PageParameters();
		    params.add("isFromTopMenu", "true");
		    setResponsePage(AgentEditPage.class, params);
		}
	    }

	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while activating the agent with Id {}",
		    customer.getId(), e);
	    personalDataErrorMessages.error(getLocalizer().getString(
		    "ERROR.DELETE_AGENT", this));
	}
    }

    private void changePassword(PasswordTextField oldPassword,
	    PasswordTextField newPassword) {
	boolean updateStatus = false;
	try {
	    if (validatePassword(oldPassword, newPassword)) {

		setCustomerCredential(customer.getId(), getConfirmPassword());
		getMobiliserWebSession().info(
			getLocalizer().getString(
				"MESSAGE.EDIT_AGENT_PASSWORD_UPDATED", this));

	    }
	    int oldBlockedStatus = getMobiliserWebSession().getDbBlrReason();
	    int newBlockedStatus = -1;

	    if (customer.getBlackListReason() != null) {

		newBlockedStatus = customer.getBlackListReason();
	    }

	    if (oldBlockedStatus == newBlockedStatus) {
		return;
	    } else {
		CustomerBean customerBean = getMobiliserWebSession()
			.getCustomer();

		customerBean.setBlackListReason(newBlockedStatus);
		updateStatus = updateCustomerDetail(customerBean);
		if (updateStatus) {
		    getMobiliserWebSession().setDbBlrReason(newBlockedStatus);
		    getMobiliserWebSession()
			    .info(getLocalizer()
				    .getString(
					    "MESSAGE.EDIT_AGENT_BLACKLIST_REASON_UPDATED",
					    this));
		    getMobiliserWebSession().setCustomer(customerBean);
		    customer = customerBean;
		    setEditMode(true);
		    checkButtonVisibality();
		} else {
		    customerBean.setBlackListReason(oldBlockedStatus);
		    getMobiliserWebSession().setDbBlrReason(oldBlockedStatus);
		    getMobiliserWebSession().setCustomer(customerBean);
		}
	    }

	} catch (Exception e) {
	    error(getLocalizer().getString(
		    "ERROR.EDIT_AGENT_BLACKLIST_REASON_FAILURE", this));
	    LOG.error("# An error occurred while updating Blacklist Reason.", e);
	}
    }

    private void activateAgent() {
	LOG.debug("# AgentEditPage.activateAgent()");
	boolean activateStatus = false;
	// setBlackListReason 0 and update
	try {
	    CustomerBean customerBean = getMobiliserWebSession().getCustomer();
	    customerBean
		    .setBlackListReason(Constants.DEFAULT_MERCHANT_BLACKLSTREASON);
	    activateStatus = updateCustomerDetail(customerBean);
	    if (activateStatus) {
		getMobiliserWebSession().info(
			getLocalizer().getString("MESSAGE.AGENT_ACTIVATED",
				this));
		customer.setBlackListReason(Constants.DEFAULT_MERCHANT_BLACKLSTREASON);
		customerBean = getMobiliserWebSession().getCustomer();
		customerBean
			.setBlackListReason(Constants.DEFAULT_MERCHANT_BLACKLSTREASON);
		getMobiliserWebSession().setCustomer(customerBean);
		setEditMode(true);
		checkButtonVisibality();
	    } else {
		customerBean
			.setBlackListReason(Constants.PENDING_REG_MERCHANT_BLACKLSTREASON);
		getMobiliserWebSession().setCustomer(customerBean);
	    }

	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred whilte activating the agent with Id "
			    + customer.getId(), e);
	    personalDataErrorMessages.error(getLocalizer().getString(
		    "ERROR.ACTIVATE_AGENT", this));
	}
    }

    private boolean equalContent(BalanceAlert oldBa, BalanceAlert newBa) {
	if (oldBa == null || newBa == null)
	    return oldBa == newBa;
	long oldId = oldBa.getId();
	long newId = newBa.getId();

	return oldId == newId
		&& oldBa.getThreshold() == newBa.getThreshold()
		&& ((oldBa.getEmails() == null && newBa.getEmails() == null) || (oldBa
			.getEmails() != null && newBa.getEmails() != null && oldBa
			.getEmails().equals(newBa.getEmails())))
		&& ((oldBa.getMsisdns() == null && newBa.getMsisdns() == null) || (oldBa
			.getMsisdns() != null && newBa.getMsisdns() != null && oldBa
			.getMsisdns().equals(newBa.getMsisdns())))
		&& ((oldBa.getTemplateName() == null && newBa.getTemplateName() == null) || (oldBa
			.getTemplateName() != null
			&& newBa.getTemplateName() != null && oldBa
			.getTemplateName().equals(newBa.getTemplateName())))
		&& ((oldBa.getCountry() == null && newBa.getCountry() == null) || (oldBa
			.getCountry() != null && newBa.getCountry() != null && oldBa
			.getCountry().equals(newBa.getCountry())))
		&& ((oldBa.getLanguage() == null && newBa.getLanguage() == null) || (oldBa
			.getLanguage() != null && newBa.getLanguage() != null && oldBa
			.getLanguage().equals(newBa.getLanguage())))
		&& oldBa.isOnlyTransition() == newBa.isOnlyTransition();
    }

    private void checkButtonVisibality() {
	isVisible = true;
	canActivate = false;
	deleteVisible = false;
	if (customer.getBlackListReason() != null) {
	    if (customer.getBlackListReason() == Constants.PENDING_REG_MERCHANT_BLACKLSTREASON) {
		isVisible = false;
		canActivate = true;
		deleteVisible = true;
	    }
	}
	if (!getMobiliserWebSession().hasPrivilege(
		Constants.PRIV_CUSTOMER_BLACKLIST)) {
	    isVisible = false;
	}
	if (!getMobiliserWebSession().hasPrivilege(
		Constants.PRIV_ACTIVATE_DESCENDANTS)) {
	    canActivate = false;
	}
	this.deleteButton.setVisible(deleteVisible);
	this.activateButton.setVisible(canActivate);
	this.blockedStatusChoice.setEnabled(isVisible);
    }

    private void createBalanceAlertDataView(final Form agentCreateForm) {
	final WebMarkupContainer balanceAlertListGrid = new WebMarkupContainer(
		"balanceAlertListGrid");
	agentCreateForm.addOrReplace(balanceAlertListGrid);
	dataProvider = new BalanceAlertDataProvider(WICKET_ID_pageable, this);
	balanceAlerList = new ArrayList<BalanceAlert>();
	final DataView<BalanceAlert> dataView = new DataView<BalanceAlert>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.getBalanceAlerts(getMobiliserWebSession());
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading balance alerts",
			    dple);
		    error(getLocalizer().getString("balanceAlert.list.failure",
			    this));
		}

		refreshTotalItemCount();
		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}
		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<BalanceAlert> item) {
		final BalanceAlert entry = item.getModelObject();
		balanceAlerList.add(entry);
		item.add(new Label(
			WICKET_ID_threshold,
			String.valueOf(PortalUtils.exists(entry.getThreshold()) ? convertAmountToString(entry
				.getThreshold()) : "")));

		item.add(new Label(WICKET_ID_onlyTransition, entry
			.isOnlyTransition() ? "Yes" : "No"));
		item.add(new Label(WICKET_ID_templateName, entry
			.getTemplateName()));
		Link editLink = new Link<BalanceAlert>(WICKET_ID_editLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			balanceAlert = (BalanceAlert) item.getModelObject();
			WebMarkupContainer bAlertGrid = new WebMarkupContainer(
				"balanceAlertGrid");
			bAlertGrid = createMarkupContainer(bAlertGrid,
				agentCreateForm, "EDIT");
			bAlertGrid.setVisible(true);
			agentCreateForm.addOrReplace(bAlertGrid);
		    }
		};

		item.add(editLink);

		Link removeLink = new Link<BalanceAlert>(WICKET_ID_removeLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			personalDataErrorMessages.setVisible(false);
			balanceAlertErrorMessages.setVisible(true);
			agentSettingErrorMessage.setVisible(false);
			loginDataErrorMessages.setVisible(false);
			BalanceAlert balAlart = (BalanceAlert) item
				.getModelObject();
			removeBalanceAlert(balAlart);
			WebMarkupContainer bAlertGrid = new WebMarkupContainer(
				"balanceAlertGrid");
			bAlertGrid = createMarkupContainer(bAlertGrid,
				agentCreateForm, "");
			bAlertGrid.setVisible(false);
			balanceAlert = null;
			agentCreateForm.addOrReplace(bAlertGrid);
			balanceAlertListGrid.setVisible(true);
		    }
		};
		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"balanceAlert.remove.confirm", this)
				+ "');"));
		item.add(removeLink);

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
	balanceAlertListGrid.addOrReplace(dataView);
	balanceAlertListGrid.addOrReplace(new OrderByBorder(
		WICKET_ID_orderByTemplateName, WICKET_ID_templateName,
		dataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the
		// page is
		// loading
		// and the sort is clicked (clicking the name header),
		// so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	balanceAlertListGrid.addOrReplace(new MultiLineLabel(
		WICKET_ID_noItemsMsg, getLocalizer().getString(
			"balance.alert.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString(
				"balance.alert.noItemsHelpMsg", this)) {
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
	balanceAlertListGrid.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	balanceAlertListGrid.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	balanceAlertListGrid.addOrReplace(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	balanceAlertListGrid.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));
	balanceAlertListGrid.setVisible(true);
    }

    public void addBalanceAlert(BalanceAlert newBA) {
	getPaymentInstrumentID();
	// Newly added Balance Alert Entry
	CreateBalanceAlertRequest balAlertReq = null;
	try {
	    balAlertReq = getNewMobiliserRequest(CreateBalanceAlertRequest.class);
	    newBA.setPaymentInstrumentId(paymentInstrumentId);

	    if (!PortalUtils.exists(newBA.getCountry())) {
		newBA.setCountry(null);
	    }
	    if (!PortalUtils.exists(newBA.getLanguage())) {
		newBA.setLanguage(null);
	    }
	    balAlertReq.setBalanceAlert(newBA);
	    CreateBalanceAlertResponse balAlertResp = wsBalanceAlertClient
		    .createBalanceAlert(balAlertReq);
	    if (!evaluateMobiliserResponse(balAlertResp))
		return;
	    getMobiliserWebSession().setBalanceAlertList(
		    getBalanceAlertfromDB(paymentInstrumentId));
	    balanceAlertErrorMessages.info(getLocalizer().getString(
		    "MESSAGE.EDIT_AGENT_BALANCE_ALERTS_SAVED", this));
	    LOG.info("# Successfully added Balance Alert data for[{}]",
		    balAlertResp.getBalanceAlertId());

	} catch (Exception e) {
	    balanceAlertErrorMessages.error(getLocalizer().getString(
		    "ERROR.AGENT_ADD_BALANCE_ALERTS_FAILURE", this));
	    LOG.error("# An error occurred while adding Balance alert data.", e);
	}

    }

    private void updateBalanceAlert(BalanceAlert updateBA) {

	getPaymentInstrumentID();

	// Existing Balance Alert Entry changed
	UpdateBalanceAlertRequest updateBalReq = null;
	try {
	    updateBalReq = getNewMobiliserRequest(UpdateBalanceAlertRequest.class);
	    updateBA.setPaymentInstrumentId(paymentInstrumentId);

	    if (!PortalUtils.exists(updateBA.getCountry())) {
		updateBA.setCountry(null);
	    }
	    if (!PortalUtils.exists(updateBA.getLanguage())) {
		updateBA.setLanguage(null);
	    }
	    updateBalReq.setBalanceAlert(updateBA);
	    UpdateBalanceAlertResponse updateBalResp = wsBalanceAlertClient
		    .updateBalanceAlert(updateBalReq);
	    if (!evaluateMobiliserResponse(updateBalResp))
		return;
	    LOG.info("# Successfully updated Balance Alert data for[{}]",
		    updateBA.getId());
	    getMobiliserWebSession().setBalanceAlertList(
		    getBalanceAlertfromDB(paymentInstrumentId));
	    balanceAlertErrorMessages.info(getLocalizer().getString(
		    "MESSAGE.EDIT_AGENT_BALANCE_ALERTS_UPDATED", this));
	} catch (Exception e) {
	    error(getLocalizer().getString(
		    "ERROR.AGENT_ADD_BALANCE_ALERTS_FAILURE", this));
	    LOG.error("# An error occurred while updating Balance alert data.",
		    e);
	}

    }

    public void removeBalanceAlert(BalanceAlert oldBA) {
	getPaymentInstrumentID();
	DeleteBalanceAlertRequest deleteBalReq = null;
	try {
	    deleteBalReq = getNewMobiliserRequest(DeleteBalanceAlertRequest.class);
	    deleteBalReq.setBalanceAlertId(oldBA.getId());
	    DeleteBalanceAlertResponse deleteBalResp = wsBalanceAlertClient
		    .deleteBalanceAlert(deleteBalReq);
	    if (!evaluateMobiliserResponse(deleteBalResp))
		return;
	    getMobiliserWebSession().setBalanceAlertList(
		    getBalanceAlertfromDB(paymentInstrumentId));
	    balanceAlertErrorMessages.info(getLocalizer().getString(
		    "MESSAGE.EDIT_AGENT_BALANCE_ALERTS_REMOVED", this));
	    LOG.info("# Successfully deleted Balance Alert data for[{}]",
		    oldBA.getId());
	} catch (Exception e) {
	    balanceAlertErrorMessages.error(getLocalizer().getString(
		    "ERROR.AGENT_DELETE_BALANCE_ALERTS_FAILURE", this));
	    LOG.error("# An error occurred while deleting Balance alert data.",
		    e);
	}

    }

    public void setCustomer(CustomerBean customer) {
	this.customer = customer;
    }

    public CustomerBean getCustomer() {
	return customer;
    }

    public List<BalanceAlert> getBalanceAlerts() {
	return balanceAlerts;
    }

    public void setBalanceAlerts(List<BalanceAlert> balanceAlerts) {
	this.balanceAlerts = balanceAlerts;
    }

    public BalanceAlert getBalanceAlert() {
	return balanceAlert;
    }

    public void setBalanceAlert(BalanceAlert balanceAlert) {
	this.balanceAlert = balanceAlert;
    }

    public String getRoleDescription() {
	return roleDescription;
    }

    public void setConfirmPassword(String confirmPassword) {
	this.confirmPassword = confirmPassword;
    }

    public String getConfirmPassword() {
	return confirmPassword;
    }

    public void setLimitClass(LimitClass limitClass) {
	this.limitClass = limitClass;
    }

    public LimitClass getLimitClass() {
	return limitClass;
    }

    public void setEditMode(boolean editMode) {
	this.editMode = editMode;
    }

    public boolean isEditMode() {
	return editMode;
    }

}
