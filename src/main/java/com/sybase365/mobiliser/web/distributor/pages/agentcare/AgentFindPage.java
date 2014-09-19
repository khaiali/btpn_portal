package com.sybase365.mobiliser.web.distributor.pages.agentcare;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.money.contract.v5_0.customer.FindCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindHierarchicalCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindPendingCustomersRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.AddressFindBean;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.IdentificationFindBean;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSetClass;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.model.ICustomerFinder;
import com.sybase365.mobiliser.web.common.panels.FindCustomerPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_AGENTS)
public class AgentFindPage extends BaseAgentCarePage implements ICustomerFinder {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AgentFindPage.class);

    FindCustomerPanel panel;

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();
	panel = new FindCustomerPanel("findcustomerpanel", this, this, true,
		Constants.SEARCH_TYPE_AGENT);
	add(panel);
    }

    public FindHierarchicalCustomerRequest createFindHierarchicalAgentRequest(
	    IdentificationFindBean id, AddressFindBean address) {
	FindHierarchicalCustomerRequest request = null;
	try {
	    request = getNewMobiliserRequest(FindHierarchicalCustomerRequest.class);
	    request.setIdentification(id);
	    request.setAddress(address);

	} catch (Exception e) {
	    LOG.error("# An error occurred in find agent.", e);
	    error(getLocalizer().getString("agent.find.error", this));
	    return null;
	}
	return request;
    }

    public void loadAgentDetails(CustomerBean agent) {
	try {
	    List<LimitSetClass> limits = getLimitSetClassList(agent.getId(),
		    Constants.LIMIT_CUSTOMER_TYPE);
	    if (PortalUtils.exists(limits)) {
		if (PortalUtils.exists(limits)) {
		    agent.setLimitId(limits.get(0).getId());
		    agent.setLimitClass(limits.get(0).getLimitClass());
		}
	    }
	} catch (Exception e) {
	    LOG.error("Error in getLimitSet of loadAgentDetails", e);
	}

	getMobiliserWebSession().setCustomer(agent);

	setResponsePage(new AgentEditPage("edit"));
    }

    @Override
    public FindCustomerRequest createFindAgentRequest(
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
