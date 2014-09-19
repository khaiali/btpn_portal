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

import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveMsisdnBean;

/**
 * This class is used as a data provider for displaying list of approve MSISDN in the Bank Portal
 * 
 * @author Narasa Reddy.
 */
public class ApprovalMsisdnDataProvider extends SortableDataProvider<ApproveMsisdnBean> {
	private static final long serialVersionUID = 1L;

	private List<ApproveMsisdnBean> approvalList;

	public List<ApproveMsisdnBean> getApprovalList() {
		return approvalList;
	}

	public void setApprovalList(List<ApproveMsisdnBean> approvalList) {
		this.approvalList = approvalList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ApprovalMsisdnDataProvider(String defaultSortProperty, final List<ApproveMsisdnBean> approvalList) {
		this.approvalList = approvalList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ApproveMsisdnBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ApproveMsisdnBean> model(final ApproveMsisdnBean object) {
		IModel<ApproveMsisdnBean> model = new LoadableDetachableModel<ApproveMsisdnBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ApproveMsisdnBean load() {
				ApproveMsisdnBean set = null;
				for (ApproveMsisdnBean beanVar : approvalList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ApproveMsisdnBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (approvalList == null) {
			return count;
		}
		return approvalList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<ApproveMsisdnBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ApproveMsisdnBean> sublist = getIndex(approvalList, sortProperty, sortAsc).subList(first, first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ApproveMsisdnBean> getIndex(List<ApproveMsisdnBean> approvalList, String prop, boolean asc) {
		return sort(approvalList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<ApproveMsisdnBean> sort(List<ApproveMsisdnBean> entries, String property, boolean asc) {
		if (property.equals("createdBy")) {
			sortByCreatedBy(entries, asc);
		} else if (property.equals("mobileNumber")) {
			sortByMobileNo(entries, asc);
		} else if (property.equals("oldMobile")) {
			sortByOldMobile(entries, asc);
		} else if (property.equals("newMobile")) {
			sortByNewMobile(entries, asc);
		} else if (property.equals("status")) {
			sortByStatus(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByCreatedBy
	 */
	private void sortByCreatedBy(List<ApproveMsisdnBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveMsisdnBean>() {

				@Override
				public int compare(ApproveMsisdnBean arg0, ApproveMsisdnBean arg1) {
					return (arg0).getCreatedBy().compareTo((arg1).getCreatedBy());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveMsisdnBean>() {

				@Override
				public int compare(ApproveMsisdnBean arg0, ApproveMsisdnBean arg1) {
					return (arg1).getCreatedBy().compareTo((arg0).getCreatedBy());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByMobileNo
	 */
	private void sortByMobileNo(List<ApproveMsisdnBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveMsisdnBean>() {

				@Override
				public int compare(ApproveMsisdnBean arg0, ApproveMsisdnBean arg1) {
					return (arg0).getMobileNumber().compareTo((arg1).getMobileNumber());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveMsisdnBean>() {

				@Override
				public int compare(ApproveMsisdnBean arg0, ApproveMsisdnBean arg1) {
					return (arg1).getMobileNumber().compareTo((arg0).getMobileNumber());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByOldMobile
	 */
	private void sortByOldMobile(List<ApproveMsisdnBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveMsisdnBean>() {

				@Override
				public int compare(ApproveMsisdnBean arg0, ApproveMsisdnBean arg1) {
					return (arg0).getOldMobile().compareTo((arg1).getOldMobile());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveMsisdnBean>() {

				@Override
				public int compare(ApproveMsisdnBean arg0, ApproveMsisdnBean arg1) {
					return (arg1).getOldMobile().compareTo((arg0).getOldMobile());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByNewMobile
	 */
	private void sortByNewMobile(List<ApproveMsisdnBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveMsisdnBean>() {

				@Override
				public int compare(ApproveMsisdnBean arg0, ApproveMsisdnBean arg1) {
					return (arg0).getNewMobile().compareTo((arg1).getNewMobile());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveMsisdnBean>() {

				@Override
				public int compare(ApproveMsisdnBean arg0, ApproveMsisdnBean arg1) {
					return (arg1).getNewMobile().compareTo((arg0).getNewMobile());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByStatus
	 */
	private void sortByStatus(List<ApproveMsisdnBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveMsisdnBean>() {

				@Override
				public int compare(ApproveMsisdnBean arg0, ApproveMsisdnBean arg1) {
					return (arg0).getStatus().compareTo((arg1).getStatus());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveMsisdnBean>() {

				@Override
				public int compare(ApproveMsisdnBean arg0, ApproveMsisdnBean arg1) {
					return (arg1).getStatus().compareTo((arg0).getStatus());
				}
			});
		}
	}

}
