package com.sybase365.mobiliser.web.consumer.pages;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseLeftMenuView;
import com.sybase365.mobiliser.web.application.pages.BaseApplicationPage;

public abstract class BaseConsumerPage extends BaseApplicationPage {

    private static final long serialVersionUID = 1L;

    public BaseConsumerPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BaseConsumerPage(final PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	getMobiliserWebSession().setLeftMenu(buildLeftMenu());

	add(new SybaseLeftMenuView("leftMenu",
		new Model<LinkedList<IMenuEntry>>(getMobiliserWebSession()
			.getLeftMenu())));
    }

    public abstract LinkedList<IMenuEntry> buildLeftMenu();

    @Override
    protected Class getActiveMenu() {
	return this.getClass();
    }

    @Override
    protected boolean supportsSvaBalance() {
	return true;
    }
}
