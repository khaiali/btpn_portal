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

import com.sybase365.mobiliser.util.contract.v5_0.messaging.FindAttachmentsRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.FindAttachmentsResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.GetDetailedTemplateRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.GetDetailedTemplateResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.AttachmentSearchCriteria;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageAttachment;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class MessageAttachmentDataProvider extends
	SortableDataProvider<MessageAttachment> {
    private static final long serialVersionUID = 1L;
    private transient List<MessageAttachment> msgAttachmentEntries = new ArrayList<MessageAttachment>();
    private MobiliserBasePage mobBasePage;

    public MessageAttachmentDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    /**
     * Returns MessageAttachment starting with index <code>first</code> and
     * ending with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<MessageAttachment> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>MessageAttachment</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (msgAttachmentEntries == null) {
	    return count;
	}

	return msgAttachmentEntries.size();
    }

    @Override
    public final IModel<MessageAttachment> model(final MessageAttachment object) {
	IModel<MessageAttachment> model = new LoadableDetachableModel<MessageAttachment>() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected MessageAttachment load() {
		MessageAttachment set = null;
		for (MessageAttachment obj : msgAttachmentEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<MessageAttachment>(model);
    }

    public void findAttachmentsList(AttachmentSearchCriteria criteria,
	    boolean forcedReload) throws DataProviderLoadException {
	if (msgAttachmentEntries == null || forcedReload) {

	    List<MessageAttachment> allEntries = findMessageAttachmentList(criteria);

	    if (PortalUtils.exists(allEntries)) {
		msgAttachmentEntries = allEntries;
	    }
	}

    }

    private List<MessageAttachment> findMessageAttachmentList(
	    AttachmentSearchCriteria criteria) throws DataProviderLoadException {
	List<MessageAttachment> msgAttachmentList = new ArrayList<MessageAttachment>();
	try {
	    FindAttachmentsRequest request = mobBasePage
		    .getNewMobiliserRequest(FindAttachmentsRequest.class);
	    request.setCriteria(criteria);
	    FindAttachmentsResponse response = mobBasePage.wsTemplateClient
		    .findAttachments(request);
	    if (mobBasePage.evaluateMobiliserResponse(response)) {
		msgAttachmentList = response.getAttachments();
	    }

	} catch (Exception e) {
	    DataProviderLoadException dple = new DataProviderLoadException(
		    e.getMessage());
	    dple.setStackTrace(e.getStackTrace());
	    throw dple;
	}

	return msgAttachmentList;
    }

    public void loadMsgAttachmentsList(Long messageId, boolean forcedReload)
	    throws DataProviderLoadException {

	if (msgAttachmentEntries == null || forcedReload) {

	    List<MessageAttachment> allEntries = getMessageAttachmentList(messageId);

	    if (PortalUtils.exists(allEntries)) {
		msgAttachmentEntries = allEntries;
	    }
	}
    }

    private List<MessageAttachment> getMessageAttachmentList(Long messageId) {
	List<MessageAttachment> msgAttachmentList = new ArrayList<MessageAttachment>();
	try {
	    GetDetailedTemplateRequest request = mobBasePage
		    .getNewMobiliserRequest(GetDetailedTemplateRequest.class);
	    request.setTemplateId(messageId);
	    GetDetailedTemplateResponse response = mobBasePage.wsTemplateClient
		    .getDetailedTemplate(request);
	    if (mobBasePage.evaluateMobiliserResponse(response)) {
		msgAttachmentList = response.getTemplate().getAttachments();
	    }

	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return msgAttachmentList;
    }

    protected List<MessageAttachment> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<MessageAttachment> sublist = getIndex(msgAttachmentEntries,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<MessageAttachment> getIndex(
	    List<MessageAttachment> MessageAttachmentEntries, String prop,
	    boolean asc) {

	if (prop.equals("name")) {
	    return sort(MessageAttachmentEntries, asc);
	} else {
	    return MessageAttachmentEntries;
	}
    }

    private List<MessageAttachment> sort(List<MessageAttachment> entries,
	    boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<MessageAttachment>() {

		@Override
		public int compare(MessageAttachment arg0,
			MessageAttachment arg1) {
		    return (arg0).getName().compareTo((arg1).getName());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<MessageAttachment>() {

		@Override
		public int compare(MessageAttachment arg0,
			MessageAttachment arg1) {
		    return (arg1).getName().compareTo((arg0).getName());
		}
	    });
	}
	return entries;
    }

}
