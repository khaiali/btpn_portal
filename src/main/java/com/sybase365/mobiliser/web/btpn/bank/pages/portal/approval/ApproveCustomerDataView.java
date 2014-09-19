package com.sybase365.mobiliser.web.btpn.bank.pages.portal.approval;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.customer.facade.api.CustomerWrkFacade;
import com.btpnwow.core.customer.facade.contract.ApproveCustomerExWrkRequest;
import com.btpnwow.core.customer.facade.contract.ApproveCustomerExWrkResponse;
import com.btpnwow.core.customer.facade.contract.RejectCustomerExWrkRequest;
import com.btpnwow.core.customer.facade.contract.RejectCustomerExWrkResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the ApproveCustomerDataView page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ApproveCustomerDataView extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveCustomerDataView.class);

	@SpringBean(name = "customerWrkFacade")
	protected CustomerWrkFacade customerWrkFacade;

	private final CustomerRegistrationBean currentValue;
	
	private final CustomerRegistrationBean newValue;

	private FeedbackPanel feedBack;

	public ApproveCustomerDataView(CustomerRegistrationBean currentValue, CustomerRegistrationBean newValue) {
		super();
		
		this.currentValue = currentValue;
		this.newValue = newValue;
		
		initPageComponents();
	}

	protected void initPageComponents() {
		Form<ApproveCustomerDataView> form = new Form<ApproveCustomerDataView>(
				"approveCustomerDataForm", new CompoundPropertyModel<ApproveCustomerDataView>(this));

		// add feed back panel
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		final WebMarkupContainer glCode = new WebMarkupContainer("glCode");

		// Current Values
		form.add(new Label("currentValue.atmCardNo"));
		form.add(new Label("currentValue.blackListReason.value"));
		form.add(new Label("currentValue.dateOfBirth"));
		form.add(new Label("currentValue.emailId"));
		form.add(new Label("currentValue.employeeId"));
		form.add(new Label("currentValue.gender.value"));
		form.add(new Label("currentValue.highRiskBusiness"));
		form.add(new Label("currentValue.highRiskCustomer"));
		form.add(new Label("currentValue.idCardNo"));
		form.add(new Label("currentValue.idExpirationDate"));
		form.add(new Label("currentValue.idType.value"));
		form.add(new Label("currentValue.income.value"));
		form.add(new Label("currentValue.industryOfEmployee.value"));
		form.add(new Label("currentValue.taxExempted"));
		form.add(new Label("currentValue.job.value"));
		form.add(new Label("currentValue.language"));
		form.add(new Label("currentValue.maritalStatus.value"));
		form.add(new Label("currentValue.mothersMaidenName"));
		form.add(new Label("currentValue.name"));
		form.add(new Label("currentValue.nationality.value"));
		glCode.add(new Label("currentValue.glCodeId.value"));
		form.add(new Label("currentValue.occupation.value"));
		form.add(new Label("currentValue.placeOfBirth"));
		form.add(new Label("currentValue.purposeOfAccount.value"));
		form.add(new Label("currentValue.receiptMode.value"));
		form.add(new Label("currentValue.shortName"));
		form.add(new Label("currentValue.sourceofFound.value"));
		form.add(new Label("currentValue.status"));
		form.add(new Label("currentValue.street1"));
		form.add(new Label("currentValue.street2"));
		form.add(new Label("currentValue.religion.value"));
		form.add(new Label("currentValue.lastEducation.value"));
		form.add(new Label("currentValue.foreCastTransaction.value"));
		form.add(new Label("currentValue.taxCardNumber"));
		form.add(new Label("currentValue.province.value"));
		form.add(new Label("currentValue.city.value"));
		form.add(new Label("currentValue.zipCode"));
		form.add(new Label("currentValue.marketingSourceCode"));
		form.add(new Label("currentValue.referralNumber"));
		form.add(new Label("currentValue.agentCode"));

		// New Values
		form.add(new Label("newValue.atmCardNo"));
		form.add(new Label("newValue.blackListReason.value"));
		form.add(new Label("newValue.dateOfBirth"));
		form.add(new Label("newValue.emailId"));
		form.add(new Label("newValue.employeeId"));
		form.add(new Label("newValue.gender.value"));
		form.add(new Label("newValue.highRiskBusiness"));
		form.add(new Label("newValue.highRiskCustomer"));
		form.add(new Label("newValue.idCardNo"));
		form.add(new Label("newValue.idExpirationDate"));
		form.add(new Label("newValue.idType.value"));
		form.add(new Label("newValue.income.value"));
		form.add(new Label("newValue.industryOfEmployee.value"));
		form.add(new Label("newValue.taxExempted"));
		form.add(new Label("newValue.job.value"));
		form.add(new Label("newValue.language"));
		form.add(new Label("newValue.maritalStatus.value"));
		form.add(new Label("newValue.mothersMaidenName"));
		form.add(new Label("newValue.name"));
		form.add(new Label("newValue.nationality.value"));
		glCode.add(new Label("newValue.glCodeId.value"));
		form.add(new Label("newValue.occupation.value"));
		form.add(new Label("newValue.placeOfBirth"));
		form.add(new Label("newValue.purposeOfAccount.value"));
		form.add(new Label("newValue.receiptMode.value"));
		form.add(new Label("newValue.shortName"));
		form.add(new Label("newValue.sourceofFound.value"));
		form.add(new Label("newValue.status"));
		form.add(new Label("newValue.street1"));
		form.add(new Label("newValue.street2"));
		form.add(new Label("newValue.religion.value"));
		form.add(new Label("newValue.lastEducation.value"));
		form.add(new Label("newValue.foreCastTransaction.value"));
		form.add(new Label("newValue.taxCardNumber"));
		form.add(new Label("newValue.province.value"));
		form.add(new Label("newValue.city.value"));
		form.add(new Label("newValue.zipCode"));
		form.add(new Label("newValue.marketingSourceCode"));
		form.add(new Label("newValue.referralNumber"));
		form.add(new Label("newValue.agentCode"));
		
		glCode.setVisible((currentValue != null) && (currentValue.getGlCodeId() != null));
		
		form.add(glCode);
		form.add(addApproveButton());
		form.add(addRejectButton());
		form.add(addBackButton());

		add(form);
	}

	/**
	 * This method is for Approve button to approve customer.
	 */
	protected AjaxButton addApproveButton() {
		AjaxButton approveButton = new AjaxButton("approveButton") {
			
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					ApproveCustomerExWrkRequest request = MobiliserUtils.fill(new ApproveCustomerExWrkRequest(), ApproveCustomerDataView.this);
					request.setCallerId(Long.valueOf(ApproveCustomerDataView.this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
					request.setWorkflowId(ApproveCustomerDataView.this.newValue.getTaskId());
					
					ApproveCustomerExWrkResponse response = customerWrkFacade.approveWrk(request);
					
					if (MobiliserUtils.success(response)) {
						ApproveCustomerDataView.this.getWebSession().info(getLocalizer().getString("approve.success", this));
						
						setResponsePage(new ApproveCustomerData());
					} else {
						ApproveCustomerDataView.this.getWebSession().error(MobiliserUtils.errorMessage(response, this));
						
						target.addComponent(feedBack);
					}
				} catch (Exception e) {
					LOG.error("An exception was thrown.", e);
					
					ApproveCustomerDataView.this.getWebSession().error(getLocalizer().getString("approval.fail", this));
					
					target.addComponent(feedBack);
				}
			}
		};
		approveButton.setDefaultFormProcessing(true);
		
		return approveButton;
	}

	/**
	 * This method adds the reject button for the approve customer.
	 */
	protected AjaxButton addRejectButton() {
		AjaxButton rejectButton = new AjaxButton("rejectButton") {
			
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					RejectCustomerExWrkRequest request = MobiliserUtils.fill(new RejectCustomerExWrkRequest(), ApproveCustomerDataView.this);
					request.setCallerId(Long.valueOf(ApproveCustomerDataView.this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
					request.setWorkflowId(ApproveCustomerDataView.this.newValue.getTaskId());
					
					RejectCustomerExWrkResponse response = customerWrkFacade.rejectWrk(request);
					
					if (MobiliserUtils.success(response)) {
						ApproveCustomerDataView.this.getWebSession().info(getLocalizer().getString("reject.success", this));
						
						setResponsePage(new ApproveCustomerData());
					} else {
						ApproveCustomerDataView.this.getWebSession().error(MobiliserUtils.errorMessage(response, this));
						
						target.addComponent(feedBack);
					}
				} catch (Exception e) {
					LOG.error("An exception was thrown.", e);
					
					ApproveCustomerDataView.this.getWebSession().error(getLocalizer().getString("reject.fail", this));
					
					target.addComponent(feedBack);
				}
			}
		};
		
		rejectButton.setDefaultFormProcessing(false);
		
		return rejectButton;
	}

	/**
	 * This method adds the back button for the approve customer.
	 */
	protected Button addBackButton() {
		Button backButton = new Button("backButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ApproveCustomerData.class);
			}
		};
		
		backButton.setDefaultFormProcessing(false);
		return backButton;
	}
}
