package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.slf4j.Logger;

import com.btpnwow.core.customer.facade.contract.ApproveCustomerExWrkRequest;
import com.btpnwow.core.customer.facade.contract.ApproveCustomerExWrkResponse;
import com.btpnwow.core.customer.facade.contract.RejectCustomerExWrkRequest;
import com.btpnwow.core.customer.facade.contract.RejectCustomerExWrkResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankCheckerApprovalPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;

/**
 * This is the Confirm Registration Panel for Consumers, Agents in bank portal and Child Agents, Sub agents in agent
 * portal.
 * 
 * @author Vikram Gunda
 */
public class BankCheckerConfirmApprovalPanel extends ConfirmRegistrationPanel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(BankCheckerConfirmApprovalPanel.class);

	/**
	 * Constructor for this page.
	 * 
	 * @param id id Of the Panel
	 * @param basePage basePage of the Panel
	 * @param customer customer Object
	 */
	public BankCheckerConfirmApprovalPanel(String id, CustomerRegistrationBean customer, BtpnMobiliserBasePage basePage) {
		super(id, customer, basePage);
	}

	/**
	 * This method is used for add RegistrationType Info.
	 */
	@Override
	protected void addRegistrationTypeInfo(final Form<ConfirmRegistrationPanel> form, final String customerType) {
		form.add(new WebMarkupContainer("createdByContainer").add(new Label("customer.createdBy")).setVisible(true));
		form.add(new WebMarkupContainer("createdDateContainer").add(new Label("customer.createdDate")).setVisible(true));
		form.add(new Label("registrationType.headLine", getLocalizer().getString(customerType + ".registrationType.headLine", this)));
		// form.add(new Label("confirmData.registration.type", getLocalizer().getString(customerType + ".confirmData.registration.type", this)));
		// form.add(new Label("customer.registrationType.value"));
		form.add(new Label("customer.requestType"));
		form.add(new Label("customer.productType.value"));
		form.add(new Label("customer.marketingSourceCode"));
		form.add(new Label("customer.referralNumber"));
		form.add(new Label("customer.agentCode"));
		form.add(new Label("customer.territoryCode"));
		form.add(new WebMarkupContainer("statusContainer").add(new Label("customer.status")).setVisible(false));
	}

	/**
	 * This method is for adding Approve button to approve the Consumers, Bank Staff, Top Agents.
	 */
	@Override
	protected Button addBackButton(final boolean agentPortal) {
		Button backButton = new Button("back") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				BankCheckerConfirmApprovalPanel.this.handleReject();
				
				setResponsePageOnClick(agentPortal);
			}
		};
		
		backButton.setDefaultFormProcessing(true);
		
		return backButton;
	}

	/**
	 * This method adds the reject button for the Reject Panel.
	 */
	@Override
	protected Button addConfirmButton(final boolean agentPortal) {
		Button submitButton = new Button("confirm") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				BankCheckerConfirmApprovalPanel.this.handleApprove();
				
				setResponsePageOnClick(agentPortal);
			}
		};
		
		return submitButton;
	}

	/**
	 * This method adds the cancel button for the Registration Panel.
	 */
	@Override
	protected Button addCancelButton(final boolean agentPortal) {
		Button cancelButton = new Button("cancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);

			};
		};
		
		cancelButton.setDefaultFormProcessing(false);
		return cancelButton;
	}

	private void handleApprove() {
		try {
			ApproveCustomerExWrkRequest request = MobiliserUtils.fill(new ApproveCustomerExWrkRequest(), basePage);
			request.setCallerId(Long.valueOf(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
			request.setWorkflowId(customer.getTaskId());
			
			// customer.getRequestType()
			
			ApproveCustomerExWrkResponse response = customerWrkFacade.approveWrk(request);
			
			if (MobiliserUtils.success(response)) {
				basePage.getWebSession().info(getLocalizer().getString("approve.success", this));
				
				setResponsePage(BankCheckerApprovalPage.class);
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Exception e) {
			LOG.error("An exception was thrown.", e);
			
			error(getLocalizer().getString("approval.failure.exception", this));
		}
	}

	private void handleReject() {
		try {
			RejectCustomerExWrkRequest request = MobiliserUtils.fill(new RejectCustomerExWrkRequest(), basePage);
			request.setCallerId(Long.valueOf(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
			request.setWorkflowId(customer.getTaskId());
			
			RejectCustomerExWrkResponse response = customerWrkFacade.rejectWrk(request);
			
			if (MobiliserUtils.success(response)) {
				basePage.getWebSession().info(getLocalizer().getString("reject.success", this));
				
				setResponsePage(BankCheckerApprovalPage.class);
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Exception e) {
			LOG.error("An exception was thrown.", e);
			
			error(getLocalizer().getString("approval.failure.exception", this));
		}
	}

	private void setResponsePageOnClick(final boolean agentPortal) {
		setResponsePage(new BankCheckerApprovalPage());
	}
}
