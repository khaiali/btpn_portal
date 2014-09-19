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

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashinBean;

/**
 * This class is used as a data provider for displaying list of cash in details.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashInDataProvider extends SortableDataProvider<BankCashinBean> {
	private static final long serialVersionUID = 1L;

	private List<BankCashinBean> cashInList;

	public List<BankCashinBean> getcashInList() {
		return cashInList;
	}

	public void setcashInList(List<BankCashinBean> cashInList) {
		this.cashInList = cashInList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public BankPortalCashInDataProvider(String defaultSortProperty) {
		setSort(defaultSortProperty, true);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends BankCashinBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<BankCashinBean> model(final BankCashinBean object) {
		IModel<BankCashinBean> model = new LoadableDetachableModel<BankCashinBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected BankCashinBean load() {
				BankCashinBean set = null;
				for (BankCashinBean obj : cashInList) {
					if (obj.getCustomerId() == object.getCustomerId()) {
						set = obj;
						break;
					}
				}
				return set;
			}
		};

		return new CompoundPropertyModel<BankCashinBean>(model);
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
	protected List<BankCashinBean> find(int first, int count, String sortProperty, boolean sortAsc) {

		List<BankCashinBean> sublist = getIndex(cashInList, sortProperty, sortAsc).subList(first, first + count);

		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<BankCashinBean> getIndex(List<BankCashinBean> cashInList, String prop, boolean asc) {

		return sort(cashInList, prop, asc);
	}

	/**
	 * returns the sorted list
	 */
	private List<BankCashinBean> sort(List<BankCashinBean> entries, String property, boolean asc) {
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
	private void sortByCustomerId(List<BankCashinBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<BankCashinBean>() {

				@Override
				public int compare(BankCashinBean arg0, BankCashinBean arg1) {
					return (arg0).getCustomerId().compareTo((arg1).getCustomerId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<BankCashinBean>() {

				@Override
				public int compare(BankCashinBean arg0, BankCashinBean arg1) {
					return (arg1).getCustomerId().compareTo((arg0).getCustomerId());
				}
			});
		}
	}

	/**
	 * returns the sorted list by Mobile Number
	 */
	private void sortByMobileNumber(List<BankCashinBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<BankCashinBean>() {

				@Override
				public int compare(BankCashinBean arg0, BankCashinBean arg1) {
					return (arg0).getMsisdn().compareTo((arg1).getMsisdn());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<BankCashinBean>() {

				@Override
				public int compare(BankCashinBean arg0, BankCashinBean arg1) {
					return (arg1).getMsisdn().compareTo((arg0).getMsisdn());
				}
			});
		}
	}

	/**
	 * returns the sorted list by Display Name
	 */
	private void sortByDisplayName(List<BankCashinBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<BankCashinBean>() {

				@Override
				public int compare(BankCashinBean arg0, BankCashinBean arg1) {
					return (arg0).getMsisdn().compareTo((arg1).getMsisdn());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<BankCashinBean>() {

				@Override
				public int compare(BankCashinBean arg0, BankCashinBean arg1) {
					return (arg1).getMsisdn().compareTo((arg0).getMsisdn());
				}
			});
		}
	}

}
