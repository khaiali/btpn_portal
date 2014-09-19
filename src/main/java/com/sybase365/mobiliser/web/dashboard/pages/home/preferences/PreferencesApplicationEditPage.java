package com.sybase365.mobiliser.web.dashboard.pages.home.preferences;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.dashboard.pages.home.HomeMenuGroup;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesNavigator;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesTreeApplicationEntry;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_PREFS)
public class PreferencesApplicationEditPage extends HomeMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = 
            org.slf4j.LoggerFactory.getLogger(PreferencesApplicationEditPage.class);

    private IPreferencesTreeApplicationEntry selectedAppNode;
    private IPreferencesNavigator backPage;

    public PreferencesApplicationEditPage(IPreferencesTreeApplicationEntry selectedAppNode, 
	    IPreferencesNavigator backPage) {
        super();
        this.selectedAppNode = selectedAppNode;
        this.backPage = backPage;
        initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {

	add(new PreferencesApplicationPanel("preferencesApplicationPanel", 
		this.selectedAppNode, backPage, this));
    }

    @Override
    protected Class getActiveMenu() {
	return PreferencesPage.class;
    }
 
}