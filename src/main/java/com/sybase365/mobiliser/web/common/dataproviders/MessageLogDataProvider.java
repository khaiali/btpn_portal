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

import com.sybase365.mobiliser.util.contract.v5_0.messaging.FindMessageLogsRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.FindMessageLogsResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageLog;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class MessageLogDataProvider extends SortableDataProvider<MessageLog> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
	    .getLogger(MessageLogDataProvider.class);
    private transient List<MessageLog> messageLogEntries = new ArrayList<MessageLog>();
    private MobiliserBasePage mobBasePage;

    public MessageLogDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    @Override
    public Iterator<? extends MessageLog> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    protected List<MessageLog> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<MessageLog> sublist = getIndex(messageLogEntries, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<MessageLog> getIndex(List<MessageLog> messageLogEntries,
	    String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(messageLogEntries, asc);
	} else {
	    return messageLogEntries;
	}
    }

    private List<MessageLog> sort(List<MessageLog> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<MessageLog>() {

		@Override
		public int compare(MessageLog arg0, MessageLog arg1) {
		    return new Long((arg0).getId()).compareTo((arg1).getId());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<MessageLog>() {

		@Override
		public int compare(MessageLog arg0, MessageLog arg1) {
		    return new Long((arg1).getId()).compareTo((arg0).getId());
		}
	    });
	}
	return entries;
    }

    @Override
    public int size() {
	int count = 0;

	if (messageLogEntries == null) {
	    return count;
	}

	return messageLogEntries.size();
    }

    @Override
    public IModel<MessageLog> model(final MessageLog object) {
	IModel<MessageLog> model = new LoadableDetachableModel<MessageLog>() {
	    @Override
	    protected MessageLog load() {
		MessageLog set = null;
		for (MessageLog obj : messageLogEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<MessageLog>(model);
    }

    public void findMessageLogs(FindMessageLogsRequest request)
	    throws DataProviderLoadException {
	FindMessageLogsResponse response = getMobiliserBasePage().wsMessageLogClient
		.findMessageLogs(request);
	this.messageLogEntries = response.getMessageLog();
    }
}
