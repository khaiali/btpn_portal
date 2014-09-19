package com.sybase365.mobiliser.web.application.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

public class TechnicalErrorPage extends BaseLoginPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TechnicalErrorPage.class);

    public TechnicalErrorPage() {
	super();
	cleanupSession();
	LOG.info("Created new TechnicalErrorPage");
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public TechnicalErrorPage(final PageParameters parameters) {
	super(parameters);
	cleanupSession();
	LOG.info("Created new TechnicalErrorPage");
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	PageParameters param = super.getPageParameters();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final Form<?> form = new Form("technicalErrorForm",
		new CompoundPropertyModel<TechnicalErrorPage>(this));
	form.add(new FeedbackPanel("messages"));
	add(form);
    }

}
