package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.Page;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.audit.CreateNoteRequest;
import com.sybase365.mobiliser.money.contract.v5_0.audit.CreateNoteResponse;
import com.sybase365.mobiliser.money.contract.v5_0.audit.beans.Note;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_NOTE_READ)
public class ContactNotePage extends BaseCustomerCarePage {

    private static final long sserialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ContactNotePage.class);
    private CustomerBean customer;
    private Page nextPage;

    public ContactNotePage(final Page nextPage) {
	super();
	// this.customer = customer;
	this.nextPage = nextPage;
	initPageComponents();
    }

    private Note note;

    public Note getNote() {
	return note;
    }

    public void setNote(Note note) {
	this.note = note;
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {
	Form<?> form = new Form("contactForm",
		new CompoundPropertyModel<ContactNotePage>(this));

	form.add(new LocalizableLookupDropDownChoice<Integer>("note.category",
		Integer.class, Constants.RESOURCE_BUNDLE_NOTE_CATEGORY, this,
		true, true) {
	    @Override
	    protected CharSequence getDefaultChoice(Object selected) {
		return null;
	    };
	}.setNullValid(true).setRequired(true).add(new ErrorIndicator()));
	form.add(new LocalizableLookupDropDownChoice<Integer>("note.status",
		Integer.class, Constants.RESOURCE_BUNDLE_NOTE_STATUS, this,
		true, true) {
	    @Override
	    protected CharSequence getDefaultChoice(Object selected) {
		return null;
	    };
	}.setNullValid(true).setRequired(true).add(new ErrorIndicator()));

	RequiredTextField<String> subject = new RequiredTextField<String>(
		"note.subject");
	subject.add(Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier);
	form.add(subject.setRequired(true).add(new ErrorIndicator()));
	TextArea body = new TextArea("note.body", new PropertyModel(this,
		"note.body"));
	body.add(Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier);
	form.add(body.setRequired(true).add(new ErrorIndicator()));
	form.add(new Button("save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		createContactNote();
	    };
	});
	form.add(new FeedbackPanel("errorMessages"));
	add(form);
	// getMobiliserWebSession().setCustomer(customer);

    }

    private void createContactNote() {

	LOG.debug("#ContactNotePage.createContactNote()");

	try {

	    CreateNoteRequest req = getNewMobiliserRequest(CreateNoteRequest.class);
	    getNote().setCustomerId(
		    getMobiliserWebSession().getCustomer().getId());
	    req.setNote(getNote());
	    CreateNoteResponse response = wsNoteClient.createNote(req);

	    if (!evaluateMobiliserResponse(response))
		return;
	    getMobiliserWebSession().setShowContact(false);
	    LOG.debug("# Successfully created a contact note");
	    if (nextPage == null) {
		getSession().invalidate();
		getRequestCycle().setRedirect(true);
		setResponsePage(getApplication().getHomePage());
	    } else {
		setResponsePage(nextPage);
		getMobiliserWebSession().setCustomer(null);
		getMobiliserWebSession().setShowContact(false);
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while creating a contact note", e);
	    error(getLocalizer().getString("create.contactNote.error", this));
	}

    }

}
