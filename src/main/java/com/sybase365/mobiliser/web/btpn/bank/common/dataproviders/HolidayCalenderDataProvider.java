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

import com.sybase365.mobiliser.web.btpn.bank.beans.HolidayListBean;
/**
 * 
 * @author Sreenivasulu
 *
 */

public class HolidayCalenderDataProvider extends SortableDataProvider<HolidayListBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<HolidayListBean> holidayList;
	
	
	/**
	 * @return the holidayList
	 */
	public List<HolidayListBean> getHolidayList() {
		return holidayList;
	}

	/**
	 * @param holidayList the holidayList to set
	 */
	public void setHolidayList(List<HolidayListBean> holidayList) {
		this.holidayList = holidayList;
	}

	public HolidayCalenderDataProvider(String defaultSort,
			List<HolidayListBean> holidayList) {
		this.holidayList = holidayList;
		setSort(defaultSort, false);
	}

	@Override
	public Iterator<? extends HolidayListBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}


	@Override
	public IModel<HolidayListBean> model(final HolidayListBean object) {
		IModel<HolidayListBean> model = new LoadableDetachableModel<HolidayListBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected HolidayListBean load() {
				HolidayListBean set = null;
				for (HolidayListBean beanVar : holidayList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<HolidayListBean>(model);
	}
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
	protected List<HolidayListBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<HolidayListBean> sublist = getIndex(holidayList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}
	/**
	 * returns the index
	 */
	protected List<HolidayListBean> getIndex(List<HolidayListBean> holidayListBean, String prop,
		boolean asc) {
		return sort(holidayListBean, prop, asc);

	}
	/**
	 * returns the sorted list
	 */
	private List<HolidayListBean> sort(List<HolidayListBean> entries, String property, boolean asc) {
		if (property.equals("fromDate")) {
			sortByFromDate(entries, asc);
		} else if (property.equals("toDate")) {
			sortByTodate(entries, asc);
		} else if (property.equals("description")) {
			sortByDescription(entries, asc);
		} 
		return entries;
	}

	private void sortByDescription(List<HolidayListBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<HolidayListBean>() {

				@Override
				public int compare(HolidayListBean arg0, HolidayListBean arg1) {
					return (arg0).getDescription().compareTo((arg1).getDescription());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<HolidayListBean>() {

				@Override
				public int compare(HolidayListBean arg0, HolidayListBean arg1) {
					return (arg1).getDescription().compareTo((arg0).getDescription());
				}
			});
		}
	}

	private void sortByTodate(List<HolidayListBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<HolidayListBean>() {

				@Override
				public int compare(HolidayListBean arg0, HolidayListBean arg1) {
					return (arg0).getToDate().compareTo((arg1).getToDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<HolidayListBean>() {

				@Override
				public int compare(HolidayListBean arg0, HolidayListBean arg1) {
					return (arg1).getToDate().compareTo((arg0).getToDate());
				}
			});
		}
	}

	private void sortByFromDate(List<HolidayListBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<HolidayListBean>() {

				@Override
				public int compare(HolidayListBean arg0, HolidayListBean arg1) {
					return (arg0).getFromDate().compareTo((arg1).getFromDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<HolidayListBean>() {

				@Override
				public int compare(HolidayListBean arg0, HolidayListBean arg1) {
					return (arg1).getFromDate().compareTo((arg0).getFromDate());
				}
			});
		}
	}


}
