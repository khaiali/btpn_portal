package com.sybase365.mobiliser.web.btpn.bank.common.panels;

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

import com.btpnwow.core.gl.facade.contract.wrk.ApproveGLWrkRequest;
import com.btpnwow.core.gl.facade.contract.wrk.ApproveGLWrkResponse;
import com.btpnwow.core.gl.facade.contract.wrk.RejectGLWrkRequest;
import com.btpnwow.core.gl.facade.contract.wrk.RejectGLWrkResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLDetailApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLSuccessApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * @author Andi Samallangi W
 *
 */
public class ManageCustomGLConfirmApprovePanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(ManageCustomGLConfirmApprovePanel.class);
	
	protected BtpnBaseBankPortalSelfCarePage basePage;
	protected ManageCustomGeneralLedgerBean cusLedgerBean;
	protected String flag;
	
	
	public ManageCustomGLConfirmApprovePanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageCustomGeneralLedgerBean cusLedgerBean, String flag) {
		super(id);
		this.basePage = basePage;
		this.cusLedgerBean = cusLedgerBean;
		this.flag = flag;
		constructPanel();
	}
	
	protected void constructPanel() {
		
		log.info(" ### (ManageCustomGLConfirmApprovePanel) constructPanel ### "); 
		final Form<ManageCustomGLConfirmApprovePanel> form = new Form<ManageCustomGLConfirmApprovePanel>("cusGLConfirmAppForm",
			new CompoundPropertyModel<ManageCustomGLConfirmApprovePanel>(this));
		
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		form.add(feedBackPanel);
		
		log.info(" ### (ManageCustomGLConfirmApprovePanel) FLAG ### " +flag);
		if (StringUtils.equals(BtpnConstants.APPROVED, flag))
			form.add(new Label("headLine.generalLedger", getLocalizer().getString("headLine.gl.approve", this)));
		if (StringUtils.equals(BtpnConstants.REJECT, flag))
			form.add(new Label("headLine.generalLedger", getLocalizer().getString("headLine.gl.reject", this)));
		
		form.add(new Label("cusLedgerBean.glCode"));
		form.add(new Label("cusLedgerBean.selectedParentGlCode.id"));
		form.add(new Label("cusLedgerBean.selectedType.value"));
		form.add(new Label("cusLedgerBean.glDescription"));
		form.add(new Label("cusLedgerBean.note"));
		
		form.add(new AjaxButton("confirmBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(cusLedgerBean)){
					cusLedgerBean = new ManageCustomGeneralLedgerBean();
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
				setResponsePage(new ManageCustomGLDetailApprovePage(cusLedgerBean));
			}
		}.setDefaultFormProcessing(false));
		
		
		form.add(new AjaxButton("cancelBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			}
		}.setDefaultFormProcessing(false));
		
		add(form);
	}
	
	private void handleApprove(){
		log.info(" ### handleApprove ### ");
		final ApproveGLWrkRequest request;
		try {
			request = this.basePage.getNewMobiliserRequest(ApproveGLWrkRequest.class);
			log.info(" ### handleApprove WF ID ### "+cusLedgerBean.getWorkFlowId());
			request.setWorkflowId(cusLedgerBean.getWorkFlowId());
			request.setNote(cusLedgerBean.getNote());
			request.setCallerId(this.basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final ApproveGLWrkResponse response = basePage.getGlWrkClient().approveWrk(request);
			log.info(" ### handleApprove ### "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				setResponsePage(new ManageCustomGLSuccessApprovePage(cusLedgerBean, flag));
			}else{
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Approving  ===> ", e);
		}
	}
	
	
	private void handleReject(){
		
		final RejectGLWrkRequest request;
		try {
			request = this.basePage.getNewMobiliserRequest(RejectGLWrkRequest.class);
			log.info(" ### handleReject WF ID ### "+cusLedgerBean.getWorkFlowId());
			request.setWorkflowId(cusLedgerBean.getWorkFlowId());
			request.setNote(cusLedgerBean.getNote());
			request.setCallerId(this.basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final RejectGLWrkResponse response = basePage.getGlWrkClient().rejectWrk(request);
			log.info(" ### handleReject ## "+response.getStatus().getCode());
			if(basePage.evaluateBankPortalMobiliserResponse(response)){
				setResponsePage(new ManageCustomGLSuccessApprovePage(cusLedgerBean, flag));
			}else{
				handleSpecificErrorMessage(response.getStatus().getCode());
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
		final String messageKey = "error.gl." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generic error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("gl.error", this);
		}
		return message;
	}

}
