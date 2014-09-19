package com.sybase365.mobiliser.web.dashboard.pages.home.preferences.beans;

import java.io.Serializable;
import java.util.List;

import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesTreeEntry;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.DetailedMap;
import com.sybase365.mobiliser.web.util.PortalUtils;
import java.util.ArrayList;
import java.util.Collections;

public class PreferencesTreeEntry implements IPreferencesTreeEntry, Serializable {

    private String applicationName;
    private String path;
    private String name;
    private List<DetailedMap.Entry> prefs;

    public PreferencesTreeEntry(String applicationName, String path, 
	    String name, List<DetailedMap.Entry> prefs) {

        this.applicationName = applicationName;
        this.path = path;
        this.name = name;
        this.prefs = prefs;
    }

    @Override
    public void setPrefs(List<DetailedMap.Entry> prefs) {
        this.prefs = prefs;
    }

    @Override
    public String getApplicationName() {
        return this.applicationName;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getFullPath() {
        return this.path + "/" + this.name;
    }

    @Override
    public List<String> getKeys() {
	if (PortalUtils.exists(prefs)) {
	    ArrayList<String> keys = new ArrayList<String>();
	    for (DetailedMap.Entry entry : prefs) {
		keys.add(entry.getKey());
	    }
	    return keys;
    	}
	else {
	    return Collections.EMPTY_LIST;
	}
    }

    @Override
    public String getKeyValue(String key) {
	return getEntry(key).getValue();
    }

    @Override
    public String getKeyType(String key) {
	return getEntry(key).getType();
    }

    @Override
    public String getKeyDescription(String key) {
	return getEntry(key).getDescription();
    }

    private DetailedMap.Entry getEntry(String key) {
	if (PortalUtils.exists(prefs)) {
	    for (DetailedMap.Entry entry : prefs) {
		if (entry.getKey().equals(key)) {
		    return entry;
		}
	    }
    	}
	return null;
    }
}