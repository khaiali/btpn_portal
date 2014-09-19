package com.sybase365.mobiliser.web.dashboard.pages.home.preferences;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.dashboard.pages.home.HomeMenuGroup;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesNavigator;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_PREFS)
public class PreferencesApplicationAddPage extends HomeMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = 
            org.slf4j.LoggerFactory.getLogger(PreferencesApplicationAddPage.class);

    private IPreferencesNavigator backPage;

    public PreferencesApplicationAddPage(IPreferencesNavigator backPage) {
        super();
        this.backPage = backPage;
        initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {

	add(new PreferencesApplicationPanel("preferencesApplicationPanel", null,
		backPage, this));
    }

    @Override
    protected Class getActiveMenu() {
	return PreferencesPage.class;
    }
 
}