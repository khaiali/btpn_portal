package com.sybase365.mobiliser.web.btpn.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.ClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.btpnwow.core.customer.facade.api.CustomerFacade;
import com.btpnwow.core.customer.facade.contract.CustomerInformationType;
import com.btpnwow.core.customer.facade.contract.GetCustomerExRequest;
import com.btpnwow.core.customer.facade.contract.GetCustomerExResponse;
import com.btpnwow.core.security.facade.api.SecurityFacade;
import com.btpnwow.core.security.facade.contract.CustomerCredentialType;
import com.btpnwow.core.security.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.security.facade.contract.VerifyCredentialRequest;
import com.btpnwow.core.security.facade.contract.VerifyCredentialResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.framework.contract.v5_0.base.AuditDataType;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.LogoutRequest;
import com.sybase365.mobiliser.money.services.api.ISecurityEndpoint;
import com.sybase365.mobiliser.web.btpn.bank.beans.Credential;

/**
 * This is the provider for agent portals.
 * 
 * @author Andi Samallangi W
 */

public class AgentCustomerAuthenticationProvider implements AuthenticationProvider, InitializingBean {

	private static Logger log = LoggerFactory.getLogger(AgentCustomerAuthenticationProvider.class);

	private ISecurityEndpoint securityEndpoint;

	private SecurityFacade securitysClient;
	
	private CustomerFacade customerClient;

	public void setSecurityEndpoint(ISecurityEndpoint securityEndpoint) {
		this.securityEndpoint = securityEndpoint;
	}

	/**
	 * @param securitysClient the securitysClient to set
	 */
	public void setSecuritysClient(SecurityFacade securitysClient) {
		this.securitysClient = securitysClient;
	}

	/**
	 * @param customerClient the customerClient to set
	 */
	public void setCustomerClient(CustomerFacade customerClient) {
		this.customerClient = customerClient;
	}

	/**
	 * After properties set if null throws an exception.
	 * 
	 * @throws Exception exception in case loginClient is not set.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.securityEndpoint == null) {
			throw new IllegalStateException("securityEndpoint required");
		}
		if (this.securitysClient == null) {
			throw new IllegalStateException("securitysClient required");
		}
		if (this.customerClient == null) {
			throw new IllegalStateException("customerClient required");
		}
	}

	/**
	 * Authentication method which does the authentication.
	 * 
	 * @param authentication object for authentication
	 * @return authentication object
	 */
	public Authentication authenticate(Authentication authentication) 
			throws AuthenticationException {
		
		log.info("### (AgentCustomerAuthenticationProvider::authenticate) user {}, password XXX ###", authentication.getName());
		Credential credential = (Credential) authentication.getCredentials();
		
		VerifyCredentialRequest request = new VerifyCredentialRequest();

		final CustomerIdentificationType cit = new CustomerIdentificationType();
		cit.setValue(authentication.getName());
		cit.setType(BtpnConstants.IDENTIFICATION_TYPE_MOBILE_NO);
		cit.setOrgUnitId("");

		final CustomerCredentialType cct = new CustomerCredentialType();
		cct.setType(BtpnConstants.CREDENTIAL_TYPE_PIN);
		cct.setValue(credential.getCredential());

		request.setIdentification(cit);
		request.setCredential(cct);
		request.setFlags(1);

		if (Session.exists()) {

			final ClientInfo clientInfo = Session.get().getClientInfo();

			if (clientInfo instanceof WebClientInfo) {
				final WebClientInfo webClientInfo = (WebClientInfo) clientInfo;

				request.setAuditData(new AuditDataType());
				request.getAuditData().setDevice(StringUtils.substring(webClientInfo.getUserAgent(), 0, 80));
				request.getAuditData().setOtherDeviceId(webClientInfo.getProperties().getRemoteAddress());
			}
		}

		final VerifyCredentialResponse response = this.securitysClient.verifyCredential(request);
		
		if (log.isDebugEnabled()) {
			log.debug("VerifyCredential response code = " + MobiliserUtils.errorCode(response));
		}
		
		boolean credentialsExpired = false;
		
		if (!MobiliserUtils.success(response)) {
			int errorCode = MobiliserUtils.errorCode(response);

			switch (errorCode) {
			case 201:
			case 203:
				throw new UsernameNotFoundException(MobiliserUtils.errorMessage(response, null));
			case 301:
				throw new DisabledException(MobiliserUtils.errorMessage(response, null));
			case 302:
			case 322:
			case 330:
			case 399:
				throw new LockedException(MobiliserUtils.errorMessage(response, null));
			case 321:
			case 331:
			case 332:
				credentialsExpired = true;
				break;
			case 329:
			default:
				throw new BadCredentialsException(MobiliserUtils.errorMessage(response, null));
			}
		}
		
		GetCustomerExRequest req = new GetCustomerExRequest();
		
		final com.btpnwow.core.customer.facade.contract.CustomerIdentificationType cit2 = new com.btpnwow.core.customer.facade.contract.CustomerIdentificationType();
		cit2.setValue(authentication.getName());
		cit2.setType(BtpnConstants.IDENTIFICATION_TYPE_MOBILE_NO);
		cit2.setOrgUnitId("");
		
		req.setIdentification(cit2);
		req.setFlags(1);
		
		final GetCustomerExResponse response2 = this.customerClient.get(req);
		
		if (log.isDebugEnabled()) {
			log.debug("GetCustomerEx response code = " + MobiliserUtils.errorCode(response));
		}
		
		if (!MobiliserUtils.success(response2)) {
			logout(response.getSessionId());
			
			throw new UsernameNotFoundException(authentication.getName());
		}
			
		final BtpnCustomer customer = new BtpnCustomer();
		
		customer.setSessionId(response.getSessionId());
		customer.setUsername(authentication.getName());
		
		CustomerInformationType cinfo = response2.getInformation();
		
		customer.setCountry(cinfo.getNationality());
		customer.setCustomerId(cinfo.getId().longValue());
		customer.setCustomerTypeId(cinfo.getCustomerType().intValue());
		customer.setCustomerTypeCategoryId(cinfo.getCustomerTypeCategory() == null ? -1 : cinfo.getCustomerTypeCategory().intValue());
		customer.setDisplayName(cinfo.getName());
		customer.setEmail(cinfo.getEmail());
		customer.setLanguage(cinfo.getLanguage());
		customer.setOrgUnitId(cinfo.getOrgUnitId());
		customer.setOtpPrivilege(credentialsExpired ? null : BtpnConstants.PRIV_GENERATE_OTP_PRIVILEGE_MODE);
		customer.setParentId(cinfo.getParentId());
		customer.setTerritoryCode(cinfo.getTerritoryCode());
		customer.setTxnReceiptModeId(cinfo.getNotificationMode().intValue());
		customer.setUsername(authentication.getName());

		if (log.isDebugEnabled()) {
			log.debug("Privileges: " + response2.getPrivilege());
		}
		
		final Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		for (String privilege : response2.getPrivilege()) {
			authorities.add(new SimpleGrantedAuthority(privilege));
		}
		
		if (credentialsExpired) {
			authorities.add(new SimpleGrantedAuthority(BtpnConstants.PRIV_CHANGE_PASSWORD_EXPIRED));
		}
		
		return new UsernamePasswordAuthenticationToken(customer, authentication.getCredentials(), authorities);
	}
	
	private void logout(String sessionId) {
		if (sessionId == null) {
			return;
		}
		
		LogoutRequest request = new LogoutRequest();
		request.setDestroyPersistentLogin(Boolean.TRUE);
		request.setSessionId(sessionId);
		
		try {
			securityEndpoint.logout(request);
		} catch (Throwable ex) {
			// do nothing
		}
	}

	/**
	 * returns true always.
	 * 
	 * @param authentication class for authentication
	 * @return boolean true or false
	 */
	public boolean supports(Class<? extends Object> authentication) {
		return true;
	}
}
