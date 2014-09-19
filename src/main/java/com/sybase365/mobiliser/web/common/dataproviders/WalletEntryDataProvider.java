package com.sybase365.mobiliser.web.common.dataproviders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.FindPendingWalletEntriesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.FindPendingWalletEntriesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PendingWalletEntry;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.cst.pages.customercare.FindPendingWalletPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class WalletEntryDataProvider extends SortableDataProvider<WalletEntry> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(WalletEntryDataProvider.class);

    private transient List<WalletEntry> filteredWalletEntries = new ArrayList<WalletEntry>();
    private transient List<WalletEntry> allWalletEntries = new ArrayList<WalletEntry>();

    private MobiliserBasePage mobBasePage;
    private boolean isPendingWalletList;

    public WalletEntryDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    public WalletEntryDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, boolean isPendingWalletList) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
	this.isPendingWalletList = isPendingWalletList;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns WalletEntry starting with index <code>first</code> and ending
     * with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<WalletEntry> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>WalletEntry</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (filteredWalletEntries == null) {
	    return count;
	}

	return filteredWalletEntries.size();
    }

    @Override
    public final IModel<WalletEntry> model(final WalletEntry object) {
	return new DetachableWalletEntryModel(object, getMobiliserBasePage());
    }

    public void loadAllWalletEntries(Long customerId)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(allWalletEntries)) {

	    List<WalletEntry> walletEntries = getMobiliserBasePage()
		    .getWalletEntryList(customerId, null, null);

	    setAllWalletEntries(walletEntries);
	}
    }

    /**
     * @param piTypeId
     * @param fromDate
     * @param toDate
     * @throws DataProviderLoadException
     */
    public void loadPendingWalletEntries(Integer piTypeId,
	    XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate,
	    boolean forcedReload) throws DataProviderLoadException {
	if (!PortalUtils.exists(filteredWalletEntries) || forcedReload) {
	    filteredWalletEntries = new ArrayList<WalletEntry>();
	    filteredWalletEntries.addAll(fetchPendingWalletEntries(piTypeId,
		    fromDate, toDate, null));
	}
    }

    public void loadExternalBankList(Long customerId, boolean forcedReload)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(filteredWalletEntries) || forcedReload) {

	    loadAllWalletEntries(customerId);

	    filteredWalletEntries = new ArrayList<WalletEntry>();

	    if (PortalUtils.exists(allWalletEntries)) {
		for (WalletEntry we : allWalletEntries) {
		    if (we.getBankAccount() != null
			    && we.getBankAccount().getType() == Constants.PI_TYPE_EXTERNAL_BA) {
			filteredWalletEntries.add(we);
		    }
		}
	    }

	    filteredWalletEntries.addAll(fetchPendingWalletEntries(
		    Integer.valueOf(Constants.PI_TYPE_EXTERNAL_BA), null, null,
		    customerId));

	}
    }

    public void loadBankAccountList(Long customerId, boolean forcedReload)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(filteredWalletEntries) || forcedReload) {

	    loadAllWalletEntries(customerId);

	    filteredWalletEntries = new ArrayList<WalletEntry>();

	    if (PortalUtils.exists(allWalletEntries)) {
		for (WalletEntry we : allWalletEntries) {
		    if (we.getBankAccount() != null
			    && we.getBankAccount().getType() == Constants.PI_TYPE_DEFAULT_BA) {
			filteredWalletEntries.add(we);
		    }
		}
	    }
	    filteredWalletEntries.addAll(fetchPendingWalletEntries(
		    Integer.valueOf(Constants.PI_TYPE_DEFAULT_BA), null, null,
		    customerId));

	}
    }

    public void loadCreditCardList(Long customerId, boolean forcedReload)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(filteredWalletEntries) || forcedReload) {

	    loadAllWalletEntries(customerId);

	    filteredWalletEntries = new ArrayList<WalletEntry>();

	    if (PortalUtils.exists(allWalletEntries)) {
		for (WalletEntry we : allWalletEntries) {
		    if (we.getCreditCard() != null) {
			filteredWalletEntries.add(we);
		    }
		}
	    }
	    filteredWalletEntries.addAll(fetchPendingWalletEntries(
		    Integer.valueOf(Constants.PI_TYPE_DEFAULT_CR), null, null,
		    customerId));

	}
    }

    public void loadExternalAccountList(Long customerId, boolean forcedReload)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(filteredWalletEntries) || forcedReload) {

	    loadAllWalletEntries(customerId);

	    filteredWalletEntries = new ArrayList<WalletEntry>();

	    if (PortalUtils.exists(allWalletEntries)) {
		for (WalletEntry we : allWalletEntries) {
		    if (we.getExternalAccount() != null) {
			filteredWalletEntries.add(we);
		    }
		}
	    }
	}

	filteredWalletEntries.addAll(fetchPendingWalletEntries(
		Integer.valueOf(Constants.PI_TYPE_CST_EXTERNAL_ACC), null,
		null, customerId));

    }

    public void loadStoredValueAccountList(Long customerId, boolean forcedReload)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(filteredWalletEntries) || forcedReload) {

	    loadAllWalletEntries(customerId);

	    filteredWalletEntries = new ArrayList<WalletEntry>();

	    if (PortalUtils.exists(allWalletEntries)) {
		for (WalletEntry we : allWalletEntries) {
		    if (we.getSva() != null) {
			filteredWalletEntries.add(we);
		    }
		}
	    }
	}
    }

    protected List<WalletEntry> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<WalletEntry> sublist = getIndex(filteredWalletEntries,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<WalletEntry> getIndex(
	    List<WalletEntry> filteredWalletEntries, String prop, boolean asc) {

	if (!isPendingWalletList ? prop.equals("name") : prop.equals("id")) {
	    return sort(filteredWalletEntries, asc);
	} else {
	    return filteredWalletEntries;
	}

    }

    private List<WalletEntry> sort(List<WalletEntry> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<WalletEntry>() {

		@Override
		public int compare(WalletEntry arg0, WalletEntry arg1) {
		    if (!isPendingWalletList)
			return (arg0).getAlias().compareTo((arg1).getAlias());
		    else {

			return ((PendingWalletEntry) arg0)
				.getTaskId()
				.compareTo(
					((PendingWalletEntry) arg1).getTaskId());
		    }

		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<WalletEntry>() {

		@Override
		public int compare(WalletEntry arg0, WalletEntry arg1) {
		    if (!isPendingWalletList)
			return (arg1).getAlias().compareTo((arg0).getAlias());
		    else
			return ((PendingWalletEntry) arg1)
				.getTaskId()
				.compareTo(
					((PendingWalletEntry) arg0).getTaskId());
		}
	    });
	}
	return entries;
    }

    private List<WalletEntry> fetchPendingWalletEntries(Integer piTypeId,
	    XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate,
	    Long customerId) throws DataProviderLoadException {

	LOG.debug("# WalletEntryDataProvider.fetchPendingWallets(...)");

	List<WalletEntry> filteredWalletEntries = new ArrayList<WalletEntry>();
	FindPendingWalletEntriesResponse response = null;
	try {

	    FindPendingWalletEntriesRequest request = getMobiliserBasePage()
		    .getNewMobiliserRequest(
			    FindPendingWalletEntriesRequest.class);
	    request.setCustomerId(customerId);
	    request.setPiTypeId(piTypeId);
	    request.setFromDate(fromDate);
	    request.setToDate(toDate);

	    response = getMobiliserBasePage().wsWalletClient
		    .findPendingWalletEntries(request);

	    if (response.getStatus().getCode() == Constants.NO_APPROVAL_CONFIG_FOUND) {

		if (getMobiliserBasePage() instanceof FindPendingWalletPage) {
		    getMobiliserBasePage().error(
			    getMobiliserBasePage().getLocalizer().getString(
				    "wallet.approval.config.missing.error",
				    getMobiliserBasePage()));
		}
		return filteredWalletEntries;
	    }

	    if (response.getStatus().getCode() == Constants.PENDING_APPROVAL_INSUFFICIENT_PRIV_ERROR) {

		if (piTypeId.intValue() == Constants.PI_TYPE_BANK_ACCOUNT) {
		    LOG.error("# An error occurred while loading pending bank accounts");
		    getMobiliserBasePage().error(
			    getMobiliserBasePage().getLocalizer().getString(
				    "pedning.bank.account.load.error",
				    getMobiliserBasePage()));
		    return filteredWalletEntries;
		} else if (piTypeId.intValue() == Constants.PI_TYPE_DEFAULT_CR) {
		    LOG.error("# An error occurred while loading pending credit cards");
		    getMobiliserBasePage().info(
			    getMobiliserBasePage().getLocalizer().getString(
				    "pedning.credit.card.load.error",
				    getMobiliserBasePage()));
		    return filteredWalletEntries;
		} else if (piTypeId.intValue() == Constants.PI_TYPE_EXTERNAL_BA) {
		    LOG.error("# An error occurred while loading pending external accounts");
		    getMobiliserBasePage().info(
			    getMobiliserBasePage().getLocalizer().getString(
				    "pedning.external.account.load.error",
				    getMobiliserBasePage()));
		    return filteredWalletEntries;
		} else if (piTypeId.intValue() == Constants.PI_TYPE_CST_EXTERNAL_ACC) {
		    LOG.error("# An error occurred while loading pending cst external accounts");
		    getMobiliserBasePage().info(
			    getMobiliserBasePage().getLocalizer().getString(
				    "pedning.cst.external.account.load.error",
				    getMobiliserBasePage()));
		    return filteredWalletEntries;
		}

	    }

	    if (!getMobiliserBasePage().evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while loading pending wallets");
		return filteredWalletEntries;
	    }

	    filteredWalletEntries.addAll(response.getWalletEntries());

	} catch (Exception e) {
	    LOG.error("# An error occurred while laoding pending wallets");
	    throw new DataProviderLoadException();

	}
	return filteredWalletEntries;
    }

    public List<WalletEntry> getAllWalletEntries() {
	return this.allWalletEntries;
    }

    public void setAllWalletEntries(List<WalletEntry> value) {
	this.allWalletEntries = value;
    }

}