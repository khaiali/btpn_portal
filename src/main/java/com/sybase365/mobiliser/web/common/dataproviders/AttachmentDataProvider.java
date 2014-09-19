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

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetAttachmentsByCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetAttachmentsByCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Attachment;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class AttachmentDataProvider extends SortableDataProvider<Attachment> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(AttachmentDataProvider.class);
    private transient List<Attachment> attachments = new ArrayList<Attachment>();
    private MobiliserBasePage mobBasePage;

    public AttachmentDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns Attachment starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<Attachment> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>Attachment</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (attachments == null) {
	    return count;
	}

	return attachments.size();
    }

    @Override
    public final IModel<Attachment> model(final Attachment object) {
	IModel<Attachment> model = new LoadableDetachableModel<Attachment>() {

	    @Override
	    protected Attachment load() {
		Attachment set = null;
		for (Attachment obj : attachments) {
		    if (obj == object) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<Attachment>(model);
    }

    public void loadCustomerAttachments(Long customerId, boolean forcedReload)
	    throws DataProviderLoadException {

	if ((attachments == null || forcedReload)
		&& PortalUtils.exists(customerId)) {

	    try {
		GetAttachmentsByCustomerRequest attRequest = this
			.getMobiliserBasePage().getNewMobiliserRequest(
				GetAttachmentsByCustomerRequest.class);

		attRequest.setCustomerId(customerId);

		GetAttachmentsByCustomerResponse attResponse = this
			.getMobiliserBasePage().wsAttachmentClient
			.getAttachmentsByCustomer(attRequest);

		if (getMobiliserBasePage().evaluateMobiliserResponse(
			attResponse)) {
		    List<Attachment> allEntries = attResponse.getAttachments();
		    if (PortalUtils.exists(allEntries)) {
			attachments = allEntries;
		    }
		}
	    } catch (Exception e) {
		LOG.error("# Error loading attachments", e);
	    }
	}
    }

    public List<Attachment> getAttachments() {
	return attachments;
    }

    protected List<Attachment> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<Attachment> sublist = getIndex(attachments, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<Attachment> getIndex(List<Attachment> attachments,
	    String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(attachments, asc);
	} else {
	    return attachments;
	}
    }

    private List<Attachment> sort(List<Attachment> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<Attachment>() {

		@Override
		public int compare(Attachment arg0, Attachment arg1) {
		    return (arg0).getName().toString()
			    .compareTo((arg1).getName().toString());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<Attachment>() {

		@Override
		public int compare(Attachment arg0, Attachment arg1) {
		    return (arg1).getName().toString()
			    .compareTo((arg0).getName().toString());
		}
	    });
	}
	return entries;
    }
}
