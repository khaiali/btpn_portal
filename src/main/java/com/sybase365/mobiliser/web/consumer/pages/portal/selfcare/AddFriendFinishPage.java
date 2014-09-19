package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateCustomerNetworkRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateCustomerNetworkResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateCustomerNetworkRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateCustomerNetworkResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerNetwork;
import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;

@AuthorizeInstantiation(Constants.PRIV_FRIENDS_LIST)
public class AddFriendFinishPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddFriendFinishPage.class);

    private CustomerNetwork friend;
    private Boolean isAdd;
    private String msisdn;
    private long friendId;
    private String friendName;

    public AddFriendFinishPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public AddFriendFinishPage(final PageParameters parameters) {
	super(parameters);
    }

    public AddFriendFinishPage(long friendId, final CustomerNetwork friend,
	    String msisdn, String friendName, Boolean isAdd) {
	super();
	this.friend = friend;
	this.friendId = friendId;
	this.msisdn = msisdn;
	this.friendName = friendName;
	this.isAdd = isAdd;
	initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {
	Form<?> form = new Form("addFriendFinishForm",
		new CompoundPropertyModel<AddFriendFinishPage>(this));
	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<AddFriendFinishPage>(this));

	form.add(new FeedbackPanel("errorMessages"));

	labels.add(new Label("friendName"));
	labels.add(new Label("msisdn"));

	form.add(labels);

	WebMarkupContainer msisdnDiv = new WebMarkupContainer("msisdnDiv");
	msisdnDiv.add(new Label("friend.nickname"));

	if (isAdd) {
	    msisdnDiv.setVisible(false);
	    form.add(new Button("continue") {
		private static final long serialVersionUID = 1L;

		@Override
		public void onSubmit() {
		    addFriend();
		    setResponsePage(FriendsListPage.class);
		};
	    }.setDefaultFormProcessing(false));

	} else {
	    msisdnDiv.setVisible(true);

	    form.add(new Button("continue") {
		private static final long serialVersionUID = 1L;

		@Override
		public void onSubmit() {
		    editFriend();
		    setResponsePage(FriendsListPage.class);
		};
	    }.setDefaultFormProcessing(false));

	}
	labels.add(msisdnDiv);
	add(form);
    }

    @Override
    protected Class getActiveMenu() {
	return FriendsListPage.class;
    }

    private void editFriend() {
	com.sybase365.mobiliser.util.tools.wicketutils.security.Customer loggedInCustomer = ((BaseWebSession) getWebSession())
		.getLoggedInCustomer();

	try {
	    UpdateCustomerNetworkRequest request;
	    UpdateCustomerNetworkResponse response;
	    this.getFriend().setNickname(this.friend.getNickname());
	    request = getNewMobiliserRequest(UpdateCustomerNetworkRequest.class);
	    request.setCustomerNetwork(friend);
	    response = wsCustNetworkClient.updateCustomerNetwork(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while updating the consumer from friends list");
		return;
	    }

	} catch (Exception e) {
	    LOG
		    .error(
			    "# An error occurred while updating the consumer from friends list",
			    e);
	    error(getLocalizer().getString("update.friend.error", this));
	}

    }

    private void addFriend() {
	com.sybase365.mobiliser.util.tools.wicketutils.security.Customer loggedInCustomer = ((BaseWebSession) getWebSession())
		.getLoggedInCustomer();

	try {
	    PhoneNumber pn = new PhoneNumber(this.msisdn, getConfiguration()
		    .getCountryCode());
	    this.msisdn = pn.getInternationalFormat();
	    CustomerNetwork friendObj = new CustomerNetwork();
	    friendObj.setNickname(this.friend.getNickname());
	    friendObj.setParentId(loggedInCustomer.getCustomerId());
	    friendObj.setChildId(friendId);
	    friendObj.setType(Constants.CUSTOMER_NETWORK_TYPE_F_AND_F);
	    friendObj.setStatus(Constants.CUSTOMER_NETWORK_STATUS_APPROVED);
	    friendObj.setActive(Boolean.TRUE);

	    CreateCustomerNetworkRequest request = getNewMobiliserRequest(CreateCustomerNetworkRequest.class);
	    request.setCustomerNetwork(friendObj);
	    CreateCustomerNetworkResponse response = wsCustNetworkClient
		    .createCustomerNetwork(request);
	    if (!evaluateMobiliserResponse(response))
		return;
	} catch (Exception e) {
	    LOG.error("# Error while Adding a friend", e);
	    error("Error while Adding a friend");
	}

    }

    public CustomerNetwork getFriend() {
	return friend;
    }
}
