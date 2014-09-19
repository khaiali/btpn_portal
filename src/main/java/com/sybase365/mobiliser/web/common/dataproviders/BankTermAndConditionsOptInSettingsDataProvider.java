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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.OptInSetting;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author Sushil.agrawala
 */
@SuppressWarnings("serial")
public class BankTermAndConditionsOptInSettingsDataProvider extends
	SortableDataProvider<OptInSetting> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(BankTermAndConditionsOptInSettingsDataProvider.class);

    private transient List<OptInSetting> listOptinSettings;

    private MBankingClientLogic mBankingClientLogic;

    public BankTermAndConditionsOptInSettingsDataProvider(
	    String defaultSortProperty,
	    final MBankingClientLogic mBankingClientLogic) {
	setSort(defaultSortProperty, true);
	this.mBankingClientLogic = mBankingClientLogic;
    }

    /**
     * Returns OptInSettings starting with index <code>first</code> and ending
     * with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<OptInSetting> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>AlertEntry</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (listOptinSettings == null) {
	    return count;
	}

	return listOptinSettings.size();
    }

    @Override
    public final IModel<OptInSetting> model(final OptInSetting object) {
	IModel<OptInSetting> model = new LoadableDetachableModel<OptInSetting>() {
	    @Override
	    protected OptInSetting load() {
		OptInSetting set = null;
		for (OptInSetting obj : listOptinSettings) {
		    if (obj == object) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};
	return new CompoundPropertyModel<OptInSetting>(model);
    }

    public void loadBankTermAndConditions() throws DataProviderLoadException {

	if (!PortalUtils.exists(getListOptinSettings())) {
	    listOptinSettings = new ArrayList<OptInSetting>();
	    for (OptInSetting optInSettings : mBankingClientLogic
		    .getOptInSettings()) {

		if (optInSettings.getType() == 1) {
		    listOptinSettings.add(optInSettings);
		}
	    }
	}
    }

    protected List<OptInSetting> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<OptInSetting> sublist = getIndex(listOptinSettings, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<OptInSetting> getIndex(
	    List<OptInSetting> filteredOptinSettingsEntries, String prop,
	    boolean asc) {

	if (prop.equals("name")) {
	    return sort(filteredOptinSettingsEntries, asc);
	} else {
	    return filteredOptinSettingsEntries;
	}
    }

    private List<OptInSetting> sort(List<OptInSetting> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<OptInSetting>() {

		@Override
		public int compare(OptInSetting arg0, OptInSetting arg1) {
		    return (arg0).getType().compareTo((arg1).getType());

		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<OptInSetting>() {

		@Override
		public int compare(OptInSetting arg0, OptInSetting arg1) {
		    return (arg1).getType().compareTo((arg0).getType());

		}
	    });
	}
	return entries;
    }

    public void setListOptinSettings(List<OptInSetting> listOptinSettings) {
	this.listOptinSettings = listOptinSettings;
    }

    public List<OptInSetting> getListOptinSettings() {
	return listOptinSettings;
    }

}