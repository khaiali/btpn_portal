package com.sybase365.mobiliser.web.cst.pages.customercare;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.panels.LimitClassPanel;

public class IndividualLimitSetConfig extends CustomerCareMenuGroup {

    private LimitClassPanel limitClassPanel;

    private CustomerBean customer;

    public IndividualLimitSetConfig() {
	super();
	this.customer = getMobiliserWebSession().getCustomer();
	initPageComponents();
    }

    public IndividualLimitSetConfig(CustomerBean customer) {
	super();
	this.customer = customer;
	initPageComponents();
    }

    protected void initPageComponents() {

	limitClassPanel = new LimitClassPanel("limitClassPanel", this, customer);
	add(limitClassPanel);
    }

}
