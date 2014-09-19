package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.ComponentTag;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteCustomerNetworkRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteCustomerNetworkResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerNetwork;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.CustomerNetworkDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_FRIENDS_LIST)
public class FriendsListPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(FriendsListPage.class);

    // Data Model for table list
    private CustomerNetworkDataProvider dataProvider;

    List<CustomerNetwork> selectedFriends = new ArrayList<CustomerNetwork>();
    List<CustomerNetwork> friendsList;

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
    private static final String WICKET_ID_orderByNickname = "orderByNickname";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_selected = "selected";
    private static final String WICKET_ID_nickname = "nickname";
    private static final String WICKET_ID_name = "name";
    private static final String WICKET_ID_msisdn = "msisdn";
    private static final String WICKET_ID_editAction = "editAction";
    private static final String WICKET_ID_sendMoneyAction = "sendMoneyAction";
    private static final String WICKET_ID_topupAction = "topupAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public FriendsListPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public FriendsListPage(final PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Form<?> form = new Form("friendsListForm",
		new CompoundPropertyModel<FriendsListPage>(this));

	add(form);

	form.add(new FeedbackPanel("errorMessages"));

	createFriendsListDataView(form);
    }

    @Override
    protected Class getActiveMenu() {
	return this.getClass();
    }

    private void handleRemove() {
	if (!PortalUtils.exists(getSelectedFriends())) {
	    error(getLocalizer().getString(
		    "friendsList.nothing.selected.remove", this));
	    return;
	}
	CustomerNetwork tempFriend = null;
	try {
	    DeleteCustomerNetworkRequest request;
	    DeleteCustomerNetworkResponse response;

	    for (CustomerNetwork friend : getSelectedFriends()) {
		tempFriend = friend;
		request = getNewMobiliserRequest(DeleteCustomerNetworkRequest.class);
		request.setCustomerNetworkId(tempFriend.getId());
		response = wsCustNetworkClient.deleteCustomerNetwork(request);
		if (!evaluateMobiliserResponse(response)) {
		    LOG.warn("# An error occurred while deleting from the friends list");
		    return;
		}
	    }
	    getSelectedFriends().clear();

	} catch (Exception e) {
	    if (tempFriend != null)
		LOG.error(
			"# An error occurred while deleting friend [{}] from the friends list",
			tempFriend.getNickname(), e);

	    error(getLocalizer().getString("friendsList.remove.error", this));
	}

	setResponsePage(this.getClass());
    };

    public List<CustomerNetwork> getSelectedFriends() {
	return selectedFriends;
    }

    public boolean isSelected(CustomerNetwork entry) {
	return selectedFriends.contains(entry);
    }

    public void setSelectedFriends(List<CustomerNetwork> selectedFriends) {
	this.selectedFriends = selectedFriends;
    }

    protected void editFriend(CustomerNetwork friend) {
	setResponsePage(new AddFriendPage(friend, friendsList));
    }

    protected void sendMoney(CustomerNetwork friend) {
	setResponsePage(new SendMoneyFriendPage(getMsisdn(friend.getChildId())));
    }

    protected void performTopUp(CustomerNetwork friend) {
	setResponsePage(new AirTimeTopupFriendPage(
		getMsisdn(friend.getChildId())));
    }

    private String getMsisdn(Long custId) {
	try {
	    Identification ident = getCustomerIdentification(
		    custId.longValue(),
		    Integer.valueOf(Constants.IDENT_TYPE_MSISDN));
	    return ident.getIdentification();
	} catch (Exception e) {
	    LOG.error("# Error while getting customer's MSISDN", e);
	}
	return "";
    }

    private void createFriendsListDataView(Form form) {

	dataProvider = new CustomerNetworkDataProvider(WICKET_ID_nickname, this);

	friendsList = new ArrayList<CustomerNetwork>();

	final Long customerId = getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId();

	form.add(new Button("addFriend") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(new AddFriendPage(null, friendsList));
	    };
	});

	final DataView<CustomerNetwork> dataView = new DataView<CustomerNetwork>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadFriendsAndFamilyList(customerId,
			    forceReload);
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
			    "# An error occurred while loading friends and family list",
			    dple);
		    error(getLocalizer().getString("friendsList.load.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<CustomerNetwork> item) {

		final CustomerNetwork entry = item.getModelObject();

		friendsList.add(entry);

		// Select box
		AjaxCheckBox selectedCheckBox = new AjaxCheckBoxImpl(
			WICKET_ID_selected, new Model(isSelected(entry)), entry);

		selectedCheckBox.setOutputMarkupId(true);
		selectedCheckBox.setMarkupId(WICKET_ID_selected + rowIndex++);
		item.add(selectedCheckBox);

		Long friendCustId = Long.valueOf(entry.getChildId());

		Customer customer = getCustomerByIdentification(
			Constants.IDENT_TYPE_CUST_ID, friendCustId.toString());

		item.add(new Label(WICKET_ID_nickname, entry.getNickname()));

		item.add(new Label(WICKET_ID_name,
			PortalUtils.exists(customer) ? customer
				.getDisplayName() : ""));

		item.add(new Label(WICKET_ID_msisdn, getMsisdn(friendCustId)));

		// Edit Action
		Link<CustomerNetwork> editLink = new Link<CustomerNetwork>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			CustomerNetwork entry = (CustomerNetwork) getModelObject();
			editFriend(entry);
		    }
		};
		item.add(editLink);

		// Send Money Action
		Link sendMoneyLink = new Link<CustomerNetwork>(
			WICKET_ID_sendMoneyAction, item.getModel()) {
		    @Override
		    public void onClick() {
			CustomerNetwork entry = (CustomerNetwork) getModelObject();
			sendMoney(entry);
			forceReload = true;
		    }
		};
		item.add(sendMoneyLink);

		// Topup Action
		Link topupLink = new Link<CustomerNetwork>(
			WICKET_ID_topupAction, item.getModel()) {
		    @Override
		    public void onClick() {
			CustomerNetwork entry = (CustomerNetwork) getModelObject();
			performTopUp(entry);
			forceReload = true;
		    }
		};
		item.add(topupLink);

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

	    class AjaxCheckBoxImpl extends AjaxCheckBox {

		private final CustomerNetwork entry;

		public AjaxCheckBoxImpl(String id, IModel<Boolean> model,
			CustomerNetwork entry) {
		    super(id, model);
		    this.entry = entry;
		}

		@Override
		public boolean isEnabled() {
		    return true;
		}

		@Override
		protected void onComponentTag(final ComponentTag tag) {
		    super.onComponentTag(tag);
		    if (getModelObject()) {
			tag.put("checked", "checked");
		    }
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
		    boolean checkBoxSelected = getModelObject();
		    if (checkBoxSelected) {
			LOG.info("Added {} to deletion list", entry.getId());
			selectedFriends.add(entry);
		    } else {
			LOG.info("Removed {} from deletion list", entry.getId());
			selectedFriends.remove(entry);
		    }
		}
	    }

	};

	dataView.setItemsPerPage(10);
	form.add(dataView);

	form.add(new OrderByBorder(WICKET_ID_orderByNickname,
		WICKET_ID_nickname, dataProvider) {
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

	Button removeButton = new Button("removeFriend") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleRemove();
	    };

	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return super.isVisible();
		} else {
		    return false;
		}
	    }

	}.setDefaultFormProcessing(false);

	removeButton.add(new SimpleAttributeModifier("onclick",
		"return confirm('"
			+ getLocalizer().getString(
				"friendsList.remove.confirm", this) + "');"));

	form.add(removeButton);

	form.add(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer()
		.getString("friendsList.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString("friendsList.addFriendHelp", this)) {
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
	form.add(new CustomPagingNavigator(WICKET_ID_navigator, dataView));

	form.add(new Label(WICKET_ID_totalItems, new PropertyModel<String>(
		this, "totalItemString")));

	form.add(new Label(WICKET_ID_startIndex, new PropertyModel(this,
		"startIndex")));

	form.add(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));
    }
}