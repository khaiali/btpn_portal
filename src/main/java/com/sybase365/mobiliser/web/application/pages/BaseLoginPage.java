package com.sybase365.mobiliser.web.application.pages;


import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import com.sybase365.mobiliser.web.application.MobiliserApplication;

public class BaseLoginPage extends MobiliserBasePage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BaseLoginPage.class);

    public BaseLoginPage() {
	super();
	LOG.debug("Created new BaseLoginPage");
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BaseLoginPage(final PageParameters parameters) {
	super(parameters);
	LOG.debug("Created new BaseLoginPage");
    }

    /**
     * @see com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage#initOwnComponents
    @Override
     */
    @Override
    protected void initOwnPageComponents() {

	add(new Link("changeToEnglish") {
	    @Override
	    public void onClick() {
		getWebSession().setLocale(getUpdatedLocale("en_US"));
	    }
	});

	add(new Link("changeToGerman") {
	    @Override
	    public void onClick() {
		getWebSession().setLocale(getUpdatedLocale("de_DE"));
	    }
	});

	add(new Label("applicationAboutName", MobiliserApplication.VERSION.NAME));
	add(new Label("applicationAboutVersion", MobiliserApplication.VERSION.VERSION));
	add(new Label("applicationAboutDate", MobiliserApplication.VERSION.DATE));
	add(new Label("applicationAboutTag", MobiliserApplication.VERSION.TAG));
	add(new Label("applicationAboutRevision", MobiliserApplication.VERSION.REVISION));
    }

}
