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

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashOutBean;

/**
 * This class is used as a data provider for displaying list of cash out details.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashOutDataProvider extends SortableDataProvider<BankCashOutBean> {
	private static final long serialVersionUID = 1L;

	private List<BankCashOutBean> cashOutList;

	public List<BankCashOutBean> getcashOutList() {
		return cashOutList;
	}

	public void setcashOutList(List<BankCashOutBean> cashOutList) {
		this.cashOutList = cashOutList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public BankPortalCashOutDataProvider(String defaultSortProperty) {
		setSort(defaultSortProperty, true);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends BankCashOutBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<BankCashOutBean> model(final BankCashOutBean object) {
		IModel<BankCashOutBean> model = new LoadableDetachableModel<BankCashOutBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected BankCashOutBean load() {
				BankCashOutBean set = null;
				for (BankCashOutBean obj : cashOutList) {
					if (obj.getCustomerId() == object.getCustomerId()) {
						set = obj;
						break;
					}
				}
				return set;
			}
		};

		return new CompoundPropertyModel<BankCashOutBean>(model);
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
	protected List<BankCashOutBean> find(int first, int count, String sortProperty, boolean sortAsc) {

		List<BankCashOutBean> sublist = getIndex(cashOutList, sortProperty, sortAsc).subList(first, first + count);

		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<BankCashOutBean> getIndex(List<BankCashOutBean> cashOutList, String prop, boolean asc) {
		return sort(cashOutList, prop, asc);
	}

	/**
	 * returns the sorted list
	 */
	private List<BankCashOutBean> sort(List<BankCashOutBean> entries, String property, boolean asc) {
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
	private void sortByCustomerId(List<BankCashOutBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<BankCashOutBean>() {

				@Override
				public int compare(BankCashOutBean arg0, BankCashOutBean arg1) {
					return (arg0).getCustomerId().compareTo((arg1).getCustomerId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<BankCashOutBean>() {

				@Override
				public int compare(BankCashOutBean arg0, BankCashOutBean arg1) {
					return (arg1).getCustomerId().compareTo((arg0).getCustomerId());
				}
			});
		}
	}

	/**
	 * returns the sorted list by Mobile Number
	 */
	private void sortByMobileNumber(List<BankCashOutBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<BankCashOutBean>() {

				@Override
				public int compare(BankCashOutBean arg0, BankCashOutBean arg1) {
					return (arg0).getMobileNumber().compareTo((arg1).getMobileNumber());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<BankCashOutBean>() {

				@Override
				public int compare(BankCashOutBean arg0, BankCashOutBean arg1) {
					return (arg1).getMobileNumber().compareTo((arg0).getMobileNumber());
				}
			});
		}
	}

	/**
	 * returns the sorted list by Display Name
	 */
	private void sortByDisplayName(List<BankCashOutBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<BankCashOutBean>() {

				@Override
				public int compare(BankCashOutBean arg0, BankCashOutBean arg1) {
					return (arg0).getMobileNumber().compareTo((arg1).getMobileNumber());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<BankCashOutBean>() {

				@Override
				public int compare(BankCashOutBean arg0, BankCashOutBean arg1) {
					return (arg1).getMobileNumber().compareTo((arg0).getMobileNumber());
				}
			});
		}
	}

}
