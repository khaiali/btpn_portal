package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import java.util.Calendar;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.debit.facade.api.DebitFacade;
import com.btpnwow.core.debit.services.contract.v1_0.DebitInquiryRequest;
import com.btpnwow.core.debit.services.contract.v1_0.DebitPostingRequest;
import com.btpnwow.core.debit.services.contract.v1_0.DebitPostingResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.SubAccountTransferPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.SubAccountTransferSuccessPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;



public class SubAccountTransferPinPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(SubAccountTransferPinPanel.class);

	protected BtpnMobiliserBasePage basePage;
	
	private final DebitInquiryRequest inquiryRequest;

	protected SubAccountsBean subAccountBean;

	protected String subAccountPin;
	String selectedTransferType;
	String accountType;
	String userName;
	
	@SpringBean(name = "debitClient")
	private DebitFacade debitClient;

	public SubAccountTransferPinPanel(String id, BtpnMobiliserBasePage basePage, DebitInquiryRequest inquiryRequest, SubAccountsBean subAccountBean,
		String type) {
		super(id);
		this.basePage = basePage;
		this.inquiryRequest = inquiryRequest;
		this.subAccountBean = subAccountBean;
		this.selectedTransferType = type;
		userName = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
		constructPanel();
	}

	protected void constructPanel() {
		
		final Form<SubAccountTransferPinPanel> form = new Form<SubAccountTransferPinPanel>("subAccountPinForm",
			new CompoundPropertyModel<SubAccountTransferPinPanel>(this));
		
		form.add(new FeedbackPanel("errorMessages"));

		if (selectedTransferType.equals(BtpnConstants.RESOURCE_BUBDLE_TRANSFER_TYPE_PTOS)) {
			accountType = SubAccountTransferPinPanel.this.getLocalizer().getString("label.toAccount",
				SubAccountTransferPinPanel.this);
		} else if (selectedTransferType.equals(BtpnConstants.RESOURCE_BUBDLE_TRANSFER_TYPE_STOP)) {
			accountType = SubAccountTransferPinPanel.this.getLocalizer().getString("label.fromAccount",
				SubAccountTransferPinPanel.this);
		}
		
		form.add(new Label("accountType", accountType));

		form.add(new Label("subAccountBean.name"));
		form.add(new Label("subAccountBean.description"));
		form.add(new Label("subAccountBean.accountId"));
		form.add(new AmountLabel("subAccountBean.amount"));
		form.add(new Label("subAccountBean.remarks"));

		// Add PIN field to form
		form.add(new PasswordTextField("subAccountPin").setRequired(true).add(new ErrorIndicator()));

		form.add(new Button("cancelButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new SubAccountTransferPage());
			};
		}.setDefaultFormProcessing(false));

		form.add(new Button("confirmButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				
				try {
					
					if (basePage.checkCredential(getSubAccountPin())) {
						if (selectedTransferType.equals(BtpnConstants.RESOURCE_BUBDLE_TRANSFER_TYPE_PTOS)) {
							createFTPrimaryToSubRequest(subAccountBean);
						} else if (selectedTransferType.equals(BtpnConstants.RESOURCE_BUBDLE_TRANSFER_TYPE_STOP)) {
							createFTSubToPrimaryRequest(subAccountBean);
						} else {
							error("An error occurred. Try again later");
						}
					} else {
						error("Invalid Pin");
					}
				
				} catch (Exception ex) {
					log.error("#An error occurred while calling FundTransfer service", ex);
					error(getLocalizer().getString("error.exception", this));
				}
			};
		});
		
		add(form);
	}
	
	
	
	private void createFTPrimaryToSubRequest(SubAccountsBean bean) {
		
		DebitPostingResponse primaryToSubResponse = null;
		
		try {
			
			log.info(" ### (SubAccountTransferPinPanel::createFTPrimaryToSubRequest) ### ");
			final DebitPostingRequest request = basePage.getNewMobiliserRequest(DebitPostingRequest.class);
			
			/* Get Current Calendar */
			Calendar cal = Calendar.getInstance();
			XMLGregorianCalendar transDate = PortalUtils.getSaveXMLGregorianCalendar(cal);
			log.info(" ### (SubAccountTransferPinPanel::createPreFTPrimaryToSubRequest) XML GREGORIAN CURRENT DATE ### " + transDate);
		   
		    /* Request For Debit Account */
//		    DebitAccountType dbAcct = new DebitAccountType();
//			dbAcct.setNumber(userName);
//			dbAcct.setType("MSISDN");
//			dbAcct.setFlags(0);
			
			/* Request For Credit Account */
//			DebitAccountType crAcct = new DebitAccountType();
//			crAcct.setNumber(String.valueOf(subAccountBean.getAccountId()));
//			crAcct.setType("WALLET");
//			crAcct.setFlags(0);
			
//			DebitAmountAndCurrencyType dbAmtAndCurType = new DebitAmountAndCurrencyType();
//			dbAmtAndCurType.setCurrency("IDR");
//			dbAmtAndCurType.setValue(subAccountBean.getAmount());
			
			
//			DebitTransactionType obj = new DebitTransactionType();
//			obj.setDebitAccount(dbAcct);
//			obj.setCreditAccount(crAcct);
//			obj.setAmount(dbAmtAndCurType);

			
//			DebitAttributeType dbAttr = new DebitAttributeType();
//			dbAttr.setKey(110);
//			dbAttr.setValue("1");
			
//			UUID uuid2 = UUID.randomUUID();
//			String convId = uuid2.toString();
			
			request.setRepeat(false);
		    request.setConversationId(inquiryRequest.getConversationId());
		    request.setFinal(true);
		    
		    request.setProcessingCode(inquiryRequest.getProcessingCode());
		    request.setTransactionDateTime(inquiryRequest.getTransactionDateTime());
		    request.setMerchantType(inquiryRequest.getMerchantType());
		    request.setMerchantId(inquiryRequest.getMerchantId());
		    request.setTerminalId(inquiryRequest.getTerminalId());
		    request.setAcquirerId(inquiryRequest.getAcquirerId());
			
			// calling fundTransferPrimaryToSub service
			primaryToSubResponse = debitClient.posting(request);
			
			log.info(" ### (SubAccountTransferPinPanel::createFundTransferSknRtgsRequest) POSTING PRIMARY TO SUB RESPONSE ### " + primaryToSubResponse.getStatus().getCode());	
			if (basePage.evaluateConsumerPortalMobiliserResponse(primaryToSubResponse)
					&& primaryToSubResponse.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				setResponsePage(new SubAccountTransferSuccessPage(subAccountBean));
			} else {
				error(primaryToSubResponse.getStatus().getValue());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling fundTransferPrimaryToSub service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}
	
	
	private void createFTSubToPrimaryRequest(SubAccountsBean bean) {
		
		DebitPostingResponse subToPrimaryResponse = null;
		
		try {
			
			final DebitPostingRequest request = basePage.getNewMobiliserRequest(DebitPostingRequest.class);
			
			log.info(" ### (SubAccountTransferPinPanel::createFTSubToPrimaryRequest) USER NAME ### " +userName);
			
			/* Get Current Calendar */
//			Calendar cal = Calendar.getInstance();
//			XMLGregorianCalendar transDate = PortalUtils.getSaveXMLGregorianCalendar(cal);
//			log.info(" ### (SubAccountTransferPinPanel::createFTSubToPrimaryRequest) XML GREGORIAN CURRENT DATE ### " + transDate);
			
//			UUID uuid2 = UUID.randomUUID();
//			String convId = uuid2.toString();
			
			request.setRepeat(false);
		    request.setConversationId(inquiryRequest.getConversationId());
		    request.setFinal(true);
		    
		    request.setProcessingCode("102001");
		    request.setTransactionDateTime(inquiryRequest.getTransactionDateTime());
		    request.setMerchantType(inquiryRequest.getMerchantType());
		    request.setMerchantId(inquiryRequest.getMerchantId());
		    request.setTerminalId(inquiryRequest.getTerminalId());
		    request.setAcquirerId(inquiryRequest.getAcquirerId());
			
			// calling fundTransferSubToPrimary service
			subToPrimaryResponse = debitClient.posting(request);
			log.info(" ### (SubAccountTransferPinPanel::createFTSubToPrimaryRequest) POSTING FT SUB TO PRIMARY RESPONSE ### " +subToPrimaryResponse.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(subToPrimaryResponse)
					&& subToPrimaryResponse.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				setResponsePage(new SubAccountTransferSuccessPage(subAccountBean));
			} else {
				error(subToPrimaryResponse.getStatus().getValue());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling createFTSubToPrimaryRequest service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	public String getSubAccountPin() {
		return subAccountPin;
	}

	public void setSubAccountPin(String subAccountPin) {
		this.subAccountPin = subAccountPin;
	}

}
