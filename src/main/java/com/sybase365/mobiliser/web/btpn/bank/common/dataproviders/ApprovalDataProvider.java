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

import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveCustomerBean;

/**
 * This class is used as a data provider for displaying list of attachments in the Consumer Registration Page
 * 
 * @author Vikram Gunda
 */
public class ApprovalDataProvider extends SortableDataProvider<ApproveCustomerBean> {
	private static final long serialVersionUID = 1L;

	private List<ApproveCustomerBean> approvalList;

	public List<ApproveCustomerBean> getApprovalList() {
		return approvalList;
	}

	public void setApprovalList(List<ApproveCustomerBean> approvalList) {
		this.approvalList = approvalList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ApprovalDataProvider(String defaultSortProperty, final List<ApproveCustomerBean> approvalList) {
		this.approvalList = approvalList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ApproveCustomerBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ApproveCustomerBean> model(final ApproveCustomerBean object) {
		IModel<ApproveCustomerBean> model = new LoadableDetachableModel<ApproveCustomerBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ApproveCustomerBean load() {
				ApproveCustomerBean set = null;
				for (ApproveCustomerBean beanVar : approvalList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ApproveCustomerBean>(model);
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
	protected List<ApproveCustomerBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ApproveCustomerBean> sublist = getIndex(approvalList, sortProperty, sortAsc).subList(first, first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ApproveCustomerBean> getIndex(List<ApproveCustomerBean> approvalList, String prop, boolean asc) {
		return sort(approvalList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<ApproveCustomerBean> sort(List<ApproveCustomerBean> entries, String property, boolean asc) {
		if (property.equals("date")) {
			sortByDate(entries, asc);
		} else if (property.equals("requestType")) {
			sortByRequestType(entries, asc);
		} else if (property.equals("consumerType")) {
			sortByConsumerType(entries, asc);
		} else if (property.equals("mobileNumber")) {
			sortByMobileNo(entries, asc);
		} else if (property.equals("status")) {
			sortByStatus(entries, asc);
		} else if (property.equals("createdBy")) {
			sortByCreatedBy(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByDate
	 */
	private void sortByDate(List<ApproveCustomerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveCustomerBean>() {

				@Override
				public int compare(ApproveCustomerBean arg0, ApproveCustomerBean arg1) {
					return (arg0).getDate().compareTo((arg1).getDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveCustomerBean>() {

				@Override
				public int compare(ApproveCustomerBean arg0, ApproveCustomerBean arg1) {
					return (arg1).getDate().compareTo((arg0).getDate());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByRequestType
	 */
	private void sortByRequestType(List<ApproveCustomerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveCustomerBean>() {

				@Override
				public int compare(ApproveCustomerBean arg0, ApproveCustomerBean arg1) {
					return (arg0).getRequestType().compareTo((arg1).getRequestType());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveCustomerBean>() {

				@Override
				public int compare(ApproveCustomerBean arg0, ApproveCustomerBean arg1) {
					return (arg1).getRequestType().compareTo((arg0).getRequestType());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByConsumerType
	 */
	private void sortByConsumerType(List<ApproveCustomerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveCustomerBean>() {

				@Override
				public int compare(ApproveCustomerBean arg0, ApproveCustomerBean arg1) {
					return (arg0).getConsumerType().compareTo((arg1).getConsumerType());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveCustomerBean>() {

				@Override
				public int compare(ApproveCustomerBean arg0, ApproveCustomerBean arg1) {
					return (arg1).getConsumerType().compareTo((arg0).getConsumerType());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByMobileNo
	 */
	private void sortByMobileNo(List<ApproveCustomerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveCustomerBean>() {

				@Override
				public int compare(ApproveCustomerBean arg0, ApproveCustomerBean arg1) {
					return (arg0).getMobileNumber().compareTo((arg1).getMobileNumber());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveCustomerBean>() {

				@Override
				public int compare(ApproveCustomerBean arg0, ApproveCustomerBean arg1) {
					return (arg1).getMobileNumber().compareTo((arg0).getMobileNumber());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByStatus
	 */
	private void sortByStatus(List<ApproveCustomerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveCustomerBean>() {

				@Override
				public int compare(ApproveCustomerBean arg0, ApproveCustomerBean arg1) {
					return (arg0).getStatus().compareTo((arg1).getStatus());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveCustomerBean>() {

				@Override
				public int compare(ApproveCustomerBean arg0, ApproveCustomerBean arg1) {
					return (arg1).getStatus().compareTo((arg0).getStatus());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByCreatedBy
	 */
	private void sortByCreatedBy(List<ApproveCustomerBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ApproveCustomerBean>() {

				@Override
				public int compare(ApproveCustomerBean arg0, ApproveCustomerBean arg1) {
					return (arg0).getCreatedBy().compareTo((arg1).getCreatedBy());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ApproveCustomerBean>() {

				@Override
				public int compare(ApproveCustomerBean arg0, ApproveCustomerBean arg1) {
					return (arg1).getCreatedBy().compareTo((arg0).getCreatedBy());
				}
			});
		}
	}

}
