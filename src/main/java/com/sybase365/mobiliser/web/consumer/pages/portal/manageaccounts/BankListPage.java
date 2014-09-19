package com.sybase365.mobiliser.web.consumer.pages.portal.manageaccounts;

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

import com.sybase365.mobiliser.money.contract.v5_0.wallet.DeleteWalletEntryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.DeleteWalletEntryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PendingWalletEntry;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.WalletEntryDataProvider;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_BANK_ACCOUNT_LIST)
public class BankListPage extends BaseManageAccountsPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BankListPage.class);

    // Data Model for table list
    private WalletEntryDataProvider dataProvider;

    List<WalletEntry> selectedBanks = new ArrayList<WalletEntry>();
    List<WalletEntry> bankAccounts;

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
    private static final String WICKET_ID_orderByAlias = "orderByAlias";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_selected = "selected";
    private static final String WICKET_ID_alias = "alias";
    private static final String WICKET_ID_aliasLink = "aliasLink";

    private static final String WICKET_ID_accountNumber = "accountNumber";
    private static final String WICKET_ID_bankCode = "bankCode";
    private static final String WICKET_ID_accountHolderName = "accountHolderName";
    private static final String WICKET_ID_editAction = "editAction";
    private static final String WICKET_ID_sendMoneyAction = "sendMoneyAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public BankListPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BankListPage(final PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Form<?> form = new Form("bankListForm",
		new CompoundPropertyModel<BankListPage>(this));

	add(form);

	form.add(new FeedbackPanel("errorMessages"));

	createBankListDataView(form);

    }

    @Override
    protected Class getActiveMenu() {
	return this.getClass();
    }

    public List<WalletEntry> getSelectedWallets() {
	return selectedBanks;
    }

    public void setSelectedWallets(List<WalletEntry> selectedWallets) {
	this.selectedBanks = selectedWallets;
    }

    public boolean isSelected(WalletEntry entry) {
	return selectedBanks.contains(entry);
    }

    protected void editBankAccount(WalletEntry walletEntry) {
	setResponsePage(new BankExternalAccountDataPage(BankListPage.class,
		walletEntry, Integer.valueOf(Constants.PI_TYPE_EXTERNAL_BA)));
    }

    protected void sendMoney(WalletEntry wallet) {
	setResponsePage(new SendMoneyBankPage(wallet));
    }

    private void handleRemove() {
	if (selectedBanks == null || selectedBanks.isEmpty()) {
	    error(getLocalizer().getString("bankList.nothing.selected.remove",
		    this));
	    return;
	}
	DeleteWalletEntryRequest deleteRequest;
	DeleteWalletEntryResponse deleteResponse = null;
	try {

	    for (WalletEntry we : selectedBanks) {
		deleteRequest = getNewMobiliserRequest(DeleteWalletEntryRequest.class);
		deleteRequest.setWalletEntryId(we.getId());
		deleteResponse = wsWalletClient
			.deleteWalletEntry(deleteRequest);
	    }

	    if (evaluateMobiliserResponse(deleteResponse)) {
		LOG.info("Delete Wallet entry is successful!!");
	    }
	    info(getLocalizer().getString("bankList.remove.success", this));
	    setResponsePage(BankListPage.class);

	} catch (Exception e) {
	    LOG.error("# An error has occurred for deleteWalletEntry ", e);
	    error(getLocalizer().getString("walletDelete.error", this));
	}

    }

    private void createBankListDataView(Form form) {

	dataProvider = new WalletEntryDataProvider(WICKET_ID_alias, this);
	bankAccounts = new ArrayList<WalletEntry>();
	final Long customerId = getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId();

	form.add(new Button("add") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(new BankExternalAccountDataPage(
			BankListPage.class, null, Constants.PI_TYPE_EXTERNAL_BA));
	    };
	});

	final DataView<WalletEntry> dataView = new DataView<WalletEntry>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadExternalBankList(customerId, forceReload);
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
			    "# An error occurred while getting payment instrument",
			    dple);
		    error("An error occurred while getting payment instrument");
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<WalletEntry> item) {
		final WalletEntry entry = item.getModelObject();
		final boolean isPendingWallet = entry instanceof PendingWalletEntry;

		// Select box
		AjaxCheckBox selectedCheckBox = new AjaxCheckBoxImpl(
			WICKET_ID_selected, new Model(isSelected(entry)), entry);

		selectedCheckBox.setOutputMarkupId(true);
		selectedCheckBox.setMarkupId(WICKET_ID_selected + rowIndex++);
		item.add(selectedCheckBox);

		// Alias Link
		Link<WalletEntry> aliasLink = new Link<WalletEntry>(
			WICKET_ID_aliasLink, item.getModel()) {

		    @Override
		    public void onClick() {
			// WalletEntry walletEntry = (WalletEntry)
			// getModelObject();
			setResponsePage(new BankExternalAccountDataPage(
				BankListPage.class, entry,
				Integer.valueOf(Constants.PI_TYPE_EXTERNAL_BA)));
		    }
		};

		item.add(aliasLink);

		aliasLink.add(new Label(WICKET_ID_alias, entry.getAlias()));

		item.add(new Label(WICKET_ID_accountNumber, entry
			.getBankAccount().getDisplayNumber()));

		item.add(new Label(WICKET_ID_bankCode, entry.getBankAccount()
			.getBankCode()));

		item.add(new Label(WICKET_ID_accountHolderName, entry
			.getBankAccount().getAccountHolderName()));

		String tooltip = "";

		// Edit Action
		Link<WalletEntry> editLink = new Link<WalletEntry>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			WalletEntry walletEntry = (WalletEntry) getModelObject();
			editBankAccount(walletEntry);
		    }
		};
		item.add(editLink);

		// Send Money Action
		Link sendMoneyLink = new Link<WalletEntry>(
			WICKET_ID_sendMoneyAction, item.getModel()) {
		    @Override
		    public void onClick() {
			WalletEntry walletEntry = (WalletEntry) getModelObject();
			sendMoney(walletEntry);
			forceReload = true;
		    }

		};
		item.add(sendMoneyLink);

		if (isPendingWallet) {
		    sendMoneyLink.setVisible(false);
		    editLink.setVisible(false);
		}

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

		private final WalletEntry entry;

		public AjaxCheckBoxImpl(String id, IModel<Boolean> model,
			WalletEntry entry) {
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
			selectedBanks.add(entry);
		    } else {
			LOG.info("Removed {} from deletion list", entry.getId());
			selectedBanks.remove(entry);
		    }
		}
	    }

	};

	dataView.setItemsPerPage(10);
	form.add(dataView);

	form.add(new OrderByBorder(WICKET_ID_orderByAlias, WICKET_ID_alias,
		dataProvider) {
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

	Button removeButton = new Button("remove") {

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
			+ getLocalizer().getString("bankList.remove.confirm",
				this) + "');"));

	form.add(removeButton);

	form.add(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer()
		.getString("bankList.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString("bankList.addAccountHelp", this)) {
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
