package com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model;

import java.util.List;

import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.DetailedMap;

/**
 *
 * @author msw
 */
public interface IPreferencesTreeEntry {

    public List<String> getKeys();

    public void setPrefs(List<DetailedMap.Entry> prefs);

    public String getApplicationName();

    public String getPath();

    public String getName();

    public String getFullPath();

    public String getKeyValue(String key);

    public String getKeyType(String key);

    public String getKeyDescription(String key);

}
