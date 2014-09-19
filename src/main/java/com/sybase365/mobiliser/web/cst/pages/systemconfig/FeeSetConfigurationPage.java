package com.sybase365.mobiliser.web.cst.pages.systemconfig;

import com.sybase365.mobiliser.web.common.panels.FeeSetPanel;

public class FeeSetConfigurationPage extends BaseSystemConfigurationPage {

    private static final long serialVersionUID = 1L;

    private FeeSetPanel feeSetPanel;

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();
	feeSetPanel = new FeeSetPanel("feeSetPanel", this, null);
	add(feeSetPanel);
    }

}
