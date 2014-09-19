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

import com.sybase365.mobiliser.web.btpn.bank.beans.SalaryDataErrorBean;

public class SalaryDataErrorProvider extends SortableDataProvider<SalaryDataErrorBean> {
	private static final long serialVersionUID = 1L;

	private List<SalaryDataErrorBean> salaryDataErrorList;

	public List<SalaryDataErrorBean> getSalaryDataErrorList() {
		return salaryDataErrorList;
	}

	public void setSalaryDataErrorList(List<SalaryDataErrorBean> salaryDataErrorList) {
		this.salaryDataErrorList = salaryDataErrorList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public SalaryDataErrorProvider(String defaultSortProperty, final List<SalaryDataErrorBean> salaryDataErrorList) {
		this.salaryDataErrorList = salaryDataErrorList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends SalaryDataErrorBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<SalaryDataErrorBean> model(final SalaryDataErrorBean object) {
		IModel<SalaryDataErrorBean> model = new LoadableDetachableModel<SalaryDataErrorBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected SalaryDataErrorBean load() {
				SalaryDataErrorBean set = null;
				for (SalaryDataErrorBean beanVar : salaryDataErrorList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<SalaryDataErrorBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (salaryDataErrorList == null) {
			return count;
		}
		return salaryDataErrorList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<SalaryDataErrorBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<SalaryDataErrorBean> sublist = getIndex(salaryDataErrorList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<SalaryDataErrorBean> getIndex(List<SalaryDataErrorBean> customerList, String prop, boolean asc) {
		return sort(customerList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<SalaryDataErrorBean> sort(List<SalaryDataErrorBean> entries, String property, boolean asc) {
		if (property.equals("lineNo")) {
			sortByLineNo(entries, asc);
		} else if (property.equals("errorRecord")) {
			sortByErrorRecord(entries, asc);
		} else {
			sortByDescription(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByMaker
	 */
	private void sortByLineNo(List<SalaryDataErrorBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<SalaryDataErrorBean>() {

				@Override
				public int compare(SalaryDataErrorBean arg0, SalaryDataErrorBean arg1) {
					return (arg0).getLineNo().compareTo((arg1).getLineNo());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<SalaryDataErrorBean>() {

				@Override
				public int compare(SalaryDataErrorBean arg0, SalaryDataErrorBean arg1) {
					return (arg1).getLineNo().compareTo((arg0).getLineNo());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByStatus
	 */
	private void sortByErrorRecord(List<SalaryDataErrorBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<SalaryDataErrorBean>() {

				@Override
				public int compare(SalaryDataErrorBean arg0, SalaryDataErrorBean arg1) {
					return (arg0).getErrorRecord().compareTo((arg1).getErrorRecord());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<SalaryDataErrorBean>() {

				@Override
				public int compare(SalaryDataErrorBean arg0, SalaryDataErrorBean arg1) {
					return (arg1).getErrorRecord().compareTo((arg0).getErrorRecord());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByErrorDescription
	 */
	private void sortByDescription(List<SalaryDataErrorBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<SalaryDataErrorBean>() {

				@Override
				public int compare(SalaryDataErrorBean arg0, SalaryDataErrorBean arg1) {
					return (arg0).getErrorDescription().compareTo((arg1).getErrorDescription());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<SalaryDataErrorBean>() {

				@Override
				public int compare(SalaryDataErrorBean arg0, SalaryDataErrorBean arg1) {
					return (arg1).getErrorDescription().compareTo((arg0).getErrorDescription());
				}
			});
		}
	}
}
