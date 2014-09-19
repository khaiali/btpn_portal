package com.sybase365.mobiliser.web.cst.pages.systemconfig;

import com.sybase365.mobiliser.web.common.panels.LimitSetPanel;

public class LimitSetsPage extends BaseSystemConfigurationPage {

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new LimitSetPanel("limitSetPanel", this, null, Boolean.FALSE));
    }

}
