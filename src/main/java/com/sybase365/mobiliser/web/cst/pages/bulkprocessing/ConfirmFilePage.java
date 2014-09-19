package com.sybase365.mobiliser.web.cst.pages.bulkprocessing;

import com.sybase365.mobiliser.web.common.panels.ConfirmFilePanel;

public class ConfirmFilePage extends BaseBulkProcessingPage {

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new ConfirmFilePanel("confirmFilePanel", this));
    }
}
