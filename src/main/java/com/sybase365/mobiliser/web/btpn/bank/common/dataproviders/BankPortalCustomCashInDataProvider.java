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

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;


public class BankPortalCustomCashInDataProvider extends SortableDataProvider<BankCustomCashInBean> {
	private static final long serialVersionUID = 1L;

	private List<BankCustomCashInBean> cashInList;

	public List<BankCustomCashInBean> getcashInList() {
		return cashInList;
	}

	public void setcashInList(List<BankCustomCashInBean> cashInList) {
		this.cashInList = cashInList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public BankPortalCustomCashInDataProvider(String defaultSortProperty) {
		setSort(defaultSortProperty, true);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends BankCustomCashInBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	public IModel<BankCustomCashInBean> model(final BankCustomCashInBean object) {
		IModel<BankCustomCashInBean> model = new LoadableDetachableModel<BankCustomCashInBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected BankCustomCashInBean load() {
				BankCustomCashInBean set = null;
				for (BankCustomCashInBean obj : cashInList) {
					if (obj.getCustomerId() == object.getCustomerId()) {
						set = obj;
						break;
					}
				}
				return set;
			}
		};

		return new CompoundPropertyModel<BankCustomCashInBean>(model);
	}

	/**
	 * returns the size of the data list
	 */

	@Override
	public int size() {
		int count = 0;

		if (cashInList == null) {
			return count;
		}

		return cashInList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<BankCustomCashInBean> find(int first, int count, String sortProperty, boolean sortAsc) {

		List<BankCustomCashInBean> sublist = getIndex(cashInList, sortProperty, sortAsc).subList(first, first + count);

		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<BankCustomCashInBean> getIndex(List<BankCustomCashInBean> cashInList, String prop, boolean asc) {

		return sort(cashInList, prop, asc);
	}

	/**
	 * returns the sorted list
	 */
	private List<BankCustomCashInBean> sort(List<BankCustomCashInBean> entries, String property, boolean asc) {
		if (property.equals("customerId")) {
			sortByCustomerId(entries, asc);
		} else if (property.equals("mobileNumber")) {
			sortByMobileNumber(entries, asc);
		} else {
			sortByDisplayName(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by customer ID
	 */
	private void sortByCustomerId(List<BankCustomCashInBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<BankCustomCashInBean>() {

				@Override
				public int compare(BankCustomCashInBean arg0, BankCustomCashInBean arg1) {
					return (arg0).getCustomerId().compareTo((arg1).getCustomerId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<BankCustomCashInBean>() {

				@Override
				public int compare(BankCustomCashInBean arg0, BankCustomCashInBean arg1) {
					return (arg1).getCustomerId().compareTo((arg0).getCustomerId());
				}
			});
		}
	}

	/**
	 * returns the sorted list by Mobile Number
	 */
	private void sortByMobileNumber(List<BankCustomCashInBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<BankCustomCashInBean>() {

				@Override
				public int compare(BankCustomCashInBean arg0, BankCustomCashInBean arg1) {
					return (arg0).getMsisdn().compareTo((arg1).getMsisdn());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<BankCustomCashInBean>() {

				@Override
				public int compare(BankCustomCashInBean arg0, BankCustomCashInBean arg1) {
					return (arg1).getMsisdn().compareTo((arg0).getMsisdn());
				}
			});
		}
	}

	/**
	 * returns the sorted list by Display Name
	 */
	private void sortByDisplayName(List<BankCustomCashInBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<BankCustomCashInBean>() {

				@Override
				public int compare(BankCustomCashInBean arg0, BankCustomCashInBean arg1) {
					return (arg0).getMsisdn().compareTo((arg1).getMsisdn());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<BankCustomCashInBean>() {

				@Override
				public int compare(BankCustomCashInBean arg0, BankCustomCashInBean arg1) {
					return (arg1).getMsisdn().compareTo((arg0).getMsisdn());
				}
			});
		}
	}
}
