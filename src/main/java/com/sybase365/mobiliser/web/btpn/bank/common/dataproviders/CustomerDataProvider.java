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

import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;

public class CustomerDataProvider extends SortableDataProvider<CustomerRegistrationBean> {
	private static final long serialVersionUID = 1L;

	private List<CustomerRegistrationBean> customerList;

	public List<CustomerRegistrationBean> getcustomerList() {
		return customerList;
	}

	public void setcustomerList(List<CustomerRegistrationBean> customerList) {
		this.customerList = customerList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public CustomerDataProvider(String defaultSortProperty,
		final List<CustomerRegistrationBean> customerList) {
		this.customerList = customerList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends CustomerRegistrationBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<CustomerRegistrationBean> model(final CustomerRegistrationBean object) {
		IModel<CustomerRegistrationBean> model = new LoadableDetachableModel<CustomerRegistrationBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected CustomerRegistrationBean load() {
				CustomerRegistrationBean set = null;
				for (CustomerRegistrationBean beanVar : customerList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<CustomerRegistrationBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (customerList == null) {
			return count;
		}
		return customerList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<CustomerRegistrationBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<CustomerRegistrationBean> sublist = getIndex(customerList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<CustomerRegistrationBean> getIndex(List<CustomerRegistrationBean> customerList, String prop,
		boolean asc) {
		return sort(customerList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<CustomerRegistrationBean> sort(List<CustomerRegistrationBean> entries, String property, boolean asc) {
		if (property.equals("msisdn")) {
			sortByMsisdn(entries, asc);
		} else if (property.equals("displayName")) {
			sortByDisplayName(entries, asc);
		} else if (property.equals("customerType")) {
			sortByCustomerType(entries, asc);
		} 
		return entries;
	}

	/**
	 * returns the sorted list by sortByMaker
	 */
	private void sortByMsisdn(List<CustomerRegistrationBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<CustomerRegistrationBean>() {

				@Override
				public int compare(CustomerRegistrationBean arg0, CustomerRegistrationBean arg1) {
					return (arg0).getMobileNumber().compareTo((arg1).getMobileNumber());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<CustomerRegistrationBean>() {

				@Override
				public int compare(CustomerRegistrationBean arg0, CustomerRegistrationBean arg1) {
					return (arg1).getMobileNumber().compareTo((arg0).getMobileNumber());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByTransactionId
	 */
	private void sortByDisplayName(List<CustomerRegistrationBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<CustomerRegistrationBean>() {

				@Override
				public int compare(CustomerRegistrationBean arg0, CustomerRegistrationBean arg1) {
					return (arg0).getName().compareTo((arg1).getName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<CustomerRegistrationBean>() {

				@Override
				public int compare(CustomerRegistrationBean arg0, CustomerRegistrationBean arg1) {
					return (arg1).getName().compareTo((arg0).getName());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByTransactionName
	 */
	private void sortByCustomerType(List<CustomerRegistrationBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<CustomerRegistrationBean>() {

				@Override
				public int compare(CustomerRegistrationBean arg0, CustomerRegistrationBean arg1) {
					return (arg0).getCustomerType().compareTo((arg1).getCustomerType());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<CustomerRegistrationBean>() {

				@Override
				public int compare(CustomerRegistrationBean arg0, CustomerRegistrationBean arg1) {
					return (arg1).getCustomerType().compareTo((arg0).getCustomerType());
				}
			});
		}
	}


}
