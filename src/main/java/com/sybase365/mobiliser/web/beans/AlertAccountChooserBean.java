package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

/**
 * @author sagraw03 This bean hold the paymentInstrumentId selected from alert
 *         account.
 */

public class AlertAccountChooserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String paymentInstrumentId;

    public void setPaymentInstrumentId(String paymentInstrumentId) {
	this.paymentInstrumentId = paymentInstrumentId;
    }

    public String getPaymentInstrumentId() {
	return paymentInstrumentId;
    }

}
