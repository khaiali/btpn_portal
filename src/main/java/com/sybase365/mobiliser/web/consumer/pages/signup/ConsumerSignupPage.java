package com.sybase365.mobiliser.web.consumer.pages.signup;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.consumer.pages.BaseConsumerPage;

public class ConsumerSignupPage extends BaseConsumerPage {

    private final static Logger LOG = LoggerFactory
	    .getLogger(ConsumerSignupPage.class);

    public ConsumerSignupPage() {
	super();
    }

    public ConsumerSignupPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {

	LOG.debug("#ConsumerSignupLeftMenuGroup.buildLeftMenu()");

	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();

	return entries;
    }

    @Override
    public String getApplicationName() {
	return getLocalizer().getString("consumer.signup.page.title", this);
    }
}
