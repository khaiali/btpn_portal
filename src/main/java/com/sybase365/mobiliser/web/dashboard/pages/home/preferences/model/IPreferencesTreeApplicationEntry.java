package com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model;

/**
 *
 * @author msw
 */
public interface IPreferencesTreeApplicationEntry {

    public String getName();

    public String getReadPrivilege();

    public String getWritePrivilege();
    
    public String getDescription();

}
