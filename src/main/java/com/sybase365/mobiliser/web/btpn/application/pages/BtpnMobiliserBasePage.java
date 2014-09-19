package com.sybase365.mobiliser.web.btpn.application.pages;

import id.co.btpn.corp.dev.appmdwdev02.com_btpn_emoney_ws.wowtransferservices.FundTransferResponse;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_emoney_ws.wowtransferservices.InquiryDebitFundTransferResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.ClientInfo;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.btpnwow.core.account.facade.api.AccountFacade;
import com.btpnwow.core.account.facade.contract.AccountIdentificationType;
import com.btpnwow.core.account.facade.contract.GetAccountRequest;
import com.btpnwow.core.account.facade.contract.GetAccountResponse;
import com.btpnwow.core.bulk.facade.api.BulkFileFacade;
import com.btpnwow.core.bulk.facade.api.BulkFileWrkFacade;
import com.btpnwow.core.debit.facade.api.DebitFacade;
import com.btpnwow.core.fee.facade.api.BillerFeeFacade;
import com.btpnwow.core.fee.facade.api.BillerFeeWrkFacade;
import com.btpnwow.core.fee.facade.api.UseCaseFeeFacade;
import com.btpnwow.core.fee.facade.api.UseCaseFeeWrkFacade;
import com.btpnwow.core.gl.facade.api.GLFacade;
import com.btpnwow.core.gl.facade.api.GLWrkFacade;
import com.btpnwow.core.interest.facade.api.InterestFacade;
import com.btpnwow.core.interest.facade.api.InterestTaxFacade;
import com.btpnwow.core.interest.facade.api.InterestTaxWrkFacade;
import com.btpnwow.core.interest.facade.api.InterestWrkFacade;
import com.btpnwow.core.limitex.services.api.ILimitExEndpoint;
import com.btpnwow.core.preregistered.facade.api.PreRegisteredFacade;
import com.btpnwow.core.security.facade.api.SecurityFacade;
import com.btpnwow.core.security.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.security.facade.contract.CustomerOtpType;
import com.btpnwow.core.security.facade.contract.VerifyOtpRequest;
import com.btpnwow.core.security.facade.contract.VerifyOtpResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IAirtimeTopupEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IBillPaymentEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IBulkFileProcessingEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IChangeMsisdnEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IDepositEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IEditProfileEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IFeeEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IFundTransferEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IGeneralLedgerEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IHolidayCalendarEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.ILimitEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.ILoginEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IManageFavoriteEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IProductEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IRegistrationEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.ISupportEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.ITransactionGLEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.ITransactionReversalEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.api.IWithDrawEndpoint;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.CustomerAttributes;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.CustomerIdentification;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.CustomerType;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Identity;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.editprofile.CustomerEditProfileRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.registration.GetCustomerNumberRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.registration.GetCustomerNumberResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetAllSubAccountsRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.GetAllSubAccountsResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.MsisdnExistsRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.MsisdnExistsResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.ViewProfileRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.ViewProfileResponse;
import com.sybase365.mobiliser.framework.contract.v5_0.base.AuditDataType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserRequestType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType.Status;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.CheckCredentialRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.CheckCredentialResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.LogoutRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.LogoutResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.SetCredentialRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.SetCredentialResponse;
import com.sybase365.mobiliser.money.services.api.IAttributeEndpoint;
import com.sybase365.mobiliser.money.services.api.ICustomerEndpoint;
import com.sybase365.mobiliser.money.services.api.IIdentificationEndpoint;
import com.sybase365.mobiliser.money.services.api.IOtpEndpoint;
import com.sybase365.mobiliser.money.services.api.ISecurityEndpoint;
import com.sybase365.mobiliser.money.services.api.IUmgrRolesPrivilegesEndpoint;
import com.sybase365.mobiliser.money.services.api.IWalletEndpoint;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.LookupResourceLoader;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.application.MobiliserApplication;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.util.AgentPortalConfiguration;
import com.sybase365.mobiliser.web.btpn.util.BankPortalConfiguration;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnMobiliserWebSession;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.btpn.util.ConsumerPortalConfiguration;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.ConsumerPortalConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;
//import com.btpnwow.core.registration.services.api.IRegistrationEndpoint;
//import com.btpnwow.core.registration.services.contract.v1_0.registration.GetCustomerNumberRequest;
//import com.btpnwow.core.registration.services.contract.v1_0.registration.GetCustomerNumberResponse;
//import com.btpnwow.core.registration.services.api.IRegistrationEndpoint;
//import com.btpnwow.core.registration.services.contract.v1_0.registration.GetCustomerNumberRequest;
//import com.btpnwow.core.registration.services.contract.v1_0.registration.GetCustomerNumberResponse;

/**
 * This class is the page which need to have all the common methods that the BTPN applications need. Here we can inject
 * the end points that we need to invoke for a particular application.
 * 
 * @author CUSTOM
 */
public abstract class BtpnMobiliserBasePage extends BtpnBasePage {
	
	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BtpnMobiliserBasePage.class);
	
	/**
	 * End Point for Btpn Registration.
	 */
	@SpringBean(name = "registrationClient")
	private IRegistrationEndpoint registrationClient;

	/**
	 * End Point for Btpn Support.
	 */
	@SpringBean(name = "supportClient")
	private ISupportEndpoint supportClient;

	/**
	 * End Point for Btpn Login.
	 */
	@SpringBean(name = "loginClient")
	private ILoginEndpoint loginClient;

	/**
	 * End Point for Btpn fundTransfer.
	 */
	@SpringBean(name = "fundtransferClient")
	private IFundTransferEndpoint fundTransferClient;

	/**
	 * End Point for Btpn deposit.
	 */
	@SpringBean(name = "depositClient")
	private IDepositEndpoint depositClient;

	/**
	 * End Point for Btpn widthdraw.
	 */
	@SpringBean(name = "withdrawClient")
	private IWithDrawEndpoint withdrawClient;

	/**
	 * End Point for Btpn favorites.
	 */
	@SpringBean(name = "favoriteClient")
	private IManageFavoriteEndpoint favoriteClient;

	/**
	 * Configuration for prefs for Bank Portal.
	 */
	@SpringBean(name = "bankPortalConfiguration")
	private BankPortalConfiguration bankPortalPrefsConfig;

	/**
	 * Configuration for prefs for Agent Portal.
	 */
	@SpringBean(name = "agentPortalConfiguration")
	private AgentPortalConfiguration agentPortalPrefsConfig;

	/**
	 * Configuration for prefs for Customer Portal.
	 */
	@SpringBean(name = "customerPortalConfiguration")
	private ConsumerPortalConfiguration customerPortalPrefsConfig;

	/**
	 * Configuration for prefs for Customer Portal.
	 */
	@SpringBean(name = "btpnLookupMapUtilitiesImpl")
	public ILookupMapUtility lookupMapUtility;

	/**
	 * Configuration for prefs for Customer Portal.
	 */
	@SpringBean(name = "smartAuthOtpClient")
	public IOtpEndpoint wsOtpClient;

	/**
	 * End Point for Btpn ChangeMsisdn.
	 */
	@SpringBean(name = "changeMsisdnClient")
	private IChangeMsisdnEndpoint changeMsisdnClient;

	/**
	 * Configuration for prefs for Customer Portal.
	 */
	@SpringBean(name = "systemAuthCustomerContextClient")
	private ISecurityEndpoint wsSystemAuthSecurityClient;

	// private access to restrict access to only getXYZ() methods in this class
	@SpringBean(name = "customComponentConfiguration")
	private java.util.Map<String, String> customComponentConfiguration;

	@SpringBean(name = "smartAuthCustomerClient")
	public ICustomerEndpoint customerClient;

	@SpringBean(name = "smartAuthSecurityClient")
	public ISecurityEndpoint securityClient;

	@SpringBean(name = "productClient")
	public IProductEndpoint productClient;

	@SpringBean(name = "systemAuthAttributeClient")
	public IAttributeEndpoint attributeClient;

	@SpringBean(name = "generalLedgerClient")
	public IGeneralLedgerEndpoint generalLedgerClient;

	@SpringBean(name = "holidayCalendarClient")
	public IHolidayCalendarEndpoint holidayCalendarClient;

	@SpringBean(name = "limitClient")
	public ILimitEndpoint limitClient;

	@SpringBean(name = "airTimeClient")
	public IAirtimeTopupEndpoint airTimeClient;

	@SpringBean(name = "billPaymentClient")
	public IBillPaymentEndpoint billPaymentClient;

	@SpringBean(name = "transactionReversalClient")
	public ITransactionReversalEndpoint transactionReversalClient;

	@SpringBean(name = "transactionGLEndPoint")
	public ITransactionGLEndpoint transactionGLEndPoint;

	@SpringBean(name = "smartAuthIdentificationClient")
	public IIdentificationEndpoint wsIdentClient;

	@SpringBean(name = "feeClient")
	public IFeeEndpoint feeClient;

	@SpringBean(name = "systemAuthRolesPrivilegesClient")
	private IUmgrRolesPrivilegesEndpoint umgrRolesPrivsEndPoint;

	@SpringBean(name = "editProfileClient")
	public IEditProfileEndpoint editProfileClient;

	@SpringBean(name = "bulkFileProcesssingClient")
	private IBulkFileProcessingEndpoint bulkFileProcesssingClient;
	
	@SpringBean(name = "bulkFileClient")
	private BulkFileFacade bulkFileClient;

	@SpringBean(name = "bulkFileWrkClient")
	private BulkFileWrkFacade bulkFileWrkClient;

	@SpringBean(name = "debitClient")
	private DebitFacade debitClient; 

	@SpringBean(name = "limitExClient")
	public ILimitExEndpoint limitExClient;
	
	@SpringBean(name="glClient")
	private GLFacade glClient;
	
	@SpringBean(name="glWrkClient")
	private GLWrkFacade glWrkClient;
	
	@SpringBean(name="ucFeeClient")
	private UseCaseFeeFacade ucFeeClient;
	
	@SpringBean(name="ucFeeWrkClient")
	private UseCaseFeeWrkFacade ucFeeWrkClient;

	@SpringBean(name="billerFeeClient")
	private BillerFeeFacade billerFeeClient;  
	
	@SpringBean(name="billerFeeWrkClient")
	private BillerFeeWrkFacade billerFeeWrkClient;
	
	@SpringBean(name="interestClient")
	private InterestFacade interestClient;
	
	@SpringBean(name="interestWrkClient")
	private InterestWrkFacade interestWrkClient;

	@SpringBean(name="interestTaxClient")
	private InterestTaxFacade interestTaxClient;
	
	@SpringBean(name="interestTaxWrkClient")
	private InterestTaxWrkFacade interestTaxWrkClient;
	
	@SpringBean(name="accountClient")
	private AccountFacade accountClient;
	
	@SpringBean(name="favClient")
	private PreRegisteredFacade favClient;
	
	@SpringBean(name="securitysClient")
	private SecurityFacade securitysClient;
	
	@SpringBean(name="walletClient")
	private IWalletEndpoint walletClient;

	
	/**
	 * SVA Balance
	 */
	private Long svaBalance;

	/**
	 * Constructor that is invoked.
	 */
	public BtpnMobiliserBasePage() {
		super();
	}

	/**
	 * Constructor that is invoked.
	 * 
	 * @param parameters Page Paramteres that we send from one page to another.
	 */
	public BtpnMobiliserBasePage(final PageParameters parameters) {
		super(parameters);
	}

	/**
	 * Returns the Mobiliser Web Session.
	 * 
	 * @return MobiliserWebSession Customized mobiliser web session
	 */
	public BtpnMobiliserWebSession getMobiliserWebSession() {
		return (BtpnMobiliserWebSession) super.getSession();
	}

	/**
	 * Invalidates the session and sets the locale for next session.
	 */
	public void cleanupSession() {
		LOG.debug("### BtpnMobiliserBasePage::cleanupSession() ###");
		final Locale locale = (Locale) getWebSession().getLocale();
		getWebSession().invalidate();
		getWebSession().setLocale(locale);
	}

	/**
	 * Gets the build version to be displayed in the application footer.
	 */
	protected String getBuildVersion() {
		
		final StringBuilder builder = new StringBuilder();
		
		builder.append(MobiliserApplication.VERSION.DATE);
		builder.append(BtpnConstants.FOOTER_VERSION_SEPERATOR);
		builder.append(MobiliserApplication.VERSION.REVISION);
		builder.append(BtpnConstants.FOOTER_VERSION_SEPERATOR);
		builder.append(MobiliserApplication.VERSION.TAG);
		
		return builder.toString();
	}

	/**
	 * Sets the left menu to null and redirects to home page or page intended.
	 * 
	 * @param pageClass class to redirect
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void handleCancelButtonRedirectToHomePage(Class pageClass) {
		getWebSession().setLeftMenu(null);
		((BtpnMobiliserWebSession) getMobiliserWebSession()).setTopMenu(null);
		setResponsePage(pageClass);

	}

	/**
	 * Method which sets the parameters in request i.e. in MobiliserRequestType.
	 * 
	 * @param requestClass Page Mobiliser Request Class
	 * @param <Req> Page Mobiliser Request Class Type
	 * @throws Exception Exception occured
	 * @return <Req extends MobiliserRequestType> request Object Sent
	 */
	public <Req extends MobiliserRequestType> Req getNewMobiliserRequest(Class<Req> requestClass) throws Exception {

		final Req req = requestClass.newInstance();
		req.setCallback(null);
		req.setConversationId(UUID.randomUUID().toString());
		req.setOrigin("mobiliser-web");
		req.setRepeat(Boolean.FALSE);
		req.setTraceNo(UUID.randomUUID().toString());

		final ClientInfo clientInfo = getMobiliserWebSession().getClientInfo();

		if (clientInfo instanceof WebClientInfo) {
			final WebClientInfo webClientInfo = (WebClientInfo) clientInfo;

			req.setAuditData(new AuditDataType());
			req.getAuditData().setDevice(StringUtils.substring(webClientInfo.getUserAgent(), 0, 80));
			req.getAuditData().setOtherDeviceId(webClientInfo.getProperties().getRemoteAddress());
		}

		final Customer cust = getMobiliserWebSession().getBtpnLoggedInCustomer();

		if (cust != null) {
			req.setSessionId(cust.getSessionId());
		}

		prepareMobiliserRequest(req);

		return req;
	}

	/**
	 * checks whether the msisdn exists by making a call to the support service.
	 * 
	 * @param msisdn Mobile Number to be checked
	 * @return MsisdnExistsResponse
	 * @throws Exception exception occured
	 */
	public MsisdnExistsResponse checkMsisdnExists(final String msisdn, final boolean isPayer) throws Exception {
		final MsisdnExistsRequest request = getNewMobiliserRequest(MsisdnExistsRequest.class);
		request.setMsisdn(msisdn);
		request.setIsPayer(isPayer);
		final MsisdnExistsResponse msisdnResponse = supportClient.msisdnExists(request);
		return msisdnResponse;
	}

	/**
	 * Gets the customer number for registration
	 * 
	 * @param msisdn Mobile Number to be checked
	 * @return MsisdnExistsResponse
	 * @throws Exception exception occured
	 */
	public GetCustomerNumberResponse getCustomerNumber() throws Exception {
		final GetCustomerNumberRequest request = getNewMobiliserRequest(GetCustomerNumberRequest.class);
		final GetCustomerNumberResponse response = registrationClient.getCustomerNumber(request);
		return response;
	}

	/**
	 * Returns the Reg Endpoint.
	 * 
	 * @return IRegistrationEndpoint
	 */
	public IRegistrationEndpoint getRegistrationClient() {
		return registrationClient;
	}

	/**
	 * Sets the Reg Endpoint.
	 * 
	 * @param registrationClient client Object for Registration End Point
	 */
	public void setRegistrationClient(final IRegistrationEndpoint registrationClient) {
		this.registrationClient = registrationClient;
	}

	/**
	 * Returns the Support Endpoint.
	 * 
	 * @return ISupportEndpoint
	 */
	public ISupportEndpoint getSupportClient() {
		return supportClient;
	}

	/**
	 * Sets the Support Endpoint.
	 * 
	 * @param supportClient client Object for Support End Point
	 */
	public void setSupportClient(final ISupportEndpoint supportClient) {
		this.supportClient = supportClient;
	}

	/**
	 * Returns the Login Endpoint.
	 * 
	 * @return ILoginEndpoint
	 */
	public ILoginEndpoint getLoginClient() {
		return loginClient;
	}

	/**
	 * sets the Login Endpoint.
	 * 
	 * @param loginClient
	 */
	public void setLoginClient(ILoginEndpoint loginClient) {
		this.loginClient = loginClient;
	}

	public IFundTransferEndpoint getFundTransferClient() {
		return fundTransferClient;
	}

	public void setFundTransferClient(IFundTransferEndpoint fundTransferClient) {
		this.fundTransferClient = fundTransferClient;
	}

	public IDepositEndpoint getDepositClient() {
		return depositClient;
	}

	public void setDepositClient(IDepositEndpoint depositClient) {
		this.depositClient = depositClient;
	}

	public IWithDrawEndpoint getWithdrawClient() {
		return withdrawClient;
	}

	public void setWithdrawClient(IWithDrawEndpoint withdrawClient) {
		this.withdrawClient = withdrawClient;
	}

	public IManageFavoriteEndpoint getFavoriteClient() {
		return favoriteClient;
	}

	public void setFavoriteClient(IManageFavoriteEndpoint favoriteClient) {
		this.favoriteClient = favoriteClient;
	}

	public BankPortalConfiguration getBankPortalPrefsConfig() {
		return bankPortalPrefsConfig;
	}

	public void setBankPortalPrefsConfig(BankPortalConfiguration bankPortalPrefsConfig) {
		this.bankPortalPrefsConfig = bankPortalPrefsConfig;
	}

	public AgentPortalConfiguration getAgentPortalPrefsConfig() {
		return agentPortalPrefsConfig;
	}

	public void setAgentPortalPrefsConfig(AgentPortalConfiguration agentPortalPrefsConfig) {
		this.agentPortalPrefsConfig = agentPortalPrefsConfig;
	}

	public ConsumerPortalConfiguration getCustomerPortalPrefsConfig() {
		return customerPortalPrefsConfig;
	}

	public void setCustomerPortalPrefsConfig(ConsumerPortalConfiguration customerPortalPrefsConfig) {
		this.customerPortalPrefsConfig = customerPortalPrefsConfig;
	}

	public IChangeMsisdnEndpoint getChangeMsisdnClient() {
		return changeMsisdnClient;
	}

	public void setChangeMsisdnClient(IChangeMsisdnEndpoint changeMsisdnClient) {
		this.changeMsisdnClient = changeMsisdnClient;
	}

	public ILookupMapUtility getLookupMapUtility() {
		return lookupMapUtility;
	}

	public void setLookupMapUtility(ILookupMapUtility lookupMapUtility) {
		this.lookupMapUtility = lookupMapUtility;
	}

	public IOtpEndpoint getWsOtpClient() {
		return wsOtpClient;
	}

	public void setWsOtpClient(IOtpEndpoint wsOtpClient) {
		this.wsOtpClient = wsOtpClient;
	}

	public ICustomerEndpoint getCustomerClient() {
		return customerClient;
	}

	public void setCustomerClient(ICustomerEndpoint customerClient) {
		this.customerClient = customerClient;
	}

	public ISecurityEndpoint getSecurityClient() {
		return securityClient;
	}

	public void setSecurityClient(ISecurityEndpoint securityClient) {
		this.securityClient = securityClient;
	}

	public IProductEndpoint getProductClient() {
		return productClient;
	}

	public void setProductClient(IProductEndpoint productClient) {
		this.productClient = productClient;
	}

	public IAttributeEndpoint getAttributeClient() {
		return attributeClient;
	}

	public void setAttributeClient(IAttributeEndpoint attributeClient) {
		this.attributeClient = attributeClient;
	}

	public ITransactionReversalEndpoint getTransactionReversalClient() {
		return transactionReversalClient;
	}

	public void setTransactionReversalClient(ITransactionReversalEndpoint transactionReversalClient) {
		this.transactionReversalClient = transactionReversalClient;
	}

	public ITransactionGLEndpoint getTransactionGLEndPoint() {
		return transactionGLEndPoint;
	}

	public void setTransactionGLEndPoint(ITransactionGLEndpoint transactionGLEndPoint) {
		this.transactionGLEndPoint = transactionGLEndPoint;
	}

	public IBillPaymentEndpoint getBillPaymentClient() {
		return billPaymentClient;
	}

	public void setBillPaymentClient(IBillPaymentEndpoint billPaymentClient) {
		this.billPaymentClient = billPaymentClient;
	}

	public IAirtimeTopupEndpoint getAirTimeClient() {
		return airTimeClient;
	}

	public void setAirTimeClient(IAirtimeTopupEndpoint airTimeClient) {
		this.airTimeClient = airTimeClient;
	}

	public IHolidayCalendarEndpoint getHolidayCalendarClient() {
		return holidayCalendarClient;
	}

	public void setHolidayCalendarClient(IHolidayCalendarEndpoint holidayCalendarClient) {
		this.holidayCalendarClient = holidayCalendarClient;
	}

	public IEditProfileEndpoint getEditProfileClient() {
		return editProfileClient;
	}

	public void setEditProfileClient(IEditProfileEndpoint editProfileClient) {
		this.editProfileClient = editProfileClient;
	}

	public IBulkFileProcessingEndpoint getBulkFileProcesssingClient() {
		return bulkFileProcesssingClient;
	}

	public void setBulkFileProcesssingClient(IBulkFileProcessingEndpoint bulkFileProcesssingClient) {
		this.bulkFileProcesssingClient = bulkFileProcesssingClient;
	}
	
	public BulkFileFacade getBulkFileClient() {
		return bulkFileClient;
	}

	public void setBulkFileClient(BulkFileFacade bulkFileClient) {
		this.bulkFileClient = bulkFileClient;
	}
	
	public BulkFileWrkFacade getBulkFileWrkClient() {
		return bulkFileWrkClient;
	}

	public void setBulkFileWrkClient(BulkFileWrkFacade bulkFileWrkClient) {
		this.bulkFileWrkClient = bulkFileWrkClient;
	}

	public IUmgrRolesPrivilegesEndpoint getUmgrRolesPrivsEndPoint() {
		return umgrRolesPrivsEndPoint;
	}

	public void setUmgrRolesPrivsEndPoint(IUmgrRolesPrivilegesEndpoint umgrRolesPrivsEndPoint) {
		this.umgrRolesPrivsEndPoint = umgrRolesPrivsEndPoint;
	}

	/**
	 * Return a registered custom version of a base Wicket Component class
	 * 
	 * @param c The original Component class
	 * @return If a custom class is registered in the custom components map for the original class then the custom class
	 *         is returned, otherwise the original class is returned.
	 */
	@SuppressWarnings("rawtypes")
	public Class getComponent(Class<? extends Component> c, Object... args) {
		if (customComponentConfiguration.containsKey(c.getCanonicalName())) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Custom component of '{}' configured for '{}'",
					customComponentConfiguration.get(c.getCanonicalName()), c.getCanonicalName());
			}
			try {
				return Class.forName(customComponentConfiguration.get(c.getCanonicalName()));
			} catch (ClassNotFoundException ex) {
				LOG.error(
					"Custom component of '{}' was not found [ClassNotFoundException] using original instead '{}'",
					customComponentConfiguration.get(c.getCanonicalName()), c.getCanonicalName());
			}
		} else {
			LOG.debug("No custom class configured for '{}'", c.getCanonicalName());
		}
		return c;
	}

	/**
	 * Gets the display value
	 * 
	 * @param key Key of the message
	 * @param bundleName Name of the bundle
	 * @return If a custom class is registered in the custom components map for the original class then the custom class
	 *         is returned, otherwise the original class is returned.
	 */
	public String getDisplayValue(String key, String bundleName) {
		if (PortalUtils.exists(key)) {
			try {
				return getLocalizer().getString(LookupResourceLoader.LOOKUP_INDICATOR + bundleName + "." + key, this);
			} catch (MissingResourceException me) {
				return "";
			}
		}
		return "";
	}

	/**
	 * Return a registered custom version of a base Wicket Component class
	 * 
	 * @param sessionId sessionId of the consumer
	 * @return If a custom class is registered in the custom components map for the original class then the custom class
	 *         is returned, otherwise the original class is returned.
	 */
	public boolean logoutCustomer(String sessionId) {

		try {
			final LogoutRequest request = getNewMobiliserRequest(LogoutRequest.class);

			final LogoutResponse response = wsSystemAuthSecurityClient.logout(request);

			if (!evaluateMobiliserResponse(response, null)) {
				return false;
			}
		} catch (Exception e) {
			LOG.error("# Error while logging out customer", e);
		}

		return true;
	}

	/**
	 * Evaluate the mobiliser response for Consumer Portal
	 * 
	 * @param response response of the service
	 * @return boolean true if response is successful.
	 */
	public <Resp extends MobiliserResponseType> boolean evaluateConsumerPortalMobiliserResponse(final Resp response) {
		return evaluateMobiliserResponse(response, ConsumerPortalApplicationLoginPage.class);
	}
	
	/**
	 * Evaluate the mobiliser response for Consumer Portal
	 * 
	 * @param response response of the service
	 * @return boolean true if response is successful.
	 */
	public boolean evaluateConsumerPortalMobResponse(final InquiryDebitFundTransferResponse response) {
		return evaluateMobResponse(response, ConsumerPortalApplicationLoginPage.class);
	}
	
	
	/**
	 * Evaluate the mobiliser response for Consumer Portal
	 * 
	 * @param response response of the service
	 * @return boolean true if response is successful.
	 */
	public boolean evaluateConsumerPortalMobResponse(final FundTransferResponse response) {
		return evaluateMobResponse(response, ConsumerPortalApplicationLoginPage.class);
	}

	/**
	 * Evaluate the mobiliser response for Agent Portal
	 * 
	 * @param response response of the service
	 * @return boolean true if response is successful.
	 */
	public <Resp extends MobiliserResponseType> boolean evaluateAgentPortalMobiliserResponse(Resp response) {
		return evaluateMobiliserResponse(response, AgentPortalApplicationLoginPage.class);
	}

	/**
	 * Evaluate the mobiliser response for Bank Portal
	 * 
	 * @param response response of the service
	 * @return boolean true if response is successful.
	 */
	public <Resp extends MobiliserResponseType> boolean evaluateBankPortalMobiliserResponse(Resp response) {
		return evaluateMobiliserResponse(response, BankPortalApplicationLoginPage.class);
	}

	/**
	 * Evaluate the mobiliser response
	 * 
	 * @param response response of the service.
	 * @param loginClass Login class to be redirected.
	 * @return boolean true if response is successful.
	 */
	@SuppressWarnings("unchecked")
	public <Resp extends MobiliserResponseType> boolean evaluateMobiliserResponse(Resp response,
		Class<? extends Page> loginClass) {

		LOG.debug("# Response returned status: {}-{}", response.getStatus().getCode(), response.getStatus().getValue());

		if (response.getStatus().getCode() == 0) {
			return true;
		}

		// check for mobiliser session closed or expired
		if (response.getStatus().getCode() == 352 || response.getStatus().getCode() == 353) {
			LOG.debug("# Mobiliser session closed/expired, redirect to sign in page");
			// if mobiliser session gone, then can't continue with web session
			// so invalidate session and redirect to home, which will go to
			getMobiliserWebSession().invalidate();
			getRequestCycle().setRedirect(true);
			if (null != loginClass) {

				setResponsePage(getComponent(loginClass));

				// Set the mobiliser session expired and redirect it to login
				// page
				String errorMessage = null;

				errorMessage = getDisplayValue(String.valueOf(response.getStatus().getCode()),
					Constants.RESOURCE_BUNDLE_ERROR_CODES);

				if (PortalUtils.exists(errorMessage)) {
					getMobiliserWebSession().error(errorMessage);
				} else {
					getMobiliserWebSession().error(getLocalizer().getString("portal.genericError", this));
				}
			}
		}

		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean evaluateMobResponse(InquiryDebitFundTransferResponse response,
		Class<? extends Page> loginClass) {

		LOG.debug("# Response returned status: {}-{}", response.getResponseCode());

		if (Integer.parseInt(response.getResponseCode()) != 0) {
			return true;
		}

		// check for mobiliser session closed or expired
		if (Integer.parseInt(response.getResponseCode()) == 352 || Integer.parseInt(response.getResponseCode()) == 353) {
			LOG.debug("# Mobiliser session closed/expired, redirect to sign in page");
			// if mobiliser session gone, then can't continue with web session
			// so invalidate session and redirect to home, which will go to
			getMobiliserWebSession().invalidate();
			getRequestCycle().setRedirect(true);
			if (null != loginClass) {

				setResponsePage(getComponent(loginClass));

				// Set the mobiliser session expired and redirect it to login
				// page
				String errorMessage = null;

				errorMessage = getDisplayValue(String.valueOf(response.getResponseCode()),
					Constants.RESOURCE_BUNDLE_ERROR_CODES);

				if (PortalUtils.exists(errorMessage)) {
					getMobiliserWebSession().error(errorMessage);
				} else {
					getMobiliserWebSession().error(getLocalizer().getString("portal.genericError", this));
				}
			}
		}

		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean evaluateMobResponse(FundTransferResponse response,
		Class<? extends Page> loginClass) {   

		LOG.debug("# Response returned status: {}-{}", response.getResponseCode());

		if (Integer.parseInt(response.getResponseCode()) == 0) {  
			return true;
		}

		// check for mobiliser session closed or expired
		if (Integer.parseInt(response.getResponseCode()) == 352 || Integer.parseInt(response.getResponseCode()) == 353) {
			LOG.debug("# Mobiliser session closed/expired, redirect to sign in page");
			// if mobiliser session gone, then can't continue with web session
			// so invalidate session and redirect to home, which will go to
			getMobiliserWebSession().invalidate();
			getRequestCycle().setRedirect(true);
			if (null != loginClass) {

				setResponsePage(getComponent(loginClass));

				// Set the mobiliser session expired and redirect it to login
				// page
				String errorMessage = null;

				errorMessage = getDisplayValue(String.valueOf(response.getResponseCode()),
					Constants.RESOURCE_BUNDLE_ERROR_CODES);

				if (PortalUtils.exists(errorMessage)) {
					getMobiliserWebSession().error(errorMessage);
				} else {
					getMobiliserWebSession().error(getLocalizer().getString("portal.genericError", this));
				}
			}
		}

		return false;
	}

	/**
	 * This method validates the otp entered by the user.
	 * 
	 * @param otp otp to be validated
	 * @return boolean true if successfully validated, false if not successful.
	 * @throws Exception
	 */
//	public boolean generateOTP(final long customerId) throws Exception {
//		final CreateCustomOtpRequest createOtpRequest = getNewMobiliserRequest(CreateCustomOtpRequest.class);
//		createOtpRequest.setOtpType(BtpnConstants.OTP_TYPE);
//		createOtpRequest.setReferenceId(BtpnConstants.OTP_REFERENCE_ID);
//		createOtpRequest.setCustomerId(customerId);
//		final CreateCustomOtpResponse createOtpResponse = loginClient.createCustomOtp(createOtpRequest);
//		if (createOtpResponse.getStatus().getCode() == 0) {
//			return true;
//		}
//		return false;
//	}
	
	
//	public boolean generateOTP(long customerId) throws Exception {
//		
//		LOG.info(" ### (MobiliserBasePage::generatreOTP) ### ");
//    	
//    	SendOtpRequest req = getNewMobiliserRequest(SendOtpRequest.class);
//    	CustomerIdentificationType obj = new CustomerIdentificationType();
//    	obj.setType(1);
////    	String value = String.valueOf(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
//    	String custId = String.valueOf(customerId);
//    	obj.setValue(custId);
//    	LOG.info(" ### (MobiliserBasePage::generatreOTP) CUST ID ### " +customerId);
//    	req.setType(BtpnConstants.OTP_TYPE);
//    	req.setReference(67825);
//    	req.setFlags(0);
//    	
//    	SendOtpResponse response = securitysClient.sendOtp(req);
//    	if (response.getStatus().getCode() == 0) {
//    	    return true;
//    	}
//    	
//    	return false;
//    }



	/**
	 * This method validates the otp entered by the user.
	 * 
	 * @param otp otp to be validated
	 * @return boolean true if successfully validated, false if not successful.
	 * @throws Exception
	 */
//	public boolean validateOTP(String otp) throws Exception {
//		final CustomOtpRequest verifyRequest = getNewMobiliserRequest(CustomOtpRequest.class);
//		verifyRequest.setOtp(otp);
//		verifyRequest.setReferenceId(BtpnConstants.OTP_REFERENCE_ID);
//		verifyRequest.setOtpType(BtpnConstants.OTP_TYPE);
//		verifyRequest.setCustomerId(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
//		final CustomOtpResponse verifyResponse = loginClient.customOtp(verifyRequest);
//		if (verifyResponse.getStatus().getCode() == 0 && verifyResponse.isValid()) {
//			return true;
//		}
//		return false;
//	}
	
	
	public boolean validateOTP(String otp) throws Exception {
		
		LOG.info(" ### (BtpnMobiliserBasePage::validateOTP) ### ");
		LOG.info(" ### (BtpnMobiliserBasePage::validateOTP) OTP ### "+otp);
		
		final VerifyOtpRequest request = getNewMobiliserRequest(VerifyOtpRequest.class);
		CustomerIdentificationType obj = new CustomerIdentificationType();
		obj.setType(1);
		String value = String.valueOf(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
		obj.setValue(value);
		
		CustomerOtpType obj2 = new CustomerOtpType();
		obj2.setType(BtpnConstants.OTP_TYPE);
		obj2.setReference(67825);
		obj2.setValue(otp);
		
		request.setIdentification(obj);
		request.setOtp(obj2);
		request.setFlags(0);
		
		final VerifyOtpResponse response = securitysClient.verifyOtp(request);
		if (response.getStatus().getCode() == 0) {
			return true;
		}
		
		return false;
	}
	

	/**
	 * @return the debitClient
	 */
	public DebitFacade getDebitClient() {
		return debitClient;
	}

	/**
	 * @param debitClient the debitClient to set
	 */
	public void setDebitClient(DebitFacade debitClient) {
		this.debitClient = debitClient;
	}

	/**
	 * This method validates the otp entered by the user.
	 * 
	 * @param otp otp to be validated
	 * @return boolean true if successfully validated, false if not successful.
	 * @throws Exception
	 */
	public SetCredentialResponse performChangeTemporaryPin(String oldPin, String newPin, String confirmNewPin)
		throws Exception {
		final SetCredentialRequest setPinRequest = getNewMobiliserRequest(SetCredentialRequest.class);
		setPinRequest.setCustomerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
		setPinRequest.setCredentialStatus(0);
		setPinRequest.setCredential(newPin);
		setPinRequest.setCredentialType(BtpnConstants.CREDENTIAL_TYPE);
		final SetCredentialResponse setPinResponse = getSecurityClient().setCredential(setPinRequest);
		return setPinResponse;
	}

	/**
	 * This method validates the otp entered by the user.
	 * 
	 * @param pin pin is required field
	 * @return boolean true if successfully validated, false if not successful.
	 * @throws Exception
	 */
	public boolean checkCredential(String pin) throws Exception {
		final CheckCredentialRequest checkCredentialRequest = getNewMobiliserRequest(CheckCredentialRequest.class);
		checkCredentialRequest.setCredential(pin);
		checkCredentialRequest.setCredentialType(BtpnConstants.CREDENTIAL_TYPE_PIN);
		checkCredentialRequest.setCustomerId(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
		final CheckCredentialResponse checkCredentialResponse = getSecurityClient().checkCredential(
			checkCredentialRequest);
		if (checkCredentialResponse.getStatus().getCode() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * calling getAllSubAccounts service from fund transfer end point
	 */
	public List<SubAccountsBean> getSubAccountsList() {
		final List<SubAccountsBean> beanList = new ArrayList<SubAccountsBean>();
		try {
			final GetAllSubAccountsRequest request = getNewMobiliserRequest(GetAllSubAccountsRequest.class);
			final long customerId = getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			request.setCustomerId(customerId);
			final GetAllSubAccountsResponse response = getSupportClient().getAllSubAccounts(request);
			if (evaluateConsumerPortalMobiliserResponse(response)
					&& response.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				return ConverterUtils.convertToSubAccountsBean(response.getResponseBean());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling getAllSubAccounts service.", ex);
		}
		return beanList;
	}


	
	public Long getSvaBalance(String msisdn) {
		
		Long balance = null;
		
		try {
			
			final GetAccountRequest request = getNewMobiliserRequest(GetAccountRequest.class);
			
			AccountIdentificationType obj = new AccountIdentificationType();
			obj.setType("MSISDN");
			obj.setFlags(1);
			obj.setValue(msisdn);
			
			request.getIdentification().add(obj);
			
			final GetAccountResponse balanceResponse = getAccountClient().get(request);
			Status status = balanceResponse.getStatus();
			LOG.info(" ### (BtpnMobiliserBasePage::getSvaBalance) RESPONSE CODE ### " +status.getCode());
			if (evaluateConsumerPortalMobiliserResponse(balanceResponse)) {
				balance = balanceResponse.getAccount().get(0).getBalance();;
				LOG.debug(" ### (BtpnMobiliserBasePage::getSvaBalance) Total Available SVA Balance ### " + balance);
			}
			
		} catch (Exception ex) {
			LOG.error(" ### (BtpnMobiliserBasePage::getSvaBalance) Error occured while calling get balance service ### ", ex);
		}

		return balance;
	}

	/**
	 * This is used to fetch the customer details based on Customer id.
	 * 
	 * @return CustomerRegistrationBean bean used for storing customer code.
	 */
	public CustomerRegistrationBean getCustomerDetailsByCustomerId() {
		return getCustomerDetailsByCustomerId(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
	}

	/**
	 * This is used to fetch the customer details based on Customer id.
	 * 
	 * @return CustomerRegistrationBean bean used for storing customer code.
	 */
	public CustomerRegistrationBean getCustomerDetailsByCustomerId(long customerID) {
		CustomerRegistrationBean custRegBean = new CustomerRegistrationBean();
		try {
			final ViewProfileRequest request = getNewMobiliserRequest(ViewProfileRequest.class);
			request.setCustomerId(customerID);
			final ViewProfileResponse response = supportClient.viewProfile(request);
			if (evaluateConsumerPortalMobiliserResponse(response)) {
				custRegBean = ConsumerPortalConverterUtils.convertViewCustomertoCustomer(response,
					this.getLookupMapUtility(), this);
				custRegBean.setCustomerId(String.valueOf(customerID));
				List<CustomerIdentification> identificationList = response.getIdentification();
				for (CustomerIdentification identification : identificationList) {
					if (identification.getType() == BtpnConstants.IDENTIFICATION_TYPE_SVA_NO)
						custRegBean.setSvaNumber(identification.getIdentification());
				}
				custRegBean.setCustomerType(response.getCoustomer().getCustomerType().getName());
				custRegBean.setCustomerTypeId(response.getCoustomer().getCustomerType().getId());
				custRegBean.setProduct(response.getCoustomer().getCustomerType().getName());
				custRegBean.setTerritoryCode(response.getAddress().getTerritoryCode());
				custRegBean.setAddressId(response.getAddress().getId());
				custRegBean.setBlackListReson(response.getCoustomer().getBlackListReason());
				custRegBean.setStatus(response.getCoustomer().getCustomerStatus());
				custRegBean.setProductCategory(response.getCoustomer().getProductCategory());

				int blackListReason = response.getCoustomer().getBlackListReason();
				// set black list reason
				String balkListValue = String.valueOf(blackListReason);
				custRegBean.setBlackListReason(new CodeValue(balkListValue, getDisplayValue(balkListValue,
					BtpnConstants.RESOURCE_BUNDLE_ID_BALCK_LIST)));
				// BOL_IS_ACTIVE Flag from DB
				boolean bolIsActive = response.getCoustomer().isActive();
				// Customer status attribute 19
				String customerStatusAttribute = response.getCoustomer().getCustomerStatus();
				if (bolIsActive) {// Check for BOL_IS_ACTIVE if its Y then					
					if (blackListReason == BtpnConstants.BLACKLISTREASON_ZERO
							&& customerStatusAttribute.equalsIgnoreCase(BtpnConstants.CUSTOMER_STATUS_APPROVED)) {
						// If blacklist reason is zero, customer status attribute is approved set customer status to active.
						custRegBean.setCustomerStatus(new CodeValue(BtpnConstants.CUSTOMER_STATUS_ACTIVE,
							getDisplayValue(BtpnConstants.CUSTOMER_STATUS_ACTIVE,
								BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS)));
					} else if (customerStatusAttribute.equalsIgnoreCase(BtpnConstants.CUSTOMER_STATUS_PENDING)) {
						//If customer status attribute is Pending Approval
						custRegBean.setCustomerStatus(new CodeValue(BtpnConstants.CUSTOMER_STATUS_PENDING_APPROVAL,
							getDisplayValue(BtpnConstants.CUSTOMER_STATUS_PENDING_APPROVAL,
								BtpnConstants.RESOURCE_BUNDLE_PENDING_CUST_STATUS)));
					} else if (blackListReason == BtpnConstants.BLACKLISTREASON_INACTIVE) {
						// If blacklist reason is 9, set customer status to InActive.
						custRegBean.setCustomerStatus(new CodeValue(BtpnConstants.CUSTOMER_STATUS_INACTIVE,
							getDisplayValue(BtpnConstants.CUSTOMER_STATUS_INACTIVE,
								BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS)));
					}  else {
						// If blacklist reason is other than 0 or 9, set customer status to Suspend.
						custRegBean.setCustomerStatus(new CodeValue(BtpnConstants.CUSTOMER_STATUS_SUSPEND,
							getDisplayValue(BtpnConstants.CUSTOMER_STATUS_SUSPEND,
								BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS)));
					}

				} else {// Check for BOL_IS_ACTIVE if its N then Customer status is closed.
					custRegBean.setCustomerStatus(new CodeValue(BtpnConstants.CUSTOMER_STATUS_CLOSED, getDisplayValue(
						BtpnConstants.CUSTOMER_STATUS_CLOSED, BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS)));
				}
			} else {
				error(getLocalizer().getString("error.customer.details", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Error occured while calling viewCustomerProfile Service", e);
		}
		return custRegBean;
	}

	public String displayAmount(Long amount) {
		String formatedAmount = null;
		formatedAmount = BtpnUtils.formatAmount(amount, getMobiliserWebSession().getLocale());
		LOG.debug("Total Available SVA Balance : " + formatedAmount);
		return formatedAmount;
	}

	public Long getSvaBalance() {
		return svaBalance;
	}

	public void setSvaBalance(Long svaBalance) {
		this.svaBalance = svaBalance;
	}

	public CustomerEditProfileRequest populateCustomerEditProfileRequest(CustomerRegistrationBean bean) {
		CustomerEditProfileRequest request = null;
		com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Customer customer = null;
		com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Address address = null;
		try {
			request = getNewMobiliserRequest(CustomerEditProfileRequest.class);
			address = pupulateAddressRequest(bean);
			customer = populateCustomerRequest(bean);
			customer.setAddress(address);
			// setting customer attributes
			final CustomerAttributes custAttributes = new CustomerAttributes();
			custAttributes.setAgentId(bean.getAgentCode());
			custAttributes.setCity(bean.getCity() != null ? bean.getCity().getId() : null);
			custAttributes.setForecastTransaction(bean.getForeCastTransaction() != null ? bean
				.getForeCastTransaction().getId() : null);
			custAttributes
				.setLastEducation(bean.getLastEducation() != null ? bean.getLastEducation().getId() : null);
			custAttributes.setMarketingsourceCode(bean.getMarketingSourceCode());
			custAttributes.setProvince(bean.getProvince() != null ? bean.getProvince().getId() : null);
			custAttributes.setReligion(bean.getReligion() != null ? bean.getReligion().getId() : null);
			custAttributes.setReferralNumber(bean.getReferralNumber());
			custAttributes.setTaxCardNumber(bean.getTaxCardNumber());
			custAttributes.setZipCode(bean.getZipCode());
			customer.setAttributes(custAttributes);
			request.setMakerId(Long.valueOf(bean.getCustomerId()));			
			request.setCustomer(customer);
			request.setMakerId(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
		} catch (Exception ex) {
			LOG.error("Error occured while calling customerEditProfile service from Edit Profile Endpoint");
			error(getLocalizer().getString("error.exception", this));
		}
		return request;
	}

	public com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Address pupulateAddressRequest(
		CustomerRegistrationBean bean) {
		com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Address address = null;
		address = new com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Address();
		address.setId(bean.getAddressId());
		address.setCustomerId(Long.valueOf(bean.getCustomerId()));
		address.setEmail(bean.getEmailId());
		address.setEmployeeID(bean.getEmployeeId());
		address.setGender(Integer.valueOf(bean.getGender().getId()));
		address.setIncome(bean.getIncome().getId());
		address.setIndustrySectorOfEmployer(bean.getIndustryOfEmployee().getId());
		address.setJob(bean.getJob().getId());
		address.setMaidenName(bean.getMothersMaidenName());
		address.setOccupation(bean.getOccupation().getId());
		address.setPurposeOfAccount(bean.getPurposeOfAccount().getId());
		address.setShortName(bean.getShortName());
		address.setSourceOfFund(bean.getSourceofFound().getId());
		address.setStreet1(bean.getStreet1());
		address.setStreet2(bean.getStreet2());
		address.setTerritoryCode(bean.getTerritoryCode());
		return address;
	}

	public com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Customer populateCustomerRequest(
		CustomerRegistrationBean bean) {
		com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Customer customer = null;
		customer = new com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Customer();
		CustomerType customerType = new CustomerType();
		customerType.setCustomerId(Long.valueOf(bean.getCustomerId()));
		customerType.setId(bean.getCustomerTypeId());
		customerType.setName(bean.getCustomerType());
		customer.setId(Long.valueOf(bean.getCustomerId()));
		customer.setCustomerType(customerType);
		customer.setBlackListReason(Integer.valueOf(bean.getBlackListReason().getId()));
		//Set is bol active.
		if (bean.getCustomerStatus().getId().equals(BtpnConstants.CUSTOMER_STATUS_CLOSED)){
			customer.setActive(false);
		}else{
			customer.setActive(true);
		}
		//Override the customer selected blacklist reason if Customer status selected is inactive or suspend
		if (bean.getCustomerStatus().getId().equals(BtpnConstants.CUSTOMER_STATUS_INACTIVE)) {
			customer.setBlackListReason(BtpnConstants.BLACKLISTREASON_INACTIVE);
		} else if (bean.getCustomerStatus().getId().equals(BtpnConstants.CUSTOMER_STATUS_SUSPEND)) {
			//If customer status is SUSPEND set always BLACKLISTREASON_FRAUD_SUSPICION
			customer.setBlackListReason(BtpnConstants.BLACKLISTREASON_FRAUD_SUSPICION);
		} 
		customer.setCustomerNumber(bean.getCustomerNumber());
		XMLGregorianCalendar dob = PortalUtils.getSaveXMLGregorianCalendarFromDate(bean.getBirthDateString(), null);
		customer.setDateOfBirth(dob);
		customer.setHighRiskCustomer(bean.getHighRiskCustomer().getId().equalsIgnoreCase(BtpnConstants.YES_ID));
		customer.setHighRiskBusiness(bean.getHighRiskBusiness().getId().equalsIgnoreCase(BtpnConstants.YES_ID));
		customer.setIsOptimaActivated(bean.getOptimaActivated().getId().equalsIgnoreCase(BtpnConstants.YES_ID));
		customer.setIsTaxExempted(bean.getTaxExempted().getId().equalsIgnoreCase(BtpnConstants.YES_ID));
		customer.setLanguage(bean.getLanguage().getId());
		customer.setMaritalStatus(bean.getMaritalStatus().getId());
		customer.setName(bean.getName());
		customer.setNationality(bean.getNationality().getId());
		customer.setNotificationMode(1);
		customer.setParentId(bean.getParentId());
		customer.setPlaceOfBirth(bean.getPlaceOfBirth());		
		// set the Identity
		Identity identity = new Identity();
		identity.setActive(true);
		identity.setId(bean.getIdentityId());
		identity.setCustomerId(Long.valueOf(bean.getCustomerId()));
		// Set Expire Date
		XMLGregorianCalendar exDate = PortalUtils.getSaveXMLGregorianCalendarFromDate(bean.getExpireDateString(), null);
		identity.setDateExpires(exDate);
		identity.setIdentity(bean.getIdCardNo());
		identity.setIdentityType(Integer.valueOf(bean.getIdType().getId()));
		identity.setStatus(bean.getIdentitiStatus());
		customer.getIdentities().add(identity);

		// set the Identification ATM CARD
		CustomerIdentification identAtmCardNo = new CustomerIdentification();
		identAtmCardNo.setType(BtpnConstants.IDENTIFICATION_TYPE_ATM_CARD_NO);
		identAtmCardNo.setIdentification(bean.getAtmCardNo());
		identAtmCardNo.setStatus(BtpnConstants.STATUS_CODE);
		identAtmCardNo.setCustomerId(Long.valueOf(bean.getCustomerId()));
		identAtmCardNo.setId(bean.getAtmCardId());
		customer.getIdentifications().add(identAtmCardNo);
		customer.setNotificationMode(Integer.valueOf(bean.getReceiptMode().getId()));
		if (PortalUtils.exists(bean.getGlCodeId())) {
			customer.setGlCode(Long.valueOf(bean.getGlCodeId().getId()));
		}

		customer.setCustomerNumber(bean.getCustomerNumber());
		if (PortalUtils.exists(bean.getPurposeOfTransaction())) {
			customer.setPurposeOfTransaction(bean.getPurposeOfTransaction());
		}
		return customer;
	}

	public ILimitExEndpoint getLimitExClient() {
		return limitExClient;
	}

	public void setLimitExClient(ILimitExEndpoint limitExClient) {
		this.limitExClient = limitExClient;
	}

	/**
	 * @return the glClient
	 */
	public GLFacade getGlClient() {
		return glClient;
	}

	/**
	 * @param glClient the glClient to set
	 */
	public void setGlClient(GLFacade glClient) {
		this.glClient = glClient;
	}

	/**
	 * @return the glWrkClient
	 */
	public GLWrkFacade getGlWrkClient() {
		return glWrkClient;
	}

	/**
	 * @param glWrkClient the glWrkClient to set
	 */
	public void setGlWrkClient(GLWrkFacade glWrkClient) {
		this.glWrkClient = glWrkClient;
	}

	/**
	 * @return the ucFeeClient
	 */
	public UseCaseFeeFacade getUcFeeClient() {
		return ucFeeClient;
	}

	/**
	 * @param ucFeeClient the ucFeeClient to set
	 */
	public void setUcFeeClient(UseCaseFeeFacade ucFeeClient) {
		this.ucFeeClient = ucFeeClient;
	}

	/**
	 * @return the ucFeeWrkClient
	 */
	public UseCaseFeeWrkFacade getUcFeeWrkClient() {
		return ucFeeWrkClient;
	}

	/**
	 * @param ucFeeWrkClient the ucFeeWrkClient to set
	 */
	public void setUcFeeWrkClient(UseCaseFeeWrkFacade ucFeeWrkClient) {
		this.ucFeeWrkClient = ucFeeWrkClient;
	}

	/**
	 * @return the billerFeeClient
	 */
	public BillerFeeFacade getBillerFeeClient() {
		return billerFeeClient;
	}

	/**
	 * @param billerFeeClient the billerFeeClient to set
	 */
	public void setBillerFeeClient(BillerFeeFacade billerFeeClient) {
		this.billerFeeClient = billerFeeClient;
	}

	/**
	 * @return the billerFeeWrkClient
	 */
	public BillerFeeWrkFacade getBillerFeeWrkClient() {
		return billerFeeWrkClient;
	}

	/**
	 * @param billerFeeWrkClient the billerFeeWrkClient to set
	 */
	public void setBillerFeeWrkClient(BillerFeeWrkFacade billerFeeWrkClient) {
		this.billerFeeWrkClient = billerFeeWrkClient;
	}
	
	/**
	 * @return the interestClient
	 */
	public InterestFacade getInterestClient() {
		return interestClient;
	}
	
	/**
	 * @param interestClient the interestClient to set
	 */
	public void setInterestClient(InterestFacade interestClient) {
		this.interestClient = interestClient;
	}
	
	
	/**
	 * @return the interestTaxClient
	 */
	public InterestTaxFacade getInterestTaxClient() {
		return interestTaxClient;
	}
	
	/**
	 * @param interestTaxClient the interestTaxClient to set
	 */
	public void setInterestTaxClient(InterestTaxFacade interestTaxClient) {
		this.interestTaxClient = interestTaxClient;
	}
	
	
	/**
	 * @return the interestWrkClient
	 */
	public InterestWrkFacade getInterestWrkClient() {
		return interestWrkClient;
	}
	
	/**
	 * @param interestWrkClient the interestWrkClient to set
	 */
	public void setInterestWrkClient(InterestWrkFacade interestWrkClient) {
		this.interestWrkClient = interestWrkClient;
	}
	
	/**
	 * @return the interestTaxWrkClient
	 */
	public InterestTaxWrkFacade getInterestTaxWrkClient() {
		return interestTaxWrkClient;
	}
	
	/**
	 * @param interestTaxWrkClient the interestTaxWrkClient to set
	 */
	public void setInterestTaxWrkClient(InterestTaxWrkFacade interestTaxWrkClient) {
		this.interestTaxWrkClient = interestTaxWrkClient;
	}

	/**
	 * @return the accountClient
	 */
	public AccountFacade getAccountClient() {
		return accountClient;
	}

	/**
	 * @param accountClient the accountClient to set
	 */
	public void setAccountClient(AccountFacade accountClient) {
		this.accountClient = accountClient;
	}

	/**
	 * @return the favClient
	 */
	public PreRegisteredFacade getFavClient() {
		return favClient;
	}

	/**
	 * @param favClient the favClient to set
	 */
	public void setFavClient(PreRegisteredFacade favClient) {
		this.favClient = favClient;
	}

	/**
	 * @return the securitysClient
	 */
	public SecurityFacade getSecuritysClient() {
		return securitysClient;
	}

	/**
	 * @param securitysClient the securitysClient to set
	 */
	public void setSecuritysClient(SecurityFacade securitysClient) {
		this.securitysClient = securitysClient;
	}

	/**
	 * @return the walletClient
	 */
	public IWalletEndpoint getWalletClient() {
		return walletClient;
	}

	/**
	 * @param walletClient the walletClient to set
	 */
	public void setWalletClient(IWalletEndpoint walletClient) {
		this.walletClient = walletClient;
	}
	
	
	
}
