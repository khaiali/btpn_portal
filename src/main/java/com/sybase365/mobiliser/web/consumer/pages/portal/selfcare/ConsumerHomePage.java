package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CONSUMER_LOGIN)
public class ConsumerHomePage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	add(new FeedbackPanel("errorMessages"));
    }
}
