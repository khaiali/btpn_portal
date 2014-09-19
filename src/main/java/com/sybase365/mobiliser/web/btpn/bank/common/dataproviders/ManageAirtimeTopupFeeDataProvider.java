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

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageAirtimeTopupFeeBean;

/**
 * This class is used as a data provider for displaying list of products in Manage Fee Details Page
 * 
 * @author Vikram Gunda
 */
public class ManageAirtimeTopupFeeDataProvider extends SortableDataProvider<ManageAirtimeTopupFeeBean> {

	private static final long serialVersionUID = 1L;

	private List<ManageAirtimeTopupFeeBean> manageProductsList;

	public List<ManageAirtimeTopupFeeBean> getManageProductsList() {
		return manageProductsList;
	}

	public void setManageProductsList(List<ManageAirtimeTopupFeeBean> manageProductsList) {
		this.manageProductsList = manageProductsList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ManageAirtimeTopupFeeDataProvider(String defaultSortProperty,
		final List<ManageAirtimeTopupFeeBean> manageProductsList) {
		this.manageProductsList = manageProductsList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ManageAirtimeTopupFeeBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ManageAirtimeTopupFeeBean> model(final ManageAirtimeTopupFeeBean object) {
		IModel<ManageAirtimeTopupFeeBean> model = new LoadableDetachableModel<ManageAirtimeTopupFeeBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ManageAirtimeTopupFeeBean load() {
				ManageAirtimeTopupFeeBean set = null;
				for (ManageAirtimeTopupFeeBean beanVar : manageProductsList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ManageAirtimeTopupFeeBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (manageProductsList == null) {
			return count;
		}
		return manageProductsList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<ManageAirtimeTopupFeeBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ManageAirtimeTopupFeeBean> sublist = getIndex(manageProductsList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ManageAirtimeTopupFeeBean> getIndex(List<ManageAirtimeTopupFeeBean> attachmentsList, String prop,
		boolean asc) {
		return sort(attachmentsList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<ManageAirtimeTopupFeeBean> sort(List<ManageAirtimeTopupFeeBean> entries, String property, boolean asc) {
		if (property.equals("telco")) {
			sortByTelco(entries, asc);
		} else {
			sortByDenomination(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByFeeName
	 */
	private void sortByTelco(List<ManageAirtimeTopupFeeBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageAirtimeTopupFeeBean>() {

				@Override
				public int compare(ManageAirtimeTopupFeeBean arg0, ManageAirtimeTopupFeeBean arg1) {
					return (arg0).getTelco().getId().compareTo((arg1).getTelco().getId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageAirtimeTopupFeeBean>() {

				@Override
				public int compare(ManageAirtimeTopupFeeBean arg0, ManageAirtimeTopupFeeBean arg1) {
					return (arg1).getTelco().getId().compareTo((arg0).getTelco().getId());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByGlCode
	 */
	private void sortByDenomination(List<ManageAirtimeTopupFeeBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageAirtimeTopupFeeBean>() {

				@Override
				public int compare(ManageAirtimeTopupFeeBean arg0, ManageAirtimeTopupFeeBean arg1) {
					return (arg0).getDenomination().getValue().compareTo((arg1).getDenomination().getValue());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageAirtimeTopupFeeBean>() {

				@Override
				public int compare(ManageAirtimeTopupFeeBean arg0, ManageAirtimeTopupFeeBean arg1) {
					return (arg1).getDenomination().getValue().compareTo((arg0).getDenomination().getValue());
				}
			});
		}
	}
}
