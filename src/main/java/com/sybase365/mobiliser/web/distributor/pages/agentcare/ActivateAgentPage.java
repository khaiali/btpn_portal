package com.sybase365.mobiliser.web.distributor.pages.agentcare;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.FindHierarchicalCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSetClass;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.CustomerBeanDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_AGENTS)
public class ActivateAgentPage extends BaseAgentCarePage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ActivateAgentPage.class);

    private CustomerBeanDataProvider dataProvider;

    private List<CustomerBean> agentsList;
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
    private static final String WICKET_ID_orderByFirstName = "orderByFirstName";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_LidLink = "LidLink";
    private static final String WICKET_ID_LuserName = "LuserName";
    private static final String WICKET_ID_Lcreated = "Lcreated";
    private static final String WICKET_ID_LparentId = "LparentId";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public ActivateAgentPage(final PageParameters parameters) {
	super(parameters);
	initPageComponents();
    }

    public ActivateAgentPage() {
	super();
	initPageComponents();
    }

    protected void initPageComponents() {
	add(new FeedbackPanel("errorMessages"));

	// Data view

	final WebMarkupContainer dataViewContainer = new WebMarkupContainer(
		"inactiveDataViewContainer");
	add(dataViewContainer);
	try {
	    createInactiveAgentDataView(dataViewContainer);
	} catch (Exception e) {
	    LOG.error("# Error in createActivateAgentDataView", e);
	    error(getLocalizer().getString("inactive.agent.load.error", this));
	}
    }

    protected void loadAgentDetails(CustomerBean agent) {
	try {
	    Address cAddress = getCustomerAddress(agent.getId());
	    agent.setAddress(Converter.getInstance().getAddressBeanFromAddress(
		    cAddress));
	    Identification ident = getCustomerIdentification(agent.getId(),
		    Constants.IDENT_TYPE_USERNAME);
	    agent.setUserName(ident.getIdentification());
	    List<LimitSetClass> limits = getLimitSetClassList(agent.getId(),
		    Constants.LIMIT_CUSTOMER_TYPE);
	    if (PortalUtils.exists(limits)) {
		agent.setLimitId(limits.get(0).getId());
		agent.setLimitClass(limits.get(0).getLimitClass());
	    }
	} catch (Exception e) {
	    LOG.error("# Error in getLimitSet of loadAgentDetails", e);
	    error(getLocalizer().getString("inactive.agent.load.error", this));
	}
	getMobiliserWebSession().setCustomer(agent);

	setResponsePage(new AgentEditPage("edit"));
    }

    private void createInactiveAgentDataView(
	    WebMarkupContainer dataViewContainer) throws Exception {
	final FindHierarchicalCustomerRequest request = getNewMobiliserRequest(FindHierarchicalCustomerRequest.class);
	request.setBlacklistReason(Long.parseLong(Integer
		.toString(Constants.PENDING_REG_MERCHANT_BLACKLSTREASON)));
	// if agent is logged in then use parent id to get the
	// children list
	// get the agent type id list acting on behalf of agents
	// from prefs
	String agentTypeIdsStr = getConfiguration().getMerchantAgentTypeIds();
	String[] agentTypeIdsStrArr = agentTypeIdsStr.split(",");
	for (String agentTypeId : agentTypeIdsStrArr) {
	    if (PortalUtils.exists(agentTypeId)
		    && Long.parseLong(agentTypeId.trim()) == getMobiliserWebSession()
			    .getLoggedInCustomer().getCustomerTypeId()) {
		request.setAgentId(getMobiliserWebSession()
			.getLoggedInCustomer().getParentId());
		continue;
	    }
	}
	dataProvider = new CustomerBeanDataProvider(WICKET_ID_pageable, this,
		true);
	agentsList = new ArrayList<CustomerBean>();
	final DataView<CustomerBean> dataView = new DataView<CustomerBean>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.findCustomer(request, forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while finding inactive customer",
			    dple);
		    error(getLocalizer().getString("inactive.agent.load.error",
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
	    protected void populateItem(final Item<CustomerBean> item) {
		final CustomerBean entry = item.getModelObject();

		agentsList.add(entry);
		Link idLink = new Link<CustomerBean>(WICKET_ID_LidLink,
			item.getModel()) {
		    @Override
		    public void onClick() {
			CustomerBean cModel = (CustomerBean) item
				.getModelObject();
			loadAgentDetails(cModel);
			setResponsePage(new AgentEditPage("edit"));
		    }
		};
		item.add(idLink);
		idLink.add(new Label(WICKET_ID_LuserName, entry.getUserName()));
		item.add(new Label(WICKET_ID_Lcreated, PortalUtils
			.getFormattedDateTime(entry.getCreated(),
				getMobiliserWebSession().getLocale(),
				getMobiliserWebSession().getTimeZone())));
		item.add(new Label(WICKET_ID_LparentId, String.valueOf(entry
			.getParentId())));

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
	dataView.setVisible(true);
	dataView.setItemsPerPage(10);
	dataViewContainer.addOrReplace(dataView);

	dataViewContainer.addOrReplace(new OrderByBorder(
		WICKET_ID_orderByFirstName, WICKET_ID_LuserName, dataProvider) {
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
	dataViewContainer.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("activateAgent.noItemsMsg", this)) {
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
}
