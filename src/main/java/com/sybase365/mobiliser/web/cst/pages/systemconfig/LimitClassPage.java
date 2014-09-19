package com.sybase365.mobiliser.web.cst.pages.systemconfig;

import com.sybase365.mobiliser.web.common.panels.LimitClassPanel;

public class LimitClassPage extends BaseSystemConfigurationPage {

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new LimitClassPanel("limitClassPanel", this, null));
    }

}
