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

import com.sybase365.mobiliser.money.contract.v5_0.system.GetJobsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetJobsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Job;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class JobDataProvider extends SortableDataProvider<Job> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(JobDataProvider.class);

    protected transient List<Job> jobEntries = new ArrayList<Job>();

    private MobiliserBasePage mobBasePage;

    public JobDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    @Override
    public Iterator<? extends Job> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    protected List<Job> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<Job> sublist = getIndex(jobEntries, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<Job> getIndex(List<Job> jobEntries, String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(jobEntries, asc);
	} else {
	    return jobEntries;
	}
    }

    private List<Job> sort(List<Job> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<Job>() {

		@Override
		public int compare(Job arg0, Job arg1) {
		    return arg0.getImplementationUrl().compareTo(
			    (arg1).getImplementationUrl());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<Job>() {

		@Override
		public int compare(Job arg0, Job arg1) {
		    return arg1.getImplementationUrl().compareTo(
			    (arg0).getImplementationUrl());
		}
	    });
	}
	return entries;
    }

    @Override
    public IModel<Job> model(final Job object) {
	IModel<Job> model = new LoadableDetachableModel<Job>() {
	    @Override
	    protected Job load() {
		Job set = null;
		for (Job obj : jobEntries) {
		    if (obj.getJobId() == object.getJobId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<Job>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (jobEntries == null) {
	    return count;
	}

	return jobEntries.size();
    }

    public void loadJobs() throws DataProviderLoadException {

	try {
	    GetJobsRequest request = mobBasePage
		    .getNewMobiliserRequest(GetJobsRequest.class);

	    GetJobsResponse response = getMobiliserBasePage().wsJobClient
		    .getJobs(request);

	    this.jobEntries = response.getJob();
	} catch (Exception e) {
	    throw new DataProviderLoadException();
	}
    }
}
