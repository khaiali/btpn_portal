package com.sybase365.mobiliser.web.btpn.consumer.common.dataproviders;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;

/**
 * This class is used as a data provider for displaying list of accounts in the Manage SubAccounts Page
 * 
 * @author Narasa Reddy
 */
public class ManageSubAccountsDataProvider extends SortableDataProvider<SubAccountsBean> {
	private static final long serialVersionUID = 1L;

	private List<SubAccountsBean> subAccountsList;

	public List<SubAccountsBean> getsubAccountsList() {
		return subAccountsList;
	}

	public void setsubAccountsList(List<SubAccountsBean> subAccountsList) {
		this.subAccountsList = subAccountsList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ManageSubAccountsDataProvider(String defaultSortProperty, final List<SubAccountsBean> subAccountsList) {
		this.subAccountsList = subAccountsList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends SubAccountsBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<SubAccountsBean> model(final SubAccountsBean object) {
		IModel<SubAccountsBean> model = new LoadableDetachableModel<SubAccountsBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected SubAccountsBean load() {
				SubAccountsBean set = null;
				for (SubAccountsBean beanVar : subAccountsList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<SubAccountsBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (subAccountsList == null) {
			return count;
		}
		return subAccountsList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<SubAccountsBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<SubAccountsBean> sublist = getIndex(subAccountsList, sortProperty, sortAsc).subList(first, first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<SubAccountsBean> getIndex(List<SubAccountsBean> subAccountsList, String prop, boolean asc) {
		return sort(subAccountsList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<SubAccountsBean> sort(List<SubAccountsBean> entries, String property, boolean asc) {
		if (property.equals("name")) {
			sortByName(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByDate
	 */
	private void sortByName(List<SubAccountsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<SubAccountsBean>() {

				@Override
				public int compare(SubAccountsBean arg0, SubAccountsBean arg1) {
					return (arg0).getName().compareTo((arg1).getName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<SubAccountsBean>() {

				@Override
				public int compare(SubAccountsBean arg0, SubAccountsBean arg1) {
					return (arg1).getName().compareTo((arg0).getName());
				}
			});
		}
	}

}
