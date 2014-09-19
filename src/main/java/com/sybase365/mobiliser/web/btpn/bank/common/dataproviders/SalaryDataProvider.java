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

import com.sybase365.mobiliser.web.btpn.bank.beans.SalaryDataBean;

public class SalaryDataProvider extends SortableDataProvider<SalaryDataBean> {
	private static final long serialVersionUID = 1L;

	private List<SalaryDataBean> salaryDataList;	
	
	public List<SalaryDataBean> getSalaryDataList() {
		return salaryDataList;
	}

	public void setSalaryDataList(List<SalaryDataBean> salaryDataList) {
		this.salaryDataList = salaryDataList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public SalaryDataProvider(String defaultSortProperty, final List<SalaryDataBean> customerList) {
		this.salaryDataList = customerList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends SalaryDataBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<SalaryDataBean> model(final SalaryDataBean object) {
		IModel<SalaryDataBean> model = new LoadableDetachableModel<SalaryDataBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected SalaryDataBean load() {
				SalaryDataBean set = null;
				for (SalaryDataBean beanVar : salaryDataList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<SalaryDataBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (salaryDataList == null) {
			return count;
		}
		return salaryDataList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<SalaryDataBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<SalaryDataBean> sublist = getIndex(salaryDataList, sortProperty, sortAsc).subList(first, first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<SalaryDataBean> getIndex(List<SalaryDataBean> customerList, String prop, boolean asc) {
		return sort(customerList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<SalaryDataBean> sort(List<SalaryDataBean> entries, String property, boolean asc) {
		if (property.equals("fileName")) {
			sortByFileName(entries, asc);
		} else {
			sortByStatus(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByMaker
	 */
	private void sortByFileName(List<SalaryDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<SalaryDataBean>() {

				@Override
				public int compare(SalaryDataBean arg0, SalaryDataBean arg1) {
					return (arg0).getName().compareTo((arg1).getName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<SalaryDataBean>() {

				@Override
				public int compare(SalaryDataBean arg0, SalaryDataBean arg1) {
					return (arg1).getName().compareTo((arg0).getName());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByStatus
	 */
	private void sortByStatus(List<SalaryDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<SalaryDataBean>() {

				@Override
				public int compare(SalaryDataBean arg0, SalaryDataBean arg1) {
					return (arg0).getStatus().compareTo((arg1).getStatus());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<SalaryDataBean>() {

				@Override
				public int compare(SalaryDataBean arg0, SalaryDataBean arg1) {
					return (arg1).getStatus().compareTo((arg0).getStatus());
				}
			});
		}
	}

}
