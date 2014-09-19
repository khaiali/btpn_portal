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

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomUseCaseFeeBean;

/**
 * This class is used as a data provider for displaying list of products in Manage Fee Details Page
 * 
 * @author Andi Samallangi W
 */
public class ManageUseCaseFeeApproveDataProvider extends SortableDataProvider<ManageCustomUseCaseFeeBean> {

	private static final long serialVersionUID = 1L;

	private List<ManageCustomUseCaseFeeBean> manageUseCaseFeeList;
	
	/**
	 * @return the manageUseCaseFeeList
	 */
	public List<ManageCustomUseCaseFeeBean> getManageUseCaseFeeList() {
		return manageUseCaseFeeList;
	}

	/**
	 * @param manageUseCaseFeeList the manageUseCaseFeeList to set
	 */
	public void setManageUseCaseFeeList(
			List<ManageCustomUseCaseFeeBean> manageUseCaseFeeList) {
		this.manageUseCaseFeeList = manageUseCaseFeeList;
	}
	
	public ManageUseCaseFeeApproveDataProvider(String defaultSortproperty){
		setSort(defaultSortproperty, true);
	}

	
	public ManageUseCaseFeeApproveDataProvider(String defaultSortproperty, 
			final List<ManageCustomUseCaseFeeBean> manageUseCaseFeeList){
		this.manageUseCaseFeeList = manageUseCaseFeeList;
		setSort(defaultSortproperty, false);
	}
	
	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ManageCustomUseCaseFeeBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}


	@Override
	public IModel<ManageCustomUseCaseFeeBean> model(
			final ManageCustomUseCaseFeeBean object) {
		IModel<ManageCustomUseCaseFeeBean> model = new LoadableDetachableModel<ManageCustomUseCaseFeeBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ManageCustomUseCaseFeeBean load() {
				ManageCustomUseCaseFeeBean set = null;
				for (ManageCustomUseCaseFeeBean beanVar : manageUseCaseFeeList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ManageCustomUseCaseFeeBean>(model);
	}


	@Override
	public int size() {
		int count = 0;
		if (manageUseCaseFeeList == null){
			return count;
		}
		return manageUseCaseFeeList.size();
	}
	
	/**
	 * returns the sorted list
	 */
	protected List<ManageCustomUseCaseFeeBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ManageCustomUseCaseFeeBean> sublist = getIndex(manageUseCaseFeeList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}
	
	/**
	 * returns the index
	 */
	protected List<ManageCustomUseCaseFeeBean> getIndex(List<ManageCustomUseCaseFeeBean> attachmentsList, String prop,
		boolean asc) {
		return sort(attachmentsList, prop, asc);
	}
	
	/**
	 * returns the sorted list
	 */
	private List<ManageCustomUseCaseFeeBean> sort(List<ManageCustomUseCaseFeeBean> entries, String property, boolean asc) {
		if (property.equals("workFlowId")) {
			sortByWorkFlowId(entries, asc);
		} else {
			sortByDescription(entries, asc);
		}
		return entries;
	}
	
	/**
	 * returns the sorted list by sortById
	 */
	private void sortByWorkFlowId(List<ManageCustomUseCaseFeeBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageCustomUseCaseFeeBean>() {

				@Override
				public int compare(ManageCustomUseCaseFeeBean arg0, ManageCustomUseCaseFeeBean arg1) {
					return (arg0).getWorkFlowId().compareTo((arg1).getWorkFlowId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageCustomUseCaseFeeBean>() {

				@Override
				public int compare(ManageCustomUseCaseFeeBean arg0, ManageCustomUseCaseFeeBean arg1) {
					return (arg1).getWorkFlowId().compareTo((arg0).getWorkFlowId());
				}
			});
		}
	}
	
	/**
	 * returns the sorted list by sortByDescription
	 */
	private void sortByDescription(List<ManageCustomUseCaseFeeBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageCustomUseCaseFeeBean>() {

				@Override
				public int compare(ManageCustomUseCaseFeeBean arg0, ManageCustomUseCaseFeeBean arg1) {
					return (arg0).getDescription().compareTo((arg1).getDescription());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageCustomUseCaseFeeBean>() {

				@Override
				public int compare(ManageCustomUseCaseFeeBean arg0, ManageCustomUseCaseFeeBean arg1) {
					return (arg1).getDescription().compareTo((arg0).getDescription());
				}
			});
		}
	}
	
}
