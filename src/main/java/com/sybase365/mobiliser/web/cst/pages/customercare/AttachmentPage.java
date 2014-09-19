package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.panels.AddAttachmentPanel;

public class AttachmentPage extends CustomerCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private AddAttachmentPanel attachmentsPanel;

    public AttachmentPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public AttachmentPage(final PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();
	FeedbackPanel feedBackpanel = new FeedbackPanel("errorMessages");
	CustomerBean searchCustomer = getMobiliserWebSession().getCustomer();
	attachmentsPanel = new AddAttachmentPanel("attachments.panel",
		searchCustomer.getId(), this, feedBackpanel);

	add(attachmentsPanel);
	add(feedBackpanel);
    }

}
