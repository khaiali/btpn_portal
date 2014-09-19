package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.btpnwow.core.account.facade.api.AccountFacade;
import com.btpnwow.core.account.facade.contract.AccountInformationType;
import com.btpnwow.core.account.facade.contract.FindCustomerAccountRequest;
import com.btpnwow.core.account.facade.contract.FindCustomerAccountResponse;
import com.btpnwow.core.debit.facade.api.DebitFacade;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAccountType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAmountAndCurrencyType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitInquiryRequest;
import com.btpnwow.core.debit.services.contract.v1_0.DebitInquiryResponse;
import com.btpnwow.core.debit.services.contract.v1_0.DebitTransactionType;
import com.btpnwow.core.security.facade.api.SecurityFacade;
import com.btpnwow.core.security.facade.contract.CustomerCredentialType;
import com.btpnwow.core.security.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.security.facade.contract.VerifyCredentialRequest;
import com.btpnwow.core.security.facade.contract.VerifyCredentialResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType.Status;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCashInOutConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AccountDropDownChoice;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the BankPortalCustomCashInPanel page for bank portals.
 * 
 * @modified Feny Yanti
 */
public class BankPortalCashInOutPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(BankPortalCashInOutPanel.class);

	private static final String MERCHANT_TYPE = "6012";
	
	private static final String MERCHANT_ID = "BANK_PORTAL";
	
	private static final String TERMINAL_ID = "BANK_PORTAL";
	
	private static final String ACQUIRER_ID = "BTPN";
	
	private static final String CURRENCY = "IDR";
	
	private final BtpnMobiliserBasePage basePage;
	
	private final BankCustomCashInBean cashInBean;
	
	private final boolean cashOut;
	
	private WebMarkupContainer runAsContainer;
	
	private String username;
	
	private String password;
	
	@SpringBean(name="accountClient")
	private AccountFacade accountFacade;
	
	@SpringBean(name = "debitClient")
	private DebitFacade debitClient;
	
	@SpringBean(name = "securityClientProvider")
	private SecurityFacade securityFacade;
	
	public BankPortalCashInOutPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCustomCashInBean cashInBean, 
									String processingCode, boolean cashOut) {
		super(id);
		
		this.cashOut = cashOut;
		this.basePage = basePage;
		
		this.cashInBean = cashInBean == null ? new BankCustomCashInBean() : cashInBean;
		this.cashInBean.setProcessingCode(processingCode);
		this.cashInBean.setIsFinal(cashOut);
		
		constructPanel();
	}

	protected void constructPanel() {
		log.info(" ##### constructPanel ##### ");
		
		final Form<BankPortalCashInOutPanel> form = new Form<BankPortalCashInOutPanel>(
				"customCashInForm",
				new CompoundPropertyModel<BankPortalCashInOutPanel>(this));

		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("headLine.checkMobileAccount", cashOut ? "CASH-OUT" : "CASH-IN"));
		form.add(new AccountDropDownChoice(
								"cashInBean.glAccount", false, true,
								Long.toString(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()),
								1, Collections.singleton(Integer.valueOf(999)), null)
				.setNullValid(false)
				.setRequired(true)
				.add(new ErrorIndicator()));
		
		form.add(new TextField<String>("cashInBean.msisdn")
				.setRequired(true)
				//.add(new PatternValidator(this.basePage.getBankPortalPrefsConfig().getMobileRegex()))
				//.add(BtpnConstants.PHONE_NUMBER_VALIDATOR)
				.add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH)
				.add(new ErrorIndicator()));
		
		form.add(new AmountTextField<Long>("cashInBean.amount", Long.class, false)
				.setRequired(true)
				.add(new ErrorIndicator()));
		
		form.add(runAsContainer = new WebMarkupContainer("runAsContainer"));
		runAsContainer.setVisible(false);
		runAsContainer.setOutputMarkupId(true);
		runAsContainer.setOutputMarkupPlaceholderTag(true);
		
		runAsContainer.add(new TextField<String>("username")
				.setRequired(false)
				.add(BtpnConstants.LOGIN_USER_NAME_MAX_LENGTH)
				.add(new ErrorIndicator()));
		
		runAsContainer.add(new PasswordTextField("password")
				.setRequired(false)
				.add(BtpnConstants.PASSWORD_MAX_LENGTH)
				.add(new ErrorIndicator()));
		
		form.add(new Button("submitButton") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleInquiry();
			}
		});

		add(form);
	}
	
	private String runAs(String username, String password, String glCode, String orgUnitId) {
		if (!StringUtils.hasText(password)) {
			password = "";
		}
		
		// login
		VerifyCredentialRequest loginRequest = new VerifyCredentialRequest();

		final CustomerIdentificationType loginIdent = new CustomerIdentificationType();
		loginIdent.setValue(username);
		loginIdent.setType(BtpnConstants.IDENTIFICATION_TYPE_USER_NAME);

		final CustomerCredentialType loginCred = new CustomerCredentialType();
		loginCred.setType(-1);
		loginCred.setValue(password);

		loginRequest.setIdentification(loginIdent);
		loginRequest.setCredential(loginCred);
		loginRequest.setFlags(0);

		VerifyCredentialResponse loginResponse = securityFacade.verifyCredential(loginRequest);
		if (!MobiliserUtils.success(loginResponse)) {
			error(MobiliserUtils.errorMessage(loginResponse, basePage));
			return null;
		}
		
		// get account 
		com.btpnwow.core.account.facade.contract.CustomerIdentificationType accountIdent = new com.btpnwow.core.account.facade.contract.CustomerIdentificationType();
		accountIdent.setType(5);
		accountIdent.setValue(username);
		accountIdent.setOrgUnitId(orgUnitId);
		
		FindCustomerAccountRequest accountRequest = new FindCustomerAccountRequest();
		accountRequest.setCallback(null);
		accountRequest.setConversationId(UUID.randomUUID().toString());
		accountRequest.setOrigin("mobiliser-web");
		accountRequest.setRepeat(Boolean.FALSE);
		accountRequest.setTraceNo(UUID.randomUUID().toString());
		accountRequest.setIdentification(accountIdent);
		
		accountRequest.getPaymentInstrumentType().add(Integer.valueOf(999));
		
		accountRequest.setFlags(0);
		
		FindCustomerAccountResponse accountResponse = accountFacade.find(accountRequest);
		if (!MobiliserUtils.success(accountResponse)) {
			error(MobiliserUtils.errorMessage(accountResponse, basePage));
			return null;
		}
		
		List<AccountInformationType> accounts = accountResponse.getAccount();
		if ((accounts == null) || accounts.isEmpty()) {
			error(getLocalizer().getString("runAs.invalid", this));
			return null;
		}
		
		for (AccountInformationType e : accounts) {
			if (glCode.equals(e.getData())) {
				return Long.toString(e.getId());
			}
		}
		
		error(getLocalizer().getString("runAs.invalid", this));
		return null;
	}
	
	/**
	 * This method handles the cash in inquiry.
	 */
	private void handleInquiry() {
		final String conversationId = UUID.randomUUID().toString();
		
		final String orgUnitId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getOrgUnitId();
		
		String tellerWalletId = cashInBean.getGlAccount().getId();
		
		if (runAsContainer.isVisible()) {
			if (StringUtils.hasText(username)) {
				tellerWalletId = runAs(username, password, cashInBean.getGlAccount().getValue(), orgUnitId);
				
				if (tellerWalletId == null) {
					return;
				}
			}
		}
		
		try {
			
			log.info(" ##### performCashInInquiry ##### ");
			log.info(" ##### GL Account ##### " + cashInBean.getGlAccount().getIdAndValue());
			log.info(" ##### MSISDN ##### " + cashInBean.getMsisdn());			
			
			/* Request For Debit Account */
			DebitAccountType debit = new DebitAccountType();
			if (cashOut) {
				debit.setNumber(cashInBean.getMsisdn());
				debit.setType("MSISDN");
			} else {
				debit.setNumber(tellerWalletId);
				debit.setType("WALLET");
			}
			debit.setFlags(0);
			debit.setOrgUnitId(orgUnitId);
			
			/* Request For Credit Account */
			DebitAccountType credit = new DebitAccountType();
			if (cashOut) {
				credit.setNumber(tellerWalletId);
				credit.setType("WALLET");
			} else {
				credit.setNumber(cashInBean.getMsisdn());
				credit.setType("MSISDN");
			}
			credit.setOrgUnitId(orgUnitId);
			credit.setFlags(0);
			
			DebitAmountAndCurrencyType amount = new DebitAmountAndCurrencyType();
			amount.setCurrency(CURRENCY);
			amount.setValue(cashInBean.getAmount().longValue());
			
			DebitTransactionType transaction = new DebitTransactionType();
			transaction.setDebitAccount(debit);
			transaction.setCreditAccount(credit);
			transaction.setAmount(amount);
			
			final DebitInquiryRequest req = basePage.getNewMobiliserRequest(DebitInquiryRequest.class); 
			req.setConversationId(conversationId);
			req.setFinal(false);
			req.setProcessingCode(cashInBean.getProcessingCode());
			req.setTransactionDateTime(PortalUtils.getSaveXMLGregorianCalendar(Calendar.getInstance()));
			req.setMerchantType(MERCHANT_TYPE);
			req.setMerchantId(MERCHANT_ID);
			req.setTerminalId(TERMINAL_ID);
			req.setAcquirerId(ACQUIRER_ID);
			req.setDescription(cashOut ? "Cash Out Via Bank" : "Cash In Via Bank");
			req.getTransaction().add(transaction);
			
			log.info("### REQUEST " + req);
			final DebitInquiryResponse response = debitClient.inquiry(req);
			
			Status status = response.getStatus();
			log.info(" ### RESPONSE INQUIRY STATUS ### " + status.getCode() +"   "+status.getValue());
			
			if (MobiliserUtils.success(response)) {
				fill(cashInBean, response.getTransaction());
		
				cashInBean.setOrgUnitId(orgUnitId);
				cashInBean.setDebitAccountNumber(debit.getNumber());
				cashInBean.setDebitAccountType(debit.getType());
				cashInBean.setCreditAccountNumber(credit.getNumber());
				cashInBean.setCreditAccountType(credit.getType());
				cashInBean.setTransactionDatetime(req.getTransactionDateTime());
				cashInBean.setConversationId(conversationId);
				log.info("conversationId:"+conversationId);
				cashInBean.setMerchantType(MERCHANT_TYPE);
				cashInBean.setMerchantId(MERCHANT_ID);
				cashInBean.setTerminalId(TERMINAL_ID);
				cashInBean.setAcquirerId(ACQUIRER_ID);
				
				setResponsePage(new BankPortalCashInOutConfirmPage(cashInBean));
			} else {
				log.info(" ##### ELSE COMING ##### ");
				
				int errorCode = MobiliserUtils.errorCode(response);
				
				switch (errorCode) {
				case 2001:
				case 2002:
				case 2231:
				case 2232:
				case 2540:
				case 2543:
				case 2544:
				case 2545:
				case 2546:
				case 2547:
				case 2548:
				case 2549:
				case 2553:
				case 2554:
				case 2559:
				case 2560:
				case 2561:
				case 2562:
				case 2563:
				case 2564:
				case 2565:
				case 2566:
				case 2567:
				case 2568:
				case 2569:
				case 2570:
				case 2571:
				case 2572:
				case 2573:
				case 2574:
				case 2575:
				case 2576:
				case 2577:
				case 2578:
				case 2579:
				case 2580:
				case 2594:
				case 2595:
				case 2596:
				case 2597:
				case 2606:
				case 2607:
				case 2608:
				case 2609:
				case 2610:
				case 2612:
				case 2613:
				case 2614:
				case 2615:
				case 2706:
				case 2707:
				case 2708:
				case 2709:
				case 2710:
				case 2712:
				case 2713:
				case 2714:
				case 2715:
					error(MobiliserUtils.errorMessage(response, basePage));
					
					runAsContainer.setVisible(true);
					break;
				
				default:
					error(handleSpecificErrorMessage(errorCode, MobiliserUtils.errorMessage(response, basePage)));
					break;
				}
			}
		} catch (Exception e) {
			log.error(" ### An error occurred while calling service ### ", e);
			
			error(getLocalizer().getString("inquiry.failure.exception", BankPortalCashInOutPanel.this));
		}
	}
	
	private void fill(BankCustomCashInBean bean, List<DebitTransactionType> transactions) {
		if ((transactions != null) && !transactions.isEmpty()) {
			for (DebitTransactionType transaction : transactions) {
				if (cashOut) {
					cashInBean.setAccountName(transaction.getDebitAccount().getHolderName());
				} else {
					cashInBean.setAccountName(transaction.getCreditAccount().getHolderName());
				}
				
				cashInBean.setFee(Long.valueOf(transaction.getFee().getValue()));
			}
		}
	}
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode, String defaultMessage) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = defaultMessage;
		}
		return message;
	}

}
