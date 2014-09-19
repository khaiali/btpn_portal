package com.sybase365.mobiliser.web.checkout.exceptions;

import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType.Status;

public class ServicesException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = 560534565366291483L;
    private Status status;

    public ServicesException(String msg, Status status) {
	super(msg);
	this.status = status;
    }

    public Status getStatus() {
	return status;
    }

    public void setStatus(Status status) {
	this.status = status;
    }

}
