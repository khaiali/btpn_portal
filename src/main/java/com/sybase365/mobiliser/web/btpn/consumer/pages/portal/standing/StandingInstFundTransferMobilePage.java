package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.common.components.FavouriteDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class StandingInstFundTransferMobilePage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(StandingInstFundTransferMobilePage.class);

	protected FundTransferBean fundTransfer;
	private String filterType = BtpnConstants.FILTERTYPE_MANUAL;

	private WebMarkupContainer favouriteContainer;
	private WebMarkupContainer manualContainer;

	/**
	 * Standing instructions mobile page.
	 * 
	 * @param fundTransfer
	 */
	public StandingInstFundTransferMobilePage(final FundTransferBean fundTransfer) {
		super();
		this.fundTransfer = fundTransfer;
		initThisPageComponents();
	}

	/**
	 * Initialize the current Page componenets.
	 */
	protected void initThisPageComponents() {
		constructPanel();
	}

	/**
	 * Construct the panel for the Standing instruction fund transfer mobile page.
	 */
	protected void constructPanel() {
		
		final Form<StandingInstFundTransferMobilePage> form = new Form<StandingInstFundTransferMobilePage>(
			"fundTransferMobileForm", new CompoundPropertyModel<StandingInstFundTransferMobilePage>(this));

		// Add feedback panel for Error Messages
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);

		// Add the radio button for manual or favourite
		final RadioGroup<String> rg = new RadioGroup<String>("filterType");
		rg.add(new Radio<String>("radio.manually", Model.of(BtpnConstants.FILTERTYPE_MANUAL)));
		rg.add(new Radio<String>("radio.favourite", Model.of(BtpnConstants.FILTERTYPE_FAVORITE)));
		form.add(rg);

		// Add the manual container.
		manualContainer = new WebMarkupContainer("manualContainer");
		manualContainer.add(new TextField<String>("fundTransfer.payeeMsisdn").setRequired(true)
			.add(new PatternValidator(BtpnConstants.REGEX_PHONE_NUMBER)).add(BtpnConstants.PHONE_NUMBER_VALIDATOR)
			.add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH).add(new ErrorIndicator()));
		manualContainer.setOutputMarkupPlaceholderTag(true);
		manualContainer.setOutputMarkupId(true);
		form.add(manualContainer);

		form.add(new AmountTextField<Long>("fundTransfer.amount", Long.class, false).setRequired(true).add(
			new ErrorIndicator()));

		// Add the favourite container.
		favouriteContainer = new WebMarkupContainer("favouriteContainer");
		favouriteContainer.add(new FavouriteDropdownChoice("fundTransfer.favoriteNum", false, true,
			this.getMobiliserWebSession().getBtpnLoggedInCustomer()
				.getCustomerId(), 1).setNullValid(false).setRequired(true).add(new ErrorIndicator()));
		favouriteContainer.setOutputMarkupPlaceholderTag(true);
		favouriteContainer.setVisible(false);
		favouriteContainer.setOutputMarkupId(true);
		form.add(favouriteContainer);

		// Submit button for form.
		addSubmitButton(form);

		// Add On change behaviour for radio button.
		rg.add(new FavouriteViewChoiceComponentUpdatingBehavior());
		add(form);
	}

	/**
	 * This is used to add submit button.
	 */
	private void addSubmitButton(final Form<StandingInstFundTransferMobilePage> form) {
		final Button btnSubmit = new Button("btnSubmit") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handlePerformFundTransfer();
			};
		};
		form.add(btnSubmit);
	}

	/**
	 * This method is used for performing Fund Transfer for mobile page standing instruction.
	 */
//	private void handlePerformFundTransfer() {
//		
//		try {
//			
//			final PhoneNumber phoneNumber = new PhoneNumber(fundTransfer.getPayeeMsisdn(), this
//				.getAgentPortalPrefsConfig().getDefaultCountryCode());
//			fundTransfer.setPayeeMsisdn(phoneNumber.getInternationalFormat());
//			final MsisdnExistsResponse response = this.checkMsisdnExists(fundTransfer.getPayeeMsisdn(), false);
//			final int statusCode = response.getStatus().getCode();
//			final boolean isProceed = statusCode == BtpnConstants.MSISDN_UNREGISTERED
//					|| statusCode == BtpnConstants.MSISDN_CLOSED
//					|| statusCode == BtpnConstants.VALID_CUSTOMER_MSISDN_RESPONSE_CODE ? true : false;
//			if (isProceed) {
//				setResponsePage(new ConfirmFundTransferPage(fundTransfer, "Mobile 2 Mobile"));
//			} else {
//				error(getLocalizer().getString("error.msisdn", this));
//			}
//
//		} catch (Exception ex) {
//			LOG.error("An exception occured while calling the Msisdn Exists ===> ");
//			error(getLocalizer().getString("error.exception", this));
//		}
//	}
	
	
	private void handlePerformFundTransfer() {
		
		try {
			
			if(PortalUtils.exists(fundTransfer.getPayeeMsisdn())){
				final PhoneNumber phoneNumber = new PhoneNumber(fundTransfer.getPayeeMsisdn(), this
						.getAgentPortalPrefsConfig().getDefaultCountryCode());
				fundTransfer.setPayeeMsisdn(phoneNumber.getNationalFormat());
			}
			
			if (PortalUtils.exists(fundTransfer.getFavoriteNum())){
				fundTransfer.setPayeeMsisdn(fundTransfer.getFavoriteNum().getId());
			}
			
			setResponsePage(new ConfirmFundTransferPage(fundTransfer));

		} catch (Exception ex) {
			LOG.error("An exception occured while calling the Msisdn Exists ===> ");
			error(getLocalizer().getString("error.exception", this));
		}
	}

	/**
	 * This is class for displaying favourite list or not based on selection.
	 */
	private class FavouriteViewChoiceComponentUpdatingBehavior extends AjaxFormChoiceComponentUpdatingBehavior {

		private static final long serialVersionUID = 1L;

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			final boolean isManual = filterType.equals(BtpnConstants.FILTERTYPE_MANUAL);
			manualContainer.setVisible(isManual);
			favouriteContainer.setVisible(!isManual);
			target.addComponent(favouriteContainer);
			target.addComponent(manualContainer);
		}

	}
}
