package com.sybase365.mobiliser.web.cst.pages.customercare;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.customer.FindCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindHierarchicalCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindPendingCustomersRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.AddressFindBean;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.IdentificationFindBean;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.FeeSet;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSet;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSetClass;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.model.ICustomerFinder;
import com.sybase365.mobiliser.web.common.panels.FindCustomerPanel;
import com.sybase365.mobiliser.web.cst.pages.usermanager.EditAgentPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_CST_LOGIN)
public class ApproveCustomersPage extends BaseCustomerCarePage implements
	ICustomerFinder {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(ApproveCustomersPage.class);

    private FindCustomerPanel panel;

    public ApproveCustomersPage() {
	super();
	LOG.info("Created new ApproveCustomersPage");
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	panel = new FindCustomerPanel("findcustomerpanel", this, this, false,
		null);
	add(panel);

    }

    @Override
    public FindPendingCustomersRequest createFindPendingCustomerRequest(
	    Integer custTypeId, String userName, XMLGregorianCalendar fromDate,
	    XMLGregorianCalendar toDate, IdentificationFindBean id,
	    AddressFindBean address) {
	FindPendingCustomersRequest request = null;
	try {
	    request = getNewMobiliserRequest(FindPendingCustomersRequest.class);
	    request.setCustomerTypeId(custTypeId);
	    if (PortalUtils.exists(address)) {
		request.setFirstName(address.getFirstName());
		request.setLastName(address.getLastName());
	    }
	    request.setFromDate(fromDate);
	    request.setToDate(toDate);

	    if (PortalUtils.exists(id))
		request.setMsisdn(id.getIdentification());
	    request.setUserName(userName);
	} catch (Exception e) {
	    LOG.error("An error occurred in find pending customers", e);
	    error(getLocalizer().getString("pending.customer.find.error", this));
	    return null;
	}
	return request;
    }

    @Override
    public FindCustomerRequest createFindAgentRequest(
	    IdentificationFindBean id, AddressFindBean address) {
	return null;
    }

    @Override
    public void loadAgentDetails(CustomerBean agent) {

	if (agent.getFeeSetId().longValue() != 0)
	    setIndividualFeeSetFlag(agent);
	if (PortalUtils.exists(agent.getLimitId())
		&& agent.getLimitId().longValue() != 0) {
	    agent = setIndividualLimitSetFlag(agent);
	} else {
	    agent.setIsIndividualLimitSet(Boolean.FALSE);
	}

	// DPP handling
	try {
	    List<LimitSetClass> limits = getLimitSetClassList(agent.getId(),
		    Integer.valueOf(Constants.LIMIT_CUSTOMER_TYPE));
	    if (PortalUtils.exists(limits)) {
		if (PortalUtils.exists(limits)) {
		    agent.setLimitId(limits.get(0).getId());
		    agent.setLimitClass(limits.get(0).getLimitClass());
		}
	    }
	} catch (Exception e) {
	    LOG.error("Error in getLimitSet of loadAgentDetails", e);
	}
	agent.setPendingApproval(true);
	getMobiliserWebSession().setCustomer(agent);

	if (agent.getCustomerTypeId().intValue() != Constants.IDTYPE_AGENT)
	    setResponsePage(new StandingDataPage(agent));
	else
	    setResponsePage(new EditAgentPage(agent));
    }

    private CustomerBean setIndividualLimitSetFlag(CustomerBean agent) {
	List<LimitSet> limitSetList = null;
	try {
	    limitSetList = findLimitSet(null);
	} catch (Exception e) {
	    LOG.error("# An error occurred while fetching limit sets" + e);
	}
	Boolean isIndividualLimitSet = Boolean.TRUE;

	if (PortalUtils.exists(agent.getLimitId()) && agent.getLimitId() != 0) {
	    for (LimitSet limitSet : limitSetList) {
		if (limitSet.getId().longValue() == agent.getLimitId()
			.longValue()) {
		    isIndividualLimitSet = Boolean.FALSE;
		}
	    }
	} else {
	    isIndividualLimitSet = Boolean.FALSE;
	}

	agent.setIsIndividualLimitSet(isIndividualLimitSet);
	return agent;
    }

    @Override
    public FindHierarchicalCustomerRequest createFindHierarchicalAgentRequest(
	    IdentificationFindBean id, AddressFindBean address) {
	// nothing to do here
	return null;
    }

    private void setIndividualFeeSetFlag(CustomerBean customer) {
	List<FeeSet> feeSetList = null;
	try {
	    feeSetList = getFeeSetsList(Boolean.FALSE);

	    Boolean isIndividualFeeSet = Boolean.TRUE;
	    for (FeeSet feeSet : feeSetList) {
		if (feeSet.getId().longValue() == customer.getFeeSetId()
			.longValue()) {
		    isIndividualFeeSet = Boolean.FALSE;
		}
	    }
	    customer.setIsIndividualFeeSet(isIndividualFeeSet);
	} catch (DataProviderLoadException dpe) {
	    LOG.error("#An error occurred while fetching fee sets");
	}
    }

}
