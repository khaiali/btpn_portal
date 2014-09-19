package com.sybase365.mobiliser.web.distributor.pages.selfcare;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.springframework.util.Assert;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetTransactionDetailsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.GetTransactionDetailsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.DetailedTransaction;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.SimpleTransaction;
import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.beans.SearchTransactionCriteria;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.SimpleTransactionDataProvider;
import com.sybase365.mobiliser.web.common.model.ITransactionDetailViewer;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_MERCHANT_TXN_HISTORY)
public class TransactionHistoryPage extends SelfCareMenuGroup implements
	ITransactionDetailViewer {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TransactionHistoryPage.class);

    // Data Model for table list
    private SimpleTransactionDataProvider dataProvider;
    private List<SimpleTransaction> transactions = new ArrayList<SimpleTransaction>();
    private boolean isTxnDetailsPagePopup;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_creationDate = "creationDate";
    private static final String WICKET_ID_useCase = "useCase";
    private static final String WICKET_ID_errorCode = "errorCode";
    private static final String WICKET_ID_text = "text";
    private static final String WICKET_ID_participantName = "participantName";
    private static final String WICKET_ID_amount = "amount";
    private static final String WICKET_ID_detailsAction = "detailsAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public TransactionHistoryPage() {

	super();

	LOG.info("Created new TransactionHistoryPage");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();
	WebMarkupContainer txnhistoryview = new WebMarkupContainer(
		"txnhistoryview");
	add(txnhistoryview);
	createTransactionsListDataView(txnhistoryview);

    }

    @Override
    public WebPage getTransactionDetailViewer(SimpleTransaction bean,
	    Long customerId, WebPage backPage) {
	DetailedTransaction detailedTransaction = getTransactionDetails(bean
		.getId());
	return new TransactionDetailsPage(detailedTransaction, backPage);
    }

    private DetailedTransaction getTransactionDetails(long txnId) {
	try {
	    GetTransactionDetailsRequest request = getNewMobiliserRequest(GetTransactionDetailsRequest.class);
	    request.setTxnId(txnId);
	    GetTransactionDetailsResponse response = wsTransactionsClient
		    .getTransactionDetails(request);
	    if (evaluateMobiliserResponse(response)) {
		return response.getTransaction();
	    }
	} catch (Exception e) {
	    LOG.error("# Exception getting detailed transaction info", e);
	}
	return null;
    }

    private void createTransactionsListDataView(
	    WebMarkupContainer txnhistoryview) {
	final Customer loggedInCustomer = ((BaseWebSession) getWebSession())
		.getLoggedInCustomer();
	dataProvider = new SimpleTransactionDataProvider(
		WICKET_ID_creationDate, this);
	transactions = new ArrayList<SimpleTransaction>();

	final DataView<SimpleTransaction> dataView = new DataView<SimpleTransaction>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    final Calendar c = Calendar
			    .getInstance(getMobiliserWebSession().getTimeZone());

		    Assert.isInstanceOf(GregorianCalendar.class, c,
			    "Can only work with gregorian calendars for XMLGregorianCalendar");

		    c.set(Calendar.HOUR_OF_DAY, 0);
		    c.set(Calendar.MINUTE, 0);
		    c.set(Calendar.SECOND, 0);
		    c.set(Calendar.MILLISECOND, 0);

		    final XMLGregorianCalendar fromDateXml = PortalUtils
			    .getDatatypeFactory().newXMLGregorianCalendar(
				    (GregorianCalendar) c);

		    // do not add 24 hours since that will break on daylight
		    // saving time crossovers
		    c.add(Calendar.DAY_OF_MONTH, 1);
		    c.add(Calendar.MILLISECOND, -1);

		    final XMLGregorianCalendar toDateXml = PortalUtils
			    .getDatatypeFactory().newXMLGregorianCalendar(
				    (GregorianCalendar) c);
		    SearchTransactionCriteria criteria = new SearchTransactionCriteria();
		    criteria.setCustomerId(loggedInCustomer.getCustomerId());
		    criteria.setFromDateXml(fromDateXml);
		    criteria.setToDateXml(toDateXml);
		    criteria.setShowFaulty(Boolean.TRUE);
		    criteria.setShowInitial(Boolean.FALSE);
		    dataProvider.loadCustomerTransactions(criteria, Integer
			    .valueOf(getConfiguration()
				    .getTxnMaxNumberToFetch()), forceReload);

		    forceReload = false;

		    refreshTotalItemCount();
		} catch (DataProviderLoadException dple) {
		    error(getLocalizer().getString("transactions.load.error",
			    this));
		}

		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<SimpleTransaction> item) {

		final SimpleTransaction entry = item.getModelObject();

		item.add(new Label(WICKET_ID_creationDate, PortalUtils
			.getFormattedDateTime(entry.getCreationDate(),
				getMobiliserWebSession().getLocale(),
				getMobiliserWebSession().getTimeZone())));

		item.add(new Label(WICKET_ID_useCase, getDisplayValue(
			String.valueOf(entry.getUseCase()),
			Constants.RESOURCE_BUNDLE_USE_CASES)));
		item.add(new Label(WICKET_ID_errorCode, Integer.toString(entry
			.getErrorCode())));
		if (loggedInCustomer.getCustomerId() != entry.getPayerId()) {
		    item.add(new Label(WICKET_ID_participantName, entry
			    .getPayerDisplayName()));
		} else {
		    item.add(new Label(WICKET_ID_participantName, entry
			    .getPayeeDisplayName()));
		}

		item.add(new Label(WICKET_ID_text, entry.getText()));

		item.add(new Label(WICKET_ID_amount, getTransactionAmount(
			entry, loggedInCustomer.getCustomerId())));

		// Details Action
		Link detailsLink = new Link<SimpleTransaction>(
			WICKET_ID_detailsAction, item.getModel()) {
		    @Override
		    public void onClick() {
			SimpleTransaction entry = (SimpleTransaction) getModelObject();
			showTransactionDetails(entry);
		    }
		};

		if (isTxnDetailsPagePopup) {
		    PopupSettings popupSettings = new PopupSettings();
		    popupSettings.setWidth(375).setHeight(350)
			    .setWindowName("TxnDetailsPopup");
		    detailsLink.setPopupSettings(popupSettings);
		}

		item.add(detailsLink);

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
		int total = dataProvider.size();
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

	txnhistoryview.addOrReplace(dataView);

	txnhistoryview
		.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
			getLocalizer().getString(
				"transactionHistory.noItemsMsg", this)) {
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
	txnhistoryview.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	txnhistoryview.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	txnhistoryview.addOrReplace(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	txnhistoryview.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));
    }

    protected void showTransactionDetails(SimpleTransaction txnBean) {
	if (this != null) {
	    setResponsePage(this
		    .getTransactionDetailViewer(txnBean, null, this));
	}
    }
}