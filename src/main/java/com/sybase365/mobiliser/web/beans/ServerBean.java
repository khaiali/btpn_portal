package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

public class ServerBean implements Comparable<ServerBean>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1373865912248347095L;
    private String hostname;
    private int port;
    private boolean pingOk;

    public ServerBean() {

    }

    public ServerBean(String hostname, int port) {
	this.hostname = hostname;
	this.port = port;
    }

    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public boolean pingOk() {
	return pingOk;
    }

    public void setPingOk(boolean status) {
	this.pingOk = status;
    }

    @Override
    public String toString() {
	return this.hostname + ":" + this.port;
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
	ServerBean test = (ServerBean) obj;
	return (this.hostname.equals(test.hostname));
    }

    @Override
    public int hashCode() {
	return this.hostname != null ? this.hostname.hashCode() : 42;
    }

    @Override
    public int compareTo(ServerBean o) {
	return this.toString().compareTo(o.toString());
    }
}
