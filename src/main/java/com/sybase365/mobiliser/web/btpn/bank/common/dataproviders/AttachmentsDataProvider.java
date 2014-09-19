package com.sybase365.mobiliser.web.btpn.bank.common.dataproviders;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.sybase365.mobiliser.web.btpn.bank.beans.NotificationAttachmentsBean;

/**
 * This class is used as a data provider for displaying list of attachments in the Consumer Registration Page
 * 
 * @author Vikram Gunda
 */
public class AttachmentsDataProvider extends SortableDataProvider<NotificationAttachmentsBean> {
	private static final long serialVersionUID = 1L;

	private List<NotificationAttachmentsBean> attachmentsList;

	public List<NotificationAttachmentsBean> getAttachmentsList() {
		return attachmentsList;
	}

	public void setAttachmentsList(List<NotificationAttachmentsBean> attachmentsList) {
		this.attachmentsList = attachmentsList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public AttachmentsDataProvider(String defaultSortProperty, final List<NotificationAttachmentsBean> attachmentsList) {
		this.attachmentsList = attachmentsList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends NotificationAttachmentsBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<NotificationAttachmentsBean> model(final NotificationAttachmentsBean object) {
		IModel<NotificationAttachmentsBean> model = new LoadableDetachableModel<NotificationAttachmentsBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected NotificationAttachmentsBean load() {
				NotificationAttachmentsBean set = null;
				for (NotificationAttachmentsBean beanVar : attachmentsList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<NotificationAttachmentsBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (attachmentsList == null) {
			return count;
		}
		return attachmentsList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<NotificationAttachmentsBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<NotificationAttachmentsBean> sublist = getIndex(attachmentsList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<NotificationAttachmentsBean> getIndex(List<NotificationAttachmentsBean> attachmentsList,
		String prop, boolean asc) {
		return sort(attachmentsList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<NotificationAttachmentsBean> sort(List<NotificationAttachmentsBean> entries, String property,
		boolean asc) {
		if (property.equals("fileName")) {
			sortByFileName(entries, asc);
		} else {
			sortByUploadedDate(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByUploadedDate
	 */
	private void sortByUploadedDate(List<NotificationAttachmentsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<NotificationAttachmentsBean>() {

				@Override
				public int compare(NotificationAttachmentsBean arg0, NotificationAttachmentsBean arg1) {
					return (arg0).getUploadedDate().compareTo((arg1).getUploadedDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<NotificationAttachmentsBean>() {

				@Override
				public int compare(NotificationAttachmentsBean arg0, NotificationAttachmentsBean arg1) {
					return (arg1).getUploadedDate().compareTo((arg0).getUploadedDate());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByFileName
	 */
	private void sortByFileName(List<NotificationAttachmentsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<NotificationAttachmentsBean>() {

				@Override
				public int compare(NotificationAttachmentsBean arg0, NotificationAttachmentsBean arg1) {
					return (arg0).getFileName().compareTo((arg1).getFileName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<NotificationAttachmentsBean>() {

				@Override
				public int compare(NotificationAttachmentsBean arg0, NotificationAttachmentsBean arg1) {
					return (arg1).getFileName().compareTo((arg0).getFileName());
				}
			});
		}
	}
}
