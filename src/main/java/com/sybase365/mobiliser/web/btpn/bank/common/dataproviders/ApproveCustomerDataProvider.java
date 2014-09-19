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

import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerDataBean;

public class ApproveCustomerDataProvider extends SortableDataProvider<CustomerDataBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<CustomerDataBean> customerDataList;
	
	/**
	 * @return the customerDataList
	 */
	public List<CustomerDataBean> getCustomerDataList() {
		return customerDataList;
	}

	/**
	 * @param customerDataList the customerDataList to set
	 */
	public void setCustomerDataList(List<CustomerDataBean> customerDataList) {
		this.customerDataList = customerDataList;
	}

	public ApproveCustomerDataProvider(String defaultSort,
			List<CustomerDataBean> customerDataList) {
		this.customerDataList = customerDataList;
		setSort(defaultSort, false);
	}

	@Override
	public Iterator<? extends CustomerDataBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}


	@Override
	public IModel<CustomerDataBean> model(final CustomerDataBean object) {
		IModel<CustomerDataBean> model = new LoadableDetachableModel<CustomerDataBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected CustomerDataBean load() {
				CustomerDataBean set = null;
				for (CustomerDataBean beanVar : customerDataList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<CustomerDataBean>(model);
	}
	@Override
	public int size() {
		int count = 0;
		if (customerDataList == null) {
			return count;
		}
		return customerDataList.size();
	}
	/**
	 * returns the sorted list
	 */
	protected List<CustomerDataBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<CustomerDataBean> sublist = getIndex(customerDataList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}
	/**
	 * returns the index
	 */
	protected List<CustomerDataBean> getIndex(List<CustomerDataBean> customerDataListBean, String prop,
		boolean asc) {
		return sort(customerDataListBean, prop, asc);

	}
	/**
	 * returns the sorted list
	 */
	private List<CustomerDataBean> sort(List<CustomerDataBean> entries, String property, boolean asc) {
		if (property.equals("createdBy")) {
			sortByCreatedBy(entries, asc);
		} else if (property.equals("customer")) {
			sortByCustomer(entries, asc);
		} else if (property.equals("status")) {
			sortByStatus(entries, asc);
		} 
		return entries;
	}

	private void sortByCreatedBy(List<CustomerDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<CustomerDataBean>() {

				@Override
				public int compare(CustomerDataBean arg0, CustomerDataBean arg1) {
					return (arg0).getCreatedBy().compareTo((arg1).getCreatedBy());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<CustomerDataBean>() {

				@Override
				public int compare(CustomerDataBean arg0, CustomerDataBean arg1) {
					return (arg1).getCreatedBy().compareTo((arg0).getCreatedBy());
				}
			});
		}
	}

	private void sortByCustomer(List<CustomerDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<CustomerDataBean>() {

				@Override
				public int compare(CustomerDataBean arg0, CustomerDataBean arg1) {
					return (arg0).getCustomer().compareTo((arg1).getCustomer());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<CustomerDataBean>() {

				@Override
				public int compare(CustomerDataBean arg0, CustomerDataBean arg1) {
					return (arg1).getCustomer().compareTo((arg0).getCustomer());
				}
			});
		}
	}

	private void sortByStatus(List<CustomerDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<CustomerDataBean>() {

				@Override
				public int compare(CustomerDataBean arg0, CustomerDataBean arg1) {
					return (arg0).getStatus().compareTo((arg1).getStatus());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<CustomerDataBean>() {

				@Override
				public int compare(CustomerDataBean arg0, CustomerDataBean arg1) {
					return (arg1).getStatus().compareTo((arg0).getStatus());
				}
			});
		}
	}


}
