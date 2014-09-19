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

import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveGeneralLedgerBean;

/**
 * This class is used as a data provider for displaying list of attachments in the Consumer Registration Page
 * 
 * @author Vikram Gunda
 */
public class ApproveGeneralLedgerDataProvider extends SortableDataProvider<ApproveGeneralLedgerBean> {
	private static final long serialVersionUID = 1L;

	private List<ApproveGeneralLedgerBean> generalLedgerList;

	public List<ApproveGeneralLedgerBean> getGeneralLedgerList() {
		return generalLedgerList;
	}

	public void setAttachmentsList(List<ApproveGeneralLedgerBean> generalLedgerList) {
		this.generalLedgerList = generalLedgerList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ApproveGeneralLedgerDataProvider(String defaultSortProperty,
		final List<ApproveGeneralLedgerBean> attachmentsList) {
		this.generalLedgerList = attachmentsList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ApproveGeneralLedgerBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ApproveGeneralLedgerBean> model(final ApproveGeneralLedgerBean object) {
		IModel<ApproveGeneralLedgerBean> model = new LoadableDetachableModel<ApproveGeneralLedgerBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ApproveGeneralLedgerBean load() {
				ApproveGeneralLedgerBean set = null;
				for (ApproveGeneralLedgerBean beanVar : generalLedgerList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ApproveGeneralLedgerBean>(model);
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
	protected List<ApproveGeneralLedgerBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ApproveGeneralLedgerBean> sublist = getIndex(generalLedgerList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ApproveGeneralLedgerBean> getIndex(List<ApproveGeneralLedgerBean> attachmentsList, String prop,
		boolean asc) {
		return sort(attachmentsList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<ApproveGeneralLedgerBean> sort(List<ApproveGeneralLedgerBean> entries, String property, boolean asc) {
		if (property.equals("glCode")) {
			sortByGlCode(entries, asc);
		} else if (property.equals("glDescription")) {
			sortByGlDesc(entries, asc);
		} else if (property.equals("createdBy")) {
			sortByCreatedBy(entries, asc);
		} else {
			sortByStatus(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByRequestDate
	 */
	private void sortByGlCode(List<ApproveGeneralLedgerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveGeneralLedgerBean>() {

				@Override
				public int compare(ApproveGeneralLedgerBean arg0, ApproveGeneralLedgerBean arg1) {
					return (arg0).getGlCode().compareTo((arg1).getGlCode());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveGeneralLedgerBean>() {

				@Override
				public int compare(ApproveGeneralLedgerBean arg0, ApproveGeneralLedgerBean arg1) {
					return (arg1).getGlCode().compareTo((arg0).getGlCode());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByRequestDate
	 */
	private void sortByGlDesc(List<ApproveGeneralLedgerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveGeneralLedgerBean>() {

				@Override
				public int compare(ApproveGeneralLedgerBean arg0, ApproveGeneralLedgerBean arg1) {
					return (arg0).getGlDescription().compareTo((arg1).getGlDescription());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveGeneralLedgerBean>() {

				@Override
				public int compare(ApproveGeneralLedgerBean arg0, ApproveGeneralLedgerBean arg1) {
					return (arg1).getGlDescription().compareTo((arg0).getGlDescription());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByRequestDate
	 */
	private void sortByCreatedBy(List<ApproveGeneralLedgerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveGeneralLedgerBean>() {

				@Override
				public int compare(ApproveGeneralLedgerBean arg0, ApproveGeneralLedgerBean arg1) {
					return (arg0).getCreatedBy().compareTo((arg1).getCreatedBy());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveGeneralLedgerBean>() {

				@Override
				public int compare(ApproveGeneralLedgerBean arg0, ApproveGeneralLedgerBean arg1) {
					return (arg1).getCreatedBy().compareTo((arg0).getCreatedBy());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByRequestDate
	 */
	private void sortByStatus(List<ApproveGeneralLedgerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveGeneralLedgerBean>() {

				@Override
				public int compare(ApproveGeneralLedgerBean arg0, ApproveGeneralLedgerBean arg1) {
					return (arg0).getStatus().compareTo((arg1).getStatus());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveGeneralLedgerBean>() {

				@Override
				public int compare(ApproveGeneralLedgerBean arg0, ApproveGeneralLedgerBean arg1) {
					return (arg1).getStatus().compareTo((arg0).getStatus());
				}
			});
		}
	}
}
