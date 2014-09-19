package com.sybase365.mobiliser.web.demomerchant.util;

import java.io.Serializable;

public class ProductItem implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private long cost;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public long getCost() {
	return cost;
    }

    public void setCost(long cost) {
	this.cost = cost;
    }
}
