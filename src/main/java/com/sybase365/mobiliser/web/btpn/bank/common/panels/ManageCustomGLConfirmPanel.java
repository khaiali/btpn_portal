package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.easymock.internal.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.gl.facade.contract.wrk.AddGLWrkRequest;
import com.btpnwow.core.gl.facade.contract.wrk.AddGLWrkResponse;
import com.btpnwow.core.gl.facade.contract.wrk.UpdateGLWrkRequest;
import com.btpnwow.core.gl.facade.contract.wrk.UpdateGLWrkResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLEditPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * @author Andi Samallangi W
 *
 */
public class ManageCustomGLConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(ManageCustomGLConfirmPanel.class);
	
	protected BtpnBaseBankPortalSelfCarePage basePage;
	protected ManageCustomGeneralLedgerBean cusLedgerBean;
	protected String flag;
	
	public ManageCustomGLConfirmPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageCustomGeneralLedgerBean cusLedgerBean, String flag) {
		super(id);
		this.basePage = basePage;
		this.cusLedgerBean = cusLedgerBean;
		this.flag = flag;
		constructPanel();
	}
	
	protected void constructPanel() {
		
		log.info(" ### (ManageCustomGLConfirmPanel) constructPanel ### "); 
		final Form<ManageCustomGLConfirmPanel> form = new Form<ManageCustomGLConfirmPanel>("cusGLConfirmForm",
			new CompoundPropertyModel<ManageCustomGLConfirmPanel>(this));
		
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		form.add(feedBackPanel);
		
		form.add(new Label("cusLedgerBean.glCode"));
		form.add(new Label("cusLedgerBean.selectedParentGlCode.id"));
		form.add(new Label("cusLedgerBean.selectedType.value"));
		form.add(new Label("cusLedgerBean.glDescription"));
		form.add(new Label("cusLedgerBean.note"));
		
		
		Button confirmButton = new Button("confirmBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(){
				if (!PortalUtils.exists(cusLedgerBean)){
					cusLedgerBean = new ManageCustomGeneralLedgerBean();
				}
				if (StringUtils.equals("add", flag)){
					handleAddGeneralLedgerWrk();
				}
				if (StringUtils.equals("update", flag)){
					handleUpdateGeneralLedger();
				}
			}
		};
		form.add(confirmButton);
		
		form.add(new AjaxButton("backBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				setResponsePage(new ManageCustomGLEditPage(cusLedgerBean));
			}
		}.setDefaultFormProcessing(false));

		add(form);
	}
	
	
	private void handleAddGeneralLedgerWrk() {    
		
		final AddGLWrkRequest request;
		try {
			
			log.info(" ### handleAddGeneralLedgerWrk ### ");
			request = basePage.getNewMobiliserRequest(AddGLWrkRequest.class);
			request.setCode(Long.parseLong(cusLedgerBean.getGlCode()));
			request.setParent(cusLedgerBean.getSelectedParentGlCode() == null ? null : Long.valueOf(cusLedgerBean.getSelectedParentGlCode().getId()));
			request.setDescription(cusLedgerBean.getGlDescription());
			request.setType(cusLedgerBean.getSelectedType().getId());
			request.setNote(cusLedgerBean.getNote());
			log.info(" ### CUSTOMER ID ### " +basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setCallerId(Long.valueOf(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
			final AddGLWrkResponse response = this.basePage.getGlWrkClient().addWrk(request);
			log.info(" ### (handleAddGeneralLedgerWrk) RESPONSE CODE ### "+response.getStatus().getCode()); 
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("add.gl.success", this));
				setResponsePage(new ManageCustomGLSuccessPage(cusLedgerBean));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Add GL ===> ", e);
		}
	}
	
	private void handleUpdateGeneralLedger() {

		final UpdateGLWrkRequest request;
		try {
			request = basePage.getNewMobiliserRequest(UpdateGLWrkRequest.class);
			request.setCode(Long.parseLong(cusLedgerBean.getGlCode()));
			request.setDescription(cusLedgerBean.getGlDescription());
			request.setType(cusLedgerBean.getSelectedType().getId());
			request.setNote(cusLedgerBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final UpdateGLWrkResponse response = this.basePage.getGlWrkClient().updateWrk(request);
			log.info(" ### (handleUpdateGeneralLedger) RESPONSE CODE ### "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				getSession().info(
						getLocalizer().getString("update.gl.success", this));
				setResponsePage(new ManageCustomGLSuccessPage(cusLedgerBean));
			} else {
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Update GL ===> ", e);
		}
	}
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.gl." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("gl.error", this);
		}
		return message;
	}

}
