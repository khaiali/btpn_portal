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

import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveHolidayBean;

/**
 * This class is used as a data provider for displaying list of holiday calendar details in the Bank Portal
 * 
 * @author Narasa Reddy.
 */
public class ApprovalHolidayDataProvider extends SortableDataProvider<ApproveHolidayBean> {
	private static final long serialVersionUID = 1L;

	private List<ApproveHolidayBean> holidayList;

	public List<ApproveHolidayBean> gettxnReversalList() {
		return holidayList;
	}

	public void settxnReversalList(List<ApproveHolidayBean> txnReversalList) {
		this.holidayList = txnReversalList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ApprovalHolidayDataProvider(String defaultSortProperty, final List<ApproveHolidayBean> txnReversalList) {
		this.holidayList = txnReversalList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ApproveHolidayBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ApproveHolidayBean> model(final ApproveHolidayBean object) {
		IModel<ApproveHolidayBean> model = new LoadableDetachableModel<ApproveHolidayBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ApproveHolidayBean load() {
				ApproveHolidayBean set = null;
				for (ApproveHolidayBean beanVar : holidayList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ApproveHolidayBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (holidayList == null) {
			return count;
		}
		return holidayList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<ApproveHolidayBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ApproveHolidayBean> sublist = getIndex(holidayList, sortProperty, sortAsc).subList(first, first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ApproveHolidayBean> getIndex(List<ApproveHolidayBean> txnReversalList, String prop, boolean asc) {
		return sort(txnReversalList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<ApproveHolidayBean> sort(List<ApproveHolidayBean> entries, String property, boolean asc) {
		if (property.equals("createdBy")) {
			sortCreatedBy(entries, asc);
		} else if (property.equals("description")) {
			sortByDescription(entries, asc);
		} else if (property.equals("status")) {
			sortByStatus(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list sortCreatedBy
	 */
	private void sortCreatedBy(List<ApproveHolidayBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveHolidayBean>() {

				@Override
				public int compare(ApproveHolidayBean arg0, ApproveHolidayBean arg1) {
					return (arg0).getCreatedBy().compareTo((arg1).getCreatedBy());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveHolidayBean>() {

				@Override
				public int compare(ApproveHolidayBean arg0, ApproveHolidayBean arg1) {
					return (arg1).getCreatedBy().compareTo((arg0).getCreatedBy());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByDescription
	 */
	private void sortByDescription(List<ApproveHolidayBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveHolidayBean>() {

				@Override
				public int compare(ApproveHolidayBean arg0, ApproveHolidayBean arg1) {
					return (arg0).getDescription().compareTo((arg1).getDescription());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveHolidayBean>() {

				@Override
				public int compare(ApproveHolidayBean arg0, ApproveHolidayBean arg1) {
					return (arg1).getDescription().compareTo((arg0).getDescription());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByStatus
	 */
	private void sortByStatus(List<ApproveHolidayBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveHolidayBean>() {

				@Override
				public int compare(ApproveHolidayBean arg0, ApproveHolidayBean arg1) {
					return (arg0).getStatus().compareTo((arg1).getStatus());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveHolidayBean>() {

				@Override
				public int compare(ApproveHolidayBean arg0, ApproveHolidayBean arg1) {
					return (arg1).getStatus().compareTo((arg0).getStatus());
				}
			});
		}
	}

}
