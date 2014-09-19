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

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestApproveBean;

/**
 * This class is used as a data provider for displaying list of products in Manage Interest Details Page
 * 
 * @author Feny Yanti
 */
public class ManageInterestApproveDataProvider extends SortableDataProvider<ManageInterestApproveBean> {

	private static final long serialVersionUID = 1L;

	private List<ManageInterestApproveBean> manageInterestList;
	
	/**
	 * @return the manageUseCaseFeeList
	 */
	public List<ManageInterestApproveBean> getManageInterestList() {
		return manageInterestList;
	}

	/**
	 * @param manageInterestList the manageInterestList to set
	 */
	public void setManageInterestList(
			List<ManageInterestApproveBean> manageInterestList) {
		this.manageInterestList = manageInterestList;
	}
	
	public ManageInterestApproveDataProvider(String defaultSortproperty){
		setSort(defaultSortproperty, true);
	}

	
	public ManageInterestApproveDataProvider(String defaultSortproperty, 
			final List<ManageInterestApproveBean> manageInterestList){
		this.manageInterestList = manageInterestList;
		setSort(defaultSortproperty, false);
	}
	
	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ManageInterestApproveBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}


	@Override
	public IModel<ManageInterestApproveBean> model(
			final ManageInterestApproveBean object) {
		IModel<ManageInterestApproveBean> model = new LoadableDetachableModel<ManageInterestApproveBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ManageInterestApproveBean load() {
				ManageInterestApproveBean set = null;
				for (ManageInterestApproveBean beanVar : manageInterestList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ManageInterestApproveBean>(model);
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
	protected List<ManageInterestApproveBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ManageInterestApproveBean> sublist = getIndex(manageInterestList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}
	
	/**
	 * returns the index
	 */
	protected List<ManageInterestApproveBean> getIndex(List<ManageInterestApproveBean> attachmentsList, String prop,
		boolean asc) {
		return sort(attachmentsList, prop, asc);
	}
	
	/**
	 * returns the sorted list
	 */
	private List<ManageInterestApproveBean> sort(List<ManageInterestApproveBean> entries, String property, boolean asc) {
		if (property.equals("workFlowId")) {
			sortByWorkFlowId(entries, asc);
		} 
		else if (property.equals("description"))  {
			sortByDescription(entries, asc);
		}
		else if (property.equals("lastModifiedById"))  {
			sortByModifiedId(entries, asc);
		}
		else if (property.equals("lastModifiedByName"))  {
			sortByModifiedName(entries, asc);
		}
		
		return entries;
	}
	
	/**
	 * returns the sorted list by sortById
	 */
	private void sortByWorkFlowId(List<ManageInterestApproveBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageInterestApproveBean>() {

				@Override
				public int compare(ManageInterestApproveBean arg0, ManageInterestApproveBean arg1) {
					return (arg0).getWorkFlowId().compareTo((arg1).getWorkFlowId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageInterestApproveBean>() {

				@Override
				public int compare(ManageInterestApproveBean arg0, ManageInterestApproveBean arg1) {
					return (arg1).getWorkFlowId().compareTo((arg0).getWorkFlowId());
				}
			});
		}
	}
	
	/**
	 * returns the sorted list by sortByDescription
	 */
	private void sortByDescription(List<ManageInterestApproveBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageInterestApproveBean>() {

				@Override
				public int compare(ManageInterestApproveBean arg0, ManageInterestApproveBean arg1) {
					return (arg0).getDescription().compareTo((arg1).getDescription());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageInterestApproveBean>() {

				@Override
				public int compare(ManageInterestApproveBean arg0, ManageInterestApproveBean arg1) {
					return (arg1).getDescription().compareTo((arg0).getDescription());
				}
			});
		}
	}
	
	
	/**
	 * returns the sorted list by sortByModifiedId
	 */
	private void sortByModifiedId(List<ManageInterestApproveBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageInterestApproveBean>() {

				@Override
				public int compare(ManageInterestApproveBean arg0, ManageInterestApproveBean arg1) {
					return (arg0).getLastModifiedById().compareTo((arg1).getLastModifiedById());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageInterestApproveBean>() {

				@Override
				public int compare(ManageInterestApproveBean arg0, ManageInterestApproveBean arg1) {
					return (arg1).getLastModifiedById().compareTo((arg0).getLastModifiedById());
				}
			});
		}
	}
	
	/**
	 * returns the sorted list by sortByModifiedName
	 */
	private void sortByModifiedName(List<ManageInterestApproveBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageInterestApproveBean>() {

				@Override
				public int compare(ManageInterestApproveBean arg0, ManageInterestApproveBean arg1) {
					return (arg0).getLastModifiedByName().compareTo((arg1).getLastModifiedByName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageInterestApproveBean>() {

				@Override
				public int compare(ManageInterestApproveBean arg0, ManageInterestApproveBean arg1) {
					return (arg1).getLastModifiedByName().compareTo((arg0).getLastModifiedByName());
				}
			});
		}
	}

}
