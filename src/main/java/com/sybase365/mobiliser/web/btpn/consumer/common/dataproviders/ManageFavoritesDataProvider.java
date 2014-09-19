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

import com.sybase365.mobiliser.web.btpn.consumer.beans.ManageFavoritesBean;

/**
 * This class is used as a data provider for displaying list of favorites in the consumer Portal
 * 
 * @author Narasa Reddy.
 */
public class ManageFavoritesDataProvider extends SortableDataProvider<ManageFavoritesBean> {
	private static final long serialVersionUID = 1L;

	private List<ManageFavoritesBean> favoritesList;

	public List<ManageFavoritesBean> getfavoritesList() {
		return favoritesList;
	}

	public void setfavoritesList(List<ManageFavoritesBean> favoritesList) {
		this.favoritesList = favoritesList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public ManageFavoritesDataProvider(String defaultSortProperty, final List<ManageFavoritesBean> favoritesList) {
		this.favoritesList = favoritesList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends ManageFavoritesBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<ManageFavoritesBean> model(final ManageFavoritesBean object) {
		IModel<ManageFavoritesBean> model = new LoadableDetachableModel<ManageFavoritesBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected ManageFavoritesBean load() {
				ManageFavoritesBean set = null;
				for (ManageFavoritesBean beanVar : favoritesList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<ManageFavoritesBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (favoritesList == null) {
			return count;
		}
		return favoritesList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<ManageFavoritesBean> find(int first, int count, String sortProperty, boolean sortAsc) {
		List<ManageFavoritesBean> sublist = getIndex(favoritesList, sortProperty, sortAsc)
			.subList(first, first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<ManageFavoritesBean> getIndex(List<ManageFavoritesBean> favoritesList, String prop, boolean asc) {
		return sort(favoritesList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<ManageFavoritesBean> sort(List<ManageFavoritesBean> entries, String property, boolean asc) {
		if (property.equals("name")) {
			sortByName(entries, asc);
		} else if (property.equals("value")) {
			sortByValue(entries, asc);
		} else if (property.equals("type")) {
			sortByType(entries, asc);
		}
		return entries;
	}

	/**
	 * returns the sorted list by sortByName
	 */
	private void sortByName(List<ManageFavoritesBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageFavoritesBean>() {

				@Override
				public int compare(ManageFavoritesBean arg0, ManageFavoritesBean arg1) {
					return (arg0).getFavoriteName().compareTo((arg1).getFavoriteName());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageFavoritesBean>() {

				@Override
				public int compare(ManageFavoritesBean arg0, ManageFavoritesBean arg1) {
					return (arg1).getFavoriteName().compareTo((arg0).getFavoriteName());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByValue
	 */
	private void sortByValue(List<ManageFavoritesBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageFavoritesBean>() {

				@Override
				public int compare(ManageFavoritesBean arg0, ManageFavoritesBean arg1) {
					return (arg0).getFavoriteValue().compareTo((arg1).getFavoriteValue());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageFavoritesBean>() {

				@Override
				public int compare(ManageFavoritesBean arg0, ManageFavoritesBean arg1) {
					return (arg1).getFavoriteValue().compareTo((arg0).getFavoriteValue());
				}
			});
		}
	}

	/**
	 * returns the sorted list by sortByType
	 */
	private void sortByType(List<ManageFavoritesBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<ManageFavoritesBean>() {

				@Override
				public int compare(ManageFavoritesBean arg0, ManageFavoritesBean arg1) {
					return (arg0).getFavoritesType().getValue().compareTo((arg1).getFavoritesType().getValue());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<ManageFavoritesBean>() {

				@Override
				public int compare(ManageFavoritesBean arg0, ManageFavoritesBean arg1) {
					return (arg1).getFavoritesType().getValue().compareTo((arg0).getFavoritesType().getValue());
				}
			});
		}
	}

}
