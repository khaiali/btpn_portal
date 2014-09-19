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

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;

/**
 * This class is used as a data provider for displaying list of products in Manage Fee Details Page
 * 
 * @author Vikram Gunda
 */
public class ManageFeeDetailsDataProvider extends SortableDataProvider<ManageFeeDetailsBean> {

	private static final long serialVersionUID = 1L;

	private List<ManageFeeDetailsBean> manageProductsList;

	public List<ManageFeeDetailsBean> getManageProductsList() {
		return manageProductsList;
	}

	public void setManageProductsList(List<ManageFeeDetailsBean> manageProductsList) {
		this.manageProductsList = manageProductsList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ManageFeeDetailsDataProvider(String defaultSortProperty, final List<ManageFeeDetailsBean> manageProductsList) {
		this.manageProductsList = manageProductsList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ManageFeeDetailsBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ManageFeeDetailsBean> model(final ManageFeeDetailsBean object) {
		IModel<ManageFeeDetailsBean> model = new LoadableDetachableModel<ManageFeeDetailsBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ManageFeeDetailsBean load() {
				ManageFeeDetailsBean set = null;
				for (ManageFeeDetailsBean beanVar : manageProductsList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ManageFeeDetailsBean>(model);
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
	protected List<ManageFeeDetailsBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ManageFeeDetailsBean> sublist = getIndex(manageProductsList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ManageFeeDetailsBean> getIndex(List<ManageFeeDetailsBean> attachmentsList, String prop, boolean asc) {
		return sort(attachmentsList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<ManageFeeDetailsBean> sort(List<ManageFeeDetailsBean> entries, String property, boolean asc) {
		if (property.equals("feeName")) {
			sortByFeeName(entries, asc);
		} else if (property.equals("glCode")) {
			sortByGlCode(entries, asc);
		} else if (property.equals("fixedFee")) {
			sortByFixedFee(entries, asc);
		} else if (property.equals("percentageFee")) {
			sortByPercentageFee(entries, asc);
		} else if (property.equals("minValue")) {
			sortByMinValue(entries, asc);
		} else {
			sortByMaxValue(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByFeeName
	 */
	private void sortByFeeName(List<ManageFeeDetailsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageFeeDetailsBean>() {

				@Override
				public int compare(ManageFeeDetailsBean arg0, ManageFeeDetailsBean arg1) {
					return (arg0).getFeeName().compareTo((arg1).getFeeName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageFeeDetailsBean>() {

				@Override
				public int compare(ManageFeeDetailsBean arg0, ManageFeeDetailsBean arg1) {
					return (arg1).getFeeName().compareTo((arg0).getFeeName());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByGlCode
	 */
	private void sortByGlCode(List<ManageFeeDetailsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageFeeDetailsBean>() {

				@Override
				public int compare(ManageFeeDetailsBean arg0, ManageFeeDetailsBean arg1) {
					return (arg0).getGlCode().compareTo((arg1).getGlCode());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageFeeDetailsBean>() {

				@Override
				public int compare(ManageFeeDetailsBean arg0, ManageFeeDetailsBean arg1) {
					return (arg1).getGlCode().compareTo((arg0).getGlCode());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByProductId
	 */
	private void sortByFixedFee(List<ManageFeeDetailsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageFeeDetailsBean>() {

				@Override
				public int compare(ManageFeeDetailsBean arg0, ManageFeeDetailsBean arg1) {
					return (arg0).getFixedFee().compareTo((arg1).getFixedFee());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageFeeDetailsBean>() {

				@Override
				public int compare(ManageFeeDetailsBean arg0, ManageFeeDetailsBean arg1) {
					return (arg1).getFixedFee().compareTo((arg0).getFixedFee());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByPercentageFee
	 */
	private void sortByPercentageFee(List<ManageFeeDetailsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageFeeDetailsBean>() {

				@Override
				public int compare(ManageFeeDetailsBean arg0, ManageFeeDetailsBean arg1) {
					return (arg0).getPercentageFee().compareTo((arg1).getPercentageFee());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageFeeDetailsBean>() {

				@Override
				public int compare(ManageFeeDetailsBean arg0, ManageFeeDetailsBean arg1) {
					return (arg1).getPercentageFee().compareTo((arg0).getPercentageFee());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByPercentageFee
	 */
	private void sortByMinValue(List<ManageFeeDetailsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageFeeDetailsBean>() {

				@Override
				public int compare(ManageFeeDetailsBean arg0, ManageFeeDetailsBean arg1) {
					return (arg0).getMinValue().compareTo((arg1).getMinValue());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageFeeDetailsBean>() {

				@Override
				public int compare(ManageFeeDetailsBean arg0, ManageFeeDetailsBean arg1) {
					return (arg1).getMinValue().compareTo((arg0).getMinValue());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByPercentageFee
	 */
	private void sortByMaxValue(List<ManageFeeDetailsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageFeeDetailsBean>() {

				@Override
				public int compare(ManageFeeDetailsBean arg0, ManageFeeDetailsBean arg1) {
					return (arg0).getMaxValue().compareTo((arg1).getMaxValue());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageFeeDetailsBean>() {

				@Override
				public int compare(ManageFeeDetailsBean arg0, ManageFeeDetailsBean arg1) {
					return (arg1).getMaxValue().compareTo((arg0).getMaxValue());
				}
			});
		}
	}
}
