package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import java.util.Calendar;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.debit.facade.api.DebitFacade;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAccountType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAmountAndCurrencyType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAttributeType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitInquiryRequest;
import com.btpnwow.core.debit.services.contract.v1_0.DebitInquiryResponse;
import com.btpnwow.core.debit.services.contract.v1_0.DebitTransactionType;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.SubAccountTransferPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.SubAccountTransferPinPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class SubAccountTransferConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(SubAccountTransferConfirmPanel.class);

	protected BtpnMobiliserBasePage basePage;
	protected SubAccountsBean subAccountBean;

	@SpringBean(name = "debitClient")
	private DebitFacade debitFacade;
	
	String selectedTransferType;
	String accountType;
	String userName;
	
	public SubAccountTransferConfirmPanel(String id, BtpnMobiliserBasePage basePage) {
		super(id);
		this.basePage = basePage;
		userName = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
		constructPanel();
	}

	public SubAccountTransferConfirmPanel(String id, BtpnMobiliserBasePage basePage, SubAccountsBean bean, String type) {
		super(id);
		this.basePage = basePage;
		this.subAccountBean = bean;
		this.selectedTransferType = type;
		userName = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
		constructPanel();
	}

	protected void constructPanel() {
		
		final Form<SubAccountTransferConfirmPanel> form = new Form<SubAccountTransferConfirmPanel>(
			"subAccountConfirmForm", new CompoundPropertyModel<SubAccountTransferConfirmPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		// Add transfer Type Message
		if (selectedTransferType.equals(BtpnConstants.RESOURCE_BUBDLE_TRANSFER_TYPE_PTOS)) {
			accountType = SubAccountTransferConfirmPanel.this.getLocalizer().getString("label.toAccount",
				SubAccountTransferConfirmPanel.this);
		} else if (selectedTransferType.equals(BtpnConstants.RESOURCE_BUBDLE_TRANSFER_TYPE_STOP)) {
			accountType = SubAccountTransferConfirmPanel.this.getLocalizer().getString("label.fromAccount",
				SubAccountTransferConfirmPanel.this);
		}
		
		form.add(new Label("accountType", accountType));
		form.add(new Label("subAccountBean.name"));
		form.add(new Label("subAccountBean.description"));
		form.add(new Label("subAccountBean.accountId"));

		form.add(new AmountTextField<Long>("subAccountBean.amount", Long.class, false).setRequired(true).add(
			new ErrorIndicator()));
		form.add(new TextArea<String>("subAccountBean.remarks"));

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
				if (selectedTransferType.equals(BtpnConstants.RESOURCE_BUBDLE_TRANSFER_TYPE_PTOS)) {
					createPreFTPrimaryToSubRequest(subAccountBean);
				} else if (selectedTransferType.equals(BtpnConstants.RESOURCE_BUBDLE_TRANSFER_TYPE_STOP)) {
					createPreFTSubToPrimaryRequest(subAccountBean);
				}
			};
		});
		add(form);
	}
	
	
	private void createPreFTPrimaryToSubRequest(SubAccountsBean bean) {
		
		DebitInquiryResponse primaryToSubResponse = null;
		
		try {
			
			log.info(" ### (SubAccountTransferConfirmPanel::createPreFTPrimaryToSubRequest) ### ");

			/* Get Current Calendar */
			Calendar cal = Calendar.getInstance();
			XMLGregorianCalendar transDate = PortalUtils.getSaveXMLGregorianCalendar(cal);
			
			final DebitInquiryRequest request = basePage.getNewMobiliserRequest(DebitInquiryRequest.class);
			
			String Att1 = " Transfer Uang ";
			String Att2 = " Dari Primary ";
			String Att3 = " Ke Sub";
			String Att4 = " ";

			String DESC = Att1 + Att2 + Att3 + Att4;
			
			 /* Request For Debit Account */
		    DebitAccountType dbAcct = new DebitAccountType();
			dbAcct.setNumber(userName);
			dbAcct.setType("MSISDN");
			dbAcct.setFlags(0);
			
			/* Request For Credit Account */
			DebitAccountType crAcct = new DebitAccountType();
			log.info(" ### (SubAccountTransferConfirmPanel::createPreFTPrimaryToSubRequest) ACCOUNT ID ### " +subAccountBean.getAccountId());
			crAcct.setNumber(String.valueOf(subAccountBean.getAccountId()));
			crAcct.setType("WALLET");
			crAcct.setFlags(0);
			
			DebitAmountAndCurrencyType dbAmtAndCurType = new DebitAmountAndCurrencyType();
			dbAmtAndCurType.setCurrency("IDR");
			dbAmtAndCurType.setValue(subAccountBean.getAmount());
			
			DebitTransactionType obj = new DebitTransactionType();
			obj.setDebitAccount(dbAcct);
			obj.setCreditAccount(crAcct);
			obj.setAmount(dbAmtAndCurType);
			
			DebitAttributeType dbAttr = new DebitAttributeType();
			dbAttr.setKey(110);
			dbAttr.setValue("1");
			
		    UUID uuid2 = UUID.randomUUID();
			String convId = uuid2.toString();
			
			request.setRepeat(false);
		    request.setConversationId(convId);
		    request.setFinal(false);
		    
		    request.setProcessingCode("101901");
		    request.setTransactionDateTime(transDate);
		    request.setMerchantType("6012");
		    request.setMerchantId("BTPN");
		    request.setTerminalId(userName);
		    request.setAcquirerId("213");
		    
			request.getTransaction().add(obj);
			request.setDescription(DESC);
			request.getAttribute().add(dbAttr);
			
			// calling preFundTransferPrimaryToSub service
			primaryToSubResponse = debitFacade.inquiry(request);
			log.info(" ### (SubAccountTransferConfirmPanel::createPreFTPrimaryToSubRequest) INQUIRY RESPONSE ### " +primaryToSubResponse.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(primaryToSubResponse)) {
				setResponsePage(new SubAccountTransferPinPage(request, subAccountBean, selectedTransferType));
			} else {
				error(primaryToSubResponse.getStatus().getValue());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling preFundTransferPrimaryToSub service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}
	
	
	
	private void createPreFTSubToPrimaryRequest(SubAccountsBean bean) {
		
		DebitInquiryResponse primaryToSubResponse = null;
		
		try {
			
			log.info(" ### (SubAccountTransferConfirmPanel::createPreFTSubToPrimaryRequest) ### ");
			
			/* Get Current Calendar */
			Calendar cal = Calendar.getInstance();
			XMLGregorianCalendar transDate = PortalUtils.getSaveXMLGregorianCalendar(cal);
			
			final DebitInquiryRequest request = basePage.getNewMobiliserRequest(DebitInquiryRequest.class);
			
			String Att1 = " Transfer Uang ";
			String Att2 = " Dari Sub ";
			String Att3 = " Ke Primary ";
			String Att4 = " ";
			
			String DESC = Att1 + Att2 + Att3 + Att4;
			
			 /* Request For Debit Account */
		    DebitAccountType dbAcct = new DebitAccountType();
			dbAcct.setNumber(String.valueOf(subAccountBean.getAccountId()));
			dbAcct.setType("WALLET");
			dbAcct.setFlags(0);
			
			/* Request For Credit Account */
			DebitAccountType crAcct = new DebitAccountType();
			log.info(" ### (SubAccountTransferConfirmPanel::createPreFTSubToPrimaryRequest) ACCOUNT ID ### " +subAccountBean.getAccountId());
			crAcct.setNumber(formatedMsisdn(userName));
			crAcct.setType("MSISDN");
			crAcct.setFlags(0);
			
			DebitAmountAndCurrencyType dbAmtAndCurType = new DebitAmountAndCurrencyType();
			dbAmtAndCurType.setCurrency("IDR");
			dbAmtAndCurType.setValue(subAccountBean.getAmount());
			
			DebitTransactionType obj = new DebitTransactionType();
			obj.setDebitAccount(dbAcct);
			obj.setCreditAccount(crAcct);
			obj.setAmount(dbAmtAndCurType);
			
			DebitAttributeType dbAttr = new DebitAttributeType();
			dbAttr.setKey(110);
			dbAttr.setValue("1");
			
		    UUID uuid2 = UUID.randomUUID();
			String convId = uuid2.toString();
			
			request.setRepeat(false);
		    request.setConversationId(convId);
		    request.setFinal(false);
		    
		    request.setProcessingCode("102001");
		    request.setTransactionDateTime(transDate);
		    request.setMerchantType("6012");
		    request.setMerchantId("BTPN");
		    request.setTerminalId(formatedMsisdn(userName));
		    request.setAcquirerId("213");
		    
			request.getTransaction().add(obj);
			request.setDescription(DESC);
			request.getAttribute().add(dbAttr);
			
			// Call Service
			primaryToSubResponse = debitFacade.inquiry(request);
			log.info(" ### (SubAccountTransferConfirmPanel::createPreFTSubToPrimaryRequest) INQUIRY RESPONSE ### " +primaryToSubResponse.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(primaryToSubResponse)) {
				setResponsePage(new SubAccountTransferPinPage(request, subAccountBean, selectedTransferType));
			} else {
				error(handleSpecificErrorMessage(primaryToSubResponse.getStatus().getCode()));
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling createPreFTSubToPrimaryRequest service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}
	
	private String formatedMsisdn(String msisdn) {
		final PhoneNumber phoneNumber = new PhoneNumber(msisdn, basePage.getAgentPortalPrefsConfig()
			.getDefaultCountryCode());
		return phoneNumber.getInternationalFormat();
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
