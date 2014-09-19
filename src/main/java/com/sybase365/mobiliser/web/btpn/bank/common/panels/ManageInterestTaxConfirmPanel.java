package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.interest.facade.contract.wrk.AddInterestTaxWrkRequest;
import com.btpnwow.core.interest.facade.contract.wrk.AddInterestTaxWrkResponse;
import com.btpnwow.core.interest.facade.contract.wrk.RemoveInterestTaxWrkRequest;
import com.btpnwow.core.interest.facade.contract.wrk.RemoveInterestTaxWrkResponse;
import com.btpnwow.core.interest.facade.contract.wrk.UpdateInterestTaxWrkRequest;
import com.btpnwow.core.interest.facade.contract.wrk.UpdateInterestTaxWrkResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxAddPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxEditPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AttributePrepender;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Interest Add Panel for bank portals. This panel consists of adding fee as fixed, slab and sharing.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxConfirmPanel  extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageInterestTaxConfirmPanel.class);
	
	private BtpnBaseBankPortalSelfCarePage basePage;
	private ManageInterestTaxBean interestTaxBean;
	private String flag;


	public ManageInterestTaxConfirmPanel(final String id, final BtpnBaseBankPortalSelfCarePage basePage,
			ManageInterestTaxBean interestTaxBean, String flag) {
		super(id);
		this.basePage = basePage;
		this.interestTaxBean = interestTaxBean;
		this.flag = flag;
		constructPanel();
	}

	
	private void constructPanel() {
		
		log.info(" ### (ManageInterestTaxConfirmPanel) constructPanel ###");
		
		final Form<ManageInterestTaxConfirmPanel> form = new Form<ManageInterestTaxConfirmPanel>("interestTaxConfirmForm",
			new CompoundPropertyModel<ManageInterestTaxConfirmPanel>(this));
		
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		form.add(feedBackPanel);
		
		// Add interest management fields
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

		// Add Confirm button
		Button confirmButton = new Button("submitConfirm"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(){
				if (!PortalUtils.exists(interestTaxBean)){
					interestTaxBean = new ManageInterestTaxBean();
				}
				if (StringUtils.equals(BtpnConstants.ADD, flag)){
					handleAddInterestTaxWrk();
				}
				if (StringUtils.equals(BtpnConstants.UPDATE, flag)){
					handleUpdateInterestTaxWrk();
				}
				if (StringUtils.equals(BtpnConstants.DELETE, flag)){
					handleDeleteInterestTaxWrk();
				}
			}
		};
		confirmButton.add(new AttributePrepender("onclick", Model.of("loading(submitConfirm)"), ";"));
		form.add(confirmButton);
		
		// Add Back button
		form.add(new AjaxButton("submitBack"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (StringUtils.equals(BtpnConstants.ADD, flag)){
					setResponsePage(new ManageInterestTaxAddPage(interestTaxBean));
				}
				if (StringUtils.equals(BtpnConstants.UPDATE, flag)){
					setResponsePage(new ManageInterestTaxEditPage(interestTaxBean));
				}
			}
		}.setDefaultFormProcessing(false));
		
		// Add Cancel button
		form.add(new Button("submitCancle") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			};
		}.setDefaultFormProcessing(false));
		
		// Add add Button
		add(form);
	}
	
	
	private void handleAddInterestTaxWrk(){
		
		final AddInterestTaxWrkRequest request;
		
		try {
			
			request = basePage.getNewMobiliserRequest(AddInterestTaxWrkRequest.class);
			request.setDescription(interestTaxBean.getDescription());
			request.setCustomerIdentifier(interestTaxBean.getCustomerIdentifier());
			request.setCustomerIdentifierType( interestTaxBean.getCustomerIdentifierType());
		
			if(interestTaxBean.getCustomerTypeId()!=null)
				request.setCustomerTypeId(Integer.parseInt(interestTaxBean.getCustomerTypeId().getId()));
			request.setPaymentInstrumentId(interestTaxBean.getPaymentInstrumentId());
				
			if(interestTaxBean.getPaymentInstrumentTypeId() != null)
				request.setPaymentInstrumentTypeId(Integer.parseInt(interestTaxBean.getPaymentInstrumentTypeId().getId()));	
			
			request.setAccrueGLCode(Integer.parseInt(interestTaxBean.getAccrueGLCode().getId()));
			request.setTaxGLCode(Integer.parseInt(interestTaxBean.getTaxGLCode().getId()));
			Date validFrom = interestTaxBean.getValidFrom();
			Calendar cal = Calendar.getInstance();
			cal.setTime(validFrom);
			request.setValidFrom(PortalUtils.getSaveXMLGregorianCalendar(cal));
		
			request.setThresholdAmount(interestTaxBean.getThresholdAmount()*100);
			request.setFixedFee(interestTaxBean.getFixedFee()*100);
			request.setPercentageFee(new BigDecimal(interestTaxBean.getPercentageFee()).movePointRight(2).setScale(2, RoundingMode.DOWN).longValue());
			request.setMaximumFee(interestTaxBean.getMaximumFee());
			request.setMinimumFee(interestTaxBean.getMinimumFee());
			
			request.setNote(interestTaxBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			log.info(" ### (ManageInterestTaxConfirmPanel) BEFORE RESPONSE ### ");
			final AddInterestTaxWrkResponse response = basePage.getInterestTaxWrkClient().add(request);
			log.info(" ### (ManageInterestTaxConfirmPanel) handleAddInterestWrk RESPONSE CODE ## "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("add.interesttax.success", this));
				setResponsePage(new ManageInterestTaxSuccessPage(interestTaxBean));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Confirm Add Interest Tax ===> ", e);
		}
	}
	
	
	private void handleUpdateInterestTaxWrk(){
		
		final UpdateInterestTaxWrkRequest request;
		
		try {
			
			log.info(" ### (ManageInterestTaxConfirmPanel) handleInterestInterestTax ### ");
			request = basePage.getNewMobiliserRequest(UpdateInterestTaxWrkRequest.class);
			
			log.info(" ### (ManageInterestTaxConfirmPanel) handleInterestInterestTax ID ### " +interestTaxBean.getId());
			request.setId(interestTaxBean.getId());
			
			log.info(" ### (ManageInterestTaxConfirmPanel) handleInterestInterestTax ID ### " +interestTaxBean.getDescription());
			request.setDescription(interestTaxBean.getDescription());
			
			log.info(" ### (ManageInterestTaxConfirmPanel) handleInterestInterestTax AccGL CODE  ### " +interestTaxBean.getAccrueGLCode().getId());
			request.setAccrueGLCode(Long.parseLong(interestTaxBean.getAccrueGLCode().getId()));
			
			log.info(" ### (ManageInterestTaxConfirmPanel) handleInterestInterestTax taxGL CODE  ### " +interestTaxBean.getTaxGLCode().getId());
			request.setTaxGLCode(Long.parseLong(interestTaxBean.getTaxGLCode().getId()));
	
			request.setThresholdAmount(interestTaxBean.getThresholdAmount()*100);
			request.setFixedFee(interestTaxBean.getFixedFee()*100);
			request.setPercentageFee(new BigDecimal(interestTaxBean.getPercentageFee()).movePointRight(2).setScale(2, RoundingMode.DOWN).longValue());
			request.setMaximumFee(interestTaxBean.getMaximumFee());
			request.setMinimumFee(interestTaxBean.getMinimumFee());
			
			log.info(" ### (ManageInterestTaxConfirmPanel) handleInterestInterestTax NOTE  ### " +interestTaxBean.getNote());
			request.setNote(interestTaxBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final UpdateInterestTaxWrkResponse response = this.basePage.getInterestTaxWrkClient().update(request);
			log.info(" ### (ManageInterestTaxConfirmPanel) handleInterestInterestTax ### "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("update.interesttax.success", this));
				setResponsePage(new ManageInterestTaxSuccessPage(interestTaxBean));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Update Interest Tax Wrk ===> ", e);
		}
	}
	
private void handleDeleteInterestTaxWrk(){
		
		final RemoveInterestTaxWrkRequest request;
		
		try {
			
			log.info(" ### (ManageInterestConfirmPanel) handleInterestWrk ### ");
			request = basePage.getNewMobiliserRequest(RemoveInterestTaxWrkRequest.class);
			
			log.info(" ### (ManageInterestConfirmPanel) handleInterestWrk ID ### " +interestTaxBean.getId());
			request.setId(interestTaxBean.getId());
			request.setNote(interestTaxBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final RemoveInterestTaxWrkResponse response = this.basePage.getInterestTaxWrkClient().remove(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				
				getSession().info(getLocalizer().getString("delete.interesttax.success", this));
				setResponsePage(new ManageInterestTaxSuccessPage(interestTaxBean));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Delete Interest Wrk ===> ", e);
		}
	}
	
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.interest" + errorCode;
		String message = this.basePage.getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = this.basePage.getLocalizer().getString("interest.error", this);
		}
		return message;
	}

}
