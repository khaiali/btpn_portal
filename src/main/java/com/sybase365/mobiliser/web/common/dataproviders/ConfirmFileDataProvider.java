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

import com.sybase365.mobiliser.money.contract.v5_0.system.FindPendingBulkProcessingFilesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.FindPendingBulkProcessingFilesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.BulkFile;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class ConfirmFileDataProvider extends SortableDataProvider<BulkFile> {

    private transient List<BulkFile> pendingFiles = new ArrayList<BulkFile>();
    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(ConfirmFileDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public ConfirmFileDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, boolean ascending) {
	setSort(defaultSortProperty, ascending);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends BulkFile> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public final IModel<BulkFile> model(final BulkFile object) {
	IModel<BulkFile> model = new LoadableDetachableModel<BulkFile>() {

	    @Override
	    protected BulkFile load() {
		BulkFile set = null;
		for (BulkFile obj : pendingFiles) {
		    if (obj == object) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<BulkFile>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (pendingFiles == null) {
	    return count;
	}

	return pendingFiles.size();
    }

    protected List<BulkFile> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<BulkFile> sublist = getIndex(pendingFiles, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<BulkFile> getIndex(List<BulkFile> files, String prop,
	    boolean asc) {

	if (prop.equals("Lid")) {
	    return sort(files, asc);
	} else {
	    return files;
	}
    }

    private List<BulkFile> sort(List<BulkFile> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<BulkFile>() {

		@Override
		public int compare(BulkFile arg0, BulkFile arg1) {
		    if (Long.valueOf(arg0.getTaskId()) == Long.valueOf(arg1
			    .getTaskId())) {
			return 0;
		    } else if (Long.valueOf(arg0.getTaskId()) < Long
			    .valueOf(arg1.getTaskId())) {
			return -1;
		    } else
			return 1;
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<BulkFile>() {

		@Override
		public int compare(BulkFile arg0, BulkFile arg1) {
		    if (Long.valueOf(arg0.getTaskId()) == Long.valueOf(arg1
			    .getTaskId())) {
			return 0;
		    } else if (Long.valueOf(arg0.getTaskId()) < Long
			    .valueOf(arg1.getTaskId())) {
			return -1;
		    } else
			return 1;
		}
	    });
	}
	return entries;
    }

    public List<BulkFile> searchFiles(
	    FindPendingBulkProcessingFilesRequest req, boolean forcedReload)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(pendingFiles) || forcedReload) {
	    try {
		FindPendingBulkProcessingFilesResponse response = getMobiliserBasePage().wsBulkProcessingClient
			.findPendingBulkProcessingFiles(req);
		if (getMobiliserBasePage().evaluateMobiliserResponse(response))
		    this.pendingFiles = response.getBulkFileList();

	    } catch (Exception e) {
		LOG.error("# Error finding pending files", e);
		DataProviderLoadException dple = new DataProviderLoadException(
			e.getMessage());
		throw dple;
	    }

	}
	return pendingFiles;
    }
}
