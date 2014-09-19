package com.sybase365.mobiliser.web.distributor.pages.bulkprocessing;

import java.util.LinkedList;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.distributor.pages.BaseDistributorPage;

public class BaseBulkProcessingPage extends BaseDistributorPage {

	public BaseBulkProcessingPage() {
		super();
	}

	@Override
	public LinkedList<IMenuEntry> buildLeftMenu() {
		LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
		return entries;
	}

}
