package com.sybase365.mobiliser.web.distributor.pages.bulkprocessing;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.FileUploadPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_WALLET_SERVICES)
public class FileUploadPage extends BaseBulkProcessingPage {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(FileUploadPage.class);

	public FileUploadPage() {
		super();
		add(new FileUploadPanel("fileUploadPanel", this));
	}

}
