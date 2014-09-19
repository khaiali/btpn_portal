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

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxApproveBean;

/**
 * This class is used as a data provider for displaying list of products in Manage Interest Details Page
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxApproveDataProvider extends SortableDataProvider<ManageInterestTaxApproveBean> {

	private static final long serialVersionUID = 1L;

	private List<ManageInterestTaxApproveBean> manageInterestList;
	
	/**
	 * @return the manageUseCaseFeeList
	 */
	public List<ManageInterestTaxApproveBean> getManageInterestList() {
		return manageInterestList;
	}

	/**
	 * @param manageUseCaseFeeList the manageUseCaseFeeList to set
	 */
	public void setManageInterestList(
			List<ManageInterestTaxApproveBean> manageInterestList) {
		this.manageInterestList = manageInterestList;
	}
	
	public ManageInterestTaxApproveDataProvider(String defaultSortproperty){
		setSort(defaultSortproperty, true);
	}

	
	public ManageInterestTaxApproveDataProvider(String defaultSortproperty, 
			final List<ManageInterestTaxApproveBean> manageInterestList){
		this.manageInterestList = manageInterestList;
		setSort(defaultSortproperty, false);
	}
	
	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ManageInterestTaxApproveBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}


	@Override
	public IModel<ManageInterestTaxApproveBean> model(
			final ManageInterestTaxApproveBean object) {
		IModel<ManageInterestTaxApproveBean> model = new LoadableDetachableModel<ManageInterestTaxApproveBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ManageInterestTaxApproveBean load() {
				ManageInterestTaxApproveBean set = null;
				for (ManageInterestTaxApproveBean beanVar : manageInterestList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ManageInterestTaxApproveBean>(model);
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
	protected List<ManageInterestTaxApproveBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ManageInterestTaxApproveBean> sublist = getIndex(manageInterestList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}
	
	/**
	 * returns the index
	 */
	protected List<ManageInterestTaxApproveBean> getIndex(List<ManageInterestTaxApproveBean> attachmentsList, String prop,
		boolean asc) {
		return sort(attachmentsList, prop, asc);
	}
	
	/**
	 * returns the sorted list
	 */
	private List<ManageInterestTaxApproveBean> sort(List<ManageInterestTaxApproveBean> entries, String property, boolean asc) {
		if (property.equals("workFlowId")) {
			sortByWorkFlowId(entries, asc);
		} else if (property.equals("description"))  {
			sortByDescription(entries, asc);
		}
		else if (property.equals("lastModifiedByName"))  {
			sortByModifiedName(entries, asc);
		}
		return entries;
	}
	
	/**
	 * returns the sorted list by sortById
	 */
	private void sortByWorkFlowId(List<ManageInterestTaxApproveBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageInterestTaxApproveBean>() {

				@Override
				public int compare(ManageInterestTaxApproveBean arg0, ManageInterestTaxApproveBean arg1) {
					return (arg0).getWorkFlowId().compareTo((arg1).getWorkFlowId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageInterestTaxApproveBean>() {

				@Override
				public int compare(ManageInterestTaxApproveBean arg0, ManageInterestTaxApproveBean arg1) {
					return (arg1).getWorkFlowId().compareTo((arg0).getWorkFlowId());
				}
			});
		}
	}
	
	/**
	 * returns the sorted list by sortByDescription
	 */
	private void sortByDescription(List<ManageInterestTaxApproveBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageInterestTaxApproveBean>() {

				@Override
				public int compare(ManageInterestTaxApproveBean arg0, ManageInterestTaxApproveBean arg1) {
					return (arg0).getDescription().compareTo((arg1).getDescription());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageInterestTaxApproveBean>() {

				@Override
				public int compare(ManageInterestTaxApproveBean arg0, ManageInterestTaxApproveBean arg1) {
					return (arg1).getDescription().compareTo((arg0).getDescription());
				}
			});
		}
	}
	
	/**
	 * returns the sorted list by sortByModifiedName
	 */
	private void sortByModifiedName(List<ManageInterestTaxApproveBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageInterestTaxApproveBean>() {

				@Override
				public int compare(ManageInterestTaxApproveBean arg0, ManageInterestTaxApproveBean arg1) {
					return (arg0).getLastModifiedByName().compareTo((arg1).getLastModifiedByName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageInterestTaxApproveBean>() {

				@Override
				public int compare(ManageInterestTaxApproveBean arg0, ManageInterestTaxApproveBean arg1) {
					return (arg1).getLastModifiedByName().compareTo((arg0).getLastModifiedByName());
				}
			});
		}
	}

}
