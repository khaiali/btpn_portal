package com.sybase365.mobiliser.web.dashboard.pages.home.preferences.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.DetailedMap;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesTreeApplicationEntry;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesTreeEntry;

public class PreferencesTreeApplicationEntry implements IPreferencesTreeEntry, 
	IPreferencesTreeApplicationEntry, Serializable {

    private PreferencesTreeEntry rootNode;

    private String applicationName;
    private String readPrivilege;
    private String writePrivilege;
    private String description;

    public PreferencesTreeApplicationEntry(String applicationName, 
	    String readPrivilege, String writePrivilege, String description) {
        this.applicationName = applicationName;
        this.readPrivilege = readPrivilege;
        this.writePrivilege = writePrivilege;
        this.description = description;
    }

    public PreferencesTreeEntry getRootNode() {
        if (null == rootNode) {
	    rootNode = new PreferencesTreeEntry(applicationName, "/", 
		    applicationName, Collections.EMPTY_LIST);
        }
        return rootNode;
    }

    @Override
    public void setPrefs(List<DetailedMap.Entry> prefs) {
	// no-op for an application tree entry
    }

    @Override
    public String getApplicationName() {
	return this.applicationName;
    }

    @Override
    public String getName() {
        return this.getApplicationName();
    }

    @Override
    public String getReadPrivilege() {
        return this.readPrivilege;
    }

    @Override
    public String getWritePrivilege() {
        return this.writePrivilege;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public List<String> getKeys() {
        return getRootNode().getKeys();
    }

    @Override
    public String getPath() {
        return getRootNode().getPath();
    }

    @Override
    public String getFullPath() {
        return getRootNode().getPath();
    }

    @Override
    public String getKeyValue(String key) {
        return getRootNode().getKeyValue(key);
    }

    @Override
    public String getKeyType(String key) {
        return getRootNode().getKeyType(key);
    }

    @Override
    public String getKeyDescription(String key) {
        return getRootNode().getKeyDescription(key);
    }
}
