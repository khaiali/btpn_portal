package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.panels.RegisterCustomerPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CUST_WRITE)
public class CustomerRegistration extends BaseCustomerCarePage {

    private static final long serialVersionUID = 1L;
    CustomerBean customer;

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	customer = new CustomerBean();
	add(new RegisterCustomerPanel("registerCustomerPanel", this,
		this.customer));

    }
}
