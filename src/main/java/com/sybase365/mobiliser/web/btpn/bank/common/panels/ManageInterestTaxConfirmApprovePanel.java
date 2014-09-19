package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.interest.facade.contract.wrk.ApproveInterestTaxWrkRequest;
import com.btpnwow.core.interest.facade.contract.wrk.ApproveInterestTaxWrkResponse;
import com.btpnwow.core.interest.facade.contract.wrk.RejectInterestTaxWrkRequest;
import com.btpnwow.core.interest.facade.contract.wrk.RejectInterestTaxWrkResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxDetailApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxSuccessApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Interest Confirm Approve Add Panel for bank portals. This panel consists of adding interest as fixed, slab and sharing.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxConfirmApprovePanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageInterestTaxConfirmApprovePanel.class);
	
	private BtpnBaseBankPortalSelfCarePage basePage;
	private ManageInterestTaxApproveBean interestTaxBean;
	private String flag;

	public ManageInterestTaxConfirmApprovePanel(final String id, final BtpnBaseBankPortalSelfCarePage basePage,
		ManageInterestTaxApproveBean interestTaxBean, String flag) {
		super(id);
		this.basePage = basePage;
		this.interestTaxBean = interestTaxBean;
		this.flag= flag;
		constructPanel();
	}

	
	private void constructPanel() {
		
		log.info(" ### (ManageInterestTaxConfirmApprovePanel) constructPanel ###");
		
		final Form<ManageInterestTaxConfirmApprovePanel> form = new Form<ManageInterestTaxConfirmApprovePanel>("interestTaxConfAprForm",
			new CompoundPropertyModel<ManageInterestTaxConfirmApprovePanel>(this));
		
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		form.add(feedBackPanel);
		
		// approve/reject confirm interest management fields
		form.add(new Label("interestTaxBean.customerIdentifier"));
		form.add(new Label("interestTaxBean.customerIdentifierType"));
		form.add(new Label("customerTypeId", interestTaxBean.getCustomerTypeId()==null ? "-" : interestTaxBean.getCustomerTypeId().getIdAndValue()));
		form.add(new Label("interestTaxBean.paymentInstrumentId"));
		form.add(new Label("paymentInstrumentTypeId", interestTaxBean.getPaymentInstrumentTypeId()==null? "-" : interestTaxBean.getPaymentInstrumentTypeId().getIdAndValue()));
		form.add(new Label("accrueGLCode", interestTaxBean.getAccrueGLCode().getIdAndValue()));
		form.add(new Label("taxGLCode", interestTaxBean.getTaxGLCode().getIdAndValue()));
		form.add(new Label("interestTaxBean.validFrom"));
	
		form.add(new Label("interestTaxBean.fixedFee", Long.valueOf(interestTaxBean.getFixedFee()/100).toString())); 
		form.add(new Label("interestTaxBean.percentageFee", new BigDecimal(interestTaxBean.getPercentageFee()).movePointLeft(2).setScale(2, RoundingMode.DOWN).toString()));
		form.add(new Label("interestTaxBean.thresholdAmount", Long.valueOf(interestTaxBean.getThresholdAmount()/100).toString()));
		form.add(new Label("interestTaxBean.minimumFee"));
		form.add(new Label("interestTaxBean.maximumFee",(interestTaxBean.getMaximumFee()!=null) ? String.valueOf(interestTaxBean.getMaximumFee()) : "" ));
		
		form.add(new Label("interestTaxBean.description"));
		form.add(new Label("interestTaxBean.note"));


		form.add(new Button("confirmBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(){
				if (!PortalUtils.exists(interestTaxBean)){
					interestTaxBean = new ManageInterestTaxApproveBean();
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
				setResponsePage(new ManageInterestTaxDetailApprovePage(interestTaxBean));
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
	
		final ApproveInterestTaxWrkRequest request;
		
		try {
			
			request = this.basePage.getNewMobiliserRequest(ApproveInterestTaxWrkRequest.class);
			log.info(" ### (ManageInterestTaxConfirmApprovePanel) handleApprove WF ID ### "+interestTaxBean.getWorkFlowId());
			request.setWorkflowId(interestTaxBean.getWorkFlowId());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setNote(interestTaxBean.getNote());
			final ApproveInterestTaxWrkResponse response = basePage.getInterestTaxWrkClient().approve(request);
			log.info(" ### (ManageInterestTaxConfirmApprovePanel) handleApprove RESPONSE CODE ### "+response.getStatus().getCode());
			if(basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("approve.interesttax.success", this));
				
				setResponsePage(new ManageInterestTaxSuccessApprovePage(interestTaxBean, flag));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Approving  ===> ", e);
		}
	}
	
	
	private void handleReject(){
		
		final RejectInterestTaxWrkRequest request;
	
		try {
			
			request = this.basePage.getNewMobiliserRequest(RejectInterestTaxWrkRequest.class);
			log.info(" ### (ManageInterestConfirmApprovePanel) handleReject WF ID ### "+interestTaxBean.getWorkFlowId());
			request.setWorkflowId(interestTaxBean.getWorkFlowId());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setNote(interestTaxBean.getNote());
			
			final RejectInterestTaxWrkResponse response = basePage.getInterestTaxWrkClient().reject(request);
			log.info(" ### (ManageInterestConfirmApprovePanel) handleReject RESPONSE CODE ### "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("reject.interesttax.success", this));
				
				
				setResponsePage(new ManageInterestTaxSuccessApprovePage(interestTaxBean, flag));
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
