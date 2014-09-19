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

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsApproveBean;

/**
 * This class is used as a data provider for displaying list of products in Manage Products Approve Page.
 * 
 * @author Vikram Gunda
 */
public class ManageProductsApproveProvider extends SortableDataProvider<ManageProductsApproveBean> {

	private static final long serialVersionUID = 1L;

	private List<ManageProductsApproveBean> manageApproveProductsList;

	public List<ManageProductsApproveBean> getmanageApproveProductsList() {
		return manageApproveProductsList;
	}

	public void setmanageApproveProductsList(List<ManageProductsApproveBean> manageApproveProductsList) {
		this.manageApproveProductsList = manageApproveProductsList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ManageProductsApproveProvider(String defaultSortProperty,
		final List<ManageProductsApproveBean> manageApproveProductsList) {
		this.manageApproveProductsList = manageApproveProductsList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ManageProductsApproveBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ManageProductsApproveBean> model(final ManageProductsApproveBean object) {
		IModel<ManageProductsApproveBean> model = new LoadableDetachableModel<ManageProductsApproveBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ManageProductsApproveBean load() {
				ManageProductsApproveBean set = null;
				for (ManageProductsApproveBean beanVar : manageApproveProductsList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ManageProductsApproveBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (manageApproveProductsList == null) {
			return count;
		}
		return manageApproveProductsList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<ManageProductsApproveBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ManageProductsApproveBean> sublist = getIndex(manageApproveProductsList, sortProperty, sortAsc).subList(
			first, first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ManageProductsApproveBean> getIndex(final List<ManageProductsApproveBean> entries, String prop,
		boolean asc) {
		if (prop.equals("maker")) {
			sortByMaker(entries, asc);
		} else if (prop.equals("productType")) {
			sortByProductType(entries, asc);
		} else if (prop.equals("productName")) {
			sortByName(entries, asc);
		} else {
			sortByStatus(entries, asc);
		}
		return entries;

	}

	/**
	 * returns the sorted list by sortByMaker
	 */
	private void sortByMaker(List<ManageProductsApproveBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageProductsApproveBean>() {

				@Override
				public int compare(ManageProductsApproveBean arg0, ManageProductsApproveBean arg1) {
					return (arg0).getMaker().compareTo((arg1).getMaker());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageProductsApproveBean>() {

				@Override
				public int compare(ManageProductsApproveBean arg0, ManageProductsApproveBean arg1) {
					return (arg1).getMaker().compareTo((arg0).getMaker());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByProductType
	 */
	private void sortByProductType(List<ManageProductsApproveBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageProductsApproveBean>() {

				@Override
				public int compare(ManageProductsApproveBean arg0, ManageProductsApproveBean arg1) {
					return (arg0).getProductType().compareTo((arg1).getProductType());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageProductsApproveBean>() {

				@Override
				public int compare(ManageProductsApproveBean arg0, ManageProductsApproveBean arg1) {
					return (arg1).getProductType().compareTo((arg0).getProductType());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByName
	 */
	private void sortByName(List<ManageProductsApproveBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageProductsApproveBean>() {

				@Override
				public int compare(ManageProductsApproveBean arg0, ManageProductsApproveBean arg1) {
					return (arg0).getProductName().compareTo((arg1).getProductName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageProductsApproveBean>() {

				@Override
				public int compare(ManageProductsApproveBean arg0, ManageProductsApproveBean arg1) {
					return (arg1).getProductName().compareTo((arg0).getProductName());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByStatus
	 */
	private void sortByStatus(List<ManageProductsApproveBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageProductsApproveBean>() {

				@Override
				public int compare(ManageProductsApproveBean arg0, ManageProductsApproveBean arg1) {
					return (arg0).getStatus().compareTo((arg1).getStatus());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageProductsApproveBean>() {

				@Override
				public int compare(ManageProductsApproveBean arg0, ManageProductsApproveBean arg1) {
					return (arg1).getStatus().compareTo((arg0).getStatus());
				}
			});
		}
	}

}
