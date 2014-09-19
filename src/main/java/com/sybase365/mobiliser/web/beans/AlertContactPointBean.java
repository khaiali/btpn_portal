package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerOtherIdentification;

/**
 * @author msw
 */

public class AlertContactPointBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Identification primaryIdent;
    private CustomerOtherIdentification otherIdent;

    public long getId() {
	if (primaryIdent != null) {
	    return primaryIdent.getId();
	} else {
	    return otherIdent.getId();
	}
    }

    public String getNickname() {
	if (primaryIdent != null) {
	    return "";
	} else {
	    return otherIdent.getNickname();
	}
    }

    public String getIdentification() {
	if (primaryIdent != null) {
	    return primaryIdent.getIdentification();
	} else {
	    return otherIdent.getIdentification();
	}
    }

    public void setPrimaryIdentification(Identification value) {
	this.primaryIdent = value;
    }

    public Identification getPrimaryIdentification() {
	return this.primaryIdent;
    }

    public void setOtherIdentification(CustomerOtherIdentification value) {
	this.otherIdent = value;
    }

    public CustomerOtherIdentification getOtherIdentification() {
	return this.otherIdent;
    }

}