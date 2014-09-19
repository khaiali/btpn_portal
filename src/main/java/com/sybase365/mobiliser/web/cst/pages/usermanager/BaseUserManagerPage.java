package com.sybase365.mobiliser.web.cst.pages.usermanager;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.cst.pages.BaseCstPage;

public class BaseUserManagerPage extends BaseCstPage {

    public BaseUserManagerPage() {
	super();
    }

    public BaseUserManagerPage(PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	return entries;
    }

}
