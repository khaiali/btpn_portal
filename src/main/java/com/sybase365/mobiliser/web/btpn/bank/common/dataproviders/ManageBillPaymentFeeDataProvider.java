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

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeBean;

/**
 * This class is used as a data provider for displaying list of products in Manage Fee Details Page
 * 
 * @author Feny Yanti
 */
public class ManageBillPaymentFeeDataProvider extends SortableDataProvider<ManageBillPaymentFeeBean> {

	private static final long serialVersionUID = 1L;

	private List<ManageBillPaymentFeeBean> manageBillerFeeList;
	
	/**
	 * @return the manageBillerFeeList
	 */
	public List<ManageBillPaymentFeeBean> getManageBillerFeeList() {
		return manageBillerFeeList;
	}

	/**
	 * @param manageBillerFeeList the manageBillerFeeList to set
	 */
	public void setManageBillerFeeList(
			List<ManageBillPaymentFeeBean> manageBillerFeeList) {
		this.manageBillerFeeList = manageBillerFeeList;
	}
	
	public ManageBillPaymentFeeDataProvider(String defaultSortproperty){
		setSort(defaultSortproperty, true);
	}

	
	public ManageBillPaymentFeeDataProvider(String defaultSortproperty, 
			final List<ManageBillPaymentFeeBean> manageBillerFeeList){
		this.manageBillerFeeList = manageBillerFeeList;
		setSort(defaultSortproperty, false);
	}
	
	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ManageBillPaymentFeeBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}


	@Override
	public IModel<ManageBillPaymentFeeBean> model(
			final ManageBillPaymentFeeBean object) {
		IModel<ManageBillPaymentFeeBean> model = new LoadableDetachableModel<ManageBillPaymentFeeBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ManageBillPaymentFeeBean load() {
				ManageBillPaymentFeeBean set = null;
				for (ManageBillPaymentFeeBean beanVar : manageBillerFeeList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ManageBillPaymentFeeBean>(model);
	}


	@Override
	public int size() {
		int count = 0;
		if (manageBillerFeeList == null){
			return count;
		}
		return manageBillerFeeList.size();
	}
	
	/**
	 * returns the sorted list
	 */
	protected List<ManageBillPaymentFeeBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ManageBillPaymentFeeBean> sublist = getIndex(manageBillerFeeList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}
	
	/**
	 * returns the index
	 */
	protected List<ManageBillPaymentFeeBean> getIndex(List<ManageBillPaymentFeeBean> attachmentsList, String prop,
		boolean asc) {
		return sort(attachmentsList, prop, asc);
	}
	
	/**
	 * returns the sorted list
	 */
	private List<ManageBillPaymentFeeBean> sort(List<ManageBillPaymentFeeBean> entries, String property, boolean asc) {
		if (property.equals("id")) {
			sortById(entries, asc);
		} else if(property.equals("description")) {
			sortByDescription(entries, asc);
		}
		else if(property.equals("wrkId")){
				sortByWrkId(entries, asc);
		}
	    else if(property.equals("name")){
			sortByName(entries, asc);
		}
	    else if(property.equals("date")){
			sortByDate(entries, asc);
		}
		return entries;
	}
	
	/**
	 * returns the sorted list by sortById
	 */
	private void sortById(List<ManageBillPaymentFeeBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageBillPaymentFeeBean>() {

				@Override
				public int compare(ManageBillPaymentFeeBean arg0, ManageBillPaymentFeeBean arg1) {
					return (arg0).getId().compareTo((arg1).getId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageBillPaymentFeeBean>() {

				@Override
				public int compare(ManageBillPaymentFeeBean arg0, ManageBillPaymentFeeBean arg1) {
					return (arg1).getId().compareTo((arg0).getId());
				}
			});
		}
	}
	
	/**
	 * returns the sorted list by sortByDescription
	 */
	private void sortByDescription(List<ManageBillPaymentFeeBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageBillPaymentFeeBean>() {

				@Override
				public int compare(ManageBillPaymentFeeBean arg0, ManageBillPaymentFeeBean arg1) {
					return (arg0).getDescription().compareTo((arg1).getDescription());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageBillPaymentFeeBean>() {

				@Override
				public int compare(ManageBillPaymentFeeBean arg0, ManageBillPaymentFeeBean arg1) {
					return (arg1).getDescription().compareTo((arg0).getDescription());
				}
			});
		}
	}
	
	private void sortByWrkId(List<ManageBillPaymentFeeBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageBillPaymentFeeBean>() {

				@Override
				public int compare(ManageBillPaymentFeeBean arg0, ManageBillPaymentFeeBean arg1) {
					return (arg0).getWorkflowId().compareTo((arg1).getWorkflowId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageBillPaymentFeeBean>() {

				@Override
				public int compare(ManageBillPaymentFeeBean arg0, ManageBillPaymentFeeBean arg1) {
					return (arg1).getWorkflowId().compareTo((arg0).getWorkflowId());
				}
			});
		}
	}
	
	private void sortByName(List<ManageBillPaymentFeeBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageBillPaymentFeeBean>() {

				@Override
				public int compare(ManageBillPaymentFeeBean arg0, ManageBillPaymentFeeBean arg1) {
					return (arg0).getLastModifiedByName().compareTo((arg1).getLastModifiedByName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageBillPaymentFeeBean>() {

				@Override
				public int compare(ManageBillPaymentFeeBean arg0, ManageBillPaymentFeeBean arg1) {
					return (arg1).getLastModifiedByName().compareTo((arg0).getLastModifiedByName());
				}
			});
		}
	}
	
	
	private void sortByDate(List<ManageBillPaymentFeeBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageBillPaymentFeeBean>() {

				@Override
				public int compare(ManageBillPaymentFeeBean arg0, ManageBillPaymentFeeBean arg1) {
					return (arg0).getLastModifiedByDate().compareTo((arg1).getLastModifiedByDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageBillPaymentFeeBean>() {

				@Override
				public int compare(ManageBillPaymentFeeBean arg0, ManageBillPaymentFeeBean arg1) {
					return (arg1).getLastModifiedByDate().compareTo((arg0).getLastModifiedByDate());
				}
			});
		}
	}
}
