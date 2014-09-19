package com.sybase365.mobiliser.web.checkout.exceptions;

public class TransactionNotFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TransactionNotFoundException() {
	super();
    }

    public TransactionNotFoundException(String msg) {
	super(msg);
    }

    public TransactionNotFoundException(String msg, Exception ex) {
	super(msg, ex);
    }

}
