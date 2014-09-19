package com.sybase365.mobiliser.web.checkout.pages;

import java.io.Serializable;

public class AjaxResponse implements Serializable {

    private boolean cancelVisible;
    private boolean retryVisible;
    private boolean ajaxVisible;
    private boolean redirect;

    public static final int OK = 0;
    public static final int USER_EMPTY = 1;
    public static final int USER_UNKNOWN = 2;
    public static final int OTP_MISSING = 3;

    private int status;
    private String errorMessage;
    private String returnUrl;
    private int retry;

    public AjaxResponse(int status) {
	this.status = status;
    }

    public AjaxResponse(int status, String errorMessage) {
	this.status = status;
	this.errorMessage = errorMessage;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

    public String getReturnUrl() {
	return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
	this.returnUrl = returnUrl;
    }

    public int getRetry() {
	return retry;
    }

    public void setRetry(int retry) {
	this.retry = retry;
    }

    public boolean isCancelVisible() {
	return cancelVisible;
    }

    public void setCancelVisible(boolean cancelVisible) {
	this.cancelVisible = cancelVisible;
    }

    public boolean isRetryVisible() {
	return retryVisible;
    }

    public void setRetryVisible(boolean retryVisible) {
	this.retryVisible = retryVisible;
    }

    public boolean isAjaxVisible() {
	return ajaxVisible;
    }

    public void setAjaxVisible(boolean ajaxVisible) {
	this.ajaxVisible = ajaxVisible;
    }

    public boolean isRedirect() {
	return redirect;
    }

    public void setRedirect(boolean redirect) {
	this.redirect = redirect;
    }

}
