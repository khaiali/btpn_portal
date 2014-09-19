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

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetWrkCustomersRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetWrkCustomersResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.WrkCustomer;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetUseCasePrivilegesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetUseCasePrivilegesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetWrkBulkRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetWrkBulkResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.UseCasePrivilege;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.WrkBulk;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetWrkWalletEntriesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetWrkWalletEntriesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WrkWalletEntry;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.ApprovalConfBean;
import com.sybase365.mobiliser.web.util.Constants;

@SuppressWarnings("serial")
public class ApprovalConfDataProvider extends
	SortableDataProvider<ApprovalConfBean> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(ApprovalConfDataProvider.class);
    private transient List<ApprovalConfBean> appConfList = null;
    private MobiliserBasePage mobBasePage;
    private String prop;

    public ApprovalConfDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, boolean sortAsc) {
	setSort(defaultSortProperty, sortAsc);
	this.prop = defaultSortProperty;
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns approvals starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<ApprovalConfBean> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>customers</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (appConfList == null) {
	    return count;
	}

	return appConfList.size();
    }

    @Override
    public final IModel<ApprovalConfBean> model(final ApprovalConfBean object) {
	IModel<ApprovalConfBean> model = new LoadableDetachableModel<ApprovalConfBean>() {
	    @Override
	    protected ApprovalConfBean load() {
		ApprovalConfBean set = null;
		for (ApprovalConfBean obj : appConfList) {
		    if (prop.equals("customerType")) {
			if (obj.getCustomerTypeId().equals(
				object.getCustomerTypeId())) {
			    set = obj;
			    break;
			}
		    } else if (prop.equals("piType")) {
			if (obj.getPiTypeId().equals(object.getPiTypeId())) {
			    set = obj;
			    break;
			}
		    } else if (prop.equals("useCaseType")) {
			if (obj.getUseCaseId().equals(object.getUseCaseId())) {
			    set = obj;
			    break;
			}
		    } else {
			if (obj.getBulkFileTypeId().equals(
				object.getBulkFileTypeId())) {
			    set = obj;
			    break;
			}

		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<ApprovalConfBean>(model);
    }

    public List<WrkCustomer> loadWrkCustomersList(boolean forcedReload)
	    throws DataProviderLoadException {
	List<WrkCustomer> allEntries = null;
	if (appConfList == null || forcedReload) {

	    appConfList = new ArrayList<ApprovalConfBean>();
	    ApprovalConfBean appConfObj;

	    allEntries = getApprovalWrkCustTypes();

	    for (WrkCustomer workCust : allEntries) {
		appConfObj = new ApprovalConfBean();
		appConfObj.setCustomerTypeId(workCust.getCustomerTypeId());
		appConfObj.setMakerPrivilege(workCust.getMakerPrivilege());
		appConfObj.setCheckerPrivilege(workCust.getCheckerPrivilege());
		appConfObj.setExecutePrivilege(workCust.getExecutePrivilege());
		appConfList.add(appConfObj);

	    }

	}
	return allEntries;
    }

    public List<WrkWalletEntry> loadWrkWalletsList(boolean forcedReload)
	    throws DataProviderLoadException {
	List<WrkWalletEntry> allEntries = null;
	if (appConfList == null || forcedReload) {

	    appConfList = new ArrayList<ApprovalConfBean>();

	    allEntries = getApprovalWrkWalletTypes();

	    ApprovalConfBean appConfObj;

	    for (WrkWalletEntry workPiType : allEntries) {
		appConfObj = new ApprovalConfBean();
		appConfObj.setPiTypeId(workPiType.getPiTypeId());
		appConfObj.setMakerPrivilege(workPiType.getMakerPrivilege());
		appConfObj
			.setCheckerPrivilege(workPiType.getCheckerPrivilege());
		appConfObj
			.setExecutePrivilege(workPiType.getExecutePrivilege());
		appConfList.add(appConfObj);

	    }

	}
	return allEntries;
    }

    public List<UseCasePrivilege> loadWrkUseCaseList(boolean forcedReload)
	    throws DataProviderLoadException {

	List<UseCasePrivilege> allEntries = null;

	if (appConfList == null || forcedReload) {

	    appConfList = new ArrayList<ApprovalConfBean>();

	    allEntries = getApprovalUseCaseTypes();

	    ApprovalConfBean appConfObj;

	    for (UseCasePrivilege workUseCaseType : allEntries) {
		appConfObj = new ApprovalConfBean();
		appConfObj.setUseCaseId(Integer.valueOf(workUseCaseType
			.getUseCaseId()));
		appConfObj.setMakerPrivilege(workUseCaseType
			.getMakerPrivilege());
		appConfObj.setCheckerPrivilege(workUseCaseType
			.getCheckerPrivilege());
		appConfObj.setExecutePrivilege(workUseCaseType
			.getExecutePrivilege());
		appConfObj.setPayeePrivilege(workUseCaseType
			.getPayeePrivilege());
		appConfObj.setPayerPrivilege(workUseCaseType
			.getPayerPrivilege());
		appConfObj.setCallerPrivilege(workUseCaseType
			.getCallerPrivilege());
		appConfObj.setCallerParentPrivilege(workUseCaseType
			.getCallerParentPrivilege());
		appConfObj.setCallerSelfPrivilege(workUseCaseType
			.getCallerSelfPrivilege());
		appConfList.add(appConfObj);

	    }

	}
	return allEntries;
    }

    public List<WrkBulk> loadWrkFilesList(boolean forcedReload)
	    throws DataProviderLoadException {

	List<WrkBulk> allEntries = null;

	if (appConfList == null || forcedReload) {

	    appConfList = new ArrayList<ApprovalConfBean>();

	    allEntries = getApprovalFileTypes();

	    ApprovalConfBean appConfObj;

	    for (WrkBulk workFileType : allEntries) {
		appConfObj = new ApprovalConfBean();
		appConfObj.setBulkFileTypeId(Integer.valueOf(workFileType
			.getBulkTypeId()));
		appConfObj.setMakerPrivilege(workFileType.getMakerPrivilege());
		appConfObj.setCheckerPrivilege(workFileType
			.getCheckerPrivilege());
		appConfObj.setExecutePrivilege(workFileType
			.getExecutePrivilege());

		appConfList.add(appConfObj);

	    }

	}
	return allEntries;
    }

    protected List<ApprovalConfBean> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<ApprovalConfBean> sublist = getIndex(appConfList, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<ApprovalConfBean> getIndex(
	    List<ApprovalConfBean> customerTypeEntries, String prop, boolean asc) {

	if (prop.equals("customerType") || prop.equals("piType")
		|| prop.equals("useCaseType") || prop.equals("fileType")) {
	    return sort(customerTypeEntries, asc);
	} else {
	    return customerTypeEntries;
	}
    }

    private List<ApprovalConfBean> sort(List<ApprovalConfBean> entries,
	    boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<ApprovalConfBean>() {

		@Override
		public int compare(ApprovalConfBean arg0, ApprovalConfBean arg1) {
		    if (prop.equals("customerType")) {
			return ((mobBasePage.getDisplayValue(
				String.valueOf((arg0).getCustomerTypeId()),
				Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE))).compareTo(mobBasePage.getDisplayValue(
				String.valueOf((arg1).getCustomerTypeId()),
				Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE));

		    } else if (prop.equals("piType")) {
			return ((mobBasePage.getDisplayValue(
				String.valueOf((arg0).getPiTypeId()),
				Constants.RESOURCE_BUNDLE_PI_TYPES)))
				.compareTo(mobBasePage.getDisplayValue(
					String.valueOf((arg1).getPiTypeId()),
					Constants.RESOURCE_BUNDLE_PI_TYPES));

		    } else if (prop.equals("useCaseType")) {
			return ((mobBasePage.getDisplayValue(
				String.valueOf((arg0).getUseCaseId()),
				Constants.RESOURCE_BUNDLE_USE_CASES)))
				.compareTo(mobBasePage.getDisplayValue(
					String.valueOf((arg1).getUseCaseId()),
					Constants.RESOURCE_BUNDLE_USE_CASES));

		    } else {
			return ((mobBasePage.getDisplayValue(
				String.valueOf((arg0).getBulkFileTypeId()),
				Constants.RESOURCE_BUNDLE_FILE_TYPES)))
				.compareTo(mobBasePage.getDisplayValue(String
					.valueOf((arg1).getBulkFileTypeId()),
					Constants.RESOURCE_BUNDLE_FILE_TYPES));
		    }
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<ApprovalConfBean>() {

		@Override
		public int compare(ApprovalConfBean arg0, ApprovalConfBean arg1) {
		    if (prop.equals("customerType")) {
			return ((mobBasePage.getDisplayValue(
				String.valueOf((arg1).getCustomerTypeId()),
				Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE))).compareTo(mobBasePage.getDisplayValue(
				String.valueOf((arg0).getCustomerTypeId()),
				Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE));
		    } else if (prop.equals("piType")) {
			return ((mobBasePage.getDisplayValue(
				String.valueOf((arg1).getPiTypeId()),
				Constants.RESOURCE_BUNDLE_PI_TYPES)))
				.compareTo(mobBasePage.getDisplayValue(
					String.valueOf((arg0).getPiTypeId()),
					Constants.RESOURCE_BUNDLE_PI_TYPES));
		    } else if (prop.equals("useCaseType")) {
			return ((mobBasePage.getDisplayValue(
				String.valueOf((arg1).getUseCaseId()),
				Constants.RESOURCE_BUNDLE_USE_CASES)))
				.compareTo(mobBasePage.getDisplayValue(
					String.valueOf((arg0).getUseCaseId()),
					Constants.RESOURCE_BUNDLE_USE_CASES));
		    } else {
			return ((mobBasePage.getDisplayValue(
				String.valueOf((arg1).getBulkFileTypeId()),
				Constants.RESOURCE_BUNDLE_FILE_TYPES)))
				.compareTo(mobBasePage.getDisplayValue(String
					.valueOf((arg0).getBulkFileTypeId()),
					Constants.RESOURCE_BUNDLE_FILE_TYPES));
		    }

		}
	    });
	}
	return entries;
    }

    public List<WrkCustomer> getApprovalWrkCustTypes()
	    throws DataProviderLoadException {

	LOG.debug("# ApprovalConfDataProvider.getApprovalWrkCustTypes()");
	GetWrkCustomersResponse response = null;
	try {
	    GetWrkCustomersRequest request = getMobiliserBasePage()
		    .getNewMobiliserRequest(GetWrkCustomersRequest.class);

	    response = getMobiliserBasePage().wsCustomerClient
		    .getWrkCustomers(request);

	    if (!getMobiliserBasePage().evaluateMobiliserResponse(response)) {
		LOG.warn("# Error while getting customer approval configurations");
		return null;
	    }
	} catch (Exception e) {
	    LOG.error("# Error while getting customer approval configurations",
		    e);
	    throw new DataProviderLoadException();

	}
	return response.getWrkCustomers();
    }

    public List<WrkWalletEntry> getApprovalWrkWalletTypes()
	    throws DataProviderLoadException {

	LOG.debug("# ApprovalConfDataProvider.getApprovalWrkWalletTypes()");
	GetWrkWalletEntriesResponse response = null;
	try {
	    GetWrkWalletEntriesRequest request = getMobiliserBasePage()
		    .getNewMobiliserRequest(GetWrkWalletEntriesRequest.class);

	    response = getMobiliserBasePage().wsWalletClient
		    .getWrkWalletEntries(request);

	    if (!getMobiliserBasePage().evaluateMobiliserResponse(response)) {
		LOG.warn("# Error while getting wallet approval configurations");
		return null;
	    }
	} catch (Exception e) {
	    LOG.error("# Error while getting wallet approval configurations", e);
	    throw new DataProviderLoadException();

	}
	return response.getWrkWalletEntries();
    }

    public List<UseCasePrivilege> getApprovalUseCaseTypes()
	    throws DataProviderLoadException {

	LOG.debug("# ApprovalConfDataProvider.getApprovalUseCaseTypes()");
	GetUseCasePrivilegesResponse response = null;
	try {
	    GetUseCasePrivilegesRequest request = getMobiliserBasePage()
		    .getNewMobiliserRequest(GetUseCasePrivilegesRequest.class);

	    response = getMobiliserBasePage().wsSystemConfClient
		    .getUseCasePrivileges(request);

	    if (!getMobiliserBasePage().evaluateMobiliserResponse(response)) {
		LOG.warn("# Error while getting usecase approval configurations");
		return null;
	    }
	} catch (Exception e) {
	    LOG.error("# Error while getting usecase approval configurations",
		    e);
	    throw new DataProviderLoadException();

	}
	return response.getUseCasePrivileges();
    }

    public List<WrkBulk> getApprovalFileTypes()
	    throws DataProviderLoadException {

	LOG.debug("# ApprovalConfDataProvider.getApprovalFileTypes()");
	GetWrkBulkResponse response = null;
	try {
	    GetWrkBulkRequest request = getMobiliserBasePage()
		    .getNewMobiliserRequest(GetWrkBulkRequest.class);

	    response = getMobiliserBasePage().wsBulkProcessingClient
		    .getWrkBulk(request);

	    if (!getMobiliserBasePage().evaluateMobiliserResponse(response)) {
		LOG.warn("# Error while getting file approval configurations");
		return null;
	    }
	} catch (Exception e) {
	    LOG.error("# Error while getting file approval configurations", e);
	    throw new DataProviderLoadException();

	}
	return response.getWrkBulk();
    }
}
