package com.sybase365.mobiliser.web.util;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.IGridSortState.ISortStateColumn;
import com.inmethod.grid.datagrid.DefaultDataGrid;
import com.sybase365.mobiliser.util.tools.wicketutils.SortUtils;

public class TableDataGrid<ModelType extends Serializable> implements
	Serializable {

    private static final long serialVersionUID = 1L;

    private DefaultDataGrid dataGrid;
    private boolean visible = true;

    public TableDataGrid(String id, final List<ModelType> dataList,
	    List<IGridColumn> columns) {
	// define a data
	// source IDataSource is the provider-interface which is used by the
	// DefaultDataGrid (which is created later on) to retrieve the actual
	// content of the grid
	IDataSource dataSource = new IDataSource() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void detach() {
		// nothind to do here
	    }

	    @Override
	    public void query(IQuery query, IQueryResult result) {

		// implement the sorting
		// there are two main aproaches:
		// 1.) sort the whole list
		// 2.) sort only the sub list which is displayed
		// here we sort the complete list and then select the range to
		// display
		if (query.getSortState() != null
			&& !query.getSortState().getColumns().isEmpty()) {
		    final ISortStateColumn sortColumn = query.getSortState()
			    .getColumns().get(0);
		    SortUtils.sort(
			    dataList,
			    sortColumn.getPropertyName(),
			    sortColumn.getDirection().equals(
				    IGridSortState.Direction.ASC));
		}

		// to get the items to display we have to implement our own
		// pagination
		int fromIndex = query.getFrom();
		int toIndex = query.getFrom() + query.getCount();
		if (toIndex > dataList.size())
		    toIndex = dataList.size();
		List<ModelType> itemsToDisplay = dataList.subList(fromIndex,
			toIndex);

		// initialize the result with the total count of items in
		// the grid
		result.setTotalCount(dataList.size());

		// set the items to display in the grid as determined beforehand
		result.setItems(itemsToDisplay.iterator());
	    }

	    @SuppressWarnings("unchecked")
	    @Override
	    public IModel<ModelType> model(Object object) {
		return new Model<ModelType>((ModelType) object);
	    }
	};

	// now that the column list and data source are in place the grid can
	// be put together
	dataGrid = new DefaultDataGrid(id, dataSource, columns) {
	    private static final long serialVersionUID = 1L;

	    @SuppressWarnings("unchecked")
	    @Override
	    public void selectItem(IModel itemModel, boolean selected) {
		super.selectItem(itemModel, selected);
		onItemSelect(itemModel, selected);
	    }
	};
	dataGrid.setOutputMarkupId(true);
	dataGrid.setAllowSelectMultiple(true);
	dataGrid.setClickRowToSelect(true);
	dataGrid.setClickRowToDeselect(true);
	dataGrid.setRowsPerPage(5);
	dataGrid.setAllowSelectMultiple(false);
    }

    public void onItemSelect(IModel<ModelType> itemModel, boolean selected) {

    }

    public DefaultDataGrid getDataGrid() {
	dataGrid.setVisible(isVisible());
	return dataGrid;
    }

    public boolean isVisible() {
	return this.visible;
    }

    public void setVisible(boolean visible) {
	this.visible = visible;
    }

}
