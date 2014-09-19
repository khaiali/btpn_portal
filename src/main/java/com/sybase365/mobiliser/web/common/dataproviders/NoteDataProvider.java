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

import com.sybase365.mobiliser.money.contract.v5_0.audit.GetNotesByCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.audit.GetNotesByCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.audit.beans.Note;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class NoteDataProvider extends SortableDataProvider<Note> {
    private static final Logger LOG = LoggerFactory
	    .getLogger(NoteDataProvider.class);
    private transient List<Note> noteEntries = new ArrayList<Note>();
    private MobiliserBasePage mobBasePage;

    public NoteDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    @Override
    public Iterator<? extends Note> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    protected List<Note> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<Note> sublist = getIndex(noteEntries, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<Note> getIndex(List<Note> noteEntries, String prop,
	    boolean asc) {

	if (prop.equals("name")) {
	    return sort(noteEntries, asc);
	} else {
	    return noteEntries;
	}
    }

    private List<Note> sort(List<Note> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<Note>() {

		@Override
		public int compare(Note arg0, Note arg1) {
		    return (arg0).getId().compareTo((arg1).getId());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<Note>() {

		@Override
		public int compare(Note arg0, Note arg1) {
		    return (arg1).getId().compareTo((arg0).getId());
		}
	    });
	}
	return entries;
    }

    @Override
    public IModel<Note> model(final Note object) {
	IModel<Note> model = new LoadableDetachableModel<Note>() {
	    @Override
	    protected Note load() {
		Note set = null;
		for (Note obj : noteEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<Note>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (noteEntries == null) {
	    return count;
	}

	return noteEntries.size();
    }

    public void findContactNotes(GetNotesByCustomerRequest request)
	    throws DataProviderLoadException {

	GetNotesByCustomerResponse response = getMobiliserBasePage().wsNoteClient
		.getNoteByCustomer(request);
	this.noteEntries = response.getNote();

    }
}
