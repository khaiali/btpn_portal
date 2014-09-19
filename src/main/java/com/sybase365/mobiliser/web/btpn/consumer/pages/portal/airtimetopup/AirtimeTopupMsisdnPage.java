package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.airtimetopup;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.common.components.FavouriteDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.AirtimePerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is Performing page for Airtime Topup Page. Here the topup amount, msisdn to be topped up is selected.
 * 
 * @author Vikram Gunda
 */
public class AirtimeTopupMsisdnPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private AirtimePerformBean airtimeBean;

	public AirtimeTopupMsisdnPage(final AirtimePerformBean airtimeBean) {
		super();
		this.airtimeBean = airtimeBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		final Form<AirtimeTopupMsisdnPage> form = new Form<AirtimeTopupMsisdnPage>("airtimeMsisdnForm",
			new CompoundPropertyModel<AirtimeTopupMsisdnPage>(this));

		form.add(new FeedbackPanel("errorMessages"));

		// Add the radio button for manual or favourite
		final RadioGroup<String> rg = new RadioGroup<String>("airtimeBean.manualOrFavourite");
		airtimeBean.setManualOrFavourite(BtpnConstants.BILLPAYMENT_MANUALLY);
		rg.add(new Radio<String>("radio.manually", Model.of(BtpnConstants.BILLPAYMENT_MANUALLY)));
		rg.add(new Radio<String>("radio.favourite", Model.of(BtpnConstants.BILLPAYMENT_FAVLIST)));
		form.add(rg);

		// Add the favourite container.
		final WebMarkupContainer favouriteContainer = new WebMarkupContainer("favouriteContainer");
		favouriteContainer
			.add(new FavouriteDropdownChoice("airtimeBean.selectedMsisdn", false, true,
				BtpnConstants.USECASE_AIR_TIME_TOPUP, this.getMobiliserWebSession().getBtpnLoggedInCustomer()
					.getCustomerId()).setNullValid(false).setRequired(true).add(new ErrorIndicator()));
		favouriteContainer.setOutputMarkupPlaceholderTag(true);
		favouriteContainer.setVisible(false);
		favouriteContainer.setOutputMarkupId(true);
		form.add(favouriteContainer);

		// Add the manual container.
		final WebMarkupContainer manualContainer = new WebMarkupContainer("manualContainer");
		manualContainer.add(new RequiredTextField<String>("airtimeBean.selectedMsisdn.id").add(new ErrorIndicator()));
		manualContainer.setOutputMarkupPlaceholderTag(true);
		manualContainer.setOutputMarkupId(true);
		form.add(manualContainer);

		// Submit button for form.
		addSubmitButton(form);

		// Add On change behaviour for radio button.
		rg.add(new FavouriteChoiceComponentUpdatingBehavior(favouriteContainer, manualContainer));
		add(form);
	}

	/**
	 * This is used to add submit button.
	 */
	private void addSubmitButton(final Form<AirtimeTopupMsisdnPage> form) {

		final Button btnSubmit = new Button("btnSubmit") {

			private static final long serialVersionUID = 1L;

			public void onSubmit() {
				handlePerformAirtimeTopup();
			};
		};
		form.add(btnSubmit);
	}

	/**
	 * This is class for displaying favourite list or not based on selection.
	 */
	private class FavouriteChoiceComponentUpdatingBehavior extends AjaxFormChoiceComponentUpdatingBehavior {

		private static final long serialVersionUID = 1L;

		private WebMarkupContainer favouriteContainer;

		private WebMarkupContainer manualContainer;

		public FavouriteChoiceComponentUpdatingBehavior(final WebMarkupContainer favouriteContainer,
			final WebMarkupContainer manualContainer) {
			super();
			this.manualContainer = manualContainer;
			this.favouriteContainer = favouriteContainer;
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			final boolean isManual = airtimeBean.getManualOrFavourite().equals(BtpnConstants.BILLPAYMENT_MANUALLY);
			manualContainer.setVisible(isManual);
			favouriteContainer.setVisible(!isManual);
			target.addComponent(favouriteContainer);
			target.addComponent(manualContainer);
		}

	}

	/**
	 * This method is used for performing Airtime Topup.
	 */
	private void handlePerformAirtimeTopup() {
		airtimeBean.setSourceMsisdn(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
		setResponsePage(new AirtimeTopupDenominationsPage(airtimeBean));
	}

}
