package com.sybase365.mobiliser.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponCategory;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponType;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Transaction;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.BalanceAlert;
import com.sybase365.mobiliser.money.services.api.ISecurityEndpoint;
import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.application.MobiliserApplication;
import com.sybase365.mobiliser.web.application.model.IMobiliserApplication;
import com.sybase365.mobiliser.web.application.pages.ApplicationStartPage;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.beans.RemittanceAccountBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.Credential;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerSearchBean;
import com.sybase365.mobiliser.web.btpn.util.AgentPortalConfiguration;
import com.sybase365.mobiliser.web.btpn.util.BankPortalConfiguration;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.btpn.util.ConsumerPortalConfiguration;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.checkout.util.SmsAuthenticationThread;
import com.sybase365.mobiliser.web.demomerchant.util.CartItem;

public class MobiliserWebSession extends BaseWebSession {
	
	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApplicationStartPage.class);

	private CustomerBean customer;

	private CustomerSearchBean customerSearchBean;

	private RemittanceAccountBean remittanceData;

	private AuthorisationCallThread authorisationCallThread;

	private List<BalanceAlert> balanceAlertList;

	private String pickupCode;

	private int maxViewLevel;

	private int maxCreateLevel;

	private int maxMaintainLevel;

	private int dbBlrReason;

	private String alias;

	private Transaction preAuthTxn;

	private String customerOtp;

	private int customerOtpCount;

	private boolean customerOtpLimitHit;

	private boolean showContact;

	private List<CartItem> cartItems = new ArrayList<CartItem>();

	private com.sybase365.mobiliser.web.checkout.models.Transaction transaction;

	private com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer txnPayee;

	private com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer txnPayer;

	SmsAuthenticationThread SmsThread;

	private String msisdn;

	private boolean autoCapture;

	private boolean isAlertMenuEntriesActive;

	private boolean continueToCheckout;

	private TimeZone timeZone;

	private CouponType couponType;

	private CouponCategory couponCategory;

	private boolean selfAuthenticationRequired;

	@SpringBean(name = "bankPortalAuthenticationManager")
	private AuthenticationManager bankPortalAuthenticationManager;
	
	/* Added By Andi */
//	@SpringBean(name = "bankPortalAuthManager")
//	private AuthenticationManager bankPortalAuthManager;

	@SpringBean(name = "customerAuthenticationManager")
	private AuthenticationManager customerAuthenticationManager;

	@SpringBean(name = "authenticationManager")
	private AuthenticationManager authenticationManager;
	
	/* Added By Andi */
//	@SpringBean(name = "authManager")
//	private AuthenticationManager authManager;

	/**
	 * Configuration security for BTPN Portals.
	 */
	@SpringBean(name = "smartAuthSecurityClient")
	public ISecurityEndpoint securityClient;

	private Authentication authentication;

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

	public CustomerRegistrationBean customerRegistrationBean;
	
	private final Map<String, Object> attributes;

	public MobiliserWebSession(Request request) {
		super(request);
		// setLevels();

		attributes = new ConcurrentHashMap<String, Object>();
	}
	
	public void put(String key, Object value) {
		attributes.put(key, value);
	}
	
	public Object get(String key) {
		return attributes.get(key);
	}
	
	public boolean contains(String key) {
		return attributes.containsKey(key);
	}

	@Override
	public boolean authenticate(String username, String password) {
		boolean isAuthenticated = super.authenticate(username, password);
		if (isAuthenticated) {
			setLevels();
			setLoggedInUserLocale(getLoggedInCustomer());
			setLoggedInUserTimeZone(getLoggedInCustomer());
		}
		return isAuthenticated;
	}

	private void setLoggedInUserLocale(Customer loggedInCustomer) {
		if (PortalUtils.exists(loggedInCustomer.getLanguage())) {
			if (PortalUtils.exists(loggedInCustomer.getCountry())) {
				if (PortalUtils.exists(loggedInCustomer.getOrgUnitId())) {
					setLocale(new Locale(loggedInCustomer.getLanguage(), loggedInCustomer.getCountry(),
						loggedInCustomer.getOrgUnitId()));
					return;
				}
				setLocale(new Locale(loggedInCustomer.getLanguage(), loggedInCustomer.getCountry()));
				return;
			}
			setLocale(new Locale(loggedInCustomer.getLanguage()));
		}
	}

	private void setLoggedInUserTimeZone(Customer loggedInCustomer) {
	
		if (PortalUtils.exists(loggedInCustomer.getTimeZone()))
			setTimeZone(TimeZone.getTimeZone(loggedInCustomer.getTimeZone()));
		else if (PortalUtils.exists(loggedInCustomer.getOrgUnitId())
				&& RequestCycle.get().getRequest().getPage() instanceof MobiliserBasePage) {
			MobiliserBasePage page = (MobiliserBasePage) RequestCycle.get().getRequest().getPage();
			if (PortalUtils.exists(page.getConfiguration().getTimeZoneForOrgUnit(loggedInCustomer.getOrgUnitId())))
				setTimeZone(TimeZone.getTimeZone(page.getConfiguration().getTimeZoneForOrgUnit(
					loggedInCustomer.getOrgUnitId())));
			else setTimeZone(TimeZone.getDefault());
		} else setTimeZone(TimeZone.getDefault());
	
	}

	@Override
	protected SybaseMenu buildMenu() {

		SybaseMenu menu = new SybaseMenu(null);

		List<IMobiliserApplication> allApps = ((MobiliserApplication) getApplication()).getAllApplications();

		for (IMobiliserApplication app : allApps) {
			app.buildMenu(menu, getRoles());
		}

		return menu;
	}

	public boolean hasPrivilege(String privilege) {
		List<GrantedAuthority> authority = (List<GrantedAuthority>) SecurityContextHolder.getContext()
			.getAuthentication().getAuthorities();
		if (authority.contains(new SimpleGrantedAuthority(privilege))) {
			return true;
		}
		return false;
	}

	public boolean hasM2MRole(CustomerBean cBean) {
		if (cBean == null || cBean.getCustomerTypeId() == null)
			return false;
		int roleID = cBean.getCustomerTypeId();
		return Constants.CUSTOMER_ROLES_M2M.contains(roleID);
	}

	public boolean hasM2MRole(Customer customer) {
		if (customer == null)
			return false;
		return Constants.CUSTOMER_ROLES_M2M.contains(customer.getCustomerTypeId());
	}

	private void setLevels() {
		List<GrantedAuthority> authorities = (List<GrantedAuthority>) SecurityContextHolder.getContext()
			.getAuthentication().getAuthorities();
		for (GrantedAuthority authority : authorities) {
			String priv = authority.getAuthority();

			if (maxViewLevel >= 0) {
				// check if all children are allowed to be viewed by this user
				if (priv.equals(Constants.PRIV_VIEW_DESCENDANTS)) {
					maxViewLevel = -1;
					continue;
				}
				// check if a view-level is set in the current privilege
				Matcher m = Pattern.compile(Constants.PRIV_REGEX_VIEW_CHILDREN_LEVEL).matcher(priv);
				if (m.matches() && m.groupCount() == 1) {
					int level = Integer.valueOf(m.group(1)).intValue();
					if (level > maxViewLevel)
						maxViewLevel = level;
					continue;
				}
			}

			if (maxCreateLevel >= 0) {
				// check if there is no limit level for creation of children
				if (priv.equals(Constants.PRIV_CREATE_DESCENDANTS)) {
					maxCreateLevel = -1;
					continue;
				}
				// check if a create-level is set in the current privilege
				Matcher m = Pattern.compile(Constants.PRIV_REGEX_CREATE_CHILDREN_LEVEL).matcher(priv);
				if (m.matches() && m.groupCount() == 1) {
					int level = Integer.valueOf(m.group(1)).intValue();
					if (level > maxCreateLevel)
						maxCreateLevel = level;
					continue;
				}
			}

			if (maxMaintainLevel >= 0) {
				// check if this user has no limit level regarding to the
				// agents he is allowed to maintain
				if (priv.equals(Constants.PRIV_MAINTAIN_DESCENDANTS)) {
					maxMaintainLevel = -1;
					continue;
				}
				// check if a maintain-level is set in the current privilege
				Matcher m = Pattern.compile(Constants.PRIV_REGEX_MAINTAIN_CHILDREN_LEVEL).matcher(priv);
				if (m.matches() && m.groupCount() == 1) {
					int level = Integer.valueOf(m.group(1)).intValue();
					if (level > maxMaintainLevel)
						maxMaintainLevel = level;
				}
			}

		}
	}

	public void setCustomer(CustomerBean customer) {
		this.customer = customer;
		if (customer != null && customer.getBlackListReason() != null) {
			this.setDbBlrReason(customer.getBlackListReason());
		}
	}

	public CustomerBean getCustomer() {
		return customer;
	}

	public RemittanceAccountBean getRemittanceData() {
		return remittanceData;
	}

	public void setRemittanceData(RemittanceAccountBean remittanceData) {
		this.remittanceData = remittanceData;
	}

	public void setAuthorisationCallThread(AuthorisationCallThread authorisationCallThread) {
		this.authorisationCallThread = authorisationCallThread;
	}

	public AuthorisationCallThread getAuthorisationCallThread() {
		return authorisationCallThread;
	}

	public void setBalanceAlertList(BalanceAlert balanceAlert) {
		if (balanceAlertList == null) {
			balanceAlertList = new ArrayList<BalanceAlert>();
		}
		balanceAlertList.add(balanceAlert);
	}

	public List<BalanceAlert> getBalanceAlertList() {
		if (balanceAlertList == null) {
			balanceAlertList = new ArrayList<BalanceAlert>();
		}
		return balanceAlertList;
	}

	public void setBalanceAlertList(List<BalanceAlert> balanceAlertList) {
		this.balanceAlertList = balanceAlertList;
	}

	public String getPickupCode() {
		return pickupCode;
	}

	public void setPickupCode(String pickupCode) {
		this.pickupCode = pickupCode;
	}

	public int getMaxViewLevel() {
		return maxViewLevel;
	}

	public void setMaxViewLevel(int maxViewLevel) {
		this.maxViewLevel = maxViewLevel;
	}

	public int getMaxCreateLevel() {
		return maxCreateLevel;
	}

	public void setMaxCreateLevel(int maxCreateLevel) {
		this.maxCreateLevel = maxCreateLevel;
	}

	public int getMaxMaintainLevel() {
		return maxMaintainLevel;
	}

	public void setMaxMaintainLevel(int maxMaintainLevel) {
		this.maxMaintainLevel = maxMaintainLevel;
	}

	public void setDbBlrReason(int dbBlrReason) {
		this.dbBlrReason = dbBlrReason;
	}

	public int getDbBlrReason() {
		return dbBlrReason;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Transaction getPreAuthTxn() {
		return preAuthTxn;
	}

	public void setPreAuthTxn(Transaction preAuthTxn) {
		this.preAuthTxn = preAuthTxn;
	}

	public String getCustomerOtp() {
		return customerOtp;
	}

	public void setCustomerOtp(String customerOtp) {
		this.customerOtp = customerOtp;
	}

	public int getCustomerOtpCount() {
		return customerOtpCount;
	}

	public void setCustomerOtpCount(int customerOtpCount) {
		this.customerOtpCount = customerOtpCount;
	}

	public boolean isCustomerOtpLimitHit() {
		return customerOtpLimitHit;
	}

	public void setCustomerOtpLimitHit(boolean customerOtpLimitHit) {
		this.customerOtpLimitHit = customerOtpLimitHit;
	}

	public boolean isShowContact() {
		return showContact;
	}

	public void setShowContact(boolean showContact) {
		this.showContact = showContact;
	}

	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	public List<CartItem> getCartItems() {
		return cartItems;
	}

	public com.sybase365.mobiliser.web.checkout.models.Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(com.sybase365.mobiliser.web.checkout.models.Transaction transaction) {
		this.transaction = transaction;
	}

	public com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer getTxnPayee() {
		return txnPayee;
	}

	public void setTxnPayee(com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer txnPayee) {
		this.txnPayee = txnPayee;
	}

	public com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer getTxnPayer() {
		return txnPayer;
	}

	public void setTxnPayer(com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer txnPayer) {
		this.txnPayer = txnPayer;
	}

	public static void setSessionTimeout(int seconds) {
		HttpSession session = getHttpSession();
		if (session != null) {
			session.setMaxInactiveInterval(seconds);
			log.info("Application session timeout set to {} secs", seconds);
		}
	}

	public static int getSessionTimeout() {
		HttpSession session = getHttpSession();
		if (session != null) {
			return session.getMaxInactiveInterval();
		}
		return 0;
	}

	private static HttpSession getHttpSession() {
		WebRequest request = (WebRequest) WebRequestCycle.get().getRequest();
		return request.getHttpServletRequest().getSession();
	}

	public SmsAuthenticationThread getSmsThread() {
		return SmsThread;
	}

	public void setSmsThread(SmsAuthenticationThread smsThread) {
		SmsThread = smsThread;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public boolean isAutoCapture() {
		return autoCapture;
	}

	public void setAutoCapture(boolean autoCapture) {
		this.autoCapture = autoCapture;
	}

	public boolean isContinueToCheckout() {
		return continueToCheckout;
	}

	public void setContinueToCheckout(boolean continueToCheckout) {
		this.continueToCheckout = continueToCheckout;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public void setCouponType(CouponType couponType) {
		this.couponType = couponType;
	}

	public CouponType getCouponType() {
		return couponType;
	}

	public CouponCategory getCouponCategory() {
		return couponCategory;
	}

	public void setCouponCategory(CouponCategory couponCategory) {
		this.couponCategory = couponCategory;
	}

	/**
	 * @return the selfAuthenticationRequired
	 */
	public boolean isSelfAuthenticationRequired() {
		return selfAuthenticationRequired;
	}

	/**
	 * @param selfAuthenticationRequired the selfAuthenticationRequired to set
	 */
	public void setSelfAuthenticationRequired(boolean selfAuthenticationRequired) {
		this.selfAuthenticationRequired = selfAuthenticationRequired;
	}

	public void setAlertMenuEntriesActive(boolean isAlertMenuEntriesActive) {
		this.isAlertMenuEntriesActive = isAlertMenuEntriesActive;
	}

	public boolean isAlertMenuEntriesActive() {
		return isAlertMenuEntriesActive;
	}

	public BtpnCustomer getBtpnLoggedInCustomer() {
		final Authentication authentication = getAuthentication();
		if (authentication == null) {
			return null;
		}
		return (BtpnCustomer) authentication.getPrincipal();
	}

	public Roles getBtpnRoles() {
		Roles roles = new Roles();
		getBtpnRolesIfSignedIn(roles);
		return roles;
	}

	private void getBtpnRolesIfSignedIn(Roles roles) {
		if (isSignedIn()) {
			Authentication authentication = getAuthentication();
			if (authentication == null) {
				signOut();
				return;
			}
			for (GrantedAuthority authority : authentication.getAuthorities()) {
				roles.add(authority.getAuthority());
			}
		} else {
			roles.add(PRIV_NOT_LOGGED_IN);
		}
	}
	
	
	/**
	 * Authentication method for Bank portals.
	 * 
	 * @param username UserName to login to bank portal.
	 * @param password Password to login to bank portal.
	 * @param domain Domain of LDAP to login.
	 * @return
	 */
	public boolean authenticateBankPortalUser(String username, String password,
			String domain) throws AuthenticationException {
		boolean authenticated = false;
		Authentication authentication;
		try {
			log.debug("### (MobiliserWebSession::authenticateBankPortalUser) Start ###"); 	
			Credential credential = new Credential();
			credential.setDomain(domain);
			credential.setCredential(password);
			authentication = bankPortalAuthenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, credential));
			
			BtpnCustomer customer;
			
			if (authentication.getPrincipal() instanceof BtpnCustomer) {
				customer = (BtpnCustomer) authentication.getPrincipal();
			} else {
				customer = ConverterUtils.convertToBtpnCustomer((Customer) authentication.getPrincipal());
			}
			
			log.info("### (MobiliserWebSession::authenticateBankPortalUser) AUTHORITIES ###" +authentication.getAuthorities());
			
			authentication = new UsernamePasswordAuthenticationToken(customer, authentication.getCredentials(),
					authentication.getAuthorities());
			
			log.info("### (MobiliserWebSession::authenticateBankPortalUser) isAuthenticate ###" +authentication.isAuthenticated());
			authenticated = authentication.isAuthenticated();

			setAuthentication(authentication);
			
			if (StringUtils.hasText(customer.getLanguage())) {
				setLocale(new Locale(customer.getLanguage()));
			} else {
				setBtpnLocale(this.bankPortalPrefsConfig.getDefaultLanguage(),
						this.bankPortalPrefsConfig.getDefaultCountry());
			}
			
			signIn(authenticated);
		} catch (AuthenticationException e) {
			log.debug("### (MobiliserWebSession::authenticateBankPortalUser) User {} failed to login ###", username, e);
			authenticated = false;
			throw e;
		}
		return authenticated;
	}
	
	/**
	 * Authentication method for Bank portals.
	 * 
	 * @param username UserName to login to bank portal.
	 * @param password Password to login to bank portal.
	 * @param domain Domain of LDAP to login.
	 * @return
	 */
	public boolean authenticateBankPortalUser(String username, String password) throws AuthenticationException {
		
		boolean authenticated = false;
		Authentication authentication;
		
		try {
			log.debug("### (MobiliserWebSession::authenticateBankPortalUser) Start ###"); 	
			Credential credential = new Credential();
			
			credential.setCredential(password);
			authentication = bankPortalAuthenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, credential));
			
			final BtpnCustomer customer = ConverterUtils.convertToBtpnCustomer((Customer) authentication
					.getPrincipal());
			
			log.info("### (MobiliserWebSession::authenticateBankPortalUser) AUTHORITIES ###" +authentication.getAuthorities());
			
			authentication = new UsernamePasswordAuthenticationToken(customer, authentication.getCredentials(),
					authentication.getAuthorities());
			
			log.info("### (MobiliserWebSession::authenticateBankPortalUser) isAuthenticate ###" +authentication.isAuthenticated());
			authenticated = authentication.isAuthenticated();

			setAuthentication(authentication);
			
			if (StringUtils.hasText(customer.getLanguage())) {
				setLocale(new Locale(customer.getLanguage()));
			} else {
				setBtpnLocale(this.bankPortalPrefsConfig.getDefaultLanguage(),
						this.bankPortalPrefsConfig.getDefaultCountry());
			}
			
			signIn(authenticated);
		} catch (AuthenticationException e) {
			log.debug("### (MobiliserWebSession::authenticateBankPortalUser) User {} failed to login ###", username, e);
			authenticated = false;
			throw e;
		}
		
		return authenticated;
	}
	
	/**
	 * Check Super Admin for Bank Portals
	 * 
	 * @param username UserName to login to bank portal.
	 * @return
	 */
	public boolean checkForSuperAdmin(String username) throws AuthenticationException {
		if (username.equalsIgnoreCase(bankPortalPrefsConfig.getDefaultSuperAdmin()))
			return true;
		return false;

	}

	/**
	 * Authentication method for Bank portals.
	 * 
	 * @param username UserName to login to customer portal.
	 * @param pin pin to login to customer portal.
	 * @return
	 */
	public boolean authenticateCustomer(final String username, final String pin, final String portalType)
		throws AuthenticationException {
		
		log.info("### (MobiliserWebSession::authenticateCustomer) ###");
		boolean authenticated = false;
		
		try {
			final Credential credential = new Credential();
			credential.setCredential(pin);
			credential.setPortalType(portalType);
			
			Authentication authentication = customerAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, credential));
			setAuthentication(authentication);
			
			authenticated = authentication.isAuthenticated();
			
			BtpnCustomer customer = ConverterUtils.convertToBtpnCustomer((Customer) authentication.getPrincipal());

			if (StringUtils.hasText(customer.getLanguage())) {
				setLocale(new Locale(customer.getLanguage()));
			} else {
				if (portalType.equals(BtpnConstants.PORTAL_AGENT)) {
					setBtpnLocale(this.agentPortalPrefsConfig.getDefaultLanguage(),
						this.agentPortalPrefsConfig.getDefaultCountry());
				} else {
					setBtpnLocale(this.customerPortalPrefsConfig.getDefaultLanguage(),
						this.customerPortalPrefsConfig.getDefaultCountry());
				}
			}
			
			signIn(authenticated);
		} catch (AuthenticationException e) {
			log.debug("User {} failed to login", username, e);
			authenticated = false;
			throw e;
		}
		
		return authenticated;
	}

	public void setBtpnLocale(final String language, final String country) {
		setLocale(new Locale(language, country));
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/**
	 * @return the customerRegistrationBean
	 */
	public CustomerRegistrationBean getCustomerRegistrationBean() {
		return customerRegistrationBean;
	}

	/**
	 * @param customerRegistrationBean the customerRegistrationBean to set
	 */
	public void setCustomerRegistrationBean(CustomerRegistrationBean customerRegistrationBean) {
		this.customerRegistrationBean = customerRegistrationBean;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	public Authentication getAuthentication() {
		return authentication;
	}

	public CustomerSearchBean getCustomerSearchBean() {
		return customerSearchBean;
	}

	public void setCustomerSearchBean(CustomerSearchBean customerSearchBean) {
		this.customerSearchBean = customerSearchBean;
	}

	/**
	 * @return the bankPortalAuthenticationManager
	 */
	public AuthenticationManager getBankPortalAuthenticationManager() {
		return bankPortalAuthenticationManager;
	}

	/**
	 * @param bankPortalAuthenticationManager the bankPortalAuthenticationManager to set
	 */
	public void setBankPortalAuthenticationManager(
			AuthenticationManager bankPortalAuthenticationManager) {
		this.bankPortalAuthenticationManager = bankPortalAuthenticationManager;
	}

	/**
	 * @return the customerAuthenticationManager
	 */
	public AuthenticationManager getCustomerAuthenticationManager() {
		return customerAuthenticationManager;
	}

	/**
	 * @param customerAuthenticationManager the customerAuthenticationManager to set
	 */
	public void setCustomerAuthenticationManager(
			AuthenticationManager customerAuthenticationManager) {
		this.customerAuthenticationManager = customerAuthenticationManager;
	}

	/**
	 * @return the authManager
	 */
//	public AuthenticationManager getAuthManager() {
//		return authManager;
//	}

	/**
	 * @param authManager the authManager to set
	 */
//	public void setAuthManager(AuthenticationManager authManager) {
//		this.authManager = authManager;
//	}

	/**
	 * @return the bankPortalAuthManager
	 */
//	public AuthenticationManager getBankPortalAuthManager() {
//		return bankPortalAuthManager;
//	}

	/**
	 * @param bankPortalAuthManager the bankPortalAuthManager to set
	 */
//	public void setBankPortalAuthManager(AuthenticationManager bankPortalAuthManager) {
//		this.bankPortalAuthManager = bankPortalAuthManager;
//	}
	
	

}
