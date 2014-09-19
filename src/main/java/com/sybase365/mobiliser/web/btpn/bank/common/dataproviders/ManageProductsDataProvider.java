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

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsBean;

/**
 * This class is used as a data provider for displaying list of products in Manage Products Page
 * 
 * @author Vikram Gunda
 */
public class ManageProductsDataProvider extends SortableDataProvider<ManageProductsBean> {
	
	private static final long serialVersionUID = 1L;

	private List<ManageProductsBean> manageProductsList;

	public List<ManageProductsBean> getManageProductsList() {
		return manageProductsList;
	}

	public void setManageProductsList(List<ManageProductsBean> manageProductsList) {
		this.manageProductsList = manageProductsList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ManageProductsDataProvider(String defaultSortProperty, final List<ManageProductsBean> manageProductsList) {
		this.manageProductsList = manageProductsList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ManageProductsBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ManageProductsBean> model(final ManageProductsBean object) {
		IModel<ManageProductsBean> model = new LoadableDetachableModel<ManageProductsBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ManageProductsBean load() {
				ManageProductsBean set = null;
				for (ManageProductsBean beanVar : manageProductsList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ManageProductsBean>(model);
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
	protected List<ManageProductsBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ManageProductsBean> sublist = getIndex(manageProductsList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ManageProductsBean> getIndex(List<ManageProductsBean> attachmentsList, String prop, boolean asc) {
		return sort(attachmentsList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<ManageProductsBean> sort(List<ManageProductsBean> entries, String property, boolean asc) {
		if (property.equals("productType")) {
			sortByProductType(entries, asc);
		} else if (property.equals("productName")) {
			sortByProductName(entries, asc);
		} else {
			sortByProductId(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByProductType
	 */
	private void sortByProductType(List<ManageProductsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageProductsBean>() {

				@Override
				public int compare(ManageProductsBean arg0, ManageProductsBean arg1) {
					return (arg0).getProductType().compareTo((arg1).getProductType());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageProductsBean>() {

				@Override
				public int compare(ManageProductsBean arg0, ManageProductsBean arg1) {
					return (arg1).getProductType().compareTo((arg0).getProductType());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByProductName
	 */
	private void sortByProductName(List<ManageProductsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageProductsBean>() {

				@Override
				public int compare(ManageProductsBean arg0, ManageProductsBean arg1) {
					return (arg0).getProductName().compareTo((arg1).getProductName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageProductsBean>() {

				@Override
				public int compare(ManageProductsBean arg0, ManageProductsBean arg1) {
					return (arg1).getProductName().compareTo((arg0).getProductName());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByProductId
	 */
	private void sortByProductId(List<ManageProductsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageProductsBean>() {

				@Override
				public int compare(ManageProductsBean arg0, ManageProductsBean arg1) {
					return (arg0).getProductId().compareTo((arg1).getProductId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageProductsBean>() {

				@Override
				public int compare(ManageProductsBean arg0, ManageProductsBean arg1) {
					return (arg1).getProductId().compareTo((arg0).getProductId());
				}
			});
		}
	}
}
