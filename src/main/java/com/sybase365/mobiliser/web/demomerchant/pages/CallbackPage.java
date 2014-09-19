package com.sybase365.mobiliser.web.demomerchant.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

public class CallbackPage extends BaseDemoMerchantPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CallbackPage.class);

    public CallbackPage() {
	super();

	LOG.info("Created new CallbackPage");
    }

    public CallbackPage(PageParameters params) {
	super();
	LOG.info("Created new CallbackPage");
    }

    @Override
    protected void initOwnPageComponents() {
	add(new Label("test", "Callback Page"));
    }

}
