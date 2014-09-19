package com.sybase365.mobiliser.web.cst.pages.bulkprocessing;

import com.sybase365.mobiliser.web.common.panels.FileUploadPanel;

public class FileUploadPage extends BaseBulkProcessingPage {

	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		add(new FileUploadPanel("fileUploadPanel", this));
	}
}
