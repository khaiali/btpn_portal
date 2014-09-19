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

import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionReversalBean;

/**
 * This class is used as a data provider for displaying list of transaction reversal details in the Bank Portal
 * 
 * @author Narasa Reddy.
 */
public class ApprovalTxnReversalDataProvider extends SortableDataProvider<TransactionReversalBean> {
	private static final long serialVersionUID = 1L;

	private List<TransactionReversalBean> txnReversalList;

	public List<TransactionReversalBean> gettxnReversalList() {
		return txnReversalList;
	}

	public void settxnReversalList(List<TransactionReversalBean> txnReversalList) {
		this.txnReversalList = txnReversalList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ApprovalTxnReversalDataProvider(String defaultSortProperty,
		final List<TransactionReversalBean> txnReversalList) {
		this.txnReversalList = txnReversalList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends TransactionReversalBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<TransactionReversalBean> model(final TransactionReversalBean object) {
		IModel<TransactionReversalBean> model = new LoadableDetachableModel<TransactionReversalBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected TransactionReversalBean load() {
				TransactionReversalBean set = null;
				for (TransactionReversalBean beanVar : txnReversalList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<TransactionReversalBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (txnReversalList == null) {
			return count;
		}
		return txnReversalList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<TransactionReversalBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<TransactionReversalBean> sublist = getIndex(txnReversalList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<TransactionReversalBean> getIndex(List<TransactionReversalBean> txnReversalList, String prop,
		boolean asc) {
		return sort(txnReversalList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<TransactionReversalBean> sort(List<TransactionReversalBean> entries, String property, boolean asc) {
		if (property.equals("maker")) {
			sortByMaker(entries, asc);
		} else if (property.equals("txnId")) {
			sortByTransactionId(entries, asc);
		} else if (property.equals("name")) {
			sortByTransactionName(entries, asc);
		} else if (property.equals("useCaseId")) {
			sortByUseCaseId(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByMaker
	 */
	private void sortByMaker(List<TransactionReversalBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionReversalBean>() {

				@Override
				public int compare(TransactionReversalBean arg0, TransactionReversalBean arg1) {
					return (arg0).getMaker().compareTo((arg1).getMaker());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionReversalBean>() {

				@Override
				public int compare(TransactionReversalBean arg0, TransactionReversalBean arg1) {
					return (arg1).getMaker().compareTo((arg0).getMaker());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByTransactionId
	 */
	private void sortByTransactionId(List<TransactionReversalBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionReversalBean>() {

				@Override
				public int compare(TransactionReversalBean arg0, TransactionReversalBean arg1) {
					return (arg0).getTransactionID().compareTo((arg1).getTransactionID());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionReversalBean>() {

				@Override
				public int compare(TransactionReversalBean arg0, TransactionReversalBean arg1) {
					return (arg1).getTransactionID().compareTo((arg0).getTransactionID());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByTransactionName
	 */
	private void sortByTransactionName(List<TransactionReversalBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionReversalBean>() {

				@Override
				public int compare(TransactionReversalBean arg0, TransactionReversalBean arg1) {
					return (arg0).getTransactionName().compareTo((arg1).getTransactionName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionReversalBean>() {

				@Override
				public int compare(TransactionReversalBean arg0, TransactionReversalBean arg1) {
					return (arg1).getTransactionName().compareTo((arg0).getTransactionName());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByUseCaseId
	 */
	private void sortByUseCaseId(List<TransactionReversalBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionReversalBean>() {

				@Override
				public int compare(TransactionReversalBean arg0, TransactionReversalBean arg1) {
					return (arg0).getUseCase().compareTo((arg1).getUseCase());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionReversalBean>() {

				@Override
				public int compare(TransactionReversalBean arg0, TransactionReversalBean arg1) {
					return (arg1).getUseCase().compareTo((arg0).getUseCase());
				}
			});
		}
	}

}
