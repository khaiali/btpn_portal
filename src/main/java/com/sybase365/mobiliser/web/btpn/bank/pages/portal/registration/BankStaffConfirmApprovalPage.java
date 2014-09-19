package com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;

import com.btpnwow.core.customer.facade.api.UserWrkFacade;
import com.btpnwow.core.customer.facade.contract.ApproveUserWrkRequest;
import com.btpnwow.core.customer.facade.contract.ApproveUserWrkResponse;
import com.btpnwow.core.customer.facade.contract.RejectUserWrkRequest;
import com.btpnwow.core.customer.facade.contract.RejectUserWrkResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankStaffBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Confirm Registration Panel for Consumers, Agents in bank portal and Child Agents, Sub agents in agent
 * portal.
 * 
 * @author Vikram Gunda
 */
public class BankStaffConfirmApprovalPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(BankStaffConfirmApprovalPage.class);

	private BankStaffBean bankStaff;

	@SpringBean(name = "userWrkFacade")
	private UserWrkFacade userWrkFacade;

	public BankStaffConfirmApprovalPage(BankStaffBean bankStaff) {
		super();
		
		// Initialize the bean
		this.bankStaff = bankStaff;
		
		// build Page
		buildPage();
	}

	/**
	 * This method is used for add RegistrationType Info.
	 */
	protected void buildPage() {
		final Form<BankStaffConfirmApprovalPage> form = new Form<BankStaffConfirmApprovalPage>(
				"approvalForm", new CompoundPropertyModel<BankStaffConfirmApprovalPage>(this));
		
		form.add(new FeedbackPanel("errorMessages"));
		
		form.add(new Label("bankStaff.customerType.idAndValue"));
		form.add(new Label("bankStaff.userId"));
		form.add(new Label("bankStaff.name"));
		form.add(new Label("bankStaff.designation"));
		form.add(new Label("bankStaff.email"));
		form.add(new Label("bankStaff.language.idAndValue"));
		form.add(new Label("bankStaff.glCode.idAndValue"));
		form.add(new Label("bankStaff.territoryCode"));
		form.add(new Label("bankStaff.orgUnit.idAndValue"));
		form.add(new Label("bankStaff.createdBy"));
		form.add(new Label("bankStaff.createdDate"));
		
		form.add(new Button("btnApprove") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (BankStaffConfirmApprovalPage.this.onApprove()) {
					setResponsePage(new BankCheckerApprovalPage());
				}
			}
		}.setDefaultFormProcessing(true));
		
		form.add(new Button("btnReject") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (BankStaffConfirmApprovalPage.this.onReject()) {
					setResponsePage(new BankCheckerApprovalPage());
				}
			}
		});
		
		form.add(new Button("btnCancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			};
		}.setDefaultFormProcessing(false));
		
		add(form);
	}

	private boolean onApprove() {
		try {
			ApproveUserWrkRequest request = getNewMobiliserRequest(ApproveUserWrkRequest.class);
			request.setCallerId(Long.valueOf(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
			request.setWorkflowId(bankStaff.getWorkflowId());
			
			ApproveUserWrkResponse response = userWrkFacade.approveWrk(request);
			
			if (MobiliserUtils.success(response)) {
				getWebSession().info(getLocalizer().getString("approve.success", this));

				return true;
			}
			
			error(MobiliserUtils.errorMessage(response, this));
		} catch (Throwable ex) {
			LOG.error("An exception was thrown.", ex);
			
			error(getLocalizer().getString("approval.failure.exception", this));
		}
		
		return false;
	}
	
	private boolean onReject() {
		try {
			RejectUserWrkRequest request = getNewMobiliserRequest(RejectUserWrkRequest.class);
			request.setCallerId(Long.valueOf(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
			request.setWorkflowId(bankStaff.getWorkflowId());
			
			RejectUserWrkResponse response = userWrkFacade.rejectWrk(request);
			
			if (MobiliserUtils.success(response)) {
				getWebSession().info(getLocalizer().getString("reject.success", this));

				return true;
			}
			
			error(MobiliserUtils.errorMessage(response, this));
		} catch (Throwable ex) {
			LOG.error("An exception was thrown.", ex);
			
			error(getLocalizer().getString("approval.failure.exception", this));
		}
		
		return false;
	}
}
