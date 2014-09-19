package com.sybase365.mobiliser.web.common.dataproviders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.Invoice;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class InvoiceDataProvider extends SortableDataProvider<Invoice> {
    private MobiliserBasePage mobBasePage;
    private transient List<Invoice> invoiceEntries = new ArrayList<Invoice>();

    public InvoiceDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    @Override
    public Iterator<Invoice> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public IModel<Invoice> model(final Invoice object) {
	IModel<Invoice> model = new LoadableDetachableModel<Invoice>() {
	    @Override
	    protected Invoice load() {
		Invoice set = null;
		for (Invoice obj : invoiceEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<Invoice>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (invoiceEntries == null) {
	    return count;
	}

	return invoiceEntries.size();
    }

    protected List<Invoice> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<Invoice> sublist = getIndex(invoiceEntries, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<Invoice> getIndex(List<Invoice> InvoiceEntries, String prop,
	    boolean asc) {

	return sort(invoiceEntries, asc, prop);

    }

    private List<Invoice> sort(List<Invoice> entries, boolean asc,
	    String sortProperty) {

	if (sortProperty.equals("id")) {
	    if (asc) {

		Collections.sort(entries, new Comparator<Invoice>() {

		    @Override
		    public int compare(Invoice arg0, Invoice arg1) {
			return arg0.getId().compareTo(arg1.getId());
		    }
		});

	    } else {

		Collections.sort(entries, new Comparator<Invoice>() {

		    @Override
		    public int compare(Invoice arg0, Invoice arg1) {
			return arg1.getId().compareTo(arg0.getId());
		    }
		});
	    }
	}

	if (sortProperty.equals("date")) {
	    if (asc) {

		Collections.sort(entries, new Comparator<Invoice>() {

		    @Override
		    public int compare(Invoice arg0, Invoice arg1) {
			if (arg0.getDate() == null)
			    return -1;
			if (arg1.getDate() == null)
			    return 1;
			return arg0
				.getDate()
				.toGregorianCalendar()
				.getTime()
				.compareTo(
					arg1.getDate().toGregorianCalendar()
						.getTime());
		    }
		});

	    } else {

		Collections.sort(entries, new Comparator<Invoice>() {
		    @Override
		    public int compare(Invoice arg0, Invoice arg1) {
			if (arg1.getDate() == null)
			    return -1;
			if (arg0.getDate() == null)
			    return 1;
			return arg1
				.getDate()
				.toGregorianCalendar()
				.getTime()
				.compareTo(
					arg0.getDate().toGregorianCalendar()
						.getTime());
		    }
		});
	    }
	}

	if (sortProperty.equals("status")) {
	    if (asc) {

		Collections.sort(entries, new Comparator<Invoice>() {
		    @Override
		    public int compare(Invoice arg0, Invoice arg1) {
			return mobBasePage
				.getDisplayValue(
					String.valueOf(arg0.getStatus()),
					Constants.RESOURCE_BUNDLE_INVOICE_STATUS)
				.compareTo(
					mobBasePage.getDisplayValue(
						String.valueOf(arg1.getStatus()),
						Constants.RESOURCE_BUNDLE_INVOICE_STATUS));
		    }
		});

	    } else {

		Collections.sort(entries, new Comparator<Invoice>() {
		    @Override
		    public int compare(Invoice arg0, Invoice arg1) {
			return mobBasePage
				.getDisplayValue(
					String.valueOf(arg1.getStatus()),
					Constants.RESOURCE_BUNDLE_INVOICE_STATUS)
				.compareTo(
					mobBasePage.getDisplayValue(
						String.valueOf(arg0.getStatus()),
						Constants.RESOURCE_BUNDLE_INVOICE_STATUS));
		    }
		});
	    }
	}
	return entries;
    }

    public void loadList(Long customerId, boolean forcedReload)
	    throws DataProviderLoadException {

	if (invoiceEntries == null || forcedReload) {

	    List<Invoice> newEntries = getMobiliserBasePage().getInvoiceList(
		    customerId, Constants.INVOICE_STATUS_NEW);

	    if (PortalUtils.exists(newEntries) || newEntries.size() == 0) {
		invoiceEntries = newEntries;
	    }

	    List<Invoice> activeEntries = getMobiliserBasePage()
		    .getInvoiceList(customerId, Constants.INVOICE_STATUS_ACTIVE);

	    if (PortalUtils.exists(activeEntries)) {
		invoiceEntries.addAll(activeEntries);
	    }

	}
    }

    public void loadListByFilter(Long customerId, boolean forcedReload,
	    Long invoiceType, Integer invoiceStatus, Date fromDate, Date toDate)
	    throws DataProviderLoadException {

	if (invoiceEntries == null || forcedReload) {

	    List<Invoice> allEntries = getMobiliserBasePage().getInvoiceList(
		    customerId, invoiceStatus);

	    if (allEntries == null)
		return;

	    List<InvoiceConfiguration> icList = getMobiliserBasePage()
		    .getInvoiceConfigurationList(customerId);

	    List<Invoice> resultEntries = new ArrayList<Invoice>();
	    for (Invoice iv : allEntries) {
		boolean pass = true;

		// do not show new/active invoices
		if (invoiceStatus == null
			&& (iv.getStatus() == Constants.INVOICE_STATUS_NEW || iv
				.getStatus() == Constants.INVOICE_STATUS_ACTIVE)) {
		    pass = false;
		}

		if (invoiceStatus != null && invoiceStatus != iv.getStatus()) {
		    pass = false;
		}

		InvoiceConfiguration ic = getInvoiceConfigurationFromList(
			iv.getInvoiceConfigurationId(), icList);

		if (pass && invoiceType != null
			&& (ic == null || invoiceType != ic.getInvoiceTypeId())) {
		    pass = false;
		}

		if (pass
			&& fromDate != null
			&& iv.getDate() != null
			&& iv.getDate().toGregorianCalendar().getTime()
				.before(fromDate)) {
		    pass = false;
		}

		if (pass
			&& toDate != null
			&& iv.getDate() != null
			&& iv.getDate().toGregorianCalendar().getTime()
				.after(toDate)) {
		    pass = false;
		}

		if (pass) {
		    resultEntries.add(iv);
		}
	    }

	    if (PortalUtils.exists(resultEntries) || resultEntries.size() == 0) {
		invoiceEntries = resultEntries;
	    }
	}
    }

    private InvoiceConfiguration getInvoiceConfigurationFromList(long id,
	    List<InvoiceConfiguration> icList) {
	if (icList == null)
	    return null;
	for (InvoiceConfiguration ic : icList) {
	    if (ic.getId() == id) {
		return ic;
	    }
	}
	return null;
    }
}
