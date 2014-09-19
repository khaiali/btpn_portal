package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.airtimetopup;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.topup.ResponseData;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.topup.PerformAirtimeTopUpRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.topup.PerformAirtimeTopUpResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.common.components.AirtimeDenominationDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.AirtimePerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is Confirm Page for topup. Consumer enters the pin to perform topup.
 * 
 * @author Vikram Gunda
 */
public class AirtimeTopupDenominationsPage extends BtpnBaseConsumerPortalSelfCarePage {

	private AirtimePerformBean airtimeBean;

	private static final Logger LOG = LoggerFactory.getLogger(AirtimeTopupDenominationsPage.class);

	public AirtimeTopupDenominationsPage(final AirtimePerformBean airtimeBean) {
		super();
		this.airtimeBean = airtimeBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		final Form<AirtimeTopupDenominationsPage> form = new Form<AirtimeTopupDenominationsPage>(
			"airtimeDenominationForm", new CompoundPropertyModel<AirtimeTopupDenominationsPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new AirtimeDenominationDropdownChoice("airtimeBean.denomination", false, true, airtimeBean.getTelco()
			.getId()).setRequired(true).add(new ErrorIndicator()));
		addSubmitButton(form);
		add(form);
	}

	/**
	 * This is used to add submit button.
	 */
	private void addSubmitButton(final Form<AirtimeTopupDenominationsPage> form) {

		final Button btnSubmit = new Button("btnSubmit") {

			private static final long serialVersionUID = 1L;

			public void onSubmit() {
				handlePerformAirtimeDenomination();
			};
		};
		form.add(btnSubmit);
	}

	/**
	 * This method is used for performing Airtime Topup.
	 */
	private void handlePerformAirtimeDenomination() {
		try {
			final PerformAirtimeTopUpRequest request = getNewMobiliserRequest(PerformAirtimeTopUpRequest.class);
			request.setTopup(ConverterUtils.getRequestData(airtimeBean));
			final PerformAirtimeTopUpResponse response = this.airTimeClient.performAirtimeTopUp(request);
			if (evaluateConsumerPortalMobiliserResponse(response)) {
				final ResponseData responseData = response.getTopuptxn();
				airtimeBean.setFeeAmount(responseData.getFee());
				airtimeBean.setReference(responseData.getReference());
				airtimeBean.setTransactionId(responseData.getTransactionId());
				setResponsePage(new AirtimeTopupConfirmPage(airtimeBean));
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
			message = getLocalizer().getString("error.perform.airitime", this);
		}
		this.getWebSession().error(message);
	}

}
