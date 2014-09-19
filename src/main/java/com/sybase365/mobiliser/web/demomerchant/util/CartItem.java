package com.sybase365.mobiliser.web.demomerchant.util;

import java.io.Serializable;

public class CartItem implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private ProductItem product;
    private int count;

    public int getCount() {
	return count;
    }

    public void setCount(int count) {
	this.count = count;
    }

    public ProductItem getProduct() {
	return product;
    }

    public void setProduct(ProductItem product) {
	this.product = product;
    }

}
