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

import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionDetailsReportAgentBean;

public class TransactionDetailsReportDataProvider extends SortableDataProvider<TransactionDetailsReportAgentBean> {
	private static final long serialVersionUID = 1L;

	private List<TransactionDetailsReportAgentBean> txnDataList;

	public List<TransactionDetailsReportAgentBean> getTxnDataList() {
		return txnDataList;
	}

	public void setTxnDataList(List<TransactionDetailsReportAgentBean> txnDataList) {
		this.txnDataList = txnDataList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public TransactionDetailsReportDataProvider(String defaultSortProperty,
		final List<TransactionDetailsReportAgentBean> txnDataList) {
		this.txnDataList = txnDataList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends TransactionDetailsReportAgentBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<TransactionDetailsReportAgentBean> model(final TransactionDetailsReportAgentBean object) {
		IModel<TransactionDetailsReportAgentBean> model = new LoadableDetachableModel<TransactionDetailsReportAgentBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected TransactionDetailsReportAgentBean load() {
				TransactionDetailsReportAgentBean set = null;
				for (TransactionDetailsReportAgentBean beanVar : txnDataList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<TransactionDetailsReportAgentBean>(model);
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
	protected List<TransactionDetailsReportAgentBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<TransactionDetailsReportAgentBean> sublist = getIndex(txnDataList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<TransactionDetailsReportAgentBean> getIndex(List<TransactionDetailsReportAgentBean> txnDataList,
		String prop, boolean asc) {
		return sort(txnDataList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<TransactionDetailsReportAgentBean> sort(List<TransactionDetailsReportAgentBean> entries,
		String property, boolean asc) {
		if (property.equals("date")) {
			sortByDate(entries, asc);
		} else if (property.equals("customerAccount")) {
			sortByCustomerAccount(entries, asc);
		} else if (property.equals("agentType")) {
			sortByAgentType(entries, asc);
		} else if (property.equals("agentId")) {
			sortByAgentId(entries, asc);
		} else if (property.equals("transactionType")) {
			sortByTransactionType(entries, asc);
		} else if (property.equals("amount")) {
			sortByAmount(entries, asc);
		} else if (property.equals("biller")) {
			sortByBiller(entries, asc);
		} else {
			sortByBeneficiary(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByDate
	 */
	private void sortByDate(List<TransactionDetailsReportAgentBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg0).getDate().compareTo((arg1).getDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg1).getDate().compareTo((arg0).getDate());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByCustomerAccount
	 */
	private void sortByCustomerAccount(List<TransactionDetailsReportAgentBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg0).getCustomerAccount().compareTo((arg1).getCustomerAccount());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg1).getCustomerAccount().compareTo((arg0).getCustomerAccount());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByAgentType
	 */
	private void sortByAgentId(List<TransactionDetailsReportAgentBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg0).getAgentId().compareTo((arg1).getAgentId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg1).getAgentId().compareTo((arg0).getAgentId());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByAgentType
	 */
	private void sortByAgentType(List<TransactionDetailsReportAgentBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg0).getAgentType().compareTo((arg1).getAgentType());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg1).getAgentType().compareTo((arg0).getAgentType());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByTransactionType
	 */
	private void sortByTransactionType(List<TransactionDetailsReportAgentBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg0).getTransactionType().compareTo((arg1).getTransactionType());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg1).getTransactionType().compareTo((arg0).getTransactionType());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByAmount
	 */
	private void sortByAmount(List<TransactionDetailsReportAgentBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg0).getAmount().compareTo((arg1).getAmount());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg1).getAmount().compareTo((arg0).getAmount());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByBiller
	 */
	private void sortByBiller(List<TransactionDetailsReportAgentBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg0).getBiller().compareTo((arg1).getBiller());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg1).getBiller().compareTo((arg0).getBiller());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByBeneficiary
	 */
	private void sortByBeneficiary(List<TransactionDetailsReportAgentBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg0).getBeneificary().compareTo((arg1).getBeneificary());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionDetailsReportAgentBean>() {

				@Override
				public int compare(TransactionDetailsReportAgentBean arg0, TransactionDetailsReportAgentBean arg1) {
					return (arg1).getBeneificary().compareTo((arg0).getBeneificary());
				}
			});
		}
	}
}
