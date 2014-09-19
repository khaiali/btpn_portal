package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerNetwork;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.LevenshteinDistance;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_FRIENDS_LIST)
public class AddFriendPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddFriendPage.class);

    private CustomerNetwork friend;
    private List<CustomerNetwork> friendsList;
    private String lastName;
    private String msisdn;
    private Boolean isAdd;
    private String title = getLocalizer().getString("addFriend.title", this);

    public AddFriendPage(final CustomerNetwork friend,
	    final List<CustomerNetwork> friendsList) {
	super();
	if (friend == null)
	    isAdd = Boolean.TRUE;
	else {
	    title = getLocalizer().getString("editFriend.title", this);
	    isAdd = Boolean.FALSE;
	    this.lastName = fetchLastName(friend.getChildId());
	    this.msisdn = fetchMsisdn(friend.getChildId());
	}
	this.friend = friend;
	this.friendsList = friendsList;
	initPageComponents();
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getMsisdn() {
	return msisdn;
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

    @SuppressWarnings({ "unchecked", "serial" })
    protected void initPageComponents() {
	Form<?> form = new Form("addFriendForm",
		new CompoundPropertyModel<AddFriendPage>(this));

	RequiredTextField<String> msisdn = new RequiredTextField<String>(
		"msisdn");

	msisdn.add(new PatternValidator(Constants.REGEX_PHONE_NUMBER))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator());

	if (!isAdd)
	    msisdn.setEnabled(Boolean.FALSE);

	form.add(msisdn);

	RequiredTextField<String> lastName = new RequiredTextField<String>(
		"lastName");

	lastName.add(new PatternValidator(Constants.REGEX_FIRSTNAME))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator());

	if (!isAdd)
	    lastName.setEnabled(Boolean.FALSE);

	form.add(lastName);

	RequiredTextField<String> nickName = new RequiredTextField<String>(
		"friend.nickname");
	nickName.add(new PatternValidator(Constants.REGEX_FRIEND_NICKNAME))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator());
	form.add(nickName);

	WebMarkupContainer addFriendDiv = new WebMarkupContainer("addFriend");
	WebMarkupContainer editFriendDiv = new WebMarkupContainer("editFriend");

	if (friend != null) {
	    addFriendDiv.setVisible(false);
	    editFriendDiv.setVisible(true);

	} else {
	    editFriendDiv.setVisible(false);
	}
	form.add(editFriendDiv);

	form.add(addFriendDiv);

	addFriendDiv.add(new Button("add") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		addFriend();
	    };
	});

	addFriendDiv.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false));

	editFriendDiv.add(new Button("edit") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		editFriend();
	    };
	});

	editFriendDiv.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false));

	form.add(new FeedbackPanel("errorMessages"));

	add(form);

	add(new Label("addFriend.title", title));
    }

    @Override
    protected Class getActiveMenu() {
	return FriendsListPage.class;
    }

    private void handleBack() {
	setResponsePage(FriendsListPage.class);
    }

    private void editFriend() {
	LOG.debug("# AddFriendPage.editFriend()");
	PhoneNumber pn = new PhoneNumber(this.getMsisdn(), getConfiguration()
		.getCountryCode());

	Customer customer = getCustomerByIdentification(
		Constants.IDENT_TYPE_MSISDN, pn.getInternationalFormat());
	boolean isValidUser = false;
	Address address;
	if (PortalUtils.exists(customer)) {
	    address = getAddressByCustomer(customer.getId().longValue());
	    if (LevenshteinDistance.checkSimilarityOfStrings(address
		    .getLastName(), getLastName(), getConfiguration()
		    .getSimilarNamesMaxErrors(), getConfiguration()
		    .getSimilarNamesMinPercentage())) {
		isValidUser = true;
	    }

	}
	if (!isValidUser) {
	    LOG.warn("A user with the entered MSISDN and last name does not exist");

	    error(getLocalizer().getString(
		    "addFriend.customer.not.found.error", this));
	    return;

	}

	boolean isNickAlreadyUsed = nickNameInUse(getFriend().getId(),
		getFriend().getNickname());

	if (isNickAlreadyUsed) {
	    info(getLocalizer().getString("addFriend.duplicate.nickname", this));
	    return;
	}
	setResponsePage(new AddFriendFinishPage(customer.getId().longValue(),
		friend, this.getMsisdn(), customer.getDisplayName(), isAdd));

    }

    private boolean nickNameInUse(Long id, String nickName) {
	LOG.debug("# AddFriendPage.nickNameInUse()");
	for (CustomerNetwork frnd : friendsList) {
	    if (frnd.getNickname().equals(nickName) && frnd.getId() != id) {
		return true;
	    }
	}

	for (WalletEntry account : getBankAccounts()) {
	    if (account.getAlias().equals(nickName)) {
		return true;
	    }

	}
	return false;

    }

    private List<WalletEntry> getBankAccounts() {
	com.sybase365.mobiliser.util.tools.wicketutils.security.Customer customer = getWebSession()
		.getLoggedInCustomer();
	LOG.debug("# AddFriendPage.getBankAccounts()");
	List<WalletEntry> walletEntries = new ArrayList<WalletEntry>();
	walletEntries = getWalletEntryList(customer.getCustomerId(),
		Constants.PIS_CLASS_FILTER_BANK_ACCOUNT,
		Constants.PI_TYPE_DEFAULT_BA);

	if (walletEntries == null) {
	    LOG.debug("# An error occurred while retrieving bank accounts for duplicate nick name check  ");
	    return null;
	}
	LOG.debug("# Successfully fetched bank accounts");

	return walletEntries;

    }

    private void addFriend() {
	LOG.debug("# AddFriendPage.addFriend()");
	PhoneNumber pn = new PhoneNumber(this.getMsisdn(), getConfiguration()
		.getCountryCode());
	Customer customer = getCustomerByIdentification(
		Constants.IDENT_TYPE_MSISDN, pn.getInternationalFormat());

	if (!PortalUtils.exists(customer)) {
	    LOG.warn("A user with the entered MSISDN does not exist");

	    error(getLocalizer().getString(
		    "addFriend.customer.not.found.error", this));
	    return;

	}

	for (CustomerNetwork tempFriend : friendsList) {
	    if (tempFriend.getChildId() == customer.getId()) {

		LOG.debug("A user with the entered MSISDN already exists in the friends list");

		error(getLocalizer().getString(
			"addFriend.duplicate.friend.error", this));
		return;

	    }
	}

	Address address = getAddressByCustomer(customer.getId().longValue());
	if (!PortalUtils.exists(address)) {
	    error(getLocalizer().getString(
		    "addFriend.customer.address.fetch.error", this));
	    return;
	}
	if (!LevenshteinDistance.checkSimilarityOfStrings(
		address.getLastName(), getLastName(), getConfiguration()
			.getSimilarNamesMaxErrors(), getConfiguration()
			.getSimilarNamesMinPercentage())) {
	    LOG.debug("A user with the entered last name does not exist");

	    error(getLocalizer().getString(
		    "addFriend.customer.not.found.error", this));
	    return;
	}

	boolean isNickAlreadyUsed = nickNameInUse(getFriend().getId(),
		getFriend().getNickname());

	if (isNickAlreadyUsed) {
	    LOG.debug("Friend or bank account with the entered nick name already exisits");
	    error(getLocalizer()
		    .getString("addFriend.duplicate.nickname", this));
	    return;
	}

	setResponsePage(new AddFriendFinishPage(customer.getId(), friend,
		this.getMsisdn(), customer.getDisplayName(), isAdd));
    }

    public void setFriend(CustomerNetwork friend) {
	this.friend = friend;
    }

    public CustomerNetwork getFriend() {
	return this.friend;

    }

    private String fetchLastName(long customerId) {
	LOG.debug("# AddFriendPage.fetchLastName()");
	return getCustomerAddress(customerId).getLastName();
    }

    private String fetchMsisdn(long customerId) {
	LOG.debug("# AddFriendPage.fetchMsisdn()");
	return getIdentificationByCustomer(customerId,
		Constants.IDENT_TYPE_MSISDN).getIdentification();
    }
}
