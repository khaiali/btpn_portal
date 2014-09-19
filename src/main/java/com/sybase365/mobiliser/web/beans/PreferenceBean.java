package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

public class PreferenceBean implements Comparable<PreferenceBean>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7403960897780699441L;
    private String key;
    private String value;
    private String scheme;
    private String passphrase;
    private String type;
    private String description;

    public PreferenceBean() {

    }

    public PreferenceBean(String key, String value, String type,
	    String description) {
	this.key = key;
	this.value = value;
    }

    public String getKey() {
	return this.key;
    }

    public void setKey(String key) {
	this.key = key;
    }

    public String getValue() {
	return this.value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public String getScheme() {
	return this.scheme;
    }

    public void setScheme(String scheme) {
	this.scheme = scheme;
    }

    public String getPassphrase() {
	return this.passphrase;
    }

    public void setPassphrase(String passphrase) {
	this.passphrase = passphrase;
    }

    public String getType() {
	return this.type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getDescription() {
	return this.description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return this.key + ":" + this.value + ":" + this.type + ":"
		+ this.description;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if ((obj == null) || (obj.getClass() != this.getClass())) {
	    return false;
	}
	// object must be Test at this point
	PreferenceBean test = (PreferenceBean) obj;
	return (this.key.equals(test.key));
    }

    @Override
    public int hashCode() {
	return this.key != null ? this.key.hashCode() : 42;
    }

    @Override
    public int compareTo(PreferenceBean o) {
	return this.getKey().compareTo(o.getKey());
    }
}
