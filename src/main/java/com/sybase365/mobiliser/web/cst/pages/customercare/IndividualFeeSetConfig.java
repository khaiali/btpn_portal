package com.sybase365.mobiliser.web.cst.pages.customercare;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.panels.FeeSetPanel;

public class IndividualFeeSetConfig extends CustomerCareMenuGroup {

    private FeeSetPanel feeSetPanel;

    private CustomerBean customer;

    public IndividualFeeSetConfig() {
	super();
	this.customer = getMobiliserWebSession().getCustomer();
	initPageComponents();
	buildLeftMenu();
    }

    public IndividualFeeSetConfig(CustomerBean customer) {
	super();
	this.customer = customer;
	initPageComponents();
    }

    protected void initPageComponents() {

	feeSetPanel = new FeeSetPanel("feeSetPanel", this, this.customer);
	add(feeSetPanel);
    }

    @Override
    protected Class getActiveMenu() {
	return IndividualFeeSetConfig.class;
    }

}
