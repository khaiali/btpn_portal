package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.changemsisdn.ChangeMsisdnDetails;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.changemsisdn.ChangeMsisdnRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.changemsisdn.ChangeMsisdnResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.SearchCustomerCareMenu;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.common.panels.AttachmentsPanel;
import com.sybase365.mobiliser.web.util.PhoneNumber;

/**
 * This is the ChangeMsisdnPanel for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ChangeMsisdnPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	private String oldMsisdn;

	private String newMsisdn;

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeMsisdnPanel.class);

	public ChangeMsisdnPanel(String id, SearchCustomerCareMenu basePage) {
		this(id, basePage, null);
	}

	public ChangeMsisdnPanel(String id, SearchCustomerCareMenu basePage, AttachmentsPanel attachmentsPanel) {
		super(id);
		this.basePage = basePage;
		setOldMsisdn(basePage.getMobiliserWebSession().getCustomerRegistrationBean().getMobileNumber());
		constructPanel();
	}

	protected void constructPanel() {
		final Form<ChangeMsisdnPanel> form = new Form<ChangeMsisdnPanel>("changeMsisdnForm",
			new CompoundPropertyModel<ChangeMsisdnPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new TextField<String>("oldMsisdn").add(new ErrorIndicator()));
		form.add(new TextField<String>("newMsisdn").setRequired(true)
			.add(new PatternValidator(this.basePage.getBankPortalPrefsConfig().getMobileRegex()))
			.add(BtpnConstants.PHONE_NUMBER_VALIDATOR).add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH)
			.add(new ErrorIndicator()));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				changeMobileNumber();
			};
		});
		add(form);
	}

	/**
	 * calling changeMsisdn service from change MSISDN end point
	 */
	private void changeMobileNumber() {
		try {
			final ChangeMsisdnRequest request = basePage.getNewMobiliserRequest(ChangeMsisdnRequest.class);
			ChangeMsisdnDetails msisdnDetails = new ChangeMsisdnDetails();
			msisdnDetails.setCurrentMsisdn(getOldMsisdn());
			msisdnDetails.setNewMsisdn(formateMsisdn(getNewMsisdn()));
			msisdnDetails.setStatus(BtpnConstants.CHANGE_MSISDN_STATUS);
			request.setMakerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setMsisdnDetail(msisdnDetails);
			ChangeMsisdnResponse changeMsisdnResponse = basePage.getChangeMsisdnClient().changeMsisdn(request);
			if (basePage.evaluateBankPortalMobiliserResponse(changeMsisdnResponse)) {
				info(getLocalizer().getString("changemsisdn.success", ChangeMsisdnPanel.this));
			} else {
				error(changeMsisdnResponse.getStatus().getValue());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling changeMsisdn service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	private String formateMsisdn(String msisdn) {
		final PhoneNumber phoneNumber = new PhoneNumber(msisdn, basePage.getAgentPortalPrefsConfig()
			.getDefaultCountryCode());
		return phoneNumber.getInternationalFormat();
	}

	public String getOldMsisdn() {
		return oldMsisdn;
	}

	public void setOldMsisdn(String oldMsisdn) {
		this.oldMsisdn = oldMsisdn;
	}

	public String getNewMsisdn() {
		return newMsisdn;
	}

	public void setNewMsisdn(String newMsisdn) {
		this.newMsisdn = newMsisdn;
	}

}
