package com.sybase365.mobiliser.web.distributor.pages.selfcare;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.framework.contract.v5_0.base.MoneyAmount;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.BankAccount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.CreditCard;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.SVA;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.WalletEntryDataProvider;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_MERCHANT_LOGIN)
public class AgentDataPage extends SelfCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AgentDataPage.class);

    List<WalletEntry> walletEntries;

    // Data Models for table lists
    private WalletEntryDataProvider baDataProvider;
    List<WalletEntry> baList;
    private String baTotalItemString = null;
    private int baStartIndex = 0;
    private int baEndIndex = 0;

    private WalletEntryDataProvider ccDataProvider;
    List<WalletEntry> ccList;
    private String ccTotalItemString = null;
    private int ccStartIndex = 0;
    private int ccEndIndex = 0;

    private WalletEntryDataProvider svaDataProvider;
    List<WalletEntry> svaList;
    private String svaTotalItemString = null;
    private int svaStartIndex = 0;
    private int svaEndIndex = 0;

    private boolean forceReload = true;

    private static final String WICKET_ID_baNavigator = "baNavigator";
    private static final String WICKET_ID_baTotalItems = "baTotalItems";
    private static final String WICKET_ID_baStartIndex = "baStartIndex";
    private static final String WICKET_ID_baEndIndex = "baEndIndex";
    private static final String WICKET_ID_baPageable = "baPageable";
    private static final String WICKET_ID_baId = "baId";
    private static final String WICKET_ID_baAccountNumber = "baAccountNumber";
    private static final String WICKET_ID_baBankCode = "baBankCode";
    private static final String WICKET_ID_baBranchCode = "baBranchCode";
    private static final String WICKET_ID_baAccountHolderName = "baAccountHolderName";

    private static final String WICKET_ID_ccNavigator = "ccNavigator";
    private static final String WICKET_ID_ccTotalItems = "ccTotalItems";
    private static final String WICKET_ID_ccStartIndex = "ccStartIndex";
    private static final String WICKET_ID_ccEndIndex = "ccEndIndex";
    private static final String WICKET_ID_ccPageable = "ccPageable";
    private static final String WICKET_ID_ccId = "ccId";
    private static final String WICKET_ID_ccCardNumber = "ccCardNumber";
    private static final String WICKET_ID_ccCardHolderName = "ccCardHolderName";
    private static final String WICKET_ID_ccCardType = "ccCardType";
    private static final String WICKET_ID_ccCardExpiry = "ccCardExpiry";

    private static final String WICKET_ID_svaNavigator = "svaNavigator";
    private static final String WICKET_ID_svaTotalItems = "svaTotalItems";
    private static final String WICKET_ID_svaStartIndex = "svaStartIndex";
    private static final String WICKET_ID_svaEndIndex = "svaEndIndex";
    private static final String WICKET_ID_svaId = "svaId";
    private static final String WICKET_ID_svaAccountBalance = "svaAccountBalance";
    private static final String WICKET_ID_svaPageable = "svaPageable";

    private Address address;

    public AgentDataPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public AgentDataPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new FeedbackPanel("errorMessages"));
	address = getCustomerAddress(getMobiliserWebSession()
		.getLoggedInCustomer().getCustomerId());
	WebMarkupContainer addressDiv = new WebMarkupContainer("addressDiv",
		new CompoundPropertyModel<AgentDataPage>(this));
	addBasicAgentData();
	if (address != null) {
	    WebMarkupContainer addressLeftDiv = addAddressLeftColumn();
	    addressDiv.add(addressLeftDiv);
	    WebMarkupContainer addressRightDiv = addAddressRightColumn();
	    addressDiv.add(addressRightDiv);
	    // Keep the addressDiv visible if either left or right div is
	    // visible
	    addressDiv.setVisible(addressLeftDiv.isVisible()
		    || addressRightDiv.isVisible());
	} else {
	    addressDiv.setVisible(false);
	}
	add(addressDiv);

	getCustomerPis();

	WebMarkupContainer bankAccountsTableDiv = new WebMarkupContainer(
		"bankAccountsTableDiv");

	createBankAccountListDataView(bankAccountsTableDiv);

	add(bankAccountsTableDiv);

	WebMarkupContainer creditCardsTableDiv = new WebMarkupContainer(
		"creditCardsTableDiv");

	createCreditCardListDataView(creditCardsTableDiv);

	add(creditCardsTableDiv);

	WebMarkupContainer svasTableDiv = new WebMarkupContainer("svasTableDiv");

	createSvaListDataView(svasTableDiv);

	add(svasTableDiv);
    }

    private WebMarkupContainer addAddressRightColumn() {
	WebMarkupContainer addressRightDiv = new WebMarkupContainer(
		"addressRightDiv", new CompoundPropertyModel<AgentDataPage>(
			this));
	if (address.getState() != null || address.getZip() != null
		|| address.getCountry() != null) {
	    WebMarkupContainer stateDiv = new WebMarkupContainer("stateDiv",
		    new CompoundPropertyModel<AgentDataPage>(this));
	    if (address.getState() != null) {
		stateDiv.add(new Label("address.state"));
	    } else {
		stateDiv.setVisible(false);
	    }
	    addressRightDiv.add(stateDiv);

	    WebMarkupContainer zipDiv = new WebMarkupContainer("zipDiv",
		    new CompoundPropertyModel<AgentDataPage>(this));
	    if (address.getZip() != null) {
		zipDiv.add(new Label("address.zip"));
	    } else {
		zipDiv.setVisible(false);
	    }
	    addressRightDiv.add(zipDiv);

	    WebMarkupContainer countryDiv = new WebMarkupContainer(
		    "countryDiv",
		    new CompoundPropertyModel<AgentDataPage>(this));
	    if (address.getCountry() != null) {
		countryDiv.add(new Label("address.country"));
	    } else {
		countryDiv.setVisible(false);
	    }
	    addressRightDiv.add(countryDiv);
	} else {
	    addressRightDiv.setVisible(false);
	}
	return addressRightDiv;
    }

    private WebMarkupContainer addAddressLeftColumn() {
	WebMarkupContainer addressLeftDiv = new WebMarkupContainer(
		"addressLeftDiv",
		new CompoundPropertyModel<AgentDataPage>(this));
	if (address.getStreet1() != null || address.getStreet2() != null
		|| address.getHouseNumber() != null
		|| address.getCity() != null) {
	    WebMarkupContainer street1Div = new WebMarkupContainer(
		    "street1Div",
		    new CompoundPropertyModel<AgentDataPage>(this));
	    if (address.getStreet1() != null) {
		street1Div.add(new Label("address.street1"));
	    } else {
		street1Div.setVisible(false);
	    }
	    addressLeftDiv.add(street1Div);

	    WebMarkupContainer street2Div = new WebMarkupContainer(
		    "street2Div",
		    new CompoundPropertyModel<AgentDataPage>(this));
	    if (address.getStreet2() != null) {
		street2Div.add(new Label("address.street2"));

	    } else {
		street2Div.setVisible(false);
	    }
	    addressLeftDiv.add(street2Div);

	    WebMarkupContainer houseNumberDiv = new WebMarkupContainer(
		    "houseNumberDiv", new CompoundPropertyModel<AgentDataPage>(
			    this));
	    if (address.getHouseNumber() != null) {
		houseNumberDiv.add(new Label("address.houseNumber"));
	    } else {
		houseNumberDiv.setVisible(false);
	    }
	    addressLeftDiv.add(houseNumberDiv);

	    WebMarkupContainer cityDiv = new WebMarkupContainer("cityDiv",
		    new CompoundPropertyModel<AgentDataPage>(this));
	    if (address.getCity() != null) {
		cityDiv.add(new Label("address.city"));
	    } else {
		cityDiv.setVisible(false);
	    }
	    addressLeftDiv.add(cityDiv);
	} else {
	    addressLeftDiv.setVisible(false);
	}
	return addressLeftDiv;
    }

    private void addBasicAgentData() {

	if (address != null && address.getFirstName() != null
		&& address.getLastName() != null) {
	    add(new Label("fullName",
		    (address.getFirstName() + ' ' + address.getLastName())));
	} else if (getMobiliserWebSession().getLoggedInCustomer()
		.getDisplayName() != null) {
	    add(new Label("fullName", getMobiliserWebSession()
		    .getLoggedInCustomer().getDisplayName()));
	} else {
	    add(new Label("fullName", getMobiliserWebSession()
		    .getLoggedInCustomer().getUsername()));
	}
	WebMarkupContainer companyDiv = new WebMarkupContainer("companyDiv",
		new CompoundPropertyModel<AgentDataPage>(this));
	if (address != null && address.getCompany() != null) {
	    companyDiv.add(new Label("address.company"));
	} else {
	    companyDiv.setVisible(false);
	}
	add(companyDiv);

	WebMarkupContainer positionDiv = new WebMarkupContainer("positionDiv",
		new CompoundPropertyModel<AgentDataPage>(this));
	if (address != null && address.getPosition() != null) {
	    positionDiv.add(new Label("address.position"));
	} else {
	    positionDiv.setVisible(false);
	}
	add(positionDiv);

	WebMarkupContainer emailDiv = new WebMarkupContainer("emailDiv",
		new CompoundPropertyModel<AgentDataPage>(this));
	if (address != null && address.getEmail() != null) {
	    emailDiv.add(new Label("address.email"));
	} else {
	    emailDiv.setVisible(false);
	}
	add(emailDiv);

    }

    private void getCustomerPis() {

	walletEntries = new ArrayList<WalletEntry>();
	baList = new ArrayList<WalletEntry>();
	ccList = new ArrayList<WalletEntry>();
	svaList = new ArrayList<WalletEntry>();

	if (getMobiliserWebSession().hasPrivilege(
		Constants.PRIV_VIEW_PAYMENT_INSTRUMENTS)) {
	    try {
		List<WalletEntry> piList = getWalletEntryList(
			Long.valueOf(getMobiliserWebSession()
				.getLoggedInCustomer().getCustomerId()), null,
			null);
		for (WalletEntry pi : piList) {
		    walletEntries.add(pi);
		    if (pi.getBankAccount() != null) {
			baList.add(pi);
		    } else if (pi.getCreditCard() != null) {
			ccList.add(pi);
		    } else if (pi.getSva() != null) {
			svaList.add(pi);
		    }
		}
	    } catch (Exception e) {
		error(getLocalizer().getString("agentData.getPis.error", this));
		LOG.error("# Error in getPaymentInstrumentsByCustomerReq() ", e);
	    }
	}

    }

    private void createBankAccountListDataView(final WebMarkupContainer parent) {

	baDataProvider = new WalletEntryDataProvider(WICKET_ID_baId, this);
	baDataProvider.setAllWalletEntries(walletEntries);

	baList = new ArrayList<WalletEntry>();

	final Long customerId = Long.valueOf(getMobiliserWebSession()
		.getLoggedInCustomer().getCustomerId());

	final DataView<WalletEntry> dataView = new DataView<WalletEntry>(
		WICKET_ID_baPageable, baDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    baDataProvider.loadBankAccountList(customerId, forceReload);

		    refreshTotalItemCount();

		    if (baDataProvider.size() > 0) {
			parent.setVisible(true);
		    } else {
			parent.setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading bank accounts list",
			    dple);
		    error(getLocalizer().getString(".remove.success", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<WalletEntry> item) {

		final WalletEntry entry = item.getModelObject();

		baList.add(entry);

		BankAccount ba = entry.getBankAccount();

		item.add(new Label(WICKET_ID_baId, ba.getId().toString()));

		item.add(new Label(WICKET_ID_baAccountNumber, ba
			.getAccountNumber()));

		item.add(new Label(WICKET_ID_baBankCode, ba.getBankCode()));

		item.add(new Label(WICKET_ID_baBranchCode, ba.getBranchCode()));

		item.add(new Label(WICKET_ID_baAccountHolderName, ba
			.getAccountHolderName()));

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
		baTotalItemString = Integer.toString(baDataProvider.size());
		int total = baDataProvider.size();
		if (total > 0) {
		    baStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    baEndIndex = baStartIndex + getItemsPerPage() - 1;
		    if (baEndIndex > total)
			baEndIndex = total;
		} else {
		    baStartIndex = 0;
		    baEndIndex = 0;
		}
	    }
	};

	dataView.setItemsPerPage(3);

	parent.add(dataView);

	// Navigator example: << < 1 2 > >>
	parent.add(new CustomPagingNavigator(WICKET_ID_baNavigator, dataView));

	parent.add(new Label(WICKET_ID_baTotalItems, new PropertyModel<String>(
		this, "baTotalItemString")));

	parent.add(new Label(WICKET_ID_baStartIndex, new PropertyModel(this,
		"baStartIndex")));

	parent.add(new Label(WICKET_ID_baEndIndex, new PropertyModel(this,
		"baEndIndex")));
    }

    private void createCreditCardListDataView(final WebMarkupContainer parent) {

	ccDataProvider = new WalletEntryDataProvider(WICKET_ID_ccId, this);
	ccDataProvider.setAllWalletEntries(walletEntries);

	ccList = new ArrayList<WalletEntry>();

	final Long customerId = getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId();

	final DataView<WalletEntry> dataView = new DataView<WalletEntry>(
		WICKET_ID_ccPageable, ccDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    ccDataProvider.loadCreditCardList(customerId, forceReload);

		    refreshTotalItemCount();

		    if (ccDataProvider.size() > 0) {
			parent.setVisible(true);
		    } else {
			parent.setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading credit cards list",
			    dple);
		    error(getLocalizer().getString(".remove.success", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<WalletEntry> item) {

		final WalletEntry entry = item.getModelObject();

		ccList.add(entry);

		CreditCard cc = entry.getCreditCard();

		item.add(new Label(WICKET_ID_ccId, cc.getId().toString()));

		item.add(new Label(WICKET_ID_ccCardNumber, cc
			.getDisplayNumber()));

		item.add(new Label(WICKET_ID_ccCardHolderName, cc
			.getCardHolderName()));

		item.add(new Label(WICKET_ID_ccCardType, getDisplayValue(
			String.valueOf(cc.getCardType()), "cardtypes")));

		Integer yearExpiry = cc.getMonthExpiry();
		Integer monthExpiry = cc.getYearExpiry();

		item.add(new Label(WICKET_ID_ccCardExpiry, monthExpiry + "/"
			+ yearExpiry));

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
		ccTotalItemString = Integer.toString(ccDataProvider.size());
		int total = ccDataProvider.size();
		if (total > 0) {
		    ccStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    ccEndIndex = ccStartIndex + getItemsPerPage() - 1;
		    if (ccEndIndex > total)
			ccEndIndex = total;
		} else {
		    ccStartIndex = 0;
		    ccEndIndex = 0;
		}
	    }
	};

	dataView.setItemsPerPage(3);

	parent.add(dataView);

	// Navigator example: << < 1 2 > >>
	parent.add(new CustomPagingNavigator(WICKET_ID_ccNavigator, dataView));

	parent.add(new Label(WICKET_ID_ccTotalItems, new PropertyModel<String>(
		this, "ccTotalItemString")));

	parent.add(new Label(WICKET_ID_ccStartIndex, new PropertyModel(this,
		"ccStartIndex")));

	parent.add(new Label(WICKET_ID_ccEndIndex, new PropertyModel(this,
		"ccEndIndex")));
    }

    private void createSvaListDataView(final WebMarkupContainer parent) {

	svaDataProvider = new WalletEntryDataProvider(
		WICKET_ID_svaAccountBalance, this);
	svaDataProvider.setAllWalletEntries(walletEntries);

	svaList = new ArrayList<WalletEntry>();

	final Long customerId = Long.valueOf(getMobiliserWebSession()
		.getLoggedInCustomer().getCustomerId());

	final DataView<WalletEntry> dataView = new DataView<WalletEntry>(
		WICKET_ID_svaPageable, svaDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    svaDataProvider.loadStoredValueAccountList(customerId,
			    forceReload);

		    refreshTotalItemCount();

		    if (svaDataProvider.size() > 0) {
			parent.setVisible(true);
		    } else {
			parent.setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading stored value accounts list",
			    dple);
		    error(getLocalizer().getString(".remove.success", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<WalletEntry> item) {

		final WalletEntry entry = item.getModelObject();

		svaList.add(entry);

		SVA sva = entry.getSva();

		item.add(new Label(WICKET_ID_svaId, sva.getId().toString()));

		long debitBalance = sva.getDebitBalance();
		long creditBalance = sva.getCreditBalance();

		MoneyAmount svaBalAmt = new MoneyAmount();
		svaBalAmt.setCurrency(sva.getCurrency());
		svaBalAmt.setValue(creditBalance - debitBalance);

		item.add(new Label(WICKET_ID_svaAccountBalance,
			convertAmountToStringWithCurrency(svaBalAmt)));

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
		svaTotalItemString = Integer.toString(svaDataProvider.size());
		int total = svaDataProvider.size();
		if (total > 0) {
		    svaStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    svaEndIndex = svaStartIndex + getItemsPerPage() - 1;
		    if (svaEndIndex > total)
			svaEndIndex = total;
		} else {
		    svaStartIndex = 0;
		    svaEndIndex = 0;
		}
	    }
	};

	dataView.setItemsPerPage(3);

	parent.add(dataView);

	// Navigator example: << < 1 2 > >>
	parent.add(new CustomPagingNavigator(WICKET_ID_svaNavigator, dataView));

	parent.add(new Label(WICKET_ID_svaTotalItems,
		new PropertyModel<String>(this, "svaTotalItemString")));

	parent.add(new Label(WICKET_ID_svaStartIndex, new PropertyModel(this,
		"svaStartIndex")));

	parent.add(new Label(WICKET_ID_svaEndIndex, new PropertyModel(this,
		"svaEndIndex")));
    }

    public Address getAddress() {
	return address;
    }

    public void setAddress(Address address) {
	this.address = address;
    }

}
