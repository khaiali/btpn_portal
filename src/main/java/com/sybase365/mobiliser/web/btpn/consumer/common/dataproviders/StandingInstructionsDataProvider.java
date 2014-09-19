package com.sybase365.mobiliser.web.btpn.consumer.common.dataproviders;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.sybase365.mobiliser.web.btpn.consumer.beans.StandingInstructionsBean;


public class StandingInstructionsDataProvider extends SortableDataProvider<StandingInstructionsBean> {
	
	private static final long serialVersionUID = 1L;

	private List<StandingInstructionsBean> instructionsList;

	public List<StandingInstructionsBean> getsubAccountsList() {
		return instructionsList;
	}

	public void setsubAccountsList(List<StandingInstructionsBean> subAccountsList) {
		this.instructionsList = subAccountsList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public StandingInstructionsDataProvider(String defaultSortProperty,
		final List<StandingInstructionsBean> subAccountsList) {
		this.instructionsList = subAccountsList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends StandingInstructionsBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<StandingInstructionsBean> model(final StandingInstructionsBean object) {
		IModel<StandingInstructionsBean> model = new LoadableDetachableModel<StandingInstructionsBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected StandingInstructionsBean load() {
				StandingInstructionsBean set = null;
				for (StandingInstructionsBean beanVar : instructionsList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<StandingInstructionsBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (instructionsList == null) {
			return count;
		}
		return instructionsList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<StandingInstructionsBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<StandingInstructionsBean> sublist = getIndex(instructionsList, sortProperty, sortAsc).subList(first,
			first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<StandingInstructionsBean> getIndex(List<StandingInstructionsBean> subAccountsList, String prop,
		boolean asc) {
		return sort(subAccountsList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<StandingInstructionsBean> sort(List<StandingInstructionsBean> entries, String property, boolean asc) {
//		if (property.equals("name")) {
//			sortByName(entries, asc);
//		} else if (property.equals("payer")) {
//			sortByPayer(entries, asc);
//		} else if (property.equals("payee")) {
//			sortByPayee(entries, asc);
//		} else if (property.equals("type")) {
//			sortByType(entries, asc);
//		} else if (property.equals("startDate")) {
//			sortByStartDate(entries, asc);
//		} else if (property.equals("expiryDate")){
//			sortByExpiryDate(entries, asc);
//		} 
		
		if (property.equals("payer")) {
			sortByPayer(entries, asc);
		} else if (property.equals("payee")) {
			sortByPayee(entries, asc);
		} else if (property.equals("type")) {
			sortByType(entries, asc);
		} else if (property.equals("startDate")) {
			sortByStartDate(entries, asc);
		} else if (property.equals("expiryDate")){
			sortByExpiryDate(entries, asc);
		} 
		return entries;
	}

	/**
	 * returns the sorted list by sortByDate
	 */
	private void sortByName(List<StandingInstructionsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<StandingInstructionsBean>() {

				@Override
				public int compare(StandingInstructionsBean arg0, StandingInstructionsBean arg1) {
					return (arg0).getName().compareTo((arg1).getName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<StandingInstructionsBean>() {

				@Override
				public int compare(StandingInstructionsBean arg0, StandingInstructionsBean arg1) {
					return (arg1).getName().compareTo((arg0).getName());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByPayer
	 */
	private void sortByPayer(List<StandingInstructionsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<StandingInstructionsBean>() {

				@Override
				public int compare(StandingInstructionsBean arg0, StandingInstructionsBean arg1) {
					return (arg0).getPayer().compareTo((arg1).getPayer());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<StandingInstructionsBean>() {

				@Override
				public int compare(StandingInstructionsBean arg0, StandingInstructionsBean arg1) {
					return (arg1).getPayer().compareTo((arg0).getPayer());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByPayee
	 */
	private void sortByPayee(List<StandingInstructionsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<StandingInstructionsBean>() {

				@Override
				public int compare(StandingInstructionsBean arg0, StandingInstructionsBean arg1) {
					return (arg0).getPayee().compareTo((arg1).getPayee());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<StandingInstructionsBean>() {

				@Override
				public int compare(StandingInstructionsBean arg0, StandingInstructionsBean arg1) {
					return (arg1).getPayee().compareTo((arg0).getPayee());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByType
	 */
	private void sortByType(List<StandingInstructionsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<StandingInstructionsBean>() {

				@Override
				public int compare(StandingInstructionsBean arg0, StandingInstructionsBean arg1) {
					return (arg0).getType().compareTo((arg1).getType());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<StandingInstructionsBean>() {

				@Override
				public int compare(StandingInstructionsBean arg0, StandingInstructionsBean arg1) {
					return (arg1).getType().compareTo((arg0).getType());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByStartDate
	 */
	private void sortByStartDate(List<StandingInstructionsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<StandingInstructionsBean>() {

				@Override
				public int compare(StandingInstructionsBean arg0, StandingInstructionsBean arg1) {
					return (arg0).getStartDate().compareTo((arg1).getStartDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<StandingInstructionsBean>() {

				@Override
				public int compare(StandingInstructionsBean arg0, StandingInstructionsBean arg1) {
					return (arg1).getStartDate().compareTo((arg0).getStartDate());
				}
			});
		}
	}
	
	
	/**
	 * returns the sorted list by sortByStartDate
	 */
	private void sortByExpiryDate(List<StandingInstructionsBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<StandingInstructionsBean>() {

				@Override
				public int compare(StandingInstructionsBean arg0, StandingInstructionsBean arg1) {
					return (arg0).getExpiryDate().compareTo((arg1).getExpiryDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<StandingInstructionsBean>() {

				@Override
				public int compare(StandingInstructionsBean arg0, StandingInstructionsBean arg1) {
					return (arg1).getExpiryDate().compareTo((arg0).getExpiryDate());
				}
			});
		}
	}

}
