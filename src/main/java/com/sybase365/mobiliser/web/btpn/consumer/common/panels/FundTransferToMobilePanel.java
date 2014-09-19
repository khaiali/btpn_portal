package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import java.util.Calendar;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.debit.facade.api.DebitFacade;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAccountType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAmountAndCurrencyType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAttributeType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitInquiryRequest;
import com.btpnwow.core.debit.services.contract.v1_0.DebitInquiryResponse;
import com.btpnwow.core.debit.services.contract.v1_0.DebitTransactionType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType.Status;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.common.components.FavouriteDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.FundTransferConfirmPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class FundTransferToMobilePanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(FundTransferToMobilePanel.class);

	private static final String ORIGIN = "CONSUMER_PORTAL";
	private static final String PROCESSING_CODE	= "100801";
	private static final String MERCHANT_TYPE = "6012";
	private static final String MERCHANT_ID = "BTPN";
	private static final String TERMINAL_ID = "CONSUMER_PORTAL";
	private static final String ACQUIRER_ID = "BTPN";
	private static final String DEBIT_ACCT_TYPE = "MSISDN";
	private static final String ORG_UNIT_ID = "0901";
	private static final int DEBIT_ACCT_FLAGS = 0;
	private static final int CREDIT_ACCT_FLAGS = 0;
	private static final String CREDIT_ACCT_TYPE = "MSISDN";
	private static final String CURRENCY = "IDR";
	private static final String DESC = "FT INQUIRY";
	
	@SpringBean(name = "debitClient")
	private DebitFacade debitFacade;
	
	protected BtpnMobiliserBasePage basePage;
	protected FundTransferBean fundTransfer;
	private FeedbackPanel feedBack;
	private String filterType = BtpnConstants.FILTERTYPE_MANUAL;
	
	String selectedFundTransferType;

	public FundTransferToMobilePanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage,
		FundTransferBean fundTransferBean) {
		super(id);
		this.basePage = basePage;
		this.fundTransfer = fundTransferBean;
		selectedFundTransferType = fundTransfer.getSelectedFundTransferType();
		constructPanel();
	}

	protected void constructPanel() {

		final Form<FundTransferToMobilePanel> form = new Form<FundTransferToMobilePanel>("fundTransferMobileForm",
			new CompoundPropertyModel<FundTransferToMobilePanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		final String messageKey = "header." + selectedFundTransferType;
		String headerMessage = getLocalizer().getString(messageKey, this);
		form.add(new Label("headerMessage", headerMessage));

		RadioGroup<FundTransferToMobilePanel> radioGroup = new RadioGroup<FundTransferToMobilePanel>("radioGroup",
			new PropertyModel<FundTransferToMobilePanel>(this, "filterType"));

		@SuppressWarnings({ "rawtypes", "unchecked" })
		Radio<FundTransferToMobilePanel> enterManually = new Radio<FundTransferToMobilePanel>("EnterManually",
			new Model(BtpnConstants.FILTERTYPE_MANUAL));
		enterManually.setOutputMarkupId(true).setMarkupId("EnterManually");
		radioGroup.add(enterManually);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		Radio<FundTransferToMobilePanel> favoriteList = new Radio<FundTransferToMobilePanel>("FavoriteList", new Model(
			BtpnConstants.FILTERTYPE_FAVORITE));
		favoriteList.setOutputMarkupId(true).setMarkupId("FavoriteList");
		radioGroup.add(favoriteList);

		form.add(radioGroup);

		final WebMarkupContainer enterManualllyDiv = new WebMarkupContainer("enterManualllyDiv");
		enterManualllyDiv.setOutputMarkupId(true);
		enterManualllyDiv.setOutputMarkupPlaceholderTag(true);
		
		
		enterManualllyDiv.add(new TextField<String>("fundTransfer.payeeMsisdn").setRequired(true)
			.add(new PatternValidator(BtpnConstants.REGEX_PHONE_NUMBER)).add(BtpnConstants.PHONE_NUMBER_VALIDATOR)
			.add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH).add(new ErrorIndicator()));

		form.add(enterManualllyDiv);
		
		form.add(new AmountTextField<Long>("fundTransfer.amount", Long.class, false).setRequired(true).add(
			new ErrorIndicator()));

//		final WebMarkupContainer selectFromFavoriteDiv = new WebMarkupContainer("selectFromFavoriteDiv");
//		selectFromFavoriteDiv.setOutputMarkupId(true);
//		selectFromFavoriteDiv.setOutputMarkupPlaceholderTag(true);
//		selectFromFavoriteDiv.setVisible(false);
//		selectFromFavoriteDiv.add(new FavouriteDropdownChoice("fundTransfer.favoriteNum", false, true,
//			BtpnConstants.USECASE_FT_MOBILE_TO_MOBILE, basePage.getMobiliserWebSession().getBtpnLoggedInCustomer()
//				.getCustomerId()).setNullValid(false).setRequired(true).add(new ErrorIndicator()));
		
		final WebMarkupContainer selectFromFavoriteDiv = new WebMarkupContainer("selectFromFavoriteDiv");
		selectFromFavoriteDiv.setOutputMarkupId(true);
		selectFromFavoriteDiv.setOutputMarkupPlaceholderTag(true);
		selectFromFavoriteDiv.setVisible(false);
		selectFromFavoriteDiv.add(new FavouriteDropdownChoice("fundTransfer.favoriteNum", false, true,
			basePage.getMobiliserWebSession().getBtpnLoggedInCustomer()
				.getCustomerId(), 2).setNullValid(false).setRequired(true).add(new ErrorIndicator()));
		
		form.add(selectFromFavoriteDiv);

		enterManually.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				log.info("Selecetd Type : " + filterType);
				selectFromFavoriteDiv.setVisible(false);
				target.addComponent(selectFromFavoriteDiv);
				enterManualllyDiv.setVisible(true);
				target.addComponent(enterManualllyDiv);
			}
		});

		favoriteList.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				log.info("Selecetd Type : " + filterType);
				selectFromFavoriteDiv.setVisible(true);
				target.addComponent(selectFromFavoriteDiv);
				enterManualllyDiv.setVisible(false);
				target.addComponent(enterManualllyDiv);
			}
		});

		form.add(new AjaxButton("submitButton") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				
				try {
					
					log.info(" ### (FundTransferToMobilePanel::submit) ### ");
					String payeerAccountNo = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
					
					if(PortalUtils.exists(fundTransfer.getPayeeMsisdn())){
						final PhoneNumber phoneNumber = new PhoneNumber(fundTransfer.getPayeeMsisdn(), basePage
						.getAgentPortalPrefsConfig().getDefaultCountryCode());
						fundTransfer.setPayeeMsisdn(phoneNumber.getNationalFormat());	
					}
					
					UUID uuid = UUID.randomUUID();
					String traceNo = uuid.toString();
					final DebitInquiryRequest req = basePage.getNewMobiliserRequest(DebitInquiryRequest.class); 
					req.setOrigin(ORIGIN);
					req.setTraceNo(traceNo);
					req.setRepeat(false);
					UUID uuid2 = UUID.randomUUID();
					String convId = uuid2.toString();
					req.setConversationId(convId);
					req.setFinal(false);
					req.setProcessingCode(PROCESSING_CODE);
					
					/* Get Current Calendar */
					Calendar cal = Calendar.getInstance();
					XMLGregorianCalendar transDate = PortalUtils.getSaveXMLGregorianCalendar(cal);
					log.info(" ### XML GREGORIAN CURRENT DATE ### " + transDate);
					req.setTransactionDateTime(transDate);
					
					req.setMerchantType(MERCHANT_TYPE);
					req.setMerchantId(MERCHANT_ID);
					req.setTerminalId(TERMINAL_ID);
					req.setAcquirerId(ACQUIRER_ID);
					
					req.setDescription(DESC);
					
					DebitAmountAndCurrencyType dbAmtAndCurType = new DebitAmountAndCurrencyType();
					dbAmtAndCurType.setCurrency(CURRENCY);
					
					log.info("### (FundTransferToMobilePanel::createPreFundTransferToOtherAccRequest) AMOUNT ###" +fundTransfer.getAmount());
					dbAmtAndCurType.setValue(fundTransfer.getAmount());
					
					/* Request For Debit Account */
					DebitAccountType dbAcct = new DebitAccountType();
					dbAcct.setNumber(payeerAccountNo);
					dbAcct.setType(DEBIT_ACCT_TYPE);
					dbAcct.setOrgUnitId(ORG_UNIT_ID);
					dbAcct.setFlags(DEBIT_ACCT_FLAGS);
					
					/* Request For Credit Account */
					DebitAccountType crAcct = new DebitAccountType();
					crAcct.setNumber(PortalUtils.exists(fundTransfer.getPayeeMsisdn()) ? fundTransfer.getPayeeMsisdn() : fundTransfer.getFavoriteNum().getId());
					crAcct.setType(CREDIT_ACCT_TYPE);
					crAcct.setFlags(CREDIT_ACCT_FLAGS);
					
					DebitTransactionType obj = new DebitTransactionType();
					obj.setDebitAccount(dbAcct);
					obj.setCreditAccount(crAcct);
					obj.setAmount(dbAmtAndCurType);
					
					DebitAttributeType dbAttr = new DebitAttributeType();
					dbAttr.setKey(35);
					dbAttr.setValue(fundTransfer.getPayeeMsisdn());
					
					req.getTransaction().add(obj);
					req.getAttribute().add(dbAttr);
					log.info(" ### (FundTransferToMobilePanel::onSubmit) BEFORE SERVICE ### ");
					final DebitInquiryResponse response = debitFacade.inquiry(req);
					Status status = response.getStatus();
					log.info(" ### (FundTransferToMobilePanel::onSubmit) RESPONSE INQUIRY STATUS ### " + status.getCode() +"   "+status.getValue());
					
					if (status.getCode() == 0){
						if (basePage.evaluateBankPortalMobiliserResponse(response)) {
							for (DebitTransactionType ftBean : response.getTransaction()){
								log.info("### (FundTransferToMobilePanel::onSubmit) PAYEE NAME ###" +ftBean.getCreditAccount().getHolderName());
								fundTransfer.setPayeeMsisdn(fundTransfer.getPayeeMsisdn());
								log.info("### (FundTransferToMobilePanel::onSubmit) FEE ###" +ftBean.getFee().getValue());
								fundTransfer.setFeeAmount(ftBean.getFee().getValue());
								log.info("### (FundTransferToMobilePanel::onSubmit) PAYER NAME ###" +ftBean.getDebitAccount().getHolderName());
								fundTransfer.setPayerMsisdn(ftBean.getDebitAccount().getHolderName());
								log.info("### (FundTransferToMobilePanel::onSubmit) AMOUNT ###" +fundTransfer.getAmount());
								fundTransfer.setAmount(fundTransfer.getAmount());
								fundTransfer.setFlag("REG_MOBILE");
							}
							setResponsePage(new FundTransferConfirmPage(fundTransfer));
						} else {  
							error(handleSpecificErrorMessage(response.getStatus().getCode()));
						}
						
					} else if (status.getCode()== 2501){
						createPreFundTransferToUnRegMobileRequest(fundTransfer);
					} else {
						error(handleSpecificErrorMessage(response.getStatus().getCode()));
					}
				
				} catch (Exception e) {
					log.error(" ### An error occurred while calling service ### ", e);
					error(getLocalizer().getString("inquiry.failure.exception", FundTransferToMobilePanel.this));
				}
			};

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBack);
				super.onError(target, form);
			}
		});

		add(form);
	}

	
	private void createPreFundTransferToUnRegMobileRequest(FundTransferBean bean) {
		
		try {
			
			log.info(" ### (FundTransferToMobilePanel::submit) ### ");
			String payeerAccountNo = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			
			UUID uuid = UUID.randomUUID();
			String traceNo = uuid.toString();
			log.info(" ### TRACE NO ### " + traceNo);
			final DebitInquiryRequest req = basePage.getNewMobiliserRequest(DebitInquiryRequest.class); 
			req.setOrigin(ORIGIN);
			req.setTraceNo(traceNo);
			req.setRepeat(false);
			UUID uuid2 = UUID.randomUUID();
			String convId = uuid2.toString();
			log.info(" ### CONVERSATION ID ### " + convId);
			req.setConversationId(convId);
			req.setFinal(false);
			req.setProcessingCode("100701");
			
			/* Get Current Calendar */
			Calendar cal = Calendar.getInstance();
			XMLGregorianCalendar transDate = PortalUtils.getSaveXMLGregorianCalendar(cal);
			log.info(" ### XML GREGORIAN CURRENT DATE ### " + transDate);
			req.setTransactionDateTime(transDate);
			
			req.setMerchantType(MERCHANT_TYPE);
			req.setMerchantId(MERCHANT_ID);
			req.setTerminalId(TERMINAL_ID);
			req.setAcquirerId(ACQUIRER_ID);
			
			req.setDescription(DESC);
			
			DebitAmountAndCurrencyType dbAmtAndCurType = new DebitAmountAndCurrencyType();
			dbAmtAndCurType.setCurrency(CURRENCY);
			
			log.info("### (FundTransferToMobilePanel::createPreFundTransferToUnRegMobileRequest) AMOUNT ###" +fundTransfer.getAmount());
			dbAmtAndCurType.setValue(fundTransfer.getAmount());
			
			/* Request For Debit Account */
			DebitAccountType dbAcct = new DebitAccountType();
			dbAcct.setNumber(payeerAccountNo);
			dbAcct.setType(DEBIT_ACCT_TYPE);
			dbAcct.setOrgUnitId(ORG_UNIT_ID);
			dbAcct.setFlags(DEBIT_ACCT_FLAGS);
			
			/* Request For Credit Account */
			DebitAccountType crAcct = new DebitAccountType();
			crAcct.setNumber(PortalUtils.exists(fundTransfer.getPayeeMsisdn()) ? fundTransfer.getPayeeMsisdn() : fundTransfer.getFavoriteNum().getId());
			crAcct.setType("3");
			crAcct.setFlags(CREDIT_ACCT_FLAGS);
			
			DebitTransactionType obj = new DebitTransactionType();
			obj.setDebitAccount(dbAcct);
			obj.setCreditAccount(crAcct);
			obj.setAmount(dbAmtAndCurType);
			
			DebitAttributeType dbAttr = new DebitAttributeType();
			dbAttr.setKey(53);
			dbAttr.setValue(fundTransfer.getPayeeMsisdn());
			
			req.getTransaction().add(obj);
			req.getAttribute().add(dbAttr);
			
			final DebitInquiryResponse response = debitFacade.inquiry(req);
			Status status = response.getStatus();
			log.info(" ### (FundTransferToMobilePanel::createPreFundTransferToUnRegMobileRequest) RESPONSE INQUIRY STATUS ### " + status.getCode() +"   "+status.getValue());
			
			if (status.getCode() == 0){
				if (basePage.evaluateBankPortalMobiliserResponse(response)) {
					for (DebitTransactionType ftBean : response.getTransaction()){
						log.info("### (FundTransferToMobilePanel::createPreFundTransferToUnRegMobileRequest) PAYEE NAME ###" +ftBean.getCreditAccount().getHolderName());
						fundTransfer.setPayeeMsisdn(fundTransfer.getPayeeMsisdn());
						log.info("### (FundTransferToMobilePanel::createPreFundTransferToUnRegMobileRequest) FEE ###" +ftBean.getFee().getValue());
						fundTransfer.setFeeAmount(ftBean.getFee().getValue());
						log.info("### (FundTransferToMobilePanel::createPreFundTransferToUnRegMobileRequest) PAYER NAME ###" +ftBean.getDebitAccount().getHolderName());
						fundTransfer.setPayerMsisdn(ftBean.getDebitAccount().getHolderName());
						log.info("### (FundTransferToMobilePanel::createPreFundTransferToUnRegMobileRequest) AMOUNT ###" +fundTransfer.getAmount());
						fundTransfer.setAmount(fundTransfer.getAmount());
						fundTransfer.setFlag("UNREG_MOBILE");
					}
					setResponsePage(new FundTransferConfirmPage(fundTransfer));
				} else {  
					log.info(" ##### ELSE COMING ##### ");
					error(handleSpecificErrorMessage(response.getStatus().getCode()));
				}

			} else {
				error(handleSpecificErrorMessage(response.getStatus().getCode()));
			}
		
		} catch (Exception e) {
			log.error(" ### An error occurred while calling service ### ");
			error(getLocalizer().getString("inquiry.failure.exception", FundTransferToMobilePanel.this));
		}
	}
	
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("inquiry.fail", this);
		}
		return message;
	}
}
