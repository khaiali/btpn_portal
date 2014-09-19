package com.sybase365.mobiliser.web.distributor.pages.bulkprocessing;

import com.sybase365.mobiliser.web.common.panels.BulkHistoryPanel;

public class BulkHistoryPage extends BaseBulkProcessingPage {

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new BulkHistoryPanel("bulkHistoryPanel", this));
    }
}
