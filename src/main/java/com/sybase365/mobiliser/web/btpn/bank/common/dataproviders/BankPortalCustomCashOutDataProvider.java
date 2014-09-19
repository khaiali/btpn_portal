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

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashOutBean;

/**
 * This class is used as a data provider for displaying list of cash out details.
 * 
 * @author Andi Samallangi W
 */
public class BankPortalCustomCashOutDataProvider extends SortableDataProvider<BankCustomCashOutBean> {
	private static final long serialVersionUID = 1L;

	private List<BankCustomCashOutBean> cashOutList;

	public List<BankCustomCashOutBean> getcashOutList() {
		return cashOutList;
	}

	public void setcashOutList(List<BankCustomCashOutBean> cashOutList) {
		this.cashOutList = cashOutList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public BankPortalCustomCashOutDataProvider(String defaultSortProperty) {
		setSort(defaultSortProperty, true);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends BankCustomCashOutBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<BankCustomCashOutBean> model(final BankCustomCashOutBean object) {
		IModel<BankCustomCashOutBean> model = new LoadableDetachableModel<BankCustomCashOutBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected BankCustomCashOutBean load() {
				BankCustomCashOutBean set = null;
				for (BankCustomCashOutBean obj : cashOutList) {
					if (obj.getCustomerId() == object.getCustomerId()) {
						set = obj;
						break;
					}
				}
				return set;
			}
		};

		return new CompoundPropertyModel<BankCustomCashOutBean>(model);
	}

	/**
	 * returns the size of the data list
	 */

	@Override
	public int size() {
		int count = 0;

		if (cashOutList == null) {
			return count;
		}

		return cashOutList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<BankCustomCashOutBean> find(int first, int count, String sortProperty, boolean sortAsc) {

		List<BankCustomCashOutBean> sublist = getIndex(cashOutList, sortProperty, sortAsc).subList(first, first + count);

		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<BankCustomCashOutBean> getIndex(List<BankCustomCashOutBean> cashOutList, String prop, boolean asc) {
		return sort(cashOutList, prop, asc);
	}

	/**
	 * returns the sorted list
	 */
	private List<BankCustomCashOutBean> sort(List<BankCustomCashOutBean> entries, String property, boolean asc) {
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
	private void sortByCustomerId(List<BankCustomCashOutBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<BankCustomCashOutBean>() {

				@Override
				public int compare(BankCustomCashOutBean arg0, BankCustomCashOutBean arg1) {
					return (arg0).getCustomerId().compareTo((arg1).getCustomerId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<BankCustomCashOutBean>() {

				@Override
				public int compare(BankCustomCashOutBean arg0, BankCustomCashOutBean arg1) {
					return (arg1).getCustomerId().compareTo((arg0).getCustomerId());
				}
			});
		}
	}

	/**
	 * returns the sorted list by Mobile Number
	 */
	private void sortByMobileNumber(List<BankCustomCashOutBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<BankCustomCashOutBean>() {

				@Override
				public int compare(BankCustomCashOutBean arg0, BankCustomCashOutBean arg1) {
					return (arg0).getMobileNumber().compareTo((arg1).getMobileNumber());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<BankCustomCashOutBean>() {

				@Override
				public int compare(BankCustomCashOutBean arg0, BankCustomCashOutBean arg1) {
					return (arg1).getMobileNumber().compareTo((arg0).getMobileNumber());
				}
			});
		}
	}

	/**
	 * returns the sorted list by Display Name
	 */
	private void sortByDisplayName(List<BankCustomCashOutBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<BankCustomCashOutBean>() {

				@Override
				public int compare(BankCustomCashOutBean arg0, BankCustomCashOutBean arg1) {
					return (arg0).getMobileNumber().compareTo((arg1).getMobileNumber());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<BankCustomCashOutBean>() {

				@Override
				public int compare(BankCustomCashOutBean arg0, BankCustomCashOutBean arg1) {
					return (arg1).getMobileNumber().compareTo((arg0).getMobileNumber());
				}
			});
		}
	}

}
