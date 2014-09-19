package com.btpnwow.portal.common.util;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.ClientInfo;

import com.sybase365.mobiliser.framework.contract.v5_0.base.AuditDataType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserRequestType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLookupsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLookupsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LookupEntity;
import com.sybase365.mobiliser.money.services.api.ISystemEndpoint;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

public abstract class MobiliserUtils {
	
	public static String getExternalReferenceNo(ISystemEndpoint systemEndpoint) {
		GetLookupsRequest request = new GetLookupsRequest();
		request.setCallback(null);
		request.setConversationId(UUID.randomUUID().toString());
		request.setOrigin("mobiliser-web");
		request.setRepeat(Boolean.FALSE);
		request.setTraceNo(UUID.randomUUID().toString());
		request.setEntityName("mdwstan");

		GetLookupsResponse response = systemEndpoint.getLookups(request);
		
		if (MobiliserUtils.success(response)) {
			List<LookupEntity> entities = response.getLookupEntities();
			
			if ((entities != null) && !entities.isEmpty()) {
				return entities.get(0).getName();
			}
			
			throw new RuntimeException("response from mobiliser doesn't contain any data");
		}
		
		throw new RuntimeException(Integer.toString(MobiliserUtils.errorCode(response)).concat(" ").concat(MobiliserUtils.errorMessage(response, null)));
	}

	public static boolean success(Integer responseCode) {
		return (responseCode != null) && (responseCode.intValue() == 0);
	}
	
	public static boolean success(MobiliserResponseType resp) {
		return (resp != null) && (resp.getStatus() != null) && (resp.getStatus().getCode() == 0);
	}
	
	public static int errorCode(MobiliserResponseType resp) {
		if ((resp == null) || (resp.getStatus() == null)) {
			return 9999;
		}
		
		return resp.getStatus().getCode();
	}
	
	public static String errorMessage(MobiliserResponseType resp, Component component) {
		return errorMessage(resp, component == null ? null : component.getLocalizer(), component);
	}
	
	public static String errorMessage(MobiliserResponseType resp, Localizer localizer, Component component) {
		int errorCode = errorCode(resp);
		
		String message = null;
		
		if ((resp != null) && (resp.getStatus() != null)) {
			message = resp.getStatus().getValue();
		}
		
		if (message == null) {
			message = "ERR#".concat(String.valueOf(errorCode));
		}
		
		if ((localizer != null) && (component != null)) {
			String key = "error.".concat(String.valueOf(errorCode));
			
			String localized = localizer.getString(key, component);
			if (!key.equals(localized) && !StringUtils.isBlank(localized)) {
				message = localized;
			}
		}
		
		return message;
	}
	
	public static String errorMessage(int errorCode, String message, Component component) {
		return errorMessage(errorCode, message, component == null ? null : component.getLocalizer(), component);
	}
	
	public static String errorMessage(int errorCode, String message, Localizer localizer, Component component) {
		if (!org.springframework.util.StringUtils.hasText(message)) {
			message = "ERR#".concat(String.valueOf(errorCode));
		}
		
		if ((localizer != null) && (component != null)) {
			String key = "error.".concat(String.valueOf(errorCode));
			
			String localized = localizer.getString(key, component);
			if (!key.equals(localized) && !StringUtils.isBlank(localized)) {
				message = localized;
			}
		}
		
		return message;
	}
	
	public static String errorMessage(String errorCode, String message, Localizer localizer, Component component) {
		if (!org.springframework.util.StringUtils.hasText(message)) {
			message = "ERR#".concat(errorCode);
		}
		
		if ((localizer != null) && (component != null)) {
			String key = "error.".concat(errorCode);
			
			String localized = localizer.getString(key, component);
			if (!key.equals(localized) && !StringUtils.isBlank(localized)) {
				message = localized;
			}
		}
		
		return message;
	}
	
	public static <T extends MobiliserRequestType> T fill(T request, BtpnMobiliserBasePage page) {
		request.setCallback(null);
		request.setConversationId(UUID.randomUUID().toString());
		request.setOrigin("mobiliser-web");
		request.setRepeat(Boolean.FALSE);
		request.setTraceNo(UUID.randomUUID().toString());
		
		ClientInfo clientInfo = page.getMobiliserWebSession().getClientInfo();
		
		if (clientInfo instanceof WebClientInfo) {
			WebClientInfo webClientInfo = (WebClientInfo) clientInfo;

			request.setAuditData(new AuditDataType());
			request.getAuditData().setDevice(StringUtils.substring(webClientInfo.getUserAgent(), 0, 80));
			request.getAuditData().setOtherDeviceId(webClientInfo.getProperties().getRemoteAddress());
		}

		Customer cust = page.getMobiliserWebSession().getBtpnLoggedInCustomer();
		if (cust != null) {
			request.setSessionId(cust.getSessionId());
		}
		
		return request;
	}

	public static String getValue(String lookupName, String key, ILookupMapUtility lookupMapUtility, Component component) {
		if (key == null) {
			return null;
		}
		
		Map<String, String> entries = lookupMapUtility.getLookupEntriesMap(lookupName, component.getLocalizer(), component);
		if ((entries == null) || entries.isEmpty()) {
			return null;
		}
		
		if (entries.containsKey(key)) {
			return (String) entries.get(key);
		}
		
		for (Map.Entry<String, String> e : entries.entrySet()) {
			if (key.equalsIgnoreCase(e.getKey())) {
				return e.getValue();
			}
		}
		
		return null;
	}
	
	public static CodeValue getCodeValue(String lookupName, Object key, ILookupMapUtility lookupMapUtility, Component component) {
		return key == null ? null : getCodeValue(lookupName, key.toString(), lookupMapUtility, component);
	}

	public static CodeValue getCodeValue(String lookupName, String key, ILookupMapUtility lookupMapUtility, Component component) {
		return key == null ? null : new CodeValue(key, getValue(lookupName, key, lookupMapUtility, component));
	}

	public static CodeValue getCodeValue(Boolean key) {
		if (key == null) {
			return null;
		}
		
		if (key.booleanValue()) {
			return new CodeValue(BtpnConstants.YES_ID, BtpnConstants.YES_VALUE);
		}
		
		return new CodeValue(BtpnConstants.NO_ID, BtpnConstants.NO_VALUE);
	}
}
