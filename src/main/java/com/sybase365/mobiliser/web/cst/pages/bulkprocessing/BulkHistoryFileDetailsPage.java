package com.sybase365.mobiliser.web.cst.pages.bulkprocessing;

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.BulkFileHistoryListItem;
import com.sybase365.mobiliser.web.common.panels.BulkHistoryFileDetailsPanel;

public class BulkHistoryFileDetailsPage extends BaseBulkProcessingPage {
    private BulkFileHistoryListItem bulkFileHistoryListItem;

    public BulkHistoryFileDetailsPage() {
	super();
	initPageComponents();
    }

    public BulkHistoryFileDetailsPage(
	    BulkFileHistoryListItem bulkFileHistoryListItem) {
	super();
	this.bulkFileHistoryListItem = bulkFileHistoryListItem;
	initPageComponents();
    }

    private void initPageComponents() {
	add(new BulkHistoryFileDetailsPanel("bulkHistoryFileDetailsPanel",
		this, this.bulkFileHistoryListItem));
    }
}
