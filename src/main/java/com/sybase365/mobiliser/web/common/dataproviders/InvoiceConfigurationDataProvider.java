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

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class InvoiceConfigurationDataProvider extends
	SortableDataProvider<InvoiceConfiguration> {
    private MobiliserBasePage mobBasePage;
    private transient List<InvoiceConfiguration> invoiceConfigurationEntries = new ArrayList<InvoiceConfiguration>();

    public InvoiceConfigurationDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    @Override
    public Iterator<InvoiceConfiguration> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public IModel<InvoiceConfiguration> model(final InvoiceConfiguration object) {
	IModel<InvoiceConfiguration> model = new LoadableDetachableModel<InvoiceConfiguration>() {
	    @Override
	    protected InvoiceConfiguration load() {
		InvoiceConfiguration set = null;
		for (InvoiceConfiguration obj : invoiceConfigurationEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<InvoiceConfiguration>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (invoiceConfigurationEntries == null) {
	    return count;
	}

	return invoiceConfigurationEntries.size();
    }

    protected List<InvoiceConfiguration> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<InvoiceConfiguration> sublist = getIndex(
		invoiceConfigurationEntries, sortProperty, sortAsc).subList(
		first, first + count);

	return sublist;
    }

    protected List<InvoiceConfiguration> getIndex(
	    List<InvoiceConfiguration> invoiceConfigurationEntries,
	    String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(invoiceConfigurationEntries, asc);
	} else {
	    return invoiceConfigurationEntries;
	}
    }

    private List<InvoiceConfiguration> sort(List<InvoiceConfiguration> entries,
	    boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<InvoiceConfiguration>() {

		@Override
		public int compare(InvoiceConfiguration arg0,
			InvoiceConfiguration arg1) {
		    if (arg0.getAlias() == null)
			return -1;
		    if (arg1.getAlias() == null)
			return 1;
		    return (arg0).getAlias().compareTo((arg1).getAlias());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<InvoiceConfiguration>() {

		@Override
		public int compare(InvoiceConfiguration arg0,
			InvoiceConfiguration arg1) {
		    if (arg0.getAlias() == null)
			return 1;
		    if (arg1.getAlias() == null)
			return -1;
		    return (arg1).getAlias().compareTo((arg0).getAlias());
		}
	    });
	}
	return entries;
    }

    public void loadList(Long customerId, boolean forcedReload)
	    throws DataProviderLoadException {

	if (invoiceConfigurationEntries == null || forcedReload) {

	    List<InvoiceConfiguration> allEntries = getMobiliserBasePage()
		    .getInvoiceConfigurationCustomerAllowedList(customerId);

	    if (PortalUtils.exists(allEntries)) {
		invoiceConfigurationEntries = allEntries;
	    }
	}
    }
}
