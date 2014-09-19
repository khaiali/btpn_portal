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

import com.sybase365.mobiliser.money.contract.v5_0.system.GetFinishedBulkProcessingFilesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetFinishedBulkProcessingFilesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.BulkFileHistoryListItem;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class BulkHistoryDataProvider extends
	SortableDataProvider<BulkFileHistoryListItem> {

    private transient List<BulkFileHistoryListItem> pendingFiles = new ArrayList<BulkFileHistoryListItem>();
    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(BulkHistoryDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public BulkHistoryDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, boolean ascending) {
	setSort(defaultSortProperty, ascending);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends BulkFileHistoryListItem> iterator(int first,
	    int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public final IModel<BulkFileHistoryListItem> model(
	    final BulkFileHistoryListItem object) {
	IModel<BulkFileHistoryListItem> model = new LoadableDetachableModel<BulkFileHistoryListItem>() {

	    @Override
	    protected BulkFileHistoryListItem load() {
		BulkFileHistoryListItem set = null;
		for (BulkFileHistoryListItem obj : pendingFiles) {
		    if (obj == object) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<BulkFileHistoryListItem>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (pendingFiles == null) {
	    return count;
	}

	return pendingFiles.size();
    }

    protected List<BulkFileHistoryListItem> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<BulkFileHistoryListItem> sublist = getIndex(pendingFiles,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<BulkFileHistoryListItem> getIndex(
	    List<BulkFileHistoryListItem> files, String prop, boolean asc) {

	if (prop.equals("Lid")) {
	    return sort(files, asc);
	} else {
	    return files;
	}
    }

    private List<BulkFileHistoryListItem> sort(
	    List<BulkFileHistoryListItem> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries,
		    new Comparator<BulkFileHistoryListItem>() {

			@Override
			public int compare(BulkFileHistoryListItem arg0,
				BulkFileHistoryListItem arg1) {

			    if (arg0.getEntityId() == arg1.getEntityId()) {
				return 0;
			    } else if (arg0.getEntityId() < arg1.getEntityId()) {
				return -1;
			    } else
				return 1;
			}
		    });

	} else {

	    Collections.sort(entries,
		    new Comparator<BulkFileHistoryListItem>() {

			@Override
			public int compare(BulkFileHistoryListItem arg0,
				BulkFileHistoryListItem arg1) {
			    if (arg0.getEntityId() == arg1.getEntityId()) {
				return 0;
			    } else if (arg0.getEntityId() < arg1.getEntityId()) {
				return -1;
			    } else
				return 1;
			}
		    });
	}
	return entries;
    }

    public List<BulkFileHistoryListItem> searchFiles(
	    GetFinishedBulkProcessingFilesRequest req, boolean forcedReload)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(pendingFiles) || forcedReload) {
	    try {
		GetFinishedBulkProcessingFilesResponse response = getMobiliserBasePage().wsBulkProcessingClient
			.getFinishedBulkProcessingFiles(req);
		if (getMobiliserBasePage().evaluateMobiliserResponse(response))
		    this.pendingFiles = response.getBulkFileList();

	    } catch (Exception e) {
		LOG.error("# Error finding Bulk history files", e);
		DataProviderLoadException dple = new DataProviderLoadException(
			e.getMessage());
		throw dple;
	    }

	}
	return pendingFiles;
    }
}
