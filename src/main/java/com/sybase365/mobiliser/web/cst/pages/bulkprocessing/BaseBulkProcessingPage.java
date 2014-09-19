package com.sybase365.mobiliser.web.cst.pages.bulkprocessing;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.cst.pages.BaseCstPage;

public class BaseBulkProcessingPage extends BaseCstPage {

	public BaseBulkProcessingPage() {
		super();
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	public BaseBulkProcessingPage(final PageParameters parameters) {
		super(parameters);
	}

	@Override
	public LinkedList<IMenuEntry> buildLeftMenu() {
		LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
		return entries;
	}

}
