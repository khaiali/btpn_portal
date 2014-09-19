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

import com.btpnwow.core.interest.facade.contract.wrk.AddInterestWrkRequest;
import com.btpnwow.core.interest.facade.contract.wrk.AddInterestWrkResponse;
import com.btpnwow.core.interest.facade.contract.wrk.RemoveInterestWrkRequest;
import com.btpnwow.core.interest.facade.contract.wrk.RemoveInterestWrkResponse;
import com.btpnwow.core.interest.facade.contract.wrk.UpdateInterestWrkRequest;
import com.btpnwow.core.interest.facade.contract.wrk.UpdateInterestWrkResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestAddPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestEditPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestSuccessPage;
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
public class ManageInterestConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageInterestConfirmPanel.class);
	
	private BtpnBaseBankPortalSelfCarePage basePage;
	private ManageInterestBean interestBean;
	private String flag;


	public ManageInterestConfirmPanel(final String id, final BtpnBaseBankPortalSelfCarePage basePage,
			ManageInterestBean interestBean, String flag) {
		super(id);
		this.basePage = basePage;
		this.interestBean = interestBean;
		this.flag = flag;
		constructPanel();
	}

	
	private void constructPanel() {
		
		log.info(" ### (ManageInterestConfirmPanel) constructPanel ###");
		
		final Form<ManageInterestConfirmPanel> form = new Form<ManageInterestConfirmPanel>("interestConfirmForm",
			new CompoundPropertyModel<ManageInterestConfirmPanel>(this));
		
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		form.add(feedBackPanel);
		
		// Add interest management fields
		form.add(new Label("interestBean.customerIdentifier"));
		form.add(new Label("interestBean.customerIdentifierType"));
		form.add(new Label("customerTypeId", interestBean.getCustomerTypeId()==null ? "-" : interestBean.getCustomerTypeId().getIdAndValue()));
		form.add(new Label("interestBean.paymentInstrumentId"));
		form.add(new Label("paymentInstrumentTypeId", interestBean.getPaymentInstrumentTypeId()==null? "-" : interestBean.getPaymentInstrumentTypeId().getIdAndValue()));
		form.add(new Label("accrueGLCode", interestBean.getAccrueGLCode().getIdAndValue()));
		form.add(new Label("expenseGLCode", interestBean.getExpenseGLCode().getIdAndValue()));
		form.add(new Label("interestBean.validFrom"));
		form.add(new Label("interestBean.fixedFee"));
		form.add(new Label("interestBean.percentageFee"));
		form.add(new Label("interestBean.maximumFee"));
		form.add(new Label("interestBean.minimumFee"));
		form.add(new AmountLabel("interestBean.thresholdAmount"));
		form.add(new Label("interestBean.description"));
		form.add(new Label("interestBean.note"));

		// Add Confirm button
		Button confirmButton = new Button("submitConfirm"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(){
				if (!PortalUtils.exists(interestBean)){
					interestBean = new ManageInterestBean();
				}
				if (StringUtils.equals(BtpnConstants.ADD, flag)){
					handleAddInterestWrk();
				}
				if (StringUtils.equals(BtpnConstants.UPDATE, flag)){
					handleUpdateInterestWrk();
				}
				if (StringUtils.equals(BtpnConstants.DELETE, flag)){
					handleDeleteInterestWrk();
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
					setResponsePage(new ManageInterestAddPage(interestBean));
				}
				if (StringUtils.equals(BtpnConstants.UPDATE, flag)){
					setResponsePage(new ManageInterestEditPage(interestBean));
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
	
	
	private void handleAddInterestWrk(){
		
		final AddInterestWrkRequest request;
		
		try {
			
			log.info(" ### (ManageInterestConfirmPanel) handleAddInterestWrk ### ");
			request = basePage.getNewMobiliserRequest(AddInterestWrkRequest.class);
			request.setDescription(interestBean.getDescription());
			if(interestBean.getCustomerIdentifier()!=null)
				request.setCustomerIdentifier(interestBean.getCustomerIdentifier());
			
			request.setCustomerIdentifierType(interestBean.getCustomerIdentifierType());
			
			if(interestBean.getCustomerTypeId()!=null)
				request.setCustomerTypeId(Integer.parseInt(interestBean.getCustomerTypeId().getId()));
			
			if(interestBean.getPaymentInstrumentId()!=null)
				request.setPaymentInstrumentId(interestBean.getPaymentInstrumentId());
			
			if(interestBean.getPaymentInstrumentTypeId()!=null)
				request.setPaymentInstrumentTypeId(Integer.parseInt(interestBean.getPaymentInstrumentTypeId().getId()));
			
			request.setAccrueGLCode(Integer.parseInt(interestBean.getAccrueGLCode().getId()));			
			request.setExpenseGLCode(Integer.parseInt(interestBean.getExpenseGLCode().getId()));
			
			Date validFrom = interestBean.getValidFrom();
			Calendar cal = Calendar.getInstance();
			cal.setTime(validFrom);
			request.setValidFrom(PortalUtils.getSaveXMLGregorianCalendar(cal));
		
			request.setThresholdAmount(interestBean.getThresholdAmount());	
			request.setFixedFee(interestBean.getFixedFee()*100);
			request.setPercentageFee(new BigDecimal(interestBean.getPercentageFee()).movePointRight(2).setScale(0, RoundingMode.DOWN).longValue());
			request.setMaximumFee(interestBean.getMaximumFee());
			request.setMinimumFee(interestBean.getMinimumFee());
			
			request.setNote(interestBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final AddInterestWrkResponse response = basePage.getInterestWrkClient().add(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("add.interest.success", this));
				setResponsePage(new ManageInterestSuccessPage(interestBean));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Add Use Case Fee ===> ", e);
		}
	}
	
	
	private void handleUpdateInterestWrk(){
		
		final UpdateInterestWrkRequest request;
		
		try {
			
			log.info(" ### (ManageInterestConfirmPanel) handleInterestWrk ### ");
			request = basePage.getNewMobiliserRequest(UpdateInterestWrkRequest.class);
			request.setId(interestBean.getId());
			request.setDescription(interestBean.getDescription());
			request.setAccrueGLCode(Long.parseLong(interestBean.getAccrueGLCode().getId()));
			request.setExpenseGLCode(Long.parseLong(interestBean.getExpenseGLCode().getId()));
			
			request.setThresholdAmount(interestBean.getThresholdAmount()*100);
			request.setFixedFee(interestBean.getFixedFee()*100);
			request.setPercentageFee(new BigDecimal(interestBean.getPercentageFee()).movePointRight(2).setScale(0, RoundingMode.DOWN).longValue());
			request.setMaximumFee(interestBean.getMaximumFee());
			request.setMinimumFee(interestBean.getMinimumFee());
		
			request.setNote(interestBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final UpdateInterestWrkResponse response = this.basePage.getInterestWrkClient().update(request);
			log.info(" ### (ManageInterestConfirmPanel) handleInterestWrk ### "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				
				getSession().info(getLocalizer().getString("update.interest.success", this));
				setResponsePage(new ManageInterestSuccessPage(interestBean));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Update Interest Wrk ===> ", e);
		}
	}
	
	
	
	private void handleDeleteInterestWrk(){
		
		final RemoveInterestWrkRequest request;
		
		try {
			
			log.info(" ### (ManageInterestConfirmPanel) handleInterestWrk ### ");
			request = basePage.getNewMobiliserRequest(RemoveInterestWrkRequest.class);
			request.setId(interestBean.getId());
			request.setNote(interestBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final RemoveInterestWrkResponse response = this.basePage.getInterestWrkClient().remove(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				
				getSession().info(getLocalizer().getString("delete.interest.success", this));
				setResponsePage(new ManageInterestSuccessPage(interestBean));
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
