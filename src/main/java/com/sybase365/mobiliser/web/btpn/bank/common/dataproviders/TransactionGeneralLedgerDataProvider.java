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

import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionGeneralLedgerBean;

/**
 * This class is used as a data provider for displaying list of attachments in the Consumer Registration Page
 * 
 * @author Vikram Gunda
 */
public class TransactionGeneralLedgerDataProvider extends SortableDataProvider<TransactionGeneralLedgerBean> {
	private static final long serialVersionUID = 1L;

	private List<TransactionGeneralLedgerBean> generalLedgerList;

	public List<TransactionGeneralLedgerBean> getGeneralLedgerList() {
		return generalLedgerList;
	}

	public void setAttachmentsList(List<TransactionGeneralLedgerBean> generalLedgerList) {
		this.generalLedgerList = generalLedgerList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public TransactionGeneralLedgerDataProvider(String defaultSortProperty,
		final List<TransactionGeneralLedgerBean> attachmentsList) {
		this.generalLedgerList = attachmentsList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends TransactionGeneralLedgerBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<TransactionGeneralLedgerBean> model(final TransactionGeneralLedgerBean object) {
		IModel<TransactionGeneralLedgerBean> model = new LoadableDetachableModel<TransactionGeneralLedgerBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected TransactionGeneralLedgerBean load() {
				TransactionGeneralLedgerBean set = null;
				for (TransactionGeneralLedgerBean beanVar : generalLedgerList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<TransactionGeneralLedgerBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (generalLedgerList == null) {
			return count;
		}
		return generalLedgerList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<TransactionGeneralLedgerBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<TransactionGeneralLedgerBean> sublist = getIndex(generalLedgerList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<TransactionGeneralLedgerBean> getIndex(List<TransactionGeneralLedgerBean> attachmentsList,
		String prop, boolean asc) {
		return sort(attachmentsList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<TransactionGeneralLedgerBean> sort(List<TransactionGeneralLedgerBean> entries, String property,
		boolean asc) {
		if (property.equals("glCode")) {
			sortByGlCode(entries, asc);
		} else if (property.equals("glDescription")) {
			sortByGlDesc(entries, asc);
		} else if (property.equals("status")) {
			sortByGlStatus(entries, asc);
		} else if (property.equals("createdBy")) {
			sortByGlCreatedBy(entries, asc);
		} else {
			sortByUseCaseName(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByRequestDate
	 */
	private void sortByGlCode(List<TransactionGeneralLedgerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionGeneralLedgerBean>() {

				@Override
				public int compare(TransactionGeneralLedgerBean arg0, TransactionGeneralLedgerBean arg1) {
					return (arg0).getCurrentGL().getId().compareTo((arg1).getCurrentGL().getId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionGeneralLedgerBean>() {

				@Override
				public int compare(TransactionGeneralLedgerBean arg0, TransactionGeneralLedgerBean arg1) {
					return (arg1).getCurrentGL().getId().compareTo((arg0).getCurrentGL().getId());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByRequestDate
	 */
	private void sortByGlDesc(List<TransactionGeneralLedgerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionGeneralLedgerBean>() {

				@Override
				public int compare(TransactionGeneralLedgerBean arg0, TransactionGeneralLedgerBean arg1) {
					return (arg0).getCurrentGL().getValue().compareTo((arg1).getCurrentGL().getValue());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionGeneralLedgerBean>() {

				@Override
				public int compare(TransactionGeneralLedgerBean arg0, TransactionGeneralLedgerBean arg1) {
					return (arg1).getCurrentGL().getValue().compareTo((arg0).getCurrentGL().getValue());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByRequestDate
	 */
	private void sortByGlStatus(List<TransactionGeneralLedgerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionGeneralLedgerBean>() {

				@Override
				public int compare(TransactionGeneralLedgerBean arg0, TransactionGeneralLedgerBean arg1) {
					return (arg0).getStatus().compareTo((arg1).getStatus());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionGeneralLedgerBean>() {

				@Override
				public int compare(TransactionGeneralLedgerBean arg0, TransactionGeneralLedgerBean arg1) {
					return (arg1).getStatus().compareTo((arg0).getStatus());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByRequestDate
	 */
	private void sortByGlCreatedBy(List<TransactionGeneralLedgerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionGeneralLedgerBean>() {

				@Override
				public int compare(TransactionGeneralLedgerBean arg0, TransactionGeneralLedgerBean arg1) {
					return (arg0).getCreatedBy().compareTo((arg1).getCreatedBy());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionGeneralLedgerBean>() {

				@Override
				public int compare(TransactionGeneralLedgerBean arg0, TransactionGeneralLedgerBean arg1) {
					return (arg1).getCreatedBy().compareTo((arg0).getCreatedBy());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByRequestDate
	 */
	private void sortByUseCaseName(List<TransactionGeneralLedgerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<TransactionGeneralLedgerBean>() {

				@Override
				public int compare(TransactionGeneralLedgerBean arg0, TransactionGeneralLedgerBean arg1) {
					return (arg0).getUseCaseName().compareTo((arg1).getUseCaseName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<TransactionGeneralLedgerBean>() {

				@Override
				public int compare(TransactionGeneralLedgerBean arg0, TransactionGeneralLedgerBean arg1) {
					return (arg1).getUseCaseName().compareTo((arg0).getUseCaseName());
				}
			});
		}
	}
}
