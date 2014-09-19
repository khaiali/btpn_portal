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
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetTransactionCustomerRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetTransactionCustomerResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.BankPortalCashOutDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalCashOutDetailsPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.common.panels.AttachmentsPanel;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the CashOutPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashOutPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BankPortalCashOutPanel.class);

	private FeedbackPanel feedBack;

	protected BtpnMobiliserBasePage basePage;

	protected BankCashOutBean cashOutBean;

	private WebMarkupContainer cashOutContainer;

	private BankPortalCashOutDataProvider cashOutDataProvider;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_cashOutTotalItems = "cashOutHeader";

	private static final String WICKET_ID_cashOutNavigator = "cashOutNavigator";

	private String cashOutTotalItemString;

	private int cashOutStartIndex = 0;

	private int cashOutEndIndex = 0;

	private Component msisdnComponent;

	private Component nameComponent;

	public BankPortalCashOutPanel(String id, BtpnBaseBankPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		// Adds the panel
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCashOutPanel> form = new Form<BankPortalCashOutPanel>("cashOutForm",
			new CompoundPropertyModel<BankPortalCashOutPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		form.add(msisdnComponent = new TextField<String>("cashOutBean.mobileNumber")
			.add(new PatternValidator(this.basePage.getBankPortalPrefsConfig().getMobileRegex()))
			.add(BtpnConstants.PHONE_NUMBER_VALIDATOR).add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH)
			.add(new ErrorIndicator()));
		msisdnComponent.setOutputMarkupId(true);

		form.add(nameComponent = new TextField<String>("cashOutBean.name").add(
			BtpnConstants.REGISTRATION_DISPLAY_NAME_MAX_LENGTH).add(new ErrorIndicator()));
		nameComponent.setOutputMarkupId(true);

		cashOutContainer = new WebMarkupContainer("cashOutContainer");
		cashOutListDataView(cashOutContainer);
		cashOutContainer.setOutputMarkupId(true);
		cashOutContainer.setOutputMarkupPlaceholderTag(true);
		cashOutContainer.setVisible(false);
		form.add(cashOutContainer);

		form.add(new AjaxButton("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (!PortalUtils.exists(cashOutBean)) {
					cashOutBean = new BankCashOutBean();
				}
				cashOutBean.setCashOutList(new ArrayList<BankCashOutBean>());
				handleSearchCashOut(target);
			};

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBack);
				target.addComponent(msisdnComponent);
				target.addComponent(nameComponent);
				target.addComponent(cashOutContainer);
				super.onError(target, form);
			}
		});

		add(form);
	}

	/**
	 * This method populate the cash Out details.
	 */
	protected void cashOutListDataView(final WebMarkupContainer dataViewContainer) {
		cashOutDataProvider = new BankPortalCashOutDataProvider("customerId");

		final DataView<BankCashOutBean> dataView = new DataView<BankCashOutBean>(WICKET_ID_PAGEABLE,
			cashOutDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onBeforeRender() {
				final BankPortalCashOutDataProvider dataProvider = (BankPortalCashOutDataProvider) internalGetDataProvider();
				dataProvider.setcashOutList(cashOutBean.getCashOutList());
				refreshTotalItemCount();
				super.onBeforeRender();
			}

			@Override
			protected void populateItem(final Item<BankCashOutBean> item) {
				final BankCashOutBean entry = item.getModelObject();

				// Add the customer id Link
				final AjaxLink<BankCashOutBean> customerIdLink = new AjaxLink<BankCashOutBean>("idLink",
					item.getModel()) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						final BankCashOutBean cashOutBean = item.getModelObject();
						setResponsePage(new BankPortalCashOutDetailsPage(cashOutBean));
					}
				};
				customerIdLink.add(new Label("customerIdLink", entry.getCustomerId()));
				item.add(customerIdLink);

				// Add the MSISDN value
				item.add(new Label("msisdnValue", entry.getMobileNumber()));

				// Add the Display Name value
				item.add(new Label("displayName", entry.getDisplayName()));

				final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
				item.add(new SimpleAttributeModifier("class", cssStyle));
			}

			private void refreshTotalItemCount() {
				final int size = internalGetDataProvider().size();
				cashOutTotalItemString = new Integer(size).toString();
				if (size > 0) {
					cashOutStartIndex = getCurrentPage() * getItemsPerPage() + 1;
					cashOutEndIndex = cashOutStartIndex + getItemsPerPage() - 1;
					if (cashOutEndIndex > size) {
						cashOutEndIndex = size;
					}
				} else {
					cashOutStartIndex = 0;
					cashOutEndIndex = 0;
				}
			}

			@Override
			public boolean isVisible() {
				final BankPortalCashOutDataProvider dataProvider = (BankPortalCashOutDataProvider) internalGetDataProvider();
				dataProvider.setcashOutList(cashOutBean.getCashOutList());
				return cashOutBean.getCashOutList().size() != 0;

			}
		};

		// Add the navigation
		final BtpnCustomPagingNavigator navigator = new BtpnCustomPagingNavigator(WICKET_ID_cashOutNavigator, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return cashOutBean != null && cashOutBean.getCashOutList().size() != 0;
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
				final String displayTotalItemsText = BankPortalCashOutPanel.this.getLocalizer().getString(
					"cashOut.totalitems.header", BankPortalCashOutPanel.this);
				return String.format(displayTotalItemsText, cashOutTotalItemString, cashOutEndIndex, cashOutEndIndex);
			}

		};

		final Label cashOutHeader = new Label(WICKET_ID_cashOutTotalItems, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return cashOutBean != null && cashOutBean.getCashOutList().size() != 0;
			}
		};
		cashOutHeader.setOutputMarkupId(true);
		cashOutHeader.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(cashOutHeader);

		// Add the no items label.
		dataViewContainer.add(new Label("no.items", getLocalizer().getString("cashOut.emptyRecordsMessage", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return cashOutBean != null && cashOutBean.getCashOutList().size() == 0;

			}
		}.setRenderBodyOnly(true).setOutputMarkupPlaceholderTag(true));

		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByCustomerId", "customerId", cashOutDataProvider, dataView));
		dataViewContainer
			.add(new BtpnOrderByOrder("orderByMobileNumber", "mobileNumber", cashOutDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDisplayName", "displayName", cashOutDataProvider, dataView));
		dataViewContainer.addOrReplace(dataView);

	}

	/**
	 * This method fetches the list of CashOut Transaction List.
	 */
	private void handleSearchCashOut(AjaxRequestTarget target) {
		String internationalFormatPhNo = null;
		// Format MSISDN
		if (PortalUtils.exists(cashOutBean.getMobileNumber())) {
			final PhoneNumber phoneNumber = new PhoneNumber(cashOutBean.getMobileNumber(), basePage
				.getBankPortalPrefsConfig().getDefaultCountryCode());
			internationalFormatPhNo = phoneNumber.getInternationalFormat();
		}
		getCashOutTransactionList(internationalFormatPhNo);
		cashOutContainer.setVisible(true);
		target.addComponent(cashOutContainer);
		target.addComponent(feedBack);
		target.addComponent(msisdnComponent);
		target.addComponent(nameComponent);
	}

	/**
	 * This method fetches the list of CashOut Transaction List.
	 */
	private void getCashOutTransactionList(final String internationalFormatPhNo) {
		List<BankCashOutBean> cashOutList = new ArrayList<BankCashOutBean>();
		try {
			final GetTransactionCustomerRequest request = basePage
				.getNewMobiliserRequest(GetTransactionCustomerRequest.class);
			request.setMsisdn(internationalFormatPhNo);
			request.setCustomerName(cashOutBean.getName());
			request.getProductCategories().add(BtpnConstants.PRODUCT_CATEGORY);
			final GetTransactionCustomerResponse response = basePage.getSupportClient().getTransactionCustomer(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				cashOutList = ConverterUtils.convertToBankCashOutBean(response.getTransactionCustomers());
			} else {
				error(getLocalizer().getString("error.search", this));
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling getTransactionCustomer service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		cashOutBean.setCashOutList(cashOutList);
	}

}
