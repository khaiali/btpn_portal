package com.sybase365.mobiliser.web.common.model;

import javax.xml.datatype.XMLGregorianCalendar;

import com.sybase365.mobiliser.money.contract.v5_0.customer.FindCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindHierarchicalCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindPendingCustomersRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.AddressFindBean;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.IdentificationFindBean;
import com.sybase365.mobiliser.web.beans.CustomerBean;

public interface ICustomerFinder {
    FindHierarchicalCustomerRequest createFindHierarchicalAgentRequest(
	    IdentificationFindBean id, AddressFindBean address);

    FindCustomerRequest createFindAgentRequest(IdentificationFindBean id,
	    AddressFindBean address);

    void loadAgentDetails(CustomerBean agent);

    FindPendingCustomersRequest createFindPendingCustomerRequest(
	    Integer custTypeId, String userName, XMLGregorianCalendar fromDate,
	    XMLGregorianCalendar toDate, IdentificationFindBean id,
	    AddressFindBean address);

}
