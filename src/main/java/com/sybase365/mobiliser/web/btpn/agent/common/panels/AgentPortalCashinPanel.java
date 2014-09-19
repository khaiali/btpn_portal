package com.sybase365.mobiliser.web.btpn.agent.common.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetTransactionCustomerRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetTransactionCustomerResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.MsisdnExistsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerByIdentificationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerByIdentificationResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashinBean;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin.AgentPortalCashinDetailsPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the CashinPanel page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentPortalCashinPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(AgentPortalCashinPanel.class);

	protected BtpnMobiliserBasePage basePage;

	protected AgentCashinBean cashInBean;

	public AgentPortalCashinPanel(String id, BtpnBaseAgentPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}

	protected void constructPanel() {
		final Form<AgentPortalCashinPanel> form = new Form<AgentPortalCashinPanel>("agentCashInForm",
			new CompoundPropertyModel<AgentPortalCashinPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new TextField<String>("cashInBean.payeeMsisdn").setRequired(true)
			.add(new PatternValidator(this.basePage.getBankPortalPrefsConfig().getMobileRegex()))
			.add(BtpnConstants.PHONE_NUMBER_VALIDATOR).add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH)
			.add(new ErrorIndicator()));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				performCashIn();
			};
		});
		add(form);
	}

	/**
	 * calling getTransactionCustomer from support end point.
	 */
	public AgentCashinBean getAgentCashInTransactionList() {
		AgentCashinBean agentCashInBean = null;
		try {
			final GetTransactionCustomerRequest request = basePage
				.getNewMobiliserRequest(GetTransactionCustomerRequest.class);
			request.setMsisdn(cashInBean.getPayeeMsisdn());
			GetTransactionCustomerResponse response = basePage.getSupportClient().getTransactionCustomer(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				return ConverterUtils.convertToAgentCashinBean(response.getTransactionCustomers());
			} else {
				// Specific error message handling
				final String messageKey = "error." + response.getStatus().getCode();
				handleSpecificErrorMessage(messageKey, "error.cashin.fail");
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling getTransactionCustomer service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		return agentCashInBean;
	}

	private void performCashIn() {
		// Format MSISDN
		cashInBean.setPayeeMsisdn(formateMsisdn(cashInBean.getPayeeMsisdn()));
		int cashinMsisdn = validateMsisdn(cashInBean.getPayeeMsisdn(), false);
		int loginMsisdn = validateMsisdn(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername(),
			true);
		String otherMessageKey = "invalid.msisdn." + cashinMsisdn;

		// TopAgent Cash in
		if (loginMsisdn == BtpnConstants.VALID_TOP_AGENT_MSISDN_RESPONSE_CODE) {
			if (cashinMsisdn == BtpnConstants.VALID_CHILD_AGENT_MSISDN_RESPONSE_CODE) {
				if (isCashInHierarchySuccess(cashInBean.getPayeeMsisdn())) {
					setResponsePage(new AgentPortalCashinDetailsPage(getAgentCashInTransactionList()));
				} else {
					error(getLocalizer().getString("hierarchy.not.match", AgentPortalCashinPanel.this));
				}
			} else {
				String messageKey = "error.invalid.msisdn." + loginMsisdn + "." + cashinMsisdn;
				handleSpecificErrorMessage(messageKey, otherMessageKey);
			}
			// Agent Cash in
		} else {
			if (cashinMsisdn == BtpnConstants.VALID_CUSTOMER_MSISDN_RESPONSE_CODE) {
				setResponsePage(new AgentPortalCashinDetailsPage(getAgentCashInTransactionList()));
			} else {
				String messageKey = "error.invalid.msisdn." + loginMsisdn + "." + cashinMsisdn;
				handleSpecificErrorMessage(messageKey, otherMessageKey);
			}
		}
	}

	/**
	 * @param this method will validate the MSISDN
	 * @return
	 */
	private int validateMsisdn(String msisdn, boolean isPayer) {
		int statusCode = 0;
		try {
			MsisdnExistsResponse response = basePage.checkMsisdnExists(msisdn, isPayer);
			if (PortalUtils.exists(response)) {
				statusCode = response.getStatus().getCode();
			}
		} catch (Exception ex) {
			LOG.error("Error while calling missdnExists from support end point..", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		return statusCode;
	}

	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final String messageKey, final String genericKey) {
		String message = getLocalizer().getString(messageKey, this);
		// Generic error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString(genericKey, this);
		}
		error(message);
	}

	private boolean isCashInHierarchySuccess(String msisdn) {
		String cashInCustomerParentId = getCashInCustomerParentId(msisdn);
		String loginCustomerId = String.valueOf(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer()
			.getCustomerId());
		if (cashInCustomerParentId.equals(loginCustomerId)) {
			return true;
		}
		return false;
	}

	private String getCashInCustomerParentId(String msisdn) {
		GetCustomerByIdentificationResponse response = null;
		try {
			final GetCustomerByIdentificationRequest request = basePage
				.getNewMobiliserRequest(GetCustomerByIdentificationRequest.class);
			request.setIdentification(msisdn);
			request.setIdentificationType(BtpnConstants.IDENTIFICATION_TYPE);
			response = basePage.getCustomerClient().getCustomerByIdentification(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				return String.valueOf(response.getCustomer().getParentId());
			} else {
				// Specific error message handling
				final String messageKey = "error." + response.getStatus().getCode();
				handleSpecificErrorMessage(messageKey, "error.cashin.fail");
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling getCustomerByIdentification service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		return null;
	}

	private String formateMsisdn(String msisdn) {
		final PhoneNumber phoneNumber = new PhoneNumber(msisdn, basePage.getAgentPortalPrefsConfig()
			.getDefaultCountryCode());
		return phoneNumber.getInternationalFormat();
	}

}
