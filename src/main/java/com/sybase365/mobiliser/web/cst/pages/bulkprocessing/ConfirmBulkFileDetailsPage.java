package com.sybase365.mobiliser.web.cst.pages.bulkprocessing;

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.BulkFile;
import com.sybase365.mobiliser.web.common.panels.ConfirmBulkFileDetailsPanel;

public class ConfirmBulkFileDetailsPage extends BaseBulkProcessingPage {
    private BulkFile bulkFile;

    public ConfirmBulkFileDetailsPage() {
	super();
	initPageComponents();
    }

    public ConfirmBulkFileDetailsPage(BulkFile bulkFile) {
	super();
	this.bulkFile = bulkFile;
	initPageComponents();
    }

    private void initPageComponents() {
	add(new ConfirmBulkFileDetailsPanel("confirmBulkFileDetailsPanel",
		this, this.bulkFile));
    }
}
