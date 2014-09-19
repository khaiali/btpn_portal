package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetTransactionCustomerRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetTransactionCustomerResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashinBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.BankPortalCashInDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCashinDetailsPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the CashinPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashinPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BankPortalCashinPanel.class);

	private FeedbackPanel feedBack;

	protected BtpnMobiliserBasePage basePage;

	protected BankCashinBean cashInBean;

	private WebMarkupContainer cashInContainer;

	private BankPortalCashInDataProvider cashInDataProvider;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_cashInTotalItems = "cashInHeader";

	private static final String WICKET_ID_cashInNavigator = "cashInNavigator";

	private String cashInTotalItemString;

	private int cashInStartIndex = 0;

	private int cashInEndIndex = 0;

	private Component msisdnComponent;

	private Component nameComponent;

	
	/**
	 * Constructor for cash-in panel.
	 * 
	 * @param id
	 * @param basePage
	 */
	public BankPortalCashinPanel(String id, BtpnBaseBankPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		// Adds the panel
		constructPanel();
	}

	protected void constructPanel() {
		final Form<BankPortalCashinPanel> form = new Form<BankPortalCashinPanel>("cashInForm",
			new CompoundPropertyModel<BankPortalCashinPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		form.add(msisdnComponent = new TextField<String>("cashInBean.msisdn")
			.add(new PatternValidator(this.basePage.getBankPortalPrefsConfig().getMobileRegex()))
			.add(BtpnConstants.PHONE_NUMBER_VALIDATOR).add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH)
			.add(new ErrorIndicator()));
		msisdnComponent.setOutputMarkupId(true);

		form.add(nameComponent = new TextField<String>("cashInBean.name").add(
			BtpnConstants.REGISTRATION_DISPLAY_NAME_MAX_LENGTH).add(new ErrorIndicator()));
		nameComponent.setOutputMarkupId(true);

		cashInContainer = new WebMarkupContainer("cashInContainer");
		cashInListDataView(cashInContainer);
		cashInContainer.setOutputMarkupId(true);
		cashInContainer.setOutputMarkupPlaceholderTag(true);
		cashInContainer.setVisible(false);
		form.add(cashInContainer);

		form.add(new AjaxButton("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (!PortalUtils.exists(cashInBean)) {
					cashInBean = new BankCashinBean();
				}
				cashInBean.setCashInList(new ArrayList<BankCashinBean>());
				handleSearchCashIn(target);
			};

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBack);
				target.addComponent(msisdnComponent);
				target.addComponent(nameComponent);
				target.addComponent(cashInContainer);
				super.onError(target, form);
			}
		});

		add(form);
	}

	/**
	 * This method populate the cash in details.
	 */
	protected void cashInListDataView(final WebMarkupContainer dataViewContainer) {

		cashInDataProvider = new BankPortalCashInDataProvider("customerId");

		final DataView<BankCashinBean> dataView = new DataView<BankCashinBean>(WICKET_ID_PAGEABLE, cashInDataProvider) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onBeforeRender() {
				final BankPortalCashInDataProvider dataProvider = (BankPortalCashInDataProvider) internalGetDataProvider();
				dataProvider.setcashInList(cashInBean.getCashInList());
				refreshTotalItemCount();
				super.onBeforeRender();
			}

			@Override
			protected void populateItem(final Item<BankCashinBean> item) {
				final BankCashinBean entry = item.getModelObject();

				// Add the customer id Link
				final AjaxLink<BankCashinBean> customerIdLink = new AjaxLink<BankCashinBean>("idLink", item.getModel()) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						final BankCashinBean cashInBean = item.getModelObject();
						setResponsePage(new BankPortalCashinDetailsPage(cashInBean));
					}
				};
				customerIdLink.add(new Label("customerIdLink", entry.getCustomerId()));
				item.add(customerIdLink);

				// Add the MSISDN value
				item.add(new Label("msisdnValue", entry.getMsisdn()));

				// Add the Display Name value
				item.add(new Label("displayName", entry.getDisplayName()));

				final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
				item.add(new SimpleAttributeModifier("class", cssStyle));
			}

			private void refreshTotalItemCount() {
				final int size = internalGetDataProvider().size();
				cashInTotalItemString = new Integer(size).toString();
				if (size > 0) {
					cashInStartIndex = getCurrentPage() * getItemsPerPage() + 1;
					cashInEndIndex = cashInStartIndex + getItemsPerPage() - 1;
					if (cashInEndIndex > size) {
						cashInEndIndex = size;
					}
				} else {
					cashInStartIndex = 0;
					cashInEndIndex = 0;
				}
			}

			@Override
			public boolean isVisible() {
				final BankPortalCashInDataProvider dataProvider = (BankPortalCashInDataProvider) internalGetDataProvider();
				dataProvider.setcashInList(cashInBean.getCashInList());
				return cashInBean.getCashInList().size() != 0;

			}
		};

		// Add the navigation
		final BtpnCustomPagingNavigator navigator = new BtpnCustomPagingNavigator(WICKET_ID_cashInNavigator, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return cashInBean != null && cashInBean.getCashInList().size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);
		dataView.setItemsPerPage(20);

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = BankPortalCashinPanel.this.getLocalizer().getString(
					"cashIn.totalitems.header", BankPortalCashinPanel.this);
				return String.format(displayTotalItemsText, cashInTotalItemString, cashInEndIndex, cashInEndIndex);
			}

		};

		final Label cashInHeader = new Label(WICKET_ID_cashInTotalItems, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return cashInBean != null && cashInBean.getCashInList().size() != 0;
			}
		};
		cashInHeader.setOutputMarkupId(true);
		cashInHeader.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(cashInHeader);

		// Add the no items label.
		dataViewContainer.add(new Label("no.items", getLocalizer().getString("cashIn.emptyRecordsMessage", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return cashInBean != null && cashInBean.getCashInList().size() == 0;

			}
		}.setRenderBodyOnly(true).setOutputMarkupPlaceholderTag(true));

		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByCustomerId", "customerId", cashInDataProvider, dataView));
		dataViewContainer
			.add(new BtpnOrderByOrder("orderByMobileNumber", "mobileNumber", cashInDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDisplayName", "displayName", cashInDataProvider, dataView));
		dataViewContainer.addOrReplace(dataView);

	}

	/**
	 * This method fetches the list of CashIn Transaction List.
	 */
	private void getCashInTransactionList(final String internationalFormatPhNo) {
		List<BankCashinBean> cashInList = new ArrayList<BankCashinBean>();
		try {
			final GetTransactionCustomerRequest request = basePage
				.getNewMobiliserRequest(GetTransactionCustomerRequest.class);
			request.setMsisdn(internationalFormatPhNo);
			request.setCustomerName(cashInBean.getName());
			request.getProductCategories().add(BtpnConstants.PRODUCT_CATEGORY);
			final GetTransactionCustomerResponse response = basePage.getSupportClient().getTransactionCustomer(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				cashInList = ConverterUtils.convertToBankCashinBean(response.getTransactionCustomers());
			} else {
				error(getLocalizer().getString("error.search", this));
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling getTransactionCustomer service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		cashInBean.setCashInList(cashInList);
	}

	/**
	 * This method fetches the list of CashIn Transaction List.
	 */
	private void handleSearchCashIn(AjaxRequestTarget target) {
		String internationalFormatPhNo = null;
		// Format MSISDN
		if (PortalUtils.exists(cashInBean.getMsisdn())) {
			final PhoneNumber phoneNumber = new PhoneNumber(cashInBean.getMsisdn(), basePage.getBankPortalPrefsConfig()
				.getDefaultCountryCode());
			internationalFormatPhNo = phoneNumber.getInternationalFormat();
		}
		getCashInTransactionList(internationalFormatPhNo);
		cashInContainer.setVisible(true);
		target.addComponent(cashInContainer);
		target.addComponent(feedBack);
		target.addComponent(msisdnComponent);
		target.addComponent(nameComponent);
	}
}
