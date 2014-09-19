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

import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetPaymentInstrumentsByCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetPaymentInstrumentsByCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.ExternalAccount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PaymentInstrument;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class ExternalAccountDataProvider extends
	SortableDataProvider<ExternalAccount> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(ExternalAccountDataProvider.class);
    private transient List<ExternalAccount> accountEntries = new ArrayList<ExternalAccount>();
    private MobiliserBasePage mobBasePage;

    public ExternalAccountDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns ExternalAccount starting with index <code>first</code> and ending
     * with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<ExternalAccount> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>ExternalAccount</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (accountEntries == null) {
	    return count;
	}

	return accountEntries.size();
    }

    @Override
    public final IModel<ExternalAccount> model(final ExternalAccount object) {
	IModel<ExternalAccount> model = new LoadableDetachableModel<ExternalAccount>() {
	    @Override
	    protected ExternalAccount load() {
		ExternalAccount set = null;
		for (ExternalAccount obj : accountEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<ExternalAccount>(model);
    }

    public void loadGCashWallets(Long customerId, boolean forcedReload)
	    throws DataProviderLoadException {

	GetPaymentInstrumentsByCustomerRequest pisByCustomer;

	try {
	    pisByCustomer = mobBasePage
		    .getNewMobiliserRequest(GetPaymentInstrumentsByCustomerRequest.class);

	    pisByCustomer.setCustomerId(mobBasePage.getMobiliserWebSession()
		    .getCustomer().getId());

	    pisByCustomer
		    .setPaymentInstrumentClassFilter(Constants.EXTERNAL_ACCOUNT_CLASS_FILTER);

	    GetPaymentInstrumentsByCustomerResponse pisByCustomerRes = mobBasePage.wsWalletClient
		    .getPaymentInstrumentsByCustomer(pisByCustomer);

	    if (mobBasePage.evaluateMobiliserResponse(pisByCustomerRes)) {
		for (PaymentInstrument pi : pisByCustomerRes
			.getPaymentInstruments()) {

		    ExternalAccount extAcc = (ExternalAccount) pi;

		    if (extAcc.getType() == Constants.PI_TYPE_GCASH_WALLET) {
			accountEntries.add(extAcc);
		    }
		}
	    } else {
		LOG
			.warn("# An error occurred while retrieving the list of external accounts");
	    }
	} catch (Exception e) {
	    LOG
		    .error(
			    "# An error occurred while retrieving the list of external accounts",
			    e);
	}
    }

    public void loadGCashBankAccounts(Long customerId, boolean forcedReload)
	    throws DataProviderLoadException {

	GetPaymentInstrumentsByCustomerRequest pisByCustomer;

	try {
	    pisByCustomer = mobBasePage
		    .getNewMobiliserRequest(GetPaymentInstrumentsByCustomerRequest.class);

	    pisByCustomer.setCustomerId(mobBasePage.getMobiliserWebSession()
		    .getCustomer().getId());

	    pisByCustomer
		    .setPaymentInstrumentClassFilter(Constants.EXTERNAL_ACCOUNT_CLASS_FILTER);

	    GetPaymentInstrumentsByCustomerResponse pisByCustomerRes = mobBasePage.wsWalletClient
		    .getPaymentInstrumentsByCustomer(pisByCustomer);

	    if (mobBasePage.evaluateMobiliserResponse(pisByCustomerRes)) {
		for (PaymentInstrument pi : pisByCustomerRes
			.getPaymentInstruments()) {

		    ExternalAccount extAcc = (ExternalAccount) pi;

		    if (extAcc.getType() == Constants.PI_TYPE_GCASH_BANK_ACCOUNT) {
			accountEntries.add(extAcc);
		    }
		}
	    } else {
		LOG
			.warn("# An error occurred while retrieving the list of external accounts");
	    }
	} catch (Exception e) {
	    LOG
		    .error(
			    "# An error occurred while retrieving the list of external accounts",
			    e);
	}
    }

    protected List<ExternalAccount> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<ExternalAccount> sublist = getIndex(accountEntries, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<ExternalAccount> getIndex(
	    List<ExternalAccount> accountEntries, String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(accountEntries, asc);
	} else {
	    return accountEntries;
	}
    }

    private List<ExternalAccount> sort(List<ExternalAccount> entries,
	    boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<ExternalAccount>() {

		@Override
		public int compare(ExternalAccount arg0, ExternalAccount arg1) {
		    return (arg0).getId().toString().compareTo(
			    (arg1).getId().toString());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<ExternalAccount>() {

		@Override
		public int compare(ExternalAccount arg0, ExternalAccount arg1) {
		    return (arg1).getId().toString().compareTo(
			    (arg0).getId().toString());
		}
	    });
	}
	return entries;
    }
}
