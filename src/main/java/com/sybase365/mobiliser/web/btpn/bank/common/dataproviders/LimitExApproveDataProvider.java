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
import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;

/**
 * This class is used as a data provider for displaying list of cash out details.
 * 
 * @author Febrie Subhan
 */
public class LimitExApproveDataProvider extends SortableDataProvider<ElimitBean> {
	private static final long serialVersionUID = 1L;

	private List<ElimitBean> limitList;

	public List<ElimitBean> getLimitList() {
		return limitList;
	}

	public void setLimitList(List<ElimitBean> limitList) {
		this.limitList = limitList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public LimitExApproveDataProvider(String defaultSortProperty) {
		setSort(defaultSortProperty, true);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ElimitBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ElimitBean> model(final ElimitBean object) {
		IModel<ElimitBean> model = new LoadableDetachableModel<ElimitBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ElimitBean load() {
				ElimitBean set = null;
				for (ElimitBean obj : limitList) {
					if (obj.getTaskId() == object.getTaskId()) {
						set = obj;
						break;
					}
				}
				return set;
			}
		};

		return new CompoundPropertyModel<ElimitBean>(model);
	}

	/**
	 * returns the size of the data list
	 */

	@Override
	public int size() {
		int count = 0;

		if (limitList == null) {
			return count;
		}

		return limitList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<ElimitBean> find(int first, int count, String sortProperty, boolean sortAsc) {

		List<ElimitBean> sublist = getIndex(limitList, sortProperty, sortAsc).subList(first, first + count);

		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ElimitBean> getIndex(List<ElimitBean> cashOutList, String prop, boolean asc) {
		return sort(cashOutList, prop, asc);
	}

	/**
	 * returns the sorted list
	 */
	private List<ElimitBean> sort(List<ElimitBean> entries, String property, boolean asc) {
		if (property.equals("customerId")) {
			sortById(entries, asc);
		} else if (property.equals("mobileNumber")) {
		//	sortByMobileNumber(entries, asc);
			sortById(entries, asc);
		} else {
			//sortByDisplayName(entries, asc);
			sortById(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by customer ID
	 */
	private void sortById(List<ElimitBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ElimitBean>() {

				@Override
				public int compare(ElimitBean arg0, ElimitBean arg1) {
					return (arg1).getTaskId().compareTo((arg0).getTaskId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ElimitBean>() {

				@Override
				public int compare(ElimitBean arg0, ElimitBean arg1) {
					return (arg1).getTaskId().compareTo((arg0).getTaskId());
				}
			});
		}
	}



}
