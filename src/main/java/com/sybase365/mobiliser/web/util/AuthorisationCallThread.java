package com.sybase365.mobiliser.web.util;

import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType.Status;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.PreAuthorisationContinue;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.PreAuthorisationContinueResponse;
import com.sybase365.mobiliser.money.services.api.IPreAuthContinueEndpoint;

public class AuthorisationCallThread extends Thread implements Runnable {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AuthorisationCallThread.class);

    // private MobiliserServiceBean mobiliserConnection;
    private PreAuthorisationContinue request;

    private boolean active;
    PreAuthorisationContinueResponse response;
    private Status status;
    private Long txnId;
    private String authCode;
    private IPreAuthContinueEndpoint wsPreAuthContinueClient;

    public AuthorisationCallThread(PreAuthorisationContinue request,
	    IPreAuthContinueEndpoint wsPreAuthContinueClient) {
	super();
	this.active = true;
	this.request = request;
	this.wsPreAuthContinueClient = wsPreAuthContinueClient;
    }

    @Override
    public void run() {
	// TODO: find a way to remove this nasty thread local cludge

	boolean forceSelfAuth = SmartClientInterceptor
		.isSelfAuthenticationForced();

	if (!forceSelfAuth) {
	    SmartClientInterceptor.forceSelfAuthentication();
	}

	LOG.info("# AuthorisationCallThread.run()");
	try {
	    response = wsPreAuthContinueClient
		    .preAuthorisationContinue(request);

	    status = response.getStatus();
	    txnId = response.getTransaction().getSystemId();
	    authCode = response.getTransaction().getValue();
	    active = false;
	} catch (Exception e) {
	    LOG.error("Error in calling Pre-Authorisation Continue request", e);
	    status = new Status();
	    status.setCode(9999);
	    status.setValue(e.getMessage());
	    active = false;
	} finally {
	    if (!forceSelfAuth) {
		SmartClientInterceptor.unsetForcedSelfAuthentication();
	    }
	}
    }

    public PreAuthorisationContinueResponse getResponse() {
	return response;
    }

    public void setResponse(PreAuthorisationContinueResponse response) {
	this.response = response;
    }

    public boolean isActive() {
	return active;
    }

    public void setActive(boolean active) {
	this.active = active;
    }

    public Status getStatus() {
	return status;
    }

    public void setStatus(Status status) {
	this.status = status;
    }

    public Long getTxnId() {
	return txnId;
    }

    public void setTxnId(Long txnId) {
	this.txnId = txnId;
    }

    public String getAuthCode() {
	return authCode;
    }

    public void setAuthCode(String authCode) {
	this.authCode = authCode;
    }

}
