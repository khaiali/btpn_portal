package com.sybase365.mobiliser.web.consumer.pages.portal;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.consumer.pages.BaseConsumerPage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CONSUMER_LOGIN)
public abstract class BaseConsumerPortalPage extends BaseConsumerPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BaseConsumerPortalPage.class);

    public BaseConsumerPortalPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BaseConsumerPortalPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public String getApplicationName() {
	return getLocalizer().getString("consumer.portal.page.title", this);
    }

}
