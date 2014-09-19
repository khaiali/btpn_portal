package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.billpayment;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.billpayment.PerformBillPayRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.billpayment.PerformBillPayResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.common.components.BillPayFavouriteDropdownChoice;
import com.sybase365.mobiliser.web.btpn.common.components.SubBillerCodeDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentPerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is Performing page for bill payment which selects the sub biller code and performs the bill payment.
 * 
 * @author Vikram Gunda
 */
public class BillPaymentPerformPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private BillPaymentPerformBean billPayBean;

	private boolean isTelePhonaBillPay;

	private Component billNumberField;

	private Component favouriteField;

	private WebMarkupContainer favouriteContainer;

	private WebMarkupContainer manualContainer;

	private static final Logger LOG = LoggerFactory.getLogger(BillPaymentPerformPage.class);

	public BillPaymentPerformPage(final BillPaymentPerformBean billPayBean) {
		super();
		this.billPayBean = billPayBean;
		// If telco biller selected need to show different labels in screen.
//		isTelePhonaBillPay = billPayBean.getBillerType().getId().equals(BtpnConstants.SI_BILLER_TYPE_TELCO);
		initPageComponents();
	}

	/**
	 * This is the init page components for Bill Pay Perform page.
	 */
	protected void initPageComponents() {
		final Form<BillPaymentPerformPage> form = new Form<BillPaymentPerformPage>("subBillPaymentForm",
			new CompoundPropertyModel<BillPaymentPerformPage>(this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onError() {
				performBillPayValidations();
			};
		};
		form.add(new FeedbackPanel("errorMessages"));
		// Fetch the Sub Biller Code Dropdown choice.
//		form.add(new SubBillerCodeDropdownChoice("billPayBean.subBillerType", false, false, billPayBean.getBillerType()
//			.getId()).setRequired(true).add(new ErrorIndicator()));

		// Add the radio button for manual or favourite
		final RadioGroup<String> rg = new RadioGroup<String>("billPayBean.manualOrFavourite");
		billPayBean.setManualOrFavourite(BtpnConstants.BILLPAYMENT_MANUALLY);
		rg.add(new Radio<String>("radio.manually", Model.of(BtpnConstants.BILLPAYMENT_MANUALLY)));
		rg.add(new Radio<String>("radio.favourite", Model.of(BtpnConstants.BILLPAYMENT_FAVLIST)));
		form.add(rg);

		// Add the favourite container.
		favouriteContainer = new WebMarkupContainer("favouriteContainer");
		favouriteContainer.add(favouriteField = new BillPayFavouriteDropdownChoice("billPayBean.selectedBillerId", false,
			true, this.getMobiliserWebSession().getBtpnLoggedInCustomer()
				.getCustomerId()).setNullValid(false).add(new ErrorIndicator()));
		favouriteContainer.setOutputMarkupPlaceholderTag(true);
		favouriteContainer.setVisible(false);
		favouriteContainer.setOutputMarkupId(true);
		form.add(favouriteContainer);

		// Add the manual container.
		manualContainer = new WebMarkupContainer("manualContainer");
		final String billNumberLabel = isTelePhonaBillPay ? "label.phoneNumber" : "label.billNumber";
		manualContainer.add(new Label("label.billNumber", getLocalizer().getString(billNumberLabel, this)));
		manualContainer.add(billNumberField = new TextField<String>("billPayBean.selectedBillerId.id")
			.add(new ErrorIndicator()));
		manualContainer.setOutputMarkupPlaceholderTag(true);
		manualContainer.setOutputMarkupId(true);
		form.add(manualContainer);

		// Submit button for form.
		addSubmitButton(form);

		// Add On change behaviour for radio button.
		rg.add(new FavouriteViewChoiceComponentUpdatingBehavior());
		add(form);
	}

	/**
	 * This is used to add submit button.
	 */
	private void addSubmitButton(final Form<BillPaymentPerformPage> form) {

		final Button btnSubmit = new Button("btnSubmit") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (!performBillPayValidations()) {
					return;
				}
//				final String subBillerType = billPayBean.getSubBillerType().getId();
//				if (subBillerType.equals(BillPaymentPerformPage.this.getCustomerPortalPrefsConfig()
//					.getDefaultPlnPrePaid())) {
//					setResponsePage(new BillPaymentPlnPrePaidAmountPage(billPayBean));
//				} else {
//					handlePerformBillPayment();
//				}
			};
		};
		form.add(btnSubmit);
	}

	/**
	 * This is class for displaying favourite list or not based on selection.
	 */
	private class FavouriteViewChoiceComponentUpdatingBehavior extends AjaxFormChoiceComponentUpdatingBehavior {

		private static final long serialVersionUID = 1L;

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			final boolean isManual = billPayBean.getManualOrFavourite().equals(BtpnConstants.BILLPAYMENT_MANUALLY);
			manualContainer.setVisible(isManual);
			favouriteContainer.setVisible(!isManual);
			target.addComponent(favouriteContainer);
			target.addComponent(manualContainer);
		}

	}

	/**
	 * This method is used for performing bill payment validations
	 */
	public boolean performBillPayValidations() {
		final String errorKey = isTelePhonaBillPay ? "phonenumber.Required" : "Required";
		final Component comp = manualContainer.isVisible() ? billNumberField : favouriteField;
		if (!PortalUtils.exists(billPayBean.getSelectedBillerId())) {
			comp.error(getLocalizer().getString("billPayBean.selectedBillerId." + errorKey, this));
			return false;
		}
		if (!PortalUtils.exists(billPayBean.getSelectedBillerId().getId())) {
			comp.error(getLocalizer().getString("billPayBean.selectedBillerId." + errorKey, this));
			return false;
		}
		return true;
	}

	/**
	 * This method is used for performing bill payment.
	 */
	private void handlePerformBillPayment() {
		try {
			final PerformBillPayRequest request = this.getNewMobiliserRequest(PerformBillPayRequest.class);
			request.setBillpay(ConverterUtils.convertToBillPay(billPayBean));
			request.getBillpay().setMsisdn(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
			final PerformBillPayResponse response = this.billPaymentClient.performBillPay(request);
			if (evaluateConsumerPortalMobiliserResponse(response)) {
				ConverterUtils.convertToBillPaymentPerformBean(response, billPayBean);
				setResponsePage(new BillPaymentConfirmPage(billPayBean));
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("BillPaymentPerformPage:handlePerformBillPayment() ==> Exception occured while performing ", e);
		}
	}

	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("error.perform.billpay", this);
		}
		this.getWebSession().error(message);
	}
}
