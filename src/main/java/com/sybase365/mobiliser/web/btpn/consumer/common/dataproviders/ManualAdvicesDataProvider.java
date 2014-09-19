package com.sybase365.mobiliser.web.btpn.consumer.common.dataproviders;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.sybase365.mobiliser.web.btpn.consumer.beans.ManualAdviceBean;

/**
 * This class is used as a data provider for displaying list of manual advice for Consumer Portal.
 * 
 * @author Vikram Gunda.
 */
public class ManualAdvicesDataProvider extends SortableDataProvider<ManualAdviceBean> {

	private static final long serialVersionUID = 1L;

	private List<ManualAdviceBean> manualAdvicesList;

	public List<ManualAdviceBean> getManualAdvicesList() {
		return manualAdvicesList;
	}

	public void setManualAdvicesList(List<ManualAdviceBean> manualAdvicesList) {
		this.manualAdvicesList = manualAdvicesList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ManualAdvicesDataProvider(String defaultSortProperty, final List<ManualAdviceBean> manualAdvicesList) {
		this.manualAdvicesList = manualAdvicesList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ManualAdviceBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ManualAdviceBean> model(final ManualAdviceBean object) {
		IModel<ManualAdviceBean> model = new LoadableDetachableModel<ManualAdviceBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ManualAdviceBean load() {
				ManualAdviceBean set = null;
				for (ManualAdviceBean beanVar : manualAdvicesList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ManualAdviceBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (manualAdvicesList == null) {
			return count;
		}
		return manualAdvicesList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<ManualAdviceBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ManualAdviceBean> sublist = getIndex(manualAdvicesList, sortProperty, sortAsc).subList(first, first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ManualAdviceBean> getIndex(List<ManualAdviceBean> favoritesList, String prop, boolean asc) {
		return sort(favoritesList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<ManualAdviceBean> sort(List<ManualAdviceBean> entries, String property, boolean asc) {
		if (property.equals("transactionId")) {
			sortByTransactionId(entries, asc);
		} else if (property.equals("transactionDate")) {
			sortByTransactionDate(entries, asc);
		} else if (property.equals("amount")) {
			sortByAmount(entries, asc);
		} else if (property.equals("billerId")) {
			sortByBillerId(entries, asc);
		} else {
			sortByFeeAmount(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByTransactionId
	 */
	private void sortByTransactionId(List<ManualAdviceBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManualAdviceBean>() {

				@Override
				public int compare(ManualAdviceBean arg0, ManualAdviceBean arg1) {
					return (arg0).getTransactionId().compareTo((arg1).getTransactionId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManualAdviceBean>() {

				@Override
				public int compare(ManualAdviceBean arg0, ManualAdviceBean arg1) {
					return (arg1).getTransactionId().compareTo((arg0).getTransactionId());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByTransactionDate
	 */
	private void sortByTransactionDate(List<ManualAdviceBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManualAdviceBean>() {

				@Override
				public int compare(ManualAdviceBean arg0, ManualAdviceBean arg1) {
					return (arg0).getTransactionDate().compareTo((arg1).getTransactionDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManualAdviceBean>() {

				@Override
				public int compare(ManualAdviceBean arg0, ManualAdviceBean arg1) {
					return (arg1).getTransactionDate().compareTo((arg0).getTransactionDate());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByAmount
	 */
	private void sortByAmount(List<ManualAdviceBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManualAdviceBean>() {

				@Override
				public int compare(ManualAdviceBean arg0, ManualAdviceBean arg1) {
					return (arg0).getAmount().compareTo((arg1).getAmount());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManualAdviceBean>() {

				@Override
				public int compare(ManualAdviceBean arg0, ManualAdviceBean arg1) {
					return (arg1).getAmount().compareTo((arg0).getAmount());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByBillerId
	 */
	private void sortByBillerId(List<ManualAdviceBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManualAdviceBean>() {

				@Override
				public int compare(ManualAdviceBean arg0, ManualAdviceBean arg1) {
					return (arg0).getBillerId().compareTo((arg1).getBillerId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManualAdviceBean>() {

				@Override
				public int compare(ManualAdviceBean arg0, ManualAdviceBean arg1) {
					return (arg1).getBillerId().compareTo((arg0).getBillerId());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByFeeAmount
	 */
	private void sortByFeeAmount(List<ManualAdviceBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManualAdviceBean>() {

				@Override
				public int compare(ManualAdviceBean arg0, ManualAdviceBean arg1) {
					return (arg0).getFeeAmount().compareTo((arg1).getFeeAmount());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManualAdviceBean>() {

				@Override
				public int compare(ManualAdviceBean arg0, ManualAdviceBean arg1) {
					return (arg1).getFeeAmount().compareTo((arg0).getFeeAmount());
				}
			});
		}
	}
}
