package com.sybase365.mobiliser.web.common.reports.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.util.PortalUtils;

public abstract class BaseContextReport implements IContextReport {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(BaseContextReport.class);

    public BaseContextReport() {
    }

    protected List<KeyValue> getChoiceList(String lookupName,
	    ILookupMapUtility lookupMapUtility, final Component component,
	    final Boolean sortKeys, final boolean sortAscending,
	    List<KeyValue> addEntries, List<String> onlyTheseKeys)
	    throws Exception {

	// Creating a copy of key list so that the original this.keylist object
	// is not modified by wicket
	List<String> validKeyList = new ArrayList<String>();
	if (PortalUtils.exists(onlyTheseKeys)) {
	    for (String key : onlyTheseKeys)
		validKeyList.add(key);
	}

	List<KeyValue> entryList = new ArrayList<KeyValue>();

	final Map<String, String> lookupEntries = lookupMapUtility
		.getLookupEntriesMap(lookupName, component.getLocalizer(),
			component);

	if (lookupEntries != null && !lookupEntries.isEmpty()) {

	    for (Entry<String, String> entry : lookupEntries.entrySet()) {
		if (validKeyList.isEmpty()
			|| validKeyList.contains(entry.getKey())) {
		    entryList.add(new KeyValue<String, String>(entry.getKey(),
			    entry.getValue()));
		}
	    }
	    entryList.addAll(addEntries);

	    if (sortKeys != null && sortKeys.booleanValue()) {
		Collections.sort(entryList, new Comparator<KeyValue>() {

		    @Override
		    @SuppressWarnings({ "rawtypes", "unchecked" })
		    public int compare(KeyValue o1, KeyValue o2) {
			if (!(o1 instanceof Comparable)
				|| !(o2 instanceof Comparable)) {
			    return 0;
			}

			int result = ((Comparable) o1)
				.compareTo((Comparable) o2);

			return sortAscending ? result : (-1) * result;
		    }
		});
	    } else if (sortKeys != null && !sortKeys.booleanValue()) {
		Collections.sort(entryList, new Comparator<KeyValue>() {

		    @Override
		    public int compare(KeyValue o1, KeyValue o2) {
			if (!(o1 instanceof Comparable)
				|| !(o2 instanceof Comparable)) {
			    return 0;
			}

			// to successfully lookup the map entry for the
			// given objects we have to consider their
			// string value or else we'll have a miss
			String name1 = lookupEntries.get(o1.getValue());
			String name2 = lookupEntries.get(o2.getValue());
			if (name1 == null) {
			    if (name2 == null) {
				return 0;
			    } else {
				return sortAscending ? -1 : 1;
			    }
			} else if (name2 == null) {
			    return sortAscending ? 1 : -1;
			} else {
			    int result = name1.compareTo(name2);
			    return sortAscending ? result : (-1) * result;
			}
		    }
		});
	    }
	}

	return entryList;
    }

    @Override
    public abstract String getReportName();

    @Override
    public abstract Map<String, IContextReportParameter> getContextParameters();

    @Override
    public boolean isBatchReport() {
	return false;
    }
}
