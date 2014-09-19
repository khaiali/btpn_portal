package com.sybase365.mobiliser.web.common.dataproviders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.customer.FindCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindHierarchicalCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindPendingCustomersRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindPendingCustomersResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetPendingcustomerDetailsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetPendingcustomerDetailsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Attachment;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerAttachedAttachment;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerAttachedCredential;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerAttachedIdentification;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.PendingCustomer;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CustomerBeanDataProvider extends
	SortableDataProvider<CustomerBean> {

    private transient List<CustomerBean> agentListEntries = new ArrayList<CustomerBean>();
    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(CustomerBeanDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public CustomerBeanDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	this(defaultSortProperty, mobBasePage, true);
    }

    public CustomerBeanDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, boolean ascending) {
	setSort(defaultSortProperty, ascending);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends CustomerBean> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public IModel<CustomerBean> model(final CustomerBean object) {
	IModel<CustomerBean> model = new LoadableDetachableModel<CustomerBean>() {
	    @Override
	    protected CustomerBean load() {
		CustomerBean set = null;
		for (CustomerBean obj : agentListEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<CustomerBean>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (agentListEntries == null) {
	    return count;
	}

	return agentListEntries.size();
    }

    protected List<CustomerBean> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<CustomerBean> sublist = getIndex(agentListEntries, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<CustomerBean> getIndex(List<CustomerBean> agentListEntries,
	    String prop, boolean asc) {

	if (prop.equals("Lid")) {
	    return sort(agentListEntries, asc);
	} else {
	    return agentListEntries;
	}
    }

    private List<CustomerBean> sort(List<CustomerBean> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<CustomerBean>() {

		@Override
		public int compare(CustomerBean arg0, CustomerBean arg1) {
		    if (arg0.getId().longValue() == arg1.getId().longValue()) {
			return 0;
		    } else if (arg0.getId().longValue() < arg1.getId()
			    .longValue()) {
			return -1;
		    } else
			return 1;
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<CustomerBean>() {

		@Override
		public int compare(CustomerBean arg0, CustomerBean arg1) {
		    if (arg1.getId().longValue() == arg0.getId().longValue()) {
			return 0;
		    } else if (arg1.getId().longValue() < arg0.getId()
			    .longValue()) {
			return -1;
		    } else
			return 1;
		}
	    });
	}
	return entries;
    }

    public List<CustomerBean> findCustomer(
	    FindHierarchicalCustomerRequest request, boolean forcedReload)
	    throws DataProviderLoadException {
	if (!PortalUtils.exists(agentListEntries) || forcedReload) {
	    try {
		agentListEntries = getMobiliserBasePage().findCustomer(request);
		for (CustomerBean cb : agentListEntries) {

		    Address cAddress = getMobiliserBasePage()
			    .getCustomerAddress(cb.getId());
		    cb.setAddress(Converter.getInstance()
			    .getAddressBeanFromAddress(cAddress));

		    Identification ident;
		    ident = getMobiliserBasePage().getCustomerIdentification(
			    cb.getId(), Constants.IDENT_TYPE_USERNAME);
		    if (PortalUtils.exists(ident))
			cb.setUserName(ident.getIdentification());

		    ident = getMobiliserBasePage().getCustomerIdentification(
			    cb.getId(), Constants.IDENT_TYPE_MSISDN);
		    if (PortalUtils.exists(ident))
			cb.setMsisdn(ident.getIdentification());
		}
	    } catch (Exception e) {
		LOG.error("# Error finding agents", e);
	    }
	}
	return agentListEntries;
    }

    public void findPendingCustomers(FindPendingCustomersRequest request,
	    boolean forcedReload, String customerType)
	    throws DataProviderLoadException {
	if (!PortalUtils.exists(agentListEntries) || forcedReload) {

	    try {
		FindPendingCustomersResponse response = getMobiliserBasePage().wsCustomerClient
			.findPendingCustomers(request);

		if (response.getStatus().getCode() == Constants.NO_APPROVAL_CONFIG_FOUND) {
		    LOG
			    .warn("# Approval configuration is missing for cutomer type #"
				    + request.getCustomerTypeId());
		    getMobiliserBasePage().error(
			    getMobiliserBasePage().getLocalizer().getString(
				    "customer.approval.config.missing.error",
				    getMobiliserBasePage()));
		    return;
		}

		if (!getMobiliserBasePage().evaluateMobiliserResponse(response)) {
		    LOG
			    .warn("# An error occurred while loading pending customers");
		    return;
		}

		CustomerBean customer = new CustomerBean();
		List<PendingCustomer> pendingCustomersList = response
			.getCustomers();

		for (PendingCustomer cb : pendingCustomersList) {
		    customer.setTaskId(cb.getTaskId());

		    customer = getCustomerDetails(customer);

		    agentListEntries.add(customer);

		}

	    } catch (Exception e) {
		LOG.error(
			"# An error occurred while loading pending customers",
			e);
		throw new DataProviderLoadException();
	    }
	}

    }

    public CustomerBean getCustomerDetails(CustomerBean customer)
	    throws DataProviderLoadException {
	try {
	    GetPendingcustomerDetailsRequest request = getMobiliserBasePage()
		    .getNewMobiliserRequest(
			    GetPendingcustomerDetailsRequest.class);
	    request.setTaskId(customer.getTaskId());

	    GetPendingcustomerDetailsResponse response = getMobiliserBasePage().wsCustomerClient
		    .getPendingcustomerDetails(request);

	    customer = Converter.getInstance().getCustomerBeanFromCustomer(
		    response.getCustomer());

	    customer.setTaskId(request.getTaskId());
	    customer.setId(Long.valueOf(request.getTaskId()));

	    if (PortalUtils.exists(response.getAddresses())) {
		Address address = response.getAddresses().get(0);
		customer.setAddress(Converter.getInstance()
			.getAddressBeanFromAddress(address));

	    }

	    if (PortalUtils.exists(response.getCredentials())) {
		for (CustomerAttachedCredential credential : response
			.getCredentials()) {
		    if (credential.getType() == Constants.CREDENTIAL_TYPE_PIN) {
			customer.setPin(credential.getCredential());
		    } else
			customer.setPassword(credential.getCredential());
		}
	    }

	    if (PortalUtils.exists(response.getIdentifications())) {
		for (CustomerAttachedIdentification identification : response
			.getIdentifications()) {
		    if (identification.getType() == Constants.IDENT_TYPE_USERNAME) {
			customer
				.setUserName(identification.getIdentification());
		    } else
			customer.setMsisdn(identification.getIdentification());
		}
	    }

	    if (PortalUtils.exists(response.getAttachments())) {

		List<Attachment> attachmentList = new ArrayList<Attachment>();
		Attachment attachment;
		for (CustomerAttachedAttachment atmt : response
			.getAttachments()) {
		    attachment = new Attachment();
		    attachment.setAttachmentType(atmt.getAttachmentType());
		    attachment.setContent(atmt.getContent());
		    attachment.setContentType(atmt.getContentType());
		    attachment.setCustomerId(customer.getId());
		    attachment.setName(atmt.getName());
		    attachment.setStatus(atmt.getStatus());
		    attachmentList.add(attachment);
		}
		customer.setAttachmentList(attachmentList);
	    }

	    if (!getMobiliserBasePage().evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while loading pending customer details");
		return null;
	    }

	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while loading pending customers' det"
			    + "ails", e);
	    throw new DataProviderLoadException();
	}

	return customer;

    }

    public List<CustomerBean> findCustomer(FindCustomerRequest request,
	    boolean forcedReload, String type) throws DataProviderLoadException {
	if (!PortalUtils.exists(agentListEntries) || forcedReload) {

	    try {
		List<CustomerBean> customers = getMobiliserBasePage()
			.findCustomer(request);

		agentListEntries = new ArrayList<CustomerBean>();

		for (CustomerBean cb : customers) {
		    // exclude customer of type 0-agents and 12-Administrator in
		    // cst
		    // customer care
		    if ((Constants.SEARCH_TYPE_CUSTOMER.equals(type)
			    && cb.getCustomerTypeId() != Constants.IDTYPE_AGENT && cb
			    .getCustomerTypeId() != Constants.IDTYPE_ADMINISTRATOR)
			    ||
			    // include only customer of type 0-agents and
			    // 12-Administrator
			    // in cst user manager
			    ((Constants.SEARCH_TYPE_AGENT.equals(type) && (cb
				    .getCustomerTypeId() == Constants.IDTYPE_AGENT || cb
				    .getCustomerTypeId() == Constants.IDTYPE_ADMINISTRATOR)))
			    ||
			    // include only customer of type 8-merchant agents
			    // and 12-Administrator
			    // in cst reports agent search
			    ((Constants.SEARCH_TYPE_MERCHANT_AGENT.equals(type) && (cb
				    .getCustomerTypeId() == Constants.IDTYPE_MERCHANT_AGENT || cb
				    .getCustomerTypeId() == Constants.IDTYPE_ADMINISTRATOR)))
			    ||
			    // include only customer of type 11-merchant dealers
			    // and 12-Administrator
			    // in cst reports dealer search
			    ((Constants.SEARCH_TYPE_MERCHANT_DEALER
				    .equals(type) && (cb.getCustomerTypeId() == Constants.IDTYPE_MERCHANT_DEALER || cb
				    .getCustomerTypeId() == Constants.IDTYPE_ADMINISTRATOR)))) {
			Address cAddress = getMobiliserBasePage()
				.getCustomerAddress(cb.getId());
			cb.setAddress(Converter.getInstance()
				.getAddressBeanFromAddress(cAddress));
			Identification ident;
			ident = getMobiliserBasePage()
				.getCustomerIdentification(cb.getId(),
					Constants.IDENT_TYPE_USERNAME);
			if (PortalUtils.exists(ident))
			    cb.setUserName(ident.getIdentification());
			ident = getMobiliserBasePage()
				.getCustomerIdentification(cb.getId(),
					Constants.IDENT_TYPE_MSISDN);
			if (PortalUtils.exists(ident)) {
			    cb.setMsisdn(ident.getIdentification());
			    cb.setNetworkProvider(ident.getProvider());
			}
			agentListEntries.add(cb);
		    }
		}
	    } catch (Exception e) {
		LOG.error("# An error occurred while loading customers", e);
		throw new DataProviderLoadException();
	    }
	}
	return agentListEntries;
    }
}
