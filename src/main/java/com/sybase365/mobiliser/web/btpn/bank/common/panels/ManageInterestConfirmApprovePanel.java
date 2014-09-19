package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.interest.facade.contract.wrk.ApproveInterestWrkRequest;
import com.btpnwow.core.interest.facade.contract.wrk.ApproveInterestWrkResponse;
import com.btpnwow.core.interest.facade.contract.wrk.RejectInterestWrkRequest;
import com.btpnwow.core.interest.facade.contract.wrk.RejectInterestWrkResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestDetailApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestSuccessApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Interest Confirm Approve Add Panel for bank portals. This panel consists of adding interest as fixed, slab and sharing.
 * 
 * @author Feny Yanti
 */
public class ManageInterestConfirmApprovePanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageInterestConfirmApprovePanel.class);
	
	private BtpnBaseBankPortalSelfCarePage basePage;
	private ManageInterestApproveBean interestBean;
	private String flag;

	public ManageInterestConfirmApprovePanel(final String id, final BtpnBaseBankPortalSelfCarePage basePage,
		ManageInterestApproveBean interestBean, String flag) {
		super(id);
		this.basePage = basePage;
		this.interestBean = interestBean;
		this.flag= flag;
		constructPanel();
	}

	
	private void constructPanel() {
		
		log.info(" ### (ManageInterestConfirmApprovePanel) constructPanel ###");
		
		final Form<ManageInterestConfirmApprovePanel> form = new Form<ManageInterestConfirmApprovePanel>("interestConfAprForm",
			new CompoundPropertyModel<ManageInterestConfirmApprovePanel>(this));
		
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		form.add(feedBackPanel);
		
		// approve/reject confirm interest management fields
		form.add(new Label("interestBean.customerIdentifier"));
		form.add(new Label("interestBean.customerIdentifierType"));
		form.add(new Label("customerTypeId", interestBean.getCustomerTypeId()==null ? "-" : interestBean.getCustomerTypeId().getIdAndValue()));
		form.add(new Label("interestBean.paymentInstrumentId"));
		form.add(new Label("paymentInstrumentTypeId", interestBean.getPaymentInstrumentTypeId()==null? "-" : interestBean.getPaymentInstrumentTypeId().getIdAndValue()));
		form.add(new Label("accrueGLCode", interestBean.getAccrueGLCode().getIdAndValue()));
		form.add(new Label("expenseGLCode", interestBean.getExpenseGLCode().getIdAndValue()));
		form.add(new Label("interestBean.validFrom"));
		
		form.add(new Label("interestBean.fixedFee", Long.valueOf(interestBean.getFixedFee()/100).toString())); 
		form.add(new Label("interestBean.percentageFee", new BigDecimal(interestBean.getPercentageFee()).movePointLeft(2).setScale(2, RoundingMode.DOWN).toString()));
		form.add(new Label("interestBean.thresholdAmount", Long.valueOf(interestBean.getThresholdAmount()/100).toString()));
		form.add(new Label("interestBean.minimumFee"));
		form.add(new Label("interestBean.maximumFee",(interestBean.getMaximumFee()!=null) ? String.valueOf(interestBean.getMaximumFee()) : "" ));
		
		form.add(new Label("interestBean.description"));
		form.add(new Label("interestBean.note"));


		form.add(new AjaxButton("confirmBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(interestBean)){
					interestBean = new ManageInterestApproveBean();
				}
				if (StringUtils.equals(BtpnConstants.APPROVED, flag))
					handleApprove();
				if (StringUtils.equals(BtpnConstants.REJECT, flag))
					handleReject();
			}
		});
		
		form.add(new AjaxButton("backBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				setResponsePage(new ManageInterestDetailApprovePage(interestBean));
			}
		}.setDefaultFormProcessing(false));
		
		
		form.add(new AjaxButton("cancelBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			}
		}.setDefaultFormProcessing(false));
		
		// Add add Button
		add(form);
	}
	
	
	private void handleApprove(){
	
		final ApproveInterestWrkRequest request;
		
		try {
			request = this.basePage.getNewMobiliserRequest(ApproveInterestWrkRequest.class);
			log.info(" ### (ManageInterestConfirmApprovePanel) handleApprove WF ID ### "+interestBean.getWorkFlowId());
			request.setWorkflowId(interestBean.getWorkFlowId());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setNote(interestBean.getNote());
			final ApproveInterestWrkResponse response = basePage.getInterestWrkClient().approve(request);
			log.info(" ### (ManageInterestConfirmApprovePanel) handleApprove RESPONSE CODE ### "+response.getStatus().getCode());
			if(basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("approve.interest.success", this));
				
				setResponsePage(new ManageInterestSuccessApprovePage(interestBean, flag));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Approving  ===> ", e);
		}
	}
	
	
	private void handleReject(){
		
		final RejectInterestWrkRequest request;
	
		try {
			
			request = this.basePage.getNewMobiliserRequest(RejectInterestWrkRequest.class);
			log.info(" ### (ManageInterestConfirmApprovePanel) handleReject WF ID ### "+interestBean.getWorkFlowId());
			request.setWorkflowId(interestBean.getWorkFlowId());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setNote(interestBean.getNote());
			
			final RejectInterestWrkResponse response = basePage.getInterestWrkClient().reject(request);
			log.info(" ### (ManageInterestConfirmApprovePanel) handleReject RESPONSE CODE ### "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("reject.interest.success", this));
				
				setResponsePage(new ManageInterestSuccessApprovePage(interestBean, flag));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Rejecting  ===> ", e);
		}
	}
	
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "interest.error" + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generic error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("interest.error", this);
		}
		return message;
	}

}
