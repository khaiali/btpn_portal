package com.sybase365.mobiliser.web.common.dataproviders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.protocol.http.WebSession;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.BalanceAlert;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class BalanceAlertDataProvider extends
	SortableDataProvider<BalanceAlert> {

    private transient List<BalanceAlert> balanceAlertList = new ArrayList<BalanceAlert>();
    private MobiliserBasePage mobBasePage;

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public BalanceAlertDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends BalanceAlert> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public IModel<BalanceAlert> model(final BalanceAlert object) {
	IModel<BalanceAlert> model = new LoadableDetachableModel<BalanceAlert>() {
	    @Override
	    protected BalanceAlert load() {
		BalanceAlert set = null;
		for (BalanceAlert obj : balanceAlertList) {
		    if (obj == object) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<BalanceAlert>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (balanceAlertList == null) {
	    return count;
	}

	return balanceAlertList.size();
    }

    protected List<BalanceAlert> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<BalanceAlert> sublist = getIndex(balanceAlertList, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<BalanceAlert> getIndex(List<BalanceAlert> balanceAlertList,
	    String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(balanceAlertList, asc);
	} else {
	    return balanceAlertList;
	}
    }

    private List<BalanceAlert> sort(List<BalanceAlert> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<BalanceAlert>() {

		@Override
		public int compare(BalanceAlert arg0, BalanceAlert arg1) {
		    return (arg0).getId().compareTo((arg1).getId());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<BalanceAlert>() {

		@Override
		public int compare(BalanceAlert arg0, BalanceAlert arg1) {
		    return (arg1).getId().compareTo((arg0).getId());
		}
	    });
	}
	return entries;
    }

    public List<BalanceAlert> getBalanceAlerts(WebSession session)
	    throws DataProviderLoadException {
	this.balanceAlertList = ((MobiliserWebSession) session)
		.getBalanceAlertList();
	return balanceAlertList;
    }

}
