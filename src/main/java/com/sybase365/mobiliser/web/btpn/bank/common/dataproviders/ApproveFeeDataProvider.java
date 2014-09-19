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

import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveFeeBean;

/**
 * This class is used as a data provider for displaying list of attachments in the Consumer Registration Page
 * 
 * @author Vikram Gunda
 */
public class ApproveFeeDataProvider extends SortableDataProvider<ApproveFeeBean> {
	private static final long serialVersionUID = 1L;

	private List<ApproveFeeBean> attachmentsList;

	public List<ApproveFeeBean> getAttachmentsList() {
		return attachmentsList;
	}

	public void setAttachmentsList(List<ApproveFeeBean> attachmentsList) {
		this.attachmentsList = attachmentsList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ApproveFeeDataProvider(String defaultSortProperty, final List<ApproveFeeBean> attachmentsList) {
		this.attachmentsList = attachmentsList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ApproveFeeBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ApproveFeeBean> model(final ApproveFeeBean object) {
		IModel<ApproveFeeBean> model = new LoadableDetachableModel<ApproveFeeBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ApproveFeeBean load() {
				ApproveFeeBean set = null;
				for (ApproveFeeBean beanVar : attachmentsList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ApproveFeeBean>(model);
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
	protected List<ApproveFeeBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ApproveFeeBean> sublist = getIndex(attachmentsList, sortProperty, sortAsc).subList(first, first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ApproveFeeBean> getIndex(List<ApproveFeeBean> attachmentsList, String prop, boolean asc) {
		return sort(attachmentsList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<ApproveFeeBean> sort(List<ApproveFeeBean> entries, String property, boolean asc) {
		sortByRequestDate(entries, asc);
		return entries;
	}

	/**
	 * returns the sorted list by sortByRequestDate
	 */
	private void sortByRequestDate(List<ApproveFeeBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveFeeBean>() {

				@Override
				public int compare(ApproveFeeBean arg0, ApproveFeeBean arg1) {
					return (arg0).getRequestDate().compareTo((arg1).getRequestDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveFeeBean>() {

				@Override
				public int compare(ApproveFeeBean arg0, ApproveFeeBean arg1) {
					return (arg1).getRequestDate().compareTo((arg0).getRequestDate());
				}
			});
		}
	}
}
