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

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxBean;

/**
 * This class is used as a data provider for displaying list of products in Manage Interest Details Page
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxDataProvider extends SortableDataProvider<ManageInterestTaxBean> {

	private static final long serialVersionUID = 1L;

	private List<ManageInterestTaxBean> manageInterestList;
	
	/**
	 * @return the manageInterestList
	 */
	public List<ManageInterestTaxBean> getManageInterestList() {
		return manageInterestList;
	}

	/**
	 * @param manageInterestList the manageInterestList to set
	 */
	public void setManageInterestList(
			List<ManageInterestTaxBean> manageInterestList) {
		this.manageInterestList = manageInterestList;
	}
	
	public ManageInterestTaxDataProvider(String defaultSortproperty){
		setSort(defaultSortproperty, true);
	}

	
	public ManageInterestTaxDataProvider(String defaultSortproperty, 
			final List<ManageInterestTaxBean> manageInterestList){
		this.manageInterestList = manageInterestList;
		setSort(defaultSortproperty, false);
	}
	
	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ManageInterestTaxBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}


	@Override
	public IModel<ManageInterestTaxBean> model(
			final ManageInterestTaxBean object) {
		IModel<ManageInterestTaxBean> model = new LoadableDetachableModel<ManageInterestTaxBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ManageInterestTaxBean load() {
				ManageInterestTaxBean set = null;
				for (ManageInterestTaxBean beanVar : manageInterestList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ManageInterestTaxBean>(model);
	}


	@Override
	public int size() {
		int count = 0;
		if (manageInterestList == null){
			return count;
		}
		return manageInterestList.size();
	}
	
	/**
	 * returns the sorted list
	 */
	protected List<ManageInterestTaxBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ManageInterestTaxBean> sublist = getIndex(manageInterestList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}
	
	/**
	 * returns the index
	 */
	protected List<ManageInterestTaxBean> getIndex(List<ManageInterestTaxBean> attachmentsList, String prop,
		boolean asc) {
		return sort(attachmentsList, prop, asc);
	}
	
	/**
	 * returns the sorted list
	 */
	private List<ManageInterestTaxBean> sort(List<ManageInterestTaxBean> entries, String property, boolean asc) {
		if (property.equals("id")) {
			sortById(entries, asc);
		} else {
			sortByDescription(entries, asc);
		}
		return entries;
	}
	
	/**
	 * returns the sorted list by sortById
	 */
	private void sortById(List<ManageInterestTaxBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageInterestTaxBean>() {

				@Override
				public int compare(ManageInterestTaxBean arg0, ManageInterestTaxBean arg1) {
					return (arg0).getId().compareTo((arg1).getId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageInterestTaxBean>() {

				@Override
				public int compare(ManageInterestTaxBean arg0, ManageInterestTaxBean arg1) {
					return (arg1).getId().compareTo((arg0).getId());
				}
			});
		}
	}
	
	/**
	 * returns the sorted list by sortByDescription
	 */
	private void sortByDescription(List<ManageInterestTaxBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageInterestTaxBean>() {

				@Override
				public int compare(ManageInterestTaxBean arg0, ManageInterestTaxBean arg1) {
					return (arg0).getDescription().compareTo((arg1).getDescription());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageInterestTaxBean>() {

				@Override
				public int compare(ManageInterestTaxBean arg0, ManageInterestTaxBean arg1) {
					return (arg1).getDescription().compareTo((arg0).getDescription());
				}
			});
		}
	}
	
}
