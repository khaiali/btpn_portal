package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.DeleteWalletEntryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.DeleteWalletEntryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdateWalletEntryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdateWalletEntryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PendingWalletEntry;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.WalletEntryDataProvider;
import com.sybase365.mobiliser.web.common.model.IManageAccountsViewer;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class AccountsPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AccountsPanel.class);

    MobiliserBasePage basePage;
    Long customerId;
    boolean pageViewedByAgent;
    IManageAccountsViewer accountsViewer;
    public List<WalletEntry> walletEntries;
    private Integer highestPriority;
    private Long primaryId;

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

    private WalletEntryDataProvider eaDataProvider;
    List<WalletEntry> eaList;
    private String eaTotalItemString = null;
    private int eaStartIndex = 0;
    private int eaEndIndex = 0;

    private WalletEntryDataProvider svaDataProvider;
    List<WalletEntry> svaList;
    private String svaTotalItemString = null;
    private int svaStartIndex = 0;
    private int svaEndIndex = 0;

    private boolean forceReload = true;

    private int rowIndex = 1;
    private static final String WICKET_ID_primary = "primary";

    private static final String WICKET_ID_baNavigator = "baNavigator";
    private static final String WICKET_ID_baTotalItems = "baTotalItems";
    private static final String WICKET_ID_baStartIndex = "baStartIndex";
    private static final String WICKET_ID_baEndIndex = "baEndIndex";
    private static final String WICKET_ID_baNoItemsMsg = "baNoItemsMsg";
    private static final String WICKET_ID_baPageable = "baPageable";
    private static final String WICKET_ID_baSelected = "baSelected";
    private static final String WICKET_ID_baNickname = "baNickname";
    private static final String WICKET_ID_baAccountNumber = "baAccountNumber";
    private static final String WICKET_ID_baBankCode = "baBankCode";
    private static final String WICKET_ID_baBankStatus = "baBankStatus";
    private static final String WICKET_ID_baEditAction = "baEditAction";
    private static final String WICKET_ID_baRemoveAction = "baRemoveAction";
    private static final String WICKET_ID_baApproveAction = "baApproveAction";
    private static final String WICKET_ID_baOrderByNickname = "baOrderByNickname";

    private static final String WICKET_ID_ccNavigator = "ccNavigator";
    private static final String WICKET_ID_ccTotalItems = "ccTotalItems";
    private static final String WICKET_ID_ccStartIndex = "ccStartIndex";
    private static final String WICKET_ID_ccEndIndex = "ccEndIndex";
    private static final String WICKET_ID_ccNoItemsMsg = "ccNoItemsMsg";
    private static final String WICKET_ID_ccPageable = "ccPageable";
    private static final String WICKET_ID_ccSelected = "ccSelected";
    private static final String WICKET_ID_ccNickname = "ccNickname";
    private static final String WICKET_ID_ccOrderByNickname = "ccOrderByNickname";
    private static final String WICKET_ID_ccCardType = "ccCardType";
    private static final String WICKET_ID_ccCardNumber = "ccCardNumber";
    private static final String WICKET_ID_ccEditAction = "ccEditAction";
    private static final String WICKET_ID_ccApproveAction = "ccApproveAction";
    private static final String WICKET_ID_ccRemoveAction = "ccRemoveAction";

    private static final String WICKET_ID_eaNavigator = "eaNavigator";
    private static final String WICKET_ID_eaTotalItems = "eaTotalItems";
    private static final String WICKET_ID_eaStartIndex = "eaStartIndex";
    private static final String WICKET_ID_eaEndIndex = "eaEndIndex";
    private static final String WICKET_ID_eaNoItemsMsg = "eaNoItemsMsg";
    private static final String WICKET_ID_eaPageable = "eaPageable";
    private static final String WICKET_ID_eaSelected = "eaSelected";
    private static final String WICKET_ID_eaAlias = "eaAlias";
    private static final String WICKET_ID_eaId1 = "eaId1";
    private static final String WICKET_ID_eaId2 = "eaId2";
    private static final String WICKET_ID_eaEditAction = "eaEditAction";
    private static final String WICKET_ID_eaRemoveAction = "eaRemoveAction";
    private static final String WICKET_ID_eaApproveAction = "eaApproveAction";
    private static final String WICKET_ID_eaOrderByAlias = "eaOrderByAlias";

    private static final String WICKET_ID_svaNavigator = "svaNavigator";
    private static final String WICKET_ID_svaTotalItems = "svaTotalItems";
    private static final String WICKET_ID_svaStartIndex = "svaStartIndex";
    private static final String WICKET_ID_svaEndIndex = "svaEndIndex";
    private static final String WICKET_ID_svaAccountBalance = "svaAccountBalance";
    private static final String WICKET_ID_svaNoItemsMsg = "svaNoItemsMsg";
    private static final String WICKET_ID_svaPageable = "svaPageable";
    private static final String WICKET_ID_svaSelected = "svaSelected";
    private PrivilegedBehavior cstPriv;

    @SpringBean(name = "systemAuthAlertsClientLogic")
    protected AlertsClientLogic clientLogic;

    public AccountsPanel(String id, MobiliserBasePage mobBasePage,
	    IManageAccountsViewer accountsViewer, Long customerId,
	    boolean pageViewedByAgent) {
	super(id);
	this.basePage = mobBasePage;
	this.accountsViewer = accountsViewer;
	this.customerId = customerId;
	this.pageViewedByAgent = pageViewedByAgent;
	cstPriv = new PrivilegedBehavior(basePage, Constants.PRIV_CST_LOGIN);
	this.constructPanel();

    }

    private void constructPanel() {
	loadWalletEntries();

	forceReload = false;

	final Form<?> form = new Form<AccountsPanel>("manageAccountForm",
		new CompoundPropertyModel<AccountsPanel>(this));

	form.add(new FeedbackPanel("errorMessages"));

	final RadioGroup group = new RadioGroup("primary", new Model());
	form.add(group);
	WebMarkupContainer baListContainer = new WebMarkupContainer(
		"baListContainer");
	if (!isPageViewedByAgent())
	    baListContainer.add(new PrivilegedBehavior(basePage,
		    Constants.CAN_HAVE_BANK_ACCOUNT_PRIV));
	group.add(baListContainer);
	createBankAccountListDataView(baListContainer);

	WebMarkupContainer ccListContainer = new WebMarkupContainer(
		"ccListContainer");
	if (!isPageViewedByAgent())
	    ccListContainer.add(new PrivilegedBehavior(basePage,
		    Constants.CAN_HAVE_CREDIT_CARD_PRIV));
	group.add(ccListContainer);
	createCreditCardListDataView(ccListContainer);

	WebMarkupContainer eaListContainer = new WebMarkupContainer(
		"eaListContainer");
	eaListContainer.add(cstPriv);
	group.add(eaListContainer);
	createExternalAccountListDataView(eaListContainer);

	createSvaListDataView(group);
	Button storePrimary = new Button("storePrimary") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		storePrimary((WalletEntry) group.getModelObject());
	    };
	};
	if (isPageViewedByAgent())
	    storePrimary.add(new PrivilegedBehavior(basePage,
		    Constants.PRIV_CUST_WRITE));
	group.add(storePrimary);

	add(form);

    }

    private void storePrimary(WalletEntry selectedWallet) {
	try {
	    if (selectedWallet != null) {
		LOG
			.debug("Selected WalletEntry: {}", selectedWallet
				.getAlias());
		UpdateWalletEntryRequest updateWeRequest = basePage
			.getNewMobiliserRequest(UpdateWalletEntryRequest.class);
		selectedWallet.setBankAccount(null);
		selectedWallet.setSva(null);
		selectedWallet.setCreditCard(null);
		selectedWallet.setExternalAccount(null);
		updateWeRequest.setWalletEntry(selectedWallet);
		updateWeRequest.setPrimaryCredit(Boolean.TRUE);
		updateWeRequest.setPrimaryDebit(Boolean.TRUE);
		UpdateWalletEntryResponse upWalletResp = basePage.wsWalletClient
			.updateWalletEntry(updateWeRequest);
		if (basePage.evaluateMobiliserResponse(upWalletResp)) {
		    LOG.info("Set primary account successful!");
		    setResponsePage(basePage.getClass());
		}
	    }
	} catch (Exception e) {
	    LOG.error("# could not update walletEntry[{}] of customer[{}]",
		    new Object[] { selectedWallet.getCustomerId(),
			    selectedWallet.getPaymentInstrumentId(), e });
	    error(getLocalizer().getString("store.primary.error", this));

	}
    }

    private void loadWalletEntries() {

	if (!PortalUtils.exists(walletEntries)) {

	    walletEntries = basePage.getWalletEntryList(customerId, null, null);

	    this.highestPriority = Integer.valueOf(Integer.MAX_VALUE);

	    if (PortalUtils.exists(walletEntries)) {
		for (WalletEntry we : walletEntries) {
		    if (we.getCreditPriority() != null
			    && we.getCreditPriority() < highestPriority) {
			this.highestPriority = we.getCreditPriority();
			this.primaryId = we.getPaymentInstrumentId();
		    }
		}
	    }
	}
    }

    private void createBankAccountListDataView(WebMarkupContainer parent) {

	baDataProvider = new WalletEntryDataProvider(WICKET_ID_baNickname,
		basePage);
	baDataProvider.setAllWalletEntries(walletEntries);

	baList = new ArrayList<WalletEntry>();
	Button addAccount = new Button("addAccount") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		accountsViewer.addBankAccount();
	    };
	};
	if (isPageViewedByAgent())
	    addAccount.add(new PrivilegedBehavior(basePage,
		    Constants.PRIV_CUST_WRITE));
	parent.add(addAccount);
	final DataView<WalletEntry> dataView = new DataView<WalletEntry>(
		WICKET_ID_baPageable, baDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    baDataProvider.loadBankAccountList(customerId, forceReload);

		    refreshTotalItemCount();

		    if (baDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG
			    .error(
				    "# An error occurred while loading bank accounts list",
				    dple);
		    error(getLocalizer().getString(
			    "manageAccounts.bank.load.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<WalletEntry> item) {

		final WalletEntry entry = item.getModelObject();
		final boolean isPendingWallet = entry instanceof PendingWalletEntry;

		baList.add(entry);

		Radio selectRow = new Radio(WICKET_ID_baSelected, new Model(
			entry)) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    protected void onComponentTag(ComponentTag tag) {
			if (PortalUtils.exists(entry.getPaymentInstrumentId())
				&& entry.getPaymentInstrumentId() == primaryId) {
			    tag.put("checked", "checked");
			}
			super.onComponentTag(tag);
		    }
		};

		selectRow.setEnabled(PortalUtils.exists(entry
			.getPaymentInstrumentId()));

		item.add(selectRow);

		item.add(new Label(WICKET_ID_baNickname, entry.getAlias()));

		item.add(new Label(WICKET_ID_baAccountNumber, entry
			.getBankAccount().getDisplayNumber()));

		item.add(new Label(WICKET_ID_baBankCode, entry.getBankAccount()
			.getBankCode()));

		String accountStatus = null;
		if (entry.getBankAccount().getStatus() == 0) {
		    accountStatus = getLocalizer().getString(
			    "lookup.bankAccountStatus.0", this);
		} else {
		    accountStatus = getLocalizer().getString(
			    "lookup.bankAccountStatus.3", this);
		}
		item.add(new Label(WICKET_ID_baBankStatus, accountStatus));

		// Edit Action
		Link<WalletEntry> editLink = new Link<WalletEntry>(
			WICKET_ID_baEditAction, item.getModel()) {
		    @Override
		    public void onClick() {
			WalletEntry entry = (WalletEntry) getModelObject();
			accountsViewer.editBankAccount(entry);
		    }
		};

		editLink.setVisible(!isPendingWallet);

		// Remove Action
		Link removeLink = new Link<WalletEntry>(
			WICKET_ID_baRemoveAction, item.getModel()) {
		    @Override
		    public void onClick() {
			WalletEntry entry = (WalletEntry) getModelObject();
			removeFromWallet(entry);
			forceReload = true;
		    }
		};

		removeLink.setVisible(!isPendingWallet);

		removeLink
			.add(new SimpleAttributeModifier(
				"onclick",
				"return confirm('"
					+ getLocalizer()
						.getString(
							"manageAccounts.bankAccountsTable.remove.confirm",
							this) + "');"));

		// Approve Action
		Link approveLink = new Link<WalletEntry>(
			WICKET_ID_baApproveAction, item.getModel()) {
		    @Override
		    public void onClick() {
			setResponsePage(basePage.getResponsePage(entry));
		    }
		};

		if (isPageViewedByAgent()) {
		    if (isPendingWallet) {
			approveLink.add(new PrivilegedBehavior(basePage,
				Constants.PRIV_APPROVE_PENDING_WALLET));

		    } else {
			approveLink.setVisible(false);
		    }
		    editLink.add(new PrivilegedBehavior(basePage,
			    Constants.PRIV_CUST_WRITE));
		    removeLink.add(new PrivilegedBehavior(basePage,
			    Constants.PRIV_CUST_WRITE));
		} else {
		    approveLink.setVisible(false);
		}

		item.add(editLink);
		item.add(removeLink);
		item.add(approveLink);

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
		baTotalItemString = new Integer(baDataProvider.size())
			.toString();
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

	parent.add(new OrderByBorder(WICKET_ID_baOrderByNickname,
		WICKET_ID_baNickname, baDataProvider) {
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

	parent.add(new MultiLineLabel(WICKET_ID_baNoItemsMsg, getLocalizer()
		.getString("manageAccounts.bankAccountsTable.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString(
			"manageAccounts.bankAccountsTable.addBankAccountHelp",
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
	parent.add(new CustomPagingNavigator(WICKET_ID_baNavigator, dataView));

	parent.add(new Label(WICKET_ID_baTotalItems, new PropertyModel<String>(
		this, "baTotalItemString")));

	parent.add(new Label(WICKET_ID_baStartIndex, new PropertyModel(this,
		"baStartIndex")));

	parent.add(new Label(WICKET_ID_baEndIndex, new PropertyModel(this,
		"baEndIndex")));
    }

    private void createCreditCardListDataView(WebMarkupContainer parent) {

	ccDataProvider = new WalletEntryDataProvider(WICKET_ID_ccNickname,
		basePage);
	ccDataProvider.setAllWalletEntries(walletEntries);

	ccList = new ArrayList<WalletEntry>();
	Button addCreditCard = new Button("addCreditCard") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		accountsViewer.addCreditCard();
	    };
	};
	if (isPageViewedByAgent())
	    addCreditCard.add(new PrivilegedBehavior(basePage,
		    Constants.PRIV_CUST_WRITE));
	parent.add(addCreditCard);

	final DataView<WalletEntry> dataView = new DataView<WalletEntry>(
		WICKET_ID_ccPageable, ccDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    ccDataProvider.loadCreditCardList(customerId, forceReload);

		    refreshTotalItemCount();

		    if (ccDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG
			    .error(
				    "# An error occurred while loading credit cards list",
				    dple);
		    error(getLocalizer().getString(
			    "manageAccounts.creditcard.load.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<WalletEntry> item) {

		final WalletEntry entry = item.getModelObject();
		final boolean isPendingWallet = entry instanceof PendingWalletEntry;

		ccList.add(entry);

		Radio selectRow = new Radio(WICKET_ID_ccSelected, new Model(
			entry)) {
		    @Override
		    protected void onComponentTag(ComponentTag tag) {
			if (entry.getPaymentInstrumentId() == primaryId) {
			    tag.put("checked", "checked");
			}
			super.onComponentTag(tag);
		    }
		};

		item.add(selectRow);
		selectRow.setEnabled(PortalUtils.exists(entry
			.getPaymentInstrumentId()));

		item.add(new Label(WICKET_ID_ccNickname, entry.getAlias()));

		item.add(new Label(WICKET_ID_ccCardType, basePage
			.getDisplayValue(String.valueOf(entry.getCreditCard()
				.getCardType()), "cardtypes")));

		item.add(new Label(WICKET_ID_ccCardNumber, entry
			.getCreditCard().getDisplayNumber()));

		// Edit Action
		Link editLink = new Link<WalletEntry>(WICKET_ID_ccEditAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			WalletEntry entry = (WalletEntry) getModelObject();
			accountsViewer.editCreditCard(entry);
			forceReload = true;
		    }
		};
		editLink.add(cstPriv);

		editLink.setVisible(!isPendingWallet);

		// Remove Action
		Link removeLink = new Link<WalletEntry>(
			WICKET_ID_ccRemoveAction, item.getModel()) {
		    @Override
		    public void onClick() {
			WalletEntry entry = (WalletEntry) getModelObject();
			removeFromWallet(entry);
			forceReload = true;
		    }
		};

		removeLink.setVisible(!isPendingWallet);

		removeLink
			.add(new SimpleAttributeModifier(
				"onclick",
				"return confirm('"
					+ getLocalizer()
						.getString(
							"manageAccounts.creditCardsTable.remove.confirm",
							this) + "');"));

		// Approve Action
		Link approveLink = new Link<WalletEntry>(
			WICKET_ID_ccApproveAction, item.getModel()) {
		    @Override
		    public void onClick() {
			setResponsePage(basePage.getResponsePage(entry));
		    }
		};

		if (isPageViewedByAgent()) {
		    if (isPendingWallet) {
			approveLink.add(new PrivilegedBehavior(basePage,
				Constants.PRIV_APPROVE_PENDING_WALLET));

		    } else {
			approveLink.setVisible(false);
		    }
		    editLink.add(new PrivilegedBehavior(basePage,
			    Constants.PRIV_CUST_WRITE));
		    removeLink.add(new PrivilegedBehavior(basePage,
			    Constants.PRIV_CUST_WRITE));
		} else {
		    approveLink.setVisible(false);
		}

		item.add(editLink);
		item.add(removeLink);
		item.add(approveLink);
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
		ccTotalItemString = new Integer(ccDataProvider.size())
			.toString();
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

	parent.add(new OrderByBorder(WICKET_ID_ccOrderByNickname,
		WICKET_ID_ccNickname, ccDataProvider) {
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

	parent.add(new MultiLineLabel(WICKET_ID_ccNoItemsMsg, getLocalizer()
		.getString("manageAccounts.creditCardsTable.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString(
			"manageAccounts.creditCardsTable.addCreditCardHelp",
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
	parent.add(new CustomPagingNavigator(WICKET_ID_ccNavigator, dataView));

	parent.add(new Label(WICKET_ID_ccTotalItems, new PropertyModel<String>(
		this, "ccTotalItemString")));

	parent.add(new Label(WICKET_ID_ccStartIndex, new PropertyModel(this,
		"ccStartIndex")));

	parent.add(new Label(WICKET_ID_ccEndIndex, new PropertyModel(this,
		"ccEndIndex")));
    }

    private void createExternalAccountListDataView(WebMarkupContainer parent) {

	eaDataProvider = new WalletEntryDataProvider(WICKET_ID_eaAlias,
		basePage);
	eaDataProvider.setAllWalletEntries(walletEntries);

	eaList = new ArrayList<WalletEntry>();

	Button addExternalAccount = new Button("addExternalAccount") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		accountsViewer.addExternalAccount();
	    };
	};
	if (isPageViewedByAgent())
	    addExternalAccount.add(new PrivilegedBehavior(basePage,
		    Constants.PRIV_CUST_WRITE));

	parent.add(addExternalAccount);

	final DataView<WalletEntry> dataView = new DataView<WalletEntry>(
		WICKET_ID_eaPageable, eaDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    eaDataProvider.loadExternalAccountList(customerId,
			    forceReload);

		    refreshTotalItemCount();

		    if (eaDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG
			    .error(
				    "# An error occurred while loading external accounts list",
				    dple);
		    error(getLocalizer().getString(
			    "manageAccounts.external.load.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<WalletEntry> item) {

		final WalletEntry entry = item.getModelObject();
		final boolean isPendingWallet = entry instanceof PendingWalletEntry;

		eaList.add(entry);

		Radio selectRow = new Radio(WICKET_ID_eaSelected, new Model(
			entry)) {
		    @Override
		    protected void onComponentTag(ComponentTag tag) {
			if (entry.getPaymentInstrumentId() == primaryId) {
			    tag.put("checked", "checked");
			}
			super.onComponentTag(tag);
		    }
		};

		item.add(selectRow);
		selectRow.setEnabled(PortalUtils.exists(entry
			.getPaymentInstrumentId()));

		item.add(new Label(WICKET_ID_eaAlias, entry.getAlias()));

		item.add(new Label(WICKET_ID_eaId1, entry.getExternalAccount()
			.getId1()));

		item.add(new Label(WICKET_ID_eaId2, entry.getExternalAccount()
			.getId2()));

		// Edit Action
		Link<WalletEntry> editLink = new Link<WalletEntry>(
			WICKET_ID_eaEditAction, item.getModel()) {
		    @Override
		    public void onClick() {
			WalletEntry entry = (WalletEntry) getModelObject();
			accountsViewer.editExternalAccount(entry);
		    }
		};

		// Remove Action
		Link removeLink = new Link<WalletEntry>(
			WICKET_ID_eaRemoveAction, item.getModel()) {
		    @Override
		    public void onClick() {
			WalletEntry entry = (WalletEntry) getModelObject();
			removeFromWallet(entry);
			forceReload = true;
		    }
		};

		removeLink
			.add(new SimpleAttributeModifier(
				"onclick",
				"return confirm('"
					+ getLocalizer()
						.getString(
							"manageAccounts.externalAccountsTable.remove.confirm",
							this) + "');"));

		// Approve Action
		Link approveLink = new Link<WalletEntry>(
			WICKET_ID_eaApproveAction, item.getModel()) {
		    @Override
		    public void onClick() {
			setResponsePage(basePage.getResponsePage(entry));
		    }
		};

		approveLink.setVisible(isPendingWallet);
		item.add(approveLink);

		if (isPageViewedByAgent()) {
		    editLink.add(new PrivilegedBehavior(basePage,
			    Constants.PRIV_CUST_WRITE));
		    removeLink.add(new PrivilegedBehavior(basePage,
			    Constants.PRIV_CUST_WRITE));
		    editLink.setVisible(!isPendingWallet);
		    removeLink.setVisible(!isPendingWallet);
		}
		item.add(editLink);
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
		eaTotalItemString = new Integer(eaDataProvider.size())
			.toString();
		int total = eaDataProvider.size();
		if (total > 0) {
		    eaStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    eaEndIndex = eaStartIndex + getItemsPerPage() - 1;
		    if (eaEndIndex > total)
			eaEndIndex = total;
		} else {
		    eaStartIndex = 0;
		    eaEndIndex = 0;
		}
	    }
	};

	dataView.setItemsPerPage(3);

	parent.add(dataView);

	parent.add(new OrderByBorder(WICKET_ID_eaOrderByAlias,
		WICKET_ID_eaAlias, eaDataProvider) {
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

	parent
		.add(new MultiLineLabel(
			WICKET_ID_eaNoItemsMsg,
			getLocalizer()
				.getString(
					"manageAccounts.externalAccountsTable.noItemsMsg",
					this)
				+ "\n"
				+ getLocalizer()
					.getString(
						"manageAccounts.externalAccountsTable.addExternalAccountHelp",
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
	parent.add(new CustomPagingNavigator(WICKET_ID_eaNavigator, dataView));

	parent.add(new Label(WICKET_ID_eaTotalItems, new PropertyModel<String>(
		this, "eaTotalItemString")));

	parent.add(new Label(WICKET_ID_eaStartIndex, new PropertyModel(this,
		"eaStartIndex")));

	parent.add(new Label(WICKET_ID_eaEndIndex, new PropertyModel(this,
		"eaEndIndex")));
    }

    private void createSvaListDataView(WebMarkupContainer parent) {

	svaDataProvider = new WalletEntryDataProvider(
		WICKET_ID_svaAccountBalance, basePage);
	svaDataProvider.setAllWalletEntries(walletEntries);

	svaList = new ArrayList<WalletEntry>();
	final Button addFundsButton = new Button("addFunds") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		accountsViewer.addFunds();
	    };
	};
	final Button withdrawFundsButton = new Button("withdrawFunds") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		accountsViewer.withdrawFunds();
	    };
	};
	if (isPageViewedByAgent()) {
	    addFundsButton.add(new PrivilegedBehavior(basePage,
		    Constants.PRIV_SVA_CREDIT));
	    withdrawFundsButton.add(new PrivilegedBehavior(basePage,
		    Constants.PRIV_SVA_DEBIT));
	}
	parent.add(addFundsButton);
	parent.add(withdrawFundsButton);

	final Button creditButton = new Button("credit") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		accountsViewer.creditSva();
	    };
	};
	creditButton.add(new PrivilegedBehavior(basePage,
		Constants.PRIV_SVA_CREDIT));

	parent.add(creditButton);

	final Button debitButton = new Button("debit") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		accountsViewer.debitSva();
	    };
	};
	debitButton.add(new PrivilegedBehavior(basePage,
		Constants.PRIV_SVA_DEBIT));

	parent.add(debitButton);

	final Button balalertButton = new Button("balanceAlert") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		accountsViewer.addBalanceAlert();
	    };
	};

	balalertButton.add(new PrivilegedBehavior(basePage,
		Constants.PRIV_BALANCE_ALERT));
	parent.add(balalertButton);

	final DataView<WalletEntry> dataView = new DataView<WalletEntry>(
		WICKET_ID_svaPageable, svaDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    svaDataProvider.loadStoredValueAccountList(customerId,
			    forceReload);

		    refreshTotalItemCount();

		    if (baDataProvider.size() == 0) {
			withdrawFundsButton.setVisible(false);
			addFundsButton.add(Constants.linkLastAttributeModifier);

			if (ccDataProvider.size() == 0) {
			    addFundsButton.setVisible(false);
			    if (basePage.getMobiliserWebSession().getRoles()
				    .hasRole(Constants.PRIV_MANAGE_ACCOUNTS))
				balalertButton
					.add(Constants.linkLastAttributeModifier);
			} else {
			    debitButton.add(Constants.linkAttributeModifier);
			}
		    } else {
			debitButton.add(Constants.linkAttributeModifier);
		    }

		    if (svaDataProvider.size() > 0) {
			setVisible(true);
		    } else {
			withdrawFundsButton.setVisible(false);
			addFundsButton.setVisible(false);
			creditButton.setVisible(false);
			debitButton.setVisible(false);
			balalertButton.setVisible(false);

			setVisible(false);
		    }

		} catch (DataProviderLoadException dple) {
		    LOG
			    .error(
				    "# An error occurred while loading stored value accounts list",
				    dple);
		    error(getLocalizer().getString(
			    "manageAccounts.sva.load.error", this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<WalletEntry> item) {

		final WalletEntry entry = item.getModelObject();

		svaList.add(entry);

		Radio selectRow = new Radio(WICKET_ID_svaSelected, new Model(
			entry)) {
		    @Override
		    protected void onComponentTag(ComponentTag tag) {
			if (entry.getPaymentInstrumentId() == primaryId) {
			    tag.put("checked", "checked");
			}
			super.onComponentTag(tag);
		    }
		};

		item.add(selectRow);

		item.add(new Label(WICKET_ID_svaAccountBalance, basePage
			.convertAmountToStringWithCurrency(basePage
				.getSVABalanceAmount(entry
					.getPaymentInstrumentId()))));

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
		svaTotalItemString = new Integer(svaDataProvider.size())
			.toString();
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

	parent.add(new MultiLineLabel(WICKET_ID_svaNoItemsMsg, getLocalizer()
		.getString("manageAccounts.svasTable.noItemsMsg", this)) {
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
	parent.add(new CustomPagingNavigator(WICKET_ID_svaNavigator, dataView));

	parent.add(new Label(WICKET_ID_svaTotalItems,
		new PropertyModel<String>(this, "svaTotalItemString")));

	parent.add(new Label(WICKET_ID_svaStartIndex, new PropertyModel(this,
		"svaStartIndex")));

	parent.add(new Label(WICKET_ID_svaEndIndex, new PropertyModel(this,
		"svaEndIndex")));
    }

    public List<WalletEntry> getBankAccounts() {
	return baList;
    }

    public List<WalletEntry> getCreditCardAccounts() {
	return ccList;
    }

    public List<WalletEntry> getSVAAccounts() {
	return svaList;
    }

    protected void removeFromWallet(WalletEntry walletEntry) {
	LOG.debug("Selected WalletEntry: {}", walletEntry.getAlias());
	DeleteWalletEntryRequest deleteRequest;
	DeleteWalletEntryResponse deleteResponse;
	try {
	    deleteRequest = basePage
		    .getNewMobiliserRequest(DeleteWalletEntryRequest.class);
	    deleteRequest.setWalletEntryId(walletEntry.getId());
	    deleteResponse = basePage.wsWalletClient
		    .deleteWalletEntry(deleteRequest);

	    if (basePage.evaluateMobiliserResponse(deleteResponse)) {
		LOG.info("Delete Wallet entry is successful!!");
		/*
		 * getSession().info(
		 * getLocalizer().getString("deleteWallet.success", this));
		 */

		if (clientLogic.deleteCustomerAlertByCustomerAndData(
			customerId, Constants.ALERT_DATA_KEY_PI_ID, walletEntry
				.getPaymentInstrumentId()) != -1) {
		    setResponsePage(basePage.getClass());
		}
	    }

	} catch (Exception e) {
	    LOG.error("# An error has occurred for deleteWalletEntry ", e);
	    error(getLocalizer().getString("walletDelete.error", this));
	}

    }

    public boolean isPageViewedByAgent() {
	return pageViewedByAgent;
    }

    public void setPageViewedByAgent(boolean pageViewedByAgent) {
	this.pageViewedByAgent = pageViewedByAgent;
    }

}
