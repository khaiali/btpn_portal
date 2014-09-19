package com.sybase365.mobiliser.web.common.dataproviders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.contract.v5_0.report.GeneratedReportListBeanRequest;
import com.sybase365.mobiliser.util.contract.v5_0.report.GeneratedReportListBeanResponse;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.GeneratedReportCriteriaBean;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class StoredReportDataProvider extends SortableDataProvider<String> {

    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(StoredReportDataProvider.class);

    private Long customerId;

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    protected transient List<String> storedReportEntries = new ArrayList<String>();

    public StoredReportDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, Long customerId) {
	setSort(defaultSortProperty, true);
	this.customerId = customerId;
	this.mobBasePage = mobBasePage;
    }

    public void loadStoredReports() throws DataProviderLoadException {

	try {
	    GeneratedReportListBeanRequest request = new GeneratedReportListBeanRequest();
	    mobBasePage.prepareMobiliserRequest(request);
	    GeneratedReportCriteriaBean reportElement = new GeneratedReportCriteriaBean();
	    reportElement.setOwner(customerId);
	    request.setReportElement(reportElement);
	    GeneratedReportListBeanResponse response = mobBasePage.wsReportClient
		    .getAvailableGeneratedReports(request);

	    LOG.info("Found {} results", new Object[] { response
		    .getReportElement().getReports().size() });

	    storedReportEntries = response.getReportElement().getReports();

	} catch (Exception e) {
	    throw new DataProviderLoadException(e.getMessage());
	}
    }

    @Override
    public Iterator<? extends String> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public IModel<String> model(final String object) {
	IModel<String> model = new LoadableDetachableModel<String>() {
	    @Override
	    protected String load() {
		String set = null;
		for (String obj : storedReportEntries) {
		    if (obj.equals(object)) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<String>(model);
    }

    protected List<String> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<String> sublist = getIndex(storedReportEntries, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<String> getIndex(List<String> reportEntries, String prop,
	    boolean asc) {

	if (prop.equals("name")) {
	    return sort(reportEntries, asc);
	} else {
	    return reportEntries;
	}
    }

    @Override
    public int size() {
	int count = 0;

	if (storedReportEntries == null) {
	    return count;
	}

	return storedReportEntries.size();
    }

    private List<String> sort(List<String> entries, boolean asc) {

	Collections.sort(entries);
	return entries;
    }

}
