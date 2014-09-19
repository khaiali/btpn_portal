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
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.btpnwow.core.customer.facade.api.UserFacade;
import com.btpnwow.core.customer.facade.contract.GetUserRequest;
import com.btpnwow.core.customer.facade.contract.GetUserResponse;
import com.btpnwow.core.customer.facade.contract.UserInformationType;
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
 * This is the home page for bank portals.
 * 
 * @author Andi Samallangi W
 */
public class BankPortalAuthenticationProvider implements AuthenticationProvider, InitializingBean {

	private static Logger log = LoggerFactory.getLogger(BankPortalAuthenticationProvider.class);
	
	private ISecurityEndpoint securityEndpoint;

	private SecurityFacade securityFacade;
	
	private UserFacade userClient;

	public void setSecurityEndpoint(ISecurityEndpoint securityEndpoint) {
		this.securityEndpoint = securityEndpoint;
	}

	/**
	 * @param securityClient the securityClient to set
	 */
	public void setSecurityFacade(SecurityFacade securityFacade) {
		this.securityFacade = securityFacade;
	}

	/**
	 * @param userClient the userClient to set
	 */
	public void setUserClient(UserFacade userClient) {
		this.userClient = userClient;
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
		if (this.securityFacade == null) {
			throw new IllegalStateException("securityFacade required");
		}
		if (this.userClient == null) {
			throw new IllegalStateException("userClient required");
		}
	}
	
	/**
	 * Authentication method which does the authentication.
	 * 
	 * @param authentication object for authentication
	 * @return authentication object
	 */
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (log.isDebugEnabled()) {
			log.debug("Trying to authenticate user {}, password xxxxx.", authentication.getName());
		}
		
		Credential credential = (Credential) authentication.getCredentials();
		
		VerifyCredentialRequest request = new VerifyCredentialRequest();

		final CustomerIdentificationType cit = new CustomerIdentificationType();
		cit.setValue(authentication.getName());
		cit.setType(BtpnConstants.IDENTIFICATION_TYPE_USER_NAME);
		cit.setOrgUnitId("");

		final CustomerCredentialType cct = new CustomerCredentialType();
		cct.setType(-1);
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

		final VerifyCredentialResponse response = this.securityFacade.verifyCredential(request);
		
		if (log.isDebugEnabled()) {
			log.debug("VerifyCredential response code = " + MobiliserUtils.errorCode(response));
		}
		
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
				throw new CredentialsExpiredException(MobiliserUtils.errorMessage(response, null));
			case 329:
			case 332:
			default:
				throw new BadCredentialsException(MobiliserUtils.errorMessage(response, null));
			}
		}
		
		GetUserRequest req = new GetUserRequest();
		req.setUserId(authentication.getName());
		req.setFlags(1);
		
		final GetUserResponse cuser = this.userClient.get(req);
		
		if (log.isDebugEnabled()) {
			log.debug("GetUser response code = " + MobiliserUtils.errorCode(response));
		}
		
		if (!MobiliserUtils.success(cuser)) {
			logout(response.getSessionId());
			
			throw new UsernameNotFoundException(authentication.getName());
		}

		BtpnCustomer customer = new BtpnCustomer();
		
		customer.setSessionId(response.getSessionId());
		customer.setUsername(authentication.getName());
		
		UserInformationType cinfo = cuser.getInformation();
		
		customer.setCountry(cinfo.getCountry());
		customer.setCustomerId(cinfo.getId().longValue());
		customer.setCustomerTypeId(cinfo.getCustomerType().intValue());
		customer.setCustomerTypeCategoryId(cinfo.getCustomerTypeCategory() == null ? -1 : cinfo.getCustomerTypeCategory().intValue());
		customer.setDisplayName(cinfo.getName());
		customer.setDesignation(cinfo.getDesignation());
		customer.setEmail(cinfo.getEmail());
		customer.setGlCode(cinfo.getGlCode());
		customer.setLanguage(cinfo.getLanguage());
		customer.setOrgUnitId(cinfo.getOrgUnitId());
		customer.setTerritoryCode(cinfo.getTerritoryCode());
		customer.setUsername(authentication.getName());

		if (log.isDebugEnabled()) {
			log.debug("Privileges: " + cuser.getPrivilege());
		}
		
		final Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		for (String privilege : cuser.getPrivilege()) {
			authorities.add(new SimpleGrantedAuthority(privilege));
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
	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return true;
	}

}
