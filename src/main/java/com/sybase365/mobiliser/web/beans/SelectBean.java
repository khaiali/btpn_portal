package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

public class SelectBean implements Comparable<SelectBean>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7882063350288047377L;
    private String id;
    private String name;

    public SelectBean() {

    }

    public SelectBean(String id, String name) {
	this.id = id;
	this.name = name;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if ((obj == null) || (obj.getClass() != this.getClass())) {
	    return false;
	}
	// object must be Test at this point
	SelectBean test = (SelectBean) obj;
	return (this.id.equals(test.id));

    }

    @Override
    public int hashCode() {
	return this.id != null ? this.id.hashCode() : 42;
    }

    public int compareTo(SelectBean o) {
	return name.compareTo(o.name);
    }

    public int compareToId(SelectBean o) {
	return id.compareTo(o.id);
    }

    @Override
    public String toString() {
	return "SelectBean [id=" + id + ", name=" + name + "]";
    }

}
