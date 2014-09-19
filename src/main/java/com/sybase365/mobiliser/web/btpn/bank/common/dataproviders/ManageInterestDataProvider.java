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

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestBean;

/**
 * This class is used as a data provider for displaying list of products in Manage Interest Details Page
 * 
 * @author Feny Yanti
 */
public class ManageInterestDataProvider extends SortableDataProvider<ManageInterestBean> {

	private static final long serialVersionUID = 1L;

	private List<ManageInterestBean> manageInterestList;
	
	/**
	 * @return the manageInterestList
	 */
	public List<ManageInterestBean> getManageInterestList() {
		return manageInterestList;
	}

	/**
	 * @param manageInterestList the manageInterestList to set
	 */
	public void setManageInterestList(
			List<ManageInterestBean> manageInterestList) {
		this.manageInterestList = manageInterestList;
	}
	
	public ManageInterestDataProvider(String defaultSortproperty){
		setSort(defaultSortproperty, true);
	}

	
	public ManageInterestDataProvider(String defaultSortproperty, 
			final List<ManageInterestBean> manageInterestList){
		this.manageInterestList = manageInterestList;
		setSort(defaultSortproperty, false);
	}
	
	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ManageInterestBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}


	@Override
	public IModel<ManageInterestBean> model(
			final ManageInterestBean object) {
		IModel<ManageInterestBean> model = new LoadableDetachableModel<ManageInterestBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ManageInterestBean load() {
				ManageInterestBean set = null;
				for (ManageInterestBean beanVar : manageInterestList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ManageInterestBean>(model);
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
	protected List<ManageInterestBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ManageInterestBean> sublist = getIndex(manageInterestList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}
	
	/**
	 * returns the index
	 */
	protected List<ManageInterestBean> getIndex(List<ManageInterestBean> attachmentsList, String prop,
		boolean asc) {
		return sort(attachmentsList, prop, asc);
	}
	
	/**
	 * returns the sorted list
	 */
	private List<ManageInterestBean> sort(List<ManageInterestBean> entries, String property, boolean asc) {
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
	private void sortById(List<ManageInterestBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageInterestBean>() {

				@Override
				public int compare(ManageInterestBean arg0, ManageInterestBean arg1) {
					return (arg0).getId().compareTo((arg1).getId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageInterestBean>() {

				@Override
				public int compare(ManageInterestBean arg0, ManageInterestBean arg1) {
					return (arg1).getId().compareTo((arg0).getId());
				}
			});
		}
	}
	
	/**
	 * returns the sorted list by sortByDescription
	 */
	private void sortByDescription(List<ManageInterestBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageInterestBean>() {

				@Override
				public int compare(ManageInterestBean arg0, ManageInterestBean arg1) {
					return (arg0).getDescription().compareTo((arg1).getDescription());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageInterestBean>() {

				@Override
				public int compare(ManageInterestBean arg0, ManageInterestBean arg1) {
					return (arg1).getDescription().compareTo((arg0).getDescription());
				}
			});
		}
	}
	
}
