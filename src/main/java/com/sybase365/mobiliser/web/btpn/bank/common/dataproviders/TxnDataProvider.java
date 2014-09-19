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

import com.sybase365.mobiliser.web.btpn.bank.beans.TxnDataBean;

public class TxnDataProvider extends SortableDataProvider<TxnDataBean> {
	private static final long serialVersionUID = 1L;

	private List<TxnDataBean> txnDataList;

	public List<TxnDataBean> getTxnDataList() {
		return txnDataList;
	}

	public void setTxnDataList(List<TxnDataBean> txnDataList) {
		this.txnDataList = txnDataList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public TxnDataProvider(String defaultSortProperty,
			final List<TxnDataBean> txnDataList) {
		this.txnDataList = txnDataList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends TxnDataBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending())
				.iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<TxnDataBean> model(final TxnDataBean object) {
		IModel<TxnDataBean> model = new LoadableDetachableModel<TxnDataBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected TxnDataBean load() {
				TxnDataBean set = null;
				for (TxnDataBean beanVar : txnDataList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<TxnDataBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (txnDataList == null) {
			return count;
		}
		return txnDataList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<TxnDataBean> find(int first, int count, String sortProperty,
			boolean sortAsc) {
		List<TxnDataBean> sublist = getIndex(txnDataList, sortProperty, sortAsc)
				.subList(first, first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<TxnDataBean> getIndex(List<TxnDataBean> txnDataList,
			String prop, boolean asc) {
		return sort(txnDataList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<TxnDataBean> sort(List<TxnDataBean> entries, String property,
			boolean asc) {
		if (property.equals("id")) {
			sortById(entries, asc);
		} else if (property.equals("date")) {
			sortByDate(entries, asc);
		} else if (property.equals("type")) {
			sortByType(entries, asc);
		} else if (property.equals("amount")) {
			sortByAmount(entries, asc);
		} else if (property.equals("status")) {
			sortByStatus(entries, asc);
		} else if (property.equals("debitAmount")) {
			sortByDebitAmount(entries, asc);
		} else if (property.equals("creditAmount")) {
			sortByCreditAmount(entries, asc);
		} else {
			sortByRunningBalance(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByMaker
	 */
	private void sortById(List<TxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return String.valueOf((arg0).getId()).compareTo(
							String.valueOf((arg1).getId()));
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return String.valueOf((arg1).getId()).compareTo(
							String.valueOf((arg0).getId()));
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByStatus
	 */
	private void sortByDate(List<TxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return (arg0).getDate().compareTo((arg1).getDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return (arg1).getDate().compareTo((arg0).getDate());
				}
			});
		}
	}

	private void sortByType(List<TxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return (arg0).getType().compareTo((arg1).getType());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return (arg1).getType().compareTo((arg0).getType());
				}
			});
		}
	}

	private void sortByAmount(List<TxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return String.valueOf((arg0).getAmount()).compareTo(
							String.valueOf((arg1).getType()));
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return String.valueOf((arg1).getAmount()).compareTo(
							String.valueOf((arg0).getAmount()));
				}
			});
		}
	}

	private void sortByDebitAmount(List<TxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return String.valueOf((arg0).getDebitAmount()).compareTo(
							String.valueOf((arg1).getDebitAmount()));
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return String.valueOf((arg1).getDebitAmount()).compareTo(
							String.valueOf((arg0).getDebitAmount()));
				}
			});
		}
	}

	private void sortByCreditAmount(List<TxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return String.valueOf((arg0).getCreditAmount()).compareTo(
							String.valueOf((arg1).getCreditAmount()));
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return String.valueOf((arg1).getCreditAmount()).compareTo(
							String.valueOf((arg0).getCreditAmount()));
				}
			});
		}
	}

	private void sortByRunningBalance(List<TxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return String.valueOf((arg0).getRunningBalance())
							.compareTo(
									String.valueOf((arg1).getRunningBalance()));
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return String.valueOf((arg1).getRunningBalance())
							.compareTo(
									String.valueOf((arg0).getRunningBalance()));
				}
			});
		}
	}

	private void sortByStatus(List<TxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return (arg0).getStatus().compareTo((arg1).getStatus());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TxnDataBean>() {

				@Override
				public int compare(TxnDataBean arg0, TxnDataBean arg1) {
					return (arg1).getStatus().compareTo((arg0).getStatus());
				}
			});
		}
	}

}
