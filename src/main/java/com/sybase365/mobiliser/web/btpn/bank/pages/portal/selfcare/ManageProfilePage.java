package com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetBankUserProfileRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetBankUserProfileResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.UpdateBankUserProfileRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.UpdateBankUserProfileResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankStaffBean;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageProfilePage extends BtpnBaseBankPortalSelfCarePage {

	private BtpnCustomer customer;

	private BankStaffBean bankStaff;

	private static final Logger LOG = LoggerFactory.getLogger(ManageProfilePage.class);

	/**
	 * Constructor for this page.
	 */
	public ManageProfilePage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		constructPage();

	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		this.customer = getMobiliserWebSession().getBtpnLoggedInCustomer();
		getBankUserProfile();
		Form<ManageProfilePage> form = new Form<ManageProfilePage>("profileForm",
			new CompoundPropertyModel<ManageProfilePage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new RequiredTextField<String>("customer.username"));
		form.add(new RequiredTextField<String>("bankStaff.name").setRequired(true)
			.add(BtpnConstants.BANK_STAFF_NAME_MAX_LENGTH).add(new ErrorIndicator()));
		form.add(new RequiredTextField<String>("bankStaff.designation").setRequired(true)
			.add(BtpnConstants.BANK_STAFF_DESIGNATION_MAX_LENGTH).add(new ErrorIndicator()));
		form.add(new TextField<String>("bankStaff.territoryCode")
			.add(new PatternValidator(BtpnConstants.BANK_STAFF_TERRITORY_CODE_REGEX))
			.add(BtpnConstants.BANK_STAFF_TERRITORY_CODE_MAX_LENGTH).add(new ErrorIndicator()));
		form.add(new RequiredTextField<String>("bankStaff.email").setRequired(true)
			.add(new PatternValidator(BtpnConstants.EMAIL_ID_REGEX)).add(BtpnConstants.EMAIL_ID_MAX_LENGTH)
			.add(new ErrorIndicator()));
		form.add(addUpdateButton());
		form.add(addCancelButton());
		// Add add Button
		add(form);
	}

	/**
	 * This method adds the Add button for the Manage Profile Page
	 */
	protected Button addUpdateButton() {
		Button submitButton = new Button("btnUpdate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleUpdateBankUserProfile();
			}
		};
		return submitButton;
	}

	/**
	 * This method adds the cancel button for the Manage Profile Page
	 */
	protected Button addCancelButton() {
		Button submitButton = new Button("btnCancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				ManageProfilePage.this.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			}
		}.setDefaultFormProcessing(false);
		return submitButton;
	}

	private void getBankUserProfile() {
		try {
			final GetBankUserProfileRequest request = this.getNewMobiliserRequest(GetBankUserProfileRequest.class);
			request.setCustomerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final GetBankUserProfileResponse response = this.getSupportClient().getBankUserProfile(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				bankStaff = ConverterUtils.convertFromBankStaffProfile(response.getBankUserProfile());
			} else {
				error(getLocalizer().getString("fetch.error", this));
			}
		} catch (Exception e) {
			LOG.error("Error in updating fetching profile ==> ", e);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	private void handleUpdateBankUserProfile() {
		try {
			final UpdateBankUserProfileRequest request = this
				.getNewMobiliserRequest(UpdateBankUserProfileRequest.class);
			request.setBankUserProfile(ConverterUtils.convertToBankStaffProfile(bankStaff));
			final UpdateBankUserProfileResponse response = this.getSupportClient().updateBankUserProfile(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				info(getLocalizer().getString("update.success", this));
				refreshSessionData();
			} else {
				error(getLocalizer().getString("update.error", this));
			}
		} catch (Exception e) {
			LOG.error("Error in updating updating profile ==> ", e);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	private void refreshSessionData() {
		final BtpnCustomer customer = this.getMobiliserWebSession().getBtpnLoggedInCustomer();
		customer.setDisplayName(bankStaff.getName());
		customer.setTerritoryCode(bankStaff.getTerritoryCode());
	}
}
