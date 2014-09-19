package com.sybase365.mobiliser.web.application.clients;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.ClientInfo;
import org.apache.wicket.resource.loader.IStringResourceLoader;

import com.sybase365.mobiliser.framework.contract.v5_0.base.AuditDataType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserRequestType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.LookupResourceLoader;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.application.MobiliserApplication;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * Base superclass for all Mobiliser WS client logic handlers
 * <p>
 * &copy; 2012 by Sybase, Inc.
 * </p>
 * 
 * @author <a href='mailto:msw@sybase.com'>Mark White</a>
 */
public class BaseClientLogic {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BaseClientLogic.class);

    // set silently on request invocation
    private MobiliserWebSession session;

    public void setMobiliserWebSession(MobiliserWebSession value) {
	this.session = value;
    }

    protected MobiliserWebSession getMobiliserWebSession() {
	return this.session;
    }

    // set automatically
    @Resource(name = "mobiliserApplication")
    private MobiliserApplication application;

    public void setMobiliserApplication(MobiliserApplication value) {
	this.application = value;
    }

    protected MobiliserApplication getMobiliserApplication() {
	return this.application;
    }

    /**
     * Get and prepare a general Mobiliser WS request object. Standard fields
     * are added to the request to identify the web session, device info,
     * conversation id and trace number.
     * 
     * @param requestClass
     *            The request class as subclass of MobiliserRequestType
     * @return The prepared request class instance
     * @throws Exception
     */
    protected <Req extends MobiliserRequestType> Req getNewMobiliserRequest(
	    Class<Req> requestClass) throws Exception {

	if (!Session.exists()) {
	    throw new IllegalArgumentException(
		    "Mobiliser WS Requests need a web session");
	} else {
	    setMobiliserWebSession((MobiliserWebSession) Session.get());
	}

	Req req = requestClass.newInstance();
	req.setCallback(null);
	req.setConversationId(UUID.randomUUID().toString());
	req.setOrigin("mobiliser-web");
	req.setRepeat(Boolean.FALSE);
	req.setTraceNo(UUID.randomUUID().toString());

	final ClientInfo clientInfo = getMobiliserWebSession().getClientInfo();

	if (clientInfo instanceof WebClientInfo) {
	    final WebClientInfo webClientInfo = (WebClientInfo) clientInfo;

	    req.setAuditData(new AuditDataType());
	    req.getAuditData().setDevice(
		    StringUtils.substring(webClientInfo.getUserAgent(), 0, 80));
	    req.getAuditData().setOtherDeviceId(
		    webClientInfo.getProperties().getRemoteAddress());
	}

	final Customer cust = getMobiliserWebSession().getLoggedInCustomer();

	if (cust != null) {
	    req.setSessionId(cust.getSessionId());
	}

	prepareMobiliserRequest(req);

	return req;
    }

    /**
     * Additional preparation of the Mobiliser WS Request for subclass
     * overriding.
     * 
     * @param req
     *            The Mobiliser request instance
     */
    protected void prepareMobiliserRequest(MobiliserRequestType req) {

    }

    /**
     * Evaluate the response object and if it contain a closed or expired
     * session, invalidate the associated web session.
     * <p>
     * If the response maps to an error code, use local lookup facility to get a
     * standard error message for this code.
     * 
     * @param response
     * @return
     */
    protected <Resp extends MobiliserResponseType> boolean evaluateMobiliserResponse(
	    Resp response) {

	LOG.debug("# Response returned status code: '{}' value: '{}'", response
		.getStatus().getCode(), response.getStatus().getValue());

	if (response.getStatus().getCode() == 0) {
	    return true;
	}

	// check for mobiliser session closed or expired
	if (response.getStatus().getCode() == 352
		|| response.getStatus().getCode() == 353) {

	    LOG.debug("# Mobiliser session closed/expired, redirect to sign in page");

	    // if mobiliser session gone, then can't continue with web session
	    // so invalidate session and redirect to home, which will go to
	    getMobiliserWebSession().invalidate();
	}

	String errorMessage = loadResourceString(LookupResourceLoader.LOOKUP_INDICATOR
		+ Constants.RESOURCE_BUNDLE_ERROR_CODES
		+ "."
		+ String.valueOf(response.getStatus().getCode()));

	if (PortalUtils.exists(errorMessage)) {
	    getMobiliserWebSession().error(errorMessage);
	} else {
	    getMobiliserWebSession().error(
		    loadResourceString("portal.genericError"));
	}

	return false;
    }

    /**
     * 
     * @param key
     * @return
     */
    protected String loadResourceString(String key) {
	String value = null;

	List<IStringResourceLoader> resourceLoaderList = getMobiliserApplication()
		.getResourceSettings().getStringResourceLoaders();

	for (IStringResourceLoader resourceLoader : resourceLoaderList) {
	    value = resourceLoader.loadStringResource(MobiliserBasePage.class,
		    key, getMobiliserWebSession().getLocale(), null);
	    if (value != null) {
		return value;
	    }
	}

	LOG.warn("Couldn't load resource string for key '{}'", key);

	return value;
    }
}
