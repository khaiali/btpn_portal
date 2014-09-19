package com.sybase365.mobiliser.web.cst.pages.usermanager;

import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.customer.FindCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindHierarchicalCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindPendingCustomersRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.AddressFindBean;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.IdentificationFindBean;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.model.ICustomerFinder;
import com.sybase365.mobiliser.web.common.panels.FindCustomerPanel;
import com.sybase365.mobiliser.web.util.Constants;

public class FindAgentPage extends BaseUserManagerPage implements
	ICustomerFinder {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(FindAgentPage.class);

    private FindCustomerPanel panel;

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	panel = new FindCustomerPanel("findcustomerpanel", this, this, false,
		Constants.SEARCH_TYPE_AGENT);
	add(panel);

    }

    @Override
    public FindCustomerRequest createFindAgentRequest(
	    IdentificationFindBean id, AddressFindBean address) {
	FindCustomerRequest request = null;
	try {
	    request = getNewMobiliserRequest(FindCustomerRequest.class);
	    request.setIdentification(id);
	    request.setAddress(address);

	} catch (Exception e) {
	    LOG.warn("An error occurred in find agent.", e);
	    error(getLocalizer().getString("customer.find.error", this));
	    return null;
	}
	return request;
    }

    @Override
    public void loadAgentDetails(CustomerBean agent) {
	getMobiliserWebSession().setCustomer(agent);
	setResponsePage(EditAgentPage.class);
    }

    @Override
    public FindHierarchicalCustomerRequest createFindHierarchicalAgentRequest(
	    IdentificationFindBean id, AddressFindBean address) {
	// nothing to do here
	return null;
    }

    @Override
    public FindPendingCustomersRequest createFindPendingCustomerRequest(
	    Integer custTypeId, String userName, XMLGregorianCalendar fromDate,
	    XMLGregorianCalendar toDate, IdentificationFindBean id,
	    AddressFindBean address) {
	return null;
    }
}
