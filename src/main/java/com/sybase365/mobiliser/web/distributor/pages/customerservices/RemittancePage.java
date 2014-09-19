package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
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

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.ExternalAccount;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.ExternalAccountDataProvider;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_WALLET_SERVICES)
public class RemittancePage extends CustomerServicesMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(RemittancePage.class);

    private ExternalAccountDataProvider wDataProvider;
    private String wTotalItemString = null;
    private int wStartIndex = 0;
    private int wEndIndex = 0;

    private ExternalAccountDataProvider baDataProvider;
    private String baTotalItemString = null;
    private int baStartIndex = 0;
    private int baEndIndex = 0;

    private boolean forceReload = true;

    private static final String WICKET_ID_baNavigator = "baNavigator";
    private static final String WICKET_ID_baTotalItems = "baTotalItems";
    private static final String WICKET_ID_baStartIndex = "baStartIndex";
    private static final String WICKET_ID_baEndIndex = "baEndIndex";
    private static final String WICKET_ID_baPageable = "baPageable";
    private static final String WICKET_ID_baAccountHolder = "baAccountHolder";
    private static final String WICKET_ID_baAccountNumber = "baAccountNumber";
    private static final String WICKET_ID_baBankName = "baBankName";
    private static final String WICKET_ID_baAccountHolderAddress = "baAccountHolderAddress";
    private static final String WICKET_ID_baCountry = "baCountry";
    private static final String WICKET_ID_baRemitAction = "baRemitAction";
    private static final String WICKET_ID_baNoItemsMsg = "baNoItemsMsg";

    private static final String WICKET_ID_wNavigator = "wNavigator";
    private static final String WICKET_ID_wTotalItems = "wTotalItems";
    private static final String WICKET_ID_wStartIndex = "wStartIndex";
    private static final String WICKET_ID_wEndIndex = "wEndIndex";
    private static final String WICKET_ID_wPageable = "wPageable";
    private static final String WICKET_ID_wMsisdn = "wMsisdn";
    private static final String WICKET_ID_wRemitAction = "wRemitAction";
    private static final String WICKET_ID_wNoItemsMsg = "wNoItemsMsg";

    public RemittancePage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */

    public RemittancePage(PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	final Form<?> form = new Form<RemittancePage>("remittanceForm",
		new CompoundPropertyModel<RemittancePage>(this));

	form.add(new FeedbackPanel("errorMessages"));

	createGCashBankAccountListDataView(form);

	createGCashWalletListDataView(form);

	add(form);
    };

    private void handleRemit(ExternalAccount entry) {
	setResponsePage(new RemitMoneyPage(entry));
    }

    private void handleAddBankAccount() {
	setResponsePage(RemittanceAddAccount.class);
    }

    private void handleAddWallet() {
	setResponsePage(RemittanceAddAccount.class);
    }

    private void createGCashBankAccountListDataView(
	    final WebMarkupContainer parent) {

	baDataProvider = new ExternalAccountDataProvider(
		WICKET_ID_baAccountHolder, this);

	final Long customerId = getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId();

	parent.add(new Button("addBankAccount") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleAddBankAccount();
	    };
	});

	final DataView<ExternalAccount> dataView = new DataView<ExternalAccount>(
		WICKET_ID_baPageable, baDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    baDataProvider.loadGCashBankAccounts(customerId,
			    forceReload);
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading bank accounts list",
			    dple);
		    error(getLocalizer().getString(".remove.success", this));
		}
		if (baDataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<ExternalAccount> item) {

		final ExternalAccount entry = item.getModelObject();

		item.add(new Label(WICKET_ID_baAccountHolder, entry.getId2()));

		item.add(new Label(WICKET_ID_baAccountNumber, entry.getId4()));

		String bankName = getBank(entry.getId5());

		item.add(new Label(WICKET_ID_baBankName, PortalUtils
			.exists(bankName) ? bankName : ""));

		item.add(new Label(WICKET_ID_baAccountHolderAddress, entry
			.getId3()));

		item.add(new Label(WICKET_ID_baCountry, getDisplayValue(
			entry.getId8(), Constants.RESOURCE_BUNDLE_COUNTIRES)));

		// Remit Action
		Link<ExternalAccount> remitLink = new Link<ExternalAccount>(
			WICKET_ID_baRemitAction, item.getModel()) {
		    @Override
		    public void onClick() {
			ExternalAccount entry = (ExternalAccount) getModelObject();
			handleRemit(entry);
		    }
		};
		item.add(remitLink);

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

	parent.add(new MultiLineLabel(
		WICKET_ID_baNoItemsMsg,
		getLocalizer().getString(
			"remittance.bankAccountsTable.noItemsMsg", this)
			+ "\n"
			+ getLocalizer()
				.getString(
					"remittance.bankAccountsTable.addBankAccountHelp",
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

    private void createGCashWalletListDataView(final WebMarkupContainer parent) {

	wDataProvider = new ExternalAccountDataProvider(WICKET_ID_wMsisdn, this);

	final Long customerId = getMobiliserWebSession().getLoggedInCustomer()
		.getCustomerId();

	parent.add(new Button("addWallet") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleAddWallet();
	    };
	});

	final DataView<ExternalAccount> dataView = new DataView<ExternalAccount>(
		WICKET_ID_wPageable, wDataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    wDataProvider.loadGCashWallets(customerId, forceReload);
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading credit cards list",
			    dple);
		    error(getLocalizer().getString(".remove.success", this));
		}
		if (wDataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<ExternalAccount> item) {

		final ExternalAccount entry = item.getModelObject();

		item.add(new Label(WICKET_ID_wMsisdn, entry.getId1()));

		// Remit Action
		Link<ExternalAccount> remitLink = new Link<ExternalAccount>(
			WICKET_ID_wRemitAction, item.getModel()) {
		    @Override
		    public void onClick() {
			ExternalAccount entry = (ExternalAccount) getModelObject();
			handleRemit(entry);
		    }
		};
		item.add(remitLink);

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
		wTotalItemString = Integer.toString(wDataProvider.size());
		int total = wDataProvider.size();
		if (total > 0) {
		    wStartIndex = getCurrentPage() * getItemsPerPage() + 1;
		    wEndIndex = wStartIndex + getItemsPerPage() - 1;
		    if (wEndIndex > total)
			wEndIndex = total;
		} else {
		    wStartIndex = 0;
		    wEndIndex = 0;
		}
	    }
	};

	parent.add(new MultiLineLabel(WICKET_ID_wNoItemsMsg, getLocalizer()
		.getString("remittance.walletsTable.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString(
			"remittance.walletsTable.addWalletHelp", this)) {

	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return false;
		} else {
		    return super.isVisible();
		}
	    }
	});

	dataView.setItemsPerPage(3);

	parent.add(dataView);

	// Navigator example: << < 1 2 > >>
	parent.add(new CustomPagingNavigator(WICKET_ID_wNavigator, dataView));

	parent.add(new Label(WICKET_ID_wTotalItems, new PropertyModel<String>(
		this, "wTotalItemString")));

	parent.add(new Label(WICKET_ID_wStartIndex, new PropertyModel(this,
		"wStartIndex")));

	parent.add(new Label(WICKET_ID_wEndIndex, new PropertyModel(this,
		"wEndIndex")));
    }

}
