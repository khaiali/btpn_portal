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

import com.sybase365.mobiliser.web.btpn.bank.beans.ConsumerTransactionBean;

public class ConsumerTransactionDataProvider extends SortableDataProvider<ConsumerTransactionBean> {
	private static final long serialVersionUID = 1L;

	private List<ConsumerTransactionBean> txnDataList;

	public List<ConsumerTransactionBean> getTxnDataList() {
		return txnDataList;
	}

	public void setTxnDataList(List<ConsumerTransactionBean> txnDataList) {
		this.txnDataList = txnDataList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ConsumerTransactionDataProvider(String defaultSortProperty, final List<ConsumerTransactionBean> txnDataList) {
		this.txnDataList = txnDataList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ConsumerTransactionBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ConsumerTransactionBean> model(final ConsumerTransactionBean object) {
		IModel<ConsumerTransactionBean> model = new LoadableDetachableModel<ConsumerTransactionBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ConsumerTransactionBean load() {
				ConsumerTransactionBean set = null;
				for (ConsumerTransactionBean beanVar : txnDataList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ConsumerTransactionBean>(model);
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
	protected List<ConsumerTransactionBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ConsumerTransactionBean> sublist = getIndex(txnDataList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ConsumerTransactionBean> getIndex(List<ConsumerTransactionBean> txnDataList, String prop, boolean asc) {
		return sort(txnDataList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<ConsumerTransactionBean> sort(List<ConsumerTransactionBean> entries, String property, boolean asc) {
		if (property.equals("errorCode")) {
			sortByErrorcode(entries, asc);
		} else if (property.equals("date")) {
			sortByDate(entries, asc);
		} else if (property.equals("type")) {
			sortByType(entries, asc);
		} else if (property.equals("txnId")) {
			sortByTxnId(entries, asc);
		} else if (property.equals("name")) {
			sortByName(entries, asc);
		} else if (property.equals("details")) {
			sortByDetails(entries, asc);
		} else if (property.equals("fee")) {
			sortByFee(entries, asc);
		} else if (property.equals("debitDesc")) {
				sortByDebitDesc(entries, asc);	
		} else {
			sortByAmount(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByMaker
	 */
	private void sortByErrorcode(List<ConsumerTransactionBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg0).getErrorCode().compareTo((arg1).getErrorCode());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg1).getErrorCode().compareTo((arg0).getErrorCode());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByStatus
	 */
	private void sortByDate(List<ConsumerTransactionBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg0).getDate().compareTo((arg1).getDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg1).getDate().compareTo((arg0).getDate());
				}
			});
		}
	}

	private void sortByType(List<ConsumerTransactionBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg0).getType().compareTo((arg1).getType());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg1).getType().compareTo((arg0).getType());
				}
			});
		}
	}

	private void sortByAmount(List<ConsumerTransactionBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return arg0.getAmount().compareTo(arg1.getAmount());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return arg1.getAmount().compareTo(arg0.getAmount());
				}
			});
		}
	}

	private void sortByTxnId(List<ConsumerTransactionBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg0).getTxnId().compareTo((arg1).getTxnId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg1).getTxnId().compareTo((arg0).getTxnId());
				}
			});
		}
	}

	private void sortByName(List<ConsumerTransactionBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg0).getName().compareTo((arg1).getName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg1).getName().compareTo((arg0).getName());
				}
			});
		}
	}

	private void sortByDetails(List<ConsumerTransactionBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg0).getDetails().compareTo((arg1).getDetails());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg1).getDetails().compareTo((arg0).getDetails());
				}
			});
		}
	}

	private void sortByFee(List<ConsumerTransactionBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg0).getFee().compareTo(arg1.getFee());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return arg1.getFee().compareTo(arg0.getFee());
				}
			});
		}
	}
	
	private void sortByDebitDesc(List<ConsumerTransactionBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return (arg0).getDebitDesc().compareTo(arg1.getDebitDesc());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ConsumerTransactionBean>() {

				@Override
				public int compare(ConsumerTransactionBean arg0, ConsumerTransactionBean arg1) {
					return arg1.getDebitDesc().compareTo(arg0.getDebitDesc());
				}
			});
		}
	}

}
