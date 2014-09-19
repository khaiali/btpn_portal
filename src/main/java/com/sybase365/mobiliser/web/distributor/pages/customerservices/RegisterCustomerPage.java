package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.panels.AttachmentsPanel;
import com.sybase365.mobiliser.web.common.panels.RegisterCustomerPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_WALLET_SERVICES)
public class RegisterCustomerPage extends BaseCustomerServicesPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(RegisterCustomerPage.class);

    private CustomerBean customer;

    private AttachmentsPanel attachmentsPanel;

    // private FeedbackPanel regFeedBackPanel;

    public RegisterCustomerPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public RegisterCustomerPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();
	this.customer = new CustomerBean();
	FeedbackPanel feedBackpanel = new FeedbackPanel("errorMessages");
	attachmentsPanel = new AttachmentsPanel("attachments.panel", null,
		this, feedBackpanel);
	RegisterCustomerPanel registerCustPanel = new RegisterCustomerPanel(
		"registerCustomerPanel", this, attachmentsPanel, this.customer);

	add(attachmentsPanel);
	add(registerCustPanel);
	add(feedBackpanel);
    }

}
